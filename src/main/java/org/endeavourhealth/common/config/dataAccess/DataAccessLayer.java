package org.endeavourhealth.common.config.dataAccess;

import org.endeavourhealth.common.config.ConfigHistory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataAccessLayer {

    void initialize();

    String getConfiguration(String configId, String appIdParam);

    boolean setConfiguration(String configId, String appIdParam, String data, boolean createIfNecessary);

    Map<String, String> getConfigurations(String appIdParam);

    Set<String> getAppIds();

    boolean deleteConfiguration(String configId, String appIdParam);

    List<ConfigHistory> getConfigurationHistory(String configId, String appIdParam);
}
