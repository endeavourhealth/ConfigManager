package org.endeavourhealth.common.config;

import org.apache.commons.lang3.time.DateUtils;
import org.endeavourhealth.common.cache.ICache;
import org.endeavourhealth.common.config.models.ConfigCacheEntry;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfigCache implements ICache {
	private static Map<String, ConfigCacheEntry> _configCache = new HashMap<>();
	static int TIMEOUT = 60;

	@Override
	public String getName() {
		return "ConfigCache";
	}

	@Override
	public long getSize() {
		return _configCache.size();
	}

	@Override
	public void clearCache() {
		_configCache.clear();
	}

	public ConfigCacheEntry get(String configId, String appId) {
		ConfigCacheEntry cacheEntry = _configCache.get(configId + appId);

		if (cacheEntry == null || isExpired(cacheEntry))
			return null;

		return _configCache.get(configId + appId);
	}

	public void put(String configId, String appId, ConfigCacheEntry cacheEntry) {
		_configCache.put(configId + appId, cacheEntry);
	}

	private boolean isExpired(ConfigCacheEntry cacheEntry) {
		return  new Date().getTime() > DateUtils.addSeconds(cacheEntry.getCreateTime(), TIMEOUT).getTime();
	}
}
