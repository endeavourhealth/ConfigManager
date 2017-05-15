package org.endeavourhealth.common.config.models;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class ConfigCacheEntry {
	private static final int TIMEOUT = 1;

	private String configData;
	private Date createTime;

	public ConfigCacheEntry() {}
	public ConfigCacheEntry(String configData) {
		this.configData = configData;
		this.createTime = new Date();
	}

	public boolean isExpired() {
		return new Date().getTime() > DateUtils.addMinutes(this.createTime, TIMEOUT).getTime();
	}

	public String getConfigData() {
		return configData;
	}
}
