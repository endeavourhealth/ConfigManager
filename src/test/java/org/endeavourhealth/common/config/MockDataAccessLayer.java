package org.endeavourhealth.common.config;

import org.endeavourhealth.common.config.dataAccess.DataAccessLayer;

import java.util.Map;

public class MockDataAccessLayer implements DataAccessLayer {
	private String data;
	@Override
	public void initialize() {

	}

	@Override
	public String getConfiguration(String configId, String appIdParam) {
		return this.data;
	}

	@Override
	public boolean setConfiguration(String configId, String appIdParam, String data) {
		this.data = data;
		return true;
	}

	@Override
	public Map<String, String> getConfigurations(String appIdParam) {
		return null;
	}
}
