package org.endeavourhealth.common.config.db;

import java.util.Map;

public interface DatabaseLayer {

    void createDatabaseConnection();

    String getConfiguration(String configId, String appIdParam);

    boolean setConfiguration(String configId, String appIdParam, String data);

    Map<String, String> getConfigurations(String appIdParam);
}
