package org.endeavourhealth.common.config.models;

import java.util.Date;

public class ConfigCacheEntry {
	private String configData;
	private Date createTime;

	public ConfigCacheEntry() {}

	public ConfigCacheEntry(String configData) {
		this.configData = configData;
		this.createTime = new Date();
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public String getConfigData() {
		return this.configData;
	}
}
