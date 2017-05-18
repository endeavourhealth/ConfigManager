package org.endeavourhealth.common.config;

import org.endeavourhealth.common.cache.ICache;
import org.endeavourhealth.common.config.models.ConfigCacheEntry;

import java.util.HashMap;
import java.util.Map;

public class ConfigCache implements ICache {
	private static Map<String, ConfigCacheEntry> _configCache = new HashMap<>();

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
		return _configCache.get(configId + appId);
	}

	public void put(String configId, String appId, ConfigCacheEntry cacheEntry) {
		_configCache.put(configId + appId, cacheEntry);
	}
}
