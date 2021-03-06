package org.endeavourhealth.common.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.common.cache.CacheManager;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.common.config.dataAccess.DataAccessLayer;
import org.endeavourhealth.common.config.dataAccess.JdbcDAL;
import org.endeavourhealth.common.config.models.ConfigCacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConfigManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigManager.class);

    public static final String LOGBACK_CONFIG = "logback";
    private static final String APP_GLOBAL = "global";

    static DataAccessLayer _dataAccessLayer;
    private static String _appId;
    private static String _appSubId; //secondary ID so we can tell the difference between different Queue Reader apps
    private static ConfigCache _configCache;
    //private static LogbackReconfigureFilter _timer = null; //removed as this causes confusing logging when DB calls fail because it triggers further DB calls

    static { //runs when the main class is loaded.
        LOG.info("Config manager created");
        System.setProperty("org.jboss.logging.provider", "slf4j");
        LOG.info("Jboss logging switched to slf4j");

        _dataAccessLayer = new JdbcDAL();
        _configCache = new ConfigCache();
        CacheManager.registerCache(_configCache);
    }

    public synchronized static void Initialize(String appId) throws ConfigManagerException {
        initialize(appId, null);
    }

    public synchronized static void initialize(String appId, String appSubId) throws ConfigManagerException {

        //catch this being called again, which is a bad thing, or called again with a different appId, which is a very bad thing
        if (_appId != null) {
            if (appId.equals(_appId)) {
                LOG.warn("ConfigManager being initialised with appID [" + appId + "] more than once. This should only be called once, on application startup");
            } else if (!appId.equals(_appId)) {
                throw new ConfigManagerException("ConfigManager being initialised multiple times with different appIDs [" + _appId + "], [" + appId + "]. This should only be called once, on application startup");
            }
        }

        _appId = appId;
        _appSubId = appSubId;

        try {
            LOG.info("Initializing data access layer..");
            _dataAccessLayer.initialize();
            LOG.info("Initializing logback");
            initializeLogback();
        } catch (Exception e) {
            throw new ConfigManagerException("Failed to initialize ConfigManager [" + appId + "] : " + getStackTrace(e), e);
        }

        if (appSubId == null) {
            LOG.info("Config manager initialized [" + appId + "]");
        } else {
            LOG.info("Config manager initialized [" + appId + " - " + appSubId + "]");
        }
    }

    public static void initializeLogback(String logbackXml) {

        String logFilePrefix = _appId;
        if (_appSubId != null) {
            logFilePrefix += "-" + _appSubId;
        }

        //the logback XML uses a system property to write the log file, which we must set before loading the config
        Properties props = System.getProperties();
        props.setProperty("location.of.the.log.folder", _appId);
        props.setProperty("log.file.prefix", logFilePrefix);

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        InputStream stream = new ByteArrayInputStream(logbackXml.getBytes(StandardCharsets.UTF_8));
        try {
            configurator.doConfigure(stream);
            stream.close();
        } catch (Exception e) {
            throw new RuntimeException("Unable to configure logback", e);
        }

        //install the JUL (java.util.logging) to SLF4J logging bridge so that
        //anything using JUL will have its logging routed through SLF4J to logback, otherwise
        //we lose logging
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        //add the turbo filter to check for changes every so often (since we can't use the default one as we don't load from a file)
        /*if (_timer == null) {
            _timer = new LogbackReconfigureFilter(logbackXml, 60 * 1000);
            _timer.setContext(loggerContext);
            _timer.start();
            loggerContext.addTurboFilter(_timer);
        }*/
    }

    public static String getConfiguration(String configId) {
        return getConfiguration(configId, _appId);
    }


    public synchronized static String getConfiguration(String configId, String appIdParam) {

        //Try to get from cache using specific APP ID
        ConfigCacheEntry cached = _configCache.get(configId, appIdParam);
        if (cached != null) {
            return cached.getConfigData();
        }

        //If no app specific result, look for a global setting
        cached = _configCache.get(configId, APP_GLOBAL);
        if (cached != null) {
            return cached.getConfigData();
        }

        //Failed, now try DB for specific APP ID
        String data = _dataAccessLayer.getConfiguration(configId, appIdParam);
        if (data != null) {
            _configCache.put(configId, appIdParam, new ConfigCacheEntry(data));
            return data;
        }

        //If no app specific result, check DB for a global setting
        data = _dataAccessLayer.getConfiguration(configId, APP_GLOBAL);
        if (data != null) {
            _configCache.put(configId, APP_GLOBAL, new ConfigCacheEntry(data));
            return data;
        }

        //if we failed to find a record, we still need to add to the cache, otherwise repeated checks for
        //non-existent records (which is possible) will cause repeated DB hits
        _configCache.put(configId, APP_GLOBAL, new ConfigCacheEntry(null));

        return null;
    }

    public static JsonNode getConfigurationAsJson(String configId) throws IOException {
        String json = ConfigManager.getConfiguration(configId);
        if (!StringUtils.isEmpty(json)) {
            return ObjectMapperPool.getInstance().readTree(json);
        } else {
            return null;
        }
    }

    public static JsonNode getConfigurationAsJson(String configId, String appIdParam) throws IOException {
        String json = ConfigManager.getConfiguration(configId, appIdParam);
        if (!StringUtils.isEmpty(json)) {
            return ObjectMapperPool.getInstance().readTree(json);
        } else {
            return null;
        }
    }

    public static Map<String, String> getConfigurations(String appIdParam) {
        return _dataAccessLayer.getConfigurations(appIdParam);
    }

    public static Set<String> getAppIds() {
        return _dataAccessLayer.getAppIds();
    }


    public static Map<String, JsonNode> getConfigurationsAsJson(String appIdParam) throws IOException {
        Map<String, String> map = getConfigurations(appIdParam);

        Map<String, JsonNode> ret = new HashMap<>();

        for (String configId : map.keySet()) {
            String json = map.get(configId);
            JsonNode jsonNode = ObjectMapperPool.getInstance().readTree(json);
            ret.put(configId, jsonNode);
        }

        return ret;
    }

    public static void setConfiguration(String configId, String data) throws Exception {

        //changed to detect failure and try again with the "global" config app ID
        if (!_dataAccessLayer.setConfiguration(configId, _appId, data, false)) {
            if (!_dataAccessLayer.setConfiguration(configId, APP_GLOBAL, data, false)) {
                throw new Exception("Failed to update config record for config " + configId + " and app " + _appId + " or " + APP_GLOBAL);
            }
        }
    }

    public synchronized static boolean setConfiguration(String configId, String appIdParam, String data) {
        return _dataAccessLayer.setConfiguration(configId, appIdParam, data, true);
    }

    public synchronized static boolean deleteConfiguration(String configId, String appIdParam) {
        return _dataAccessLayer.deleteConfiguration(configId, appIdParam);
    }

    private static String getStackTrace(Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    private static void initializeLogback() {
        String logbackXml = getConfiguration(LOGBACK_CONFIG);
        if (logbackXml == null || logbackXml.isEmpty()) {
            return;
        }

        initializeLogback(logbackXml);
    }

    public static String getAppId() {
        return _appId;
    }

    public static String getAppSubId() {
        return _appSubId;
    }

    public static void shutdownLogback() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        /*if (_timer != null) {
            LOG.trace("[" + _appId + "] - Stopping logback timer ...");
            try {
                _timer.stop();
                LOG.trace("[" + _appId + "] - Logback timer stopped.");
            } catch (Exception e) {
                LOG.error("[" + _appId + "] - Error stopping logback timer", e);
            }
        }*/

        // Only use console from this point as logback is being shut down
        if (loggerContext != null) {
            System.out.println("[" + _appId + "] - Stopping logger context");
            try {
                loggerContext.stop();
                System.out.println("[" + _appId + "] - Logger context stopped");
            } catch (Exception e) {
                System.out.println("[" + _appId + "] - Error stopping logger context");
                e.printStackTrace();
            }
        }

    }

    public static List<ConfigHistory> getConfigurationHistory(String configId, String appIdParam) {
        return _dataAccessLayer.getConfigurationHistory(configId, appIdParam);
    }


}
