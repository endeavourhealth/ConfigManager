package org.endeavourhealth.common.config;

public class MockDatabaseLayer implements org.endeavourhealth.common.config.db.DatabaseLayer {
	private String data;
	@Override
	public void createDatabaseConnection() {

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
}
