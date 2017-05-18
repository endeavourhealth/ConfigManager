package org.endeavourhealth.common.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.cache.CacheManager;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.common.config.db.DatabaseLayer;
import org.endeavourhealth.common.config.db.JdbcLayer;
import org.endeavourhealth.common.config.models.ConfigCacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigManager {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigManager.class);

	public static final String LOGBACK_CONFIG = "logback";
	private static final String APP_GLOBAL = "global";

	static DatabaseLayer _databaseLayer;
	private static String _appId;
	private static ConfigCache _configCache;

	static { //runs when the main class is loaded.
		LOG.info("Config manager created");
		System.setProperty("org.jboss.logging.provider", "slf4j");
		LOG.info("Jboss logging switched to slf4j");

		_databaseLayer = new JdbcLayer();
		_configCache = new ConfigCache();
		CacheManager.registerCache(_configCache);
	}

	public synchronized static void Initialize(String appId) throws ConfigManagerException {
		_appId = appId;

		try {
			LOG.info("Connecting to config db..");
			createDatabaseConnection();
			LOG.info("Initializing logback");
			initializeLogback();
		} catch (Exception e) {
			throw new ConfigManagerException("Failed to initialize ConfigManager : "+ getStackTrace(e), e);
		}
		LOG.info("Config manager initialized");
	}

	public static void initializeLogback(String logbackXml) {

		//the logback XML uses a system property to write the log file, which we must set before loading the config
		Properties props = System.getProperties();
		props.setProperty("location.of.the.log.folder", _appId);

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

		//add the turbo filter to check for changes every so often (since we can't use the default one as we don't load from a file)
		LogbackReconfigureFilter timer = new LogbackReconfigureFilter(logbackXml, 60 * 1000);
		timer.setContext(loggerContext);
		timer.start();
		loggerContext.addTurboFilter(timer);
	}

	public static String getConfiguration(String configId) {
		return getConfiguration(configId, _appId);
	}


	public synchronized static String getConfiguration(String configId, String appIdParam) {
		// Try to get from cache
		ConfigCacheEntry cacheEntry = _configCache.get(configId, appIdParam);
		if (cacheEntry!=null && !cacheEntry.isExpired())
			return cacheEntry.getConfigData();

		// If no app specific result, look for a global setting
		cacheEntry = _configCache.get(configId, APP_GLOBAL);
		if (cacheEntry!=null && !cacheEntry.isExpired())
			return cacheEntry.getConfigData();

		// Failed, now try DB
		String data = _databaseLayer.getConfiguration(configId, appIdParam);
		if (data != null) {
			cacheEntry = new ConfigCacheEntry(data);
			_configCache.put(configId, appIdParam, cacheEntry);
			return data;
		}

		// If no app specific result, look for a global setting
		data = _databaseLayer.getConfiguration(configId, APP_GLOBAL);
		if (data != null) {
			cacheEntry = new ConfigCacheEntry(data);
			_configCache.put(configId, APP_GLOBAL, cacheEntry);
			return data;
		}

		return null;
	}

	public static JsonNode getConfigurationAsJson(String configId) throws IOException {
		String json = ConfigManager.getConfiguration(configId);
		return ObjectMapperPool.getInstance().readTree(json);
	}

	public static JsonNode getConfigurationAsJson(String configId, String appIdParam) throws IOException {
		String json = ConfigManager.getConfiguration(configId, appIdParam);
		return ObjectMapperPool.getInstance().readTree(json);
	}

	public static void setConfiguration(String configId, String data) throws Exception {

		//changed to detect failure and try again with the "global" config app ID
		if (!setConfiguration(configId, _appId, data)) {
			if (!setConfiguration(configId, APP_GLOBAL, data)) {
				throw new Exception("Failed to update config record for config " + configId + " and app " + _appId + " or " + APP_GLOBAL);
			}
		}
	}

	public synchronized static boolean setConfiguration(String configId, String appIdParam, String data) {
	    return _databaseLayer.setConfiguration(configId, appIdParam, data);
	}

	private static String getStackTrace(Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	private static synchronized void createDatabaseConnection() {
		_databaseLayer.createDatabaseConnection();
	}

	private static void initializeLogback() {
		String logbackXml = getConfiguration(LOGBACK_CONFIG);
		if (logbackXml == null || logbackXml.isEmpty()) {
			return;
		}

		initializeLogback(logbackXml);
	}
}
