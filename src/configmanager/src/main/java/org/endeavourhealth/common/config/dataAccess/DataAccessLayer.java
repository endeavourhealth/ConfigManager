package org.endeavourhealth.common.config.dataAccess;

import java.util.Map;

public interface DataAccessLayer {

    void initialize();

    String getConfiguration(String configId, String appIdParam);

    boolean setConfiguration(String configId, String appIdParam, String data);

    Map<String, String> getConfigurations(String appIdParam);
}
