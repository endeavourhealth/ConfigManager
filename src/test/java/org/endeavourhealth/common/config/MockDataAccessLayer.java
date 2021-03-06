package org.endeavourhealth.common.config;

import org.endeavourhealth.common.config.dataAccess.DataAccessLayer;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public boolean setConfiguration(String configId, String appIdParam, String data, boolean createIfNecessary) {
		this.data = data;
		return true;
	}

	@Override
	public Map<String, String> getConfigurations(String appIdParam) {
		return null;
	}

	@Override
	public Set<String> getAppIds() {
		return null;
	}

	@Override
	public boolean deleteConfiguration(String configId, String appIdParam) {
		return false;
	}

	@Override
	public List<ConfigHistory> getConfigurationHistory(String configId, String appIdParam) {
		return null;
	}
}
