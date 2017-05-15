package org.endeavourhealth.common.config.db;

public interface DatabaseLayer {
    void createDatabaseConnection();
    String getConfiguration(String configId, String appIdParam);
    boolean setConfiguration(String configId, String appIdParam, String data);
}
