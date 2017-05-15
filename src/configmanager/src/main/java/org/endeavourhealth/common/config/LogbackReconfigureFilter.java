package org.endeavourhealth.common.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

/**
 * replicates the functionality of ReconfigureOnChangeFilter but supporting
 * when the logback config is in the database rather than in a file
 */
public class LogbackReconfigureFilter extends TurboFilter {

    private final String originalXml;
    private final long checkIntervalMillis;

    private long nextCheckMillis;

    //these are copied from ReconfigureOnChangeFilter and are used as a way
    //to cut down on more expensive processing (getting system current millis)
    private long invocationCounter = 0;
    private volatile long mask = 0xF;
    private volatile long lastMaskCheck = System.currentTimeMillis();
    private static final int MAX_MASK = 0xFFFF;
    private static final long MASK_INCREASE_THRESHOLD = 100;
    private static final long MASK_DECREASE_THRESHOLD = MASK_INCREASE_THRESHOLD*8;


    public LogbackReconfigureFilter(String originalXml, long checkIntervalMillis) {
        this.originalXml = originalXml;
        this.checkIntervalMillis = checkIntervalMillis;
    }

    @Override
    public void start() {

        addInfo("Will scan for changes every " + checkIntervalMillis + "ms");
        updateNextCheck(System.currentTimeMillis());
        super.start();
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level,
                              String format, Object[] params, Throwable t) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        // for performance reasons, skip change detection (MASK-1) times out of MASK.
        // Only once every MASK calls is change detection code executed
        // Note that MASK is a variable itself.
        if (((invocationCounter++) & mask) != mask) {
            return FilterReply.NEUTRAL;
        }

        //once we pass the mask filtering, we get the system millis and check that against when we should next check
        long now = System.currentTimeMillis();
        updateMaskIfNecessary(now);

        if (now > nextCheckMillis) {

            updateNextCheck(now);

            //compare the current state of the XML
            String latestXml = ConfigManager.getConfiguration(ConfigManager.LOGBACK_CONFIG);
            if (!latestXml.equals(originalXml)) {

                // Even though reconfiguration involves resetting the loggerContext,
                // which clears the list of turbo filters including this instance, it is
                // still possible for this instance to be subsequently invoked by another
                // thread if it was already executing when the context was reset.
                nextCheckMillis = Long.MAX_VALUE;

                addInfo("Detected change in logback XML");
                context.getExecutorService().submit(new ReconfiguringRunnable(latestXml));
            }
        }

        return FilterReply.NEUTRAL;
    }

    private void updateNextCheck(long now) {
        nextCheckMillis = now + checkIntervalMillis;
    }

    private void updateMaskIfNecessary(long now) {
        final long timeElapsedSinceLastMaskUpdateCheck = now - lastMaskCheck;
        lastMaskCheck = now;
        if (timeElapsedSinceLastMaskUpdateCheck < MASK_INCREASE_THRESHOLD && (mask < MAX_MASK)) {
            mask = (mask << 1) | 1;
        } else if (timeElapsedSinceLastMaskUpdateCheck > MASK_DECREASE_THRESHOLD) {
            mask = mask >>> 2;
        }
    }

    class ReconfiguringRunnable implements Runnable {

        private String latestXml;

        public ReconfiguringRunnable(String latestXml) {
            this.latestXml = latestXml;
        }

        @Override
        public void run() {
            ConfigManager.initializeLogback(latestXml);
        }
    }
}
