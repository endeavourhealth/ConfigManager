package org.endeavourhealth.common.config.dataAccess;

import org.endeavourhealth.common.config.ConfigHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class JdbcDAL implements DataAccessLayer {
	private static final String JDBC_CLASS_ENV_VAR = "CONFIG_JDBC_CLASS";
	private static final String JDBC_URL_ENV_VAR = "CONFIG_JDBC_URL";
	private static final String JDBC_USER_ENV_VAR = "CONFIG_JDBC_USERNAME";
	private static final String JDBC_PASSWORD_ENV_VAR = "CONFIG_JDBC_PASSWORD";
	private static final Logger LOG = LoggerFactory.getLogger(JdbcDAL.class);

	@Override
	public void initialize() {
		// Do nothing - Connection created on demand
	}

	@Override
	public String getConfiguration(String configId, String appIdParam) {
		try(Connection conn = getConnection()) {
			String sql =
					" select config_data" +
							" from " +
							"    config" +
							" where app_id = ?" +
							" and   config_id = ?";

			try(PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setString(1, appIdParam);
				statement.setString(2, configId);
				ResultSet rs = statement.executeQuery();

				String data = null;
				if (rs.next())
					data = rs.getString("config_data");

				return data;
			}
		} catch (Exception e) {
			LOG.error("Error getting configuration ["+configId+"] for application ["+appIdParam+"]", e);
			return null;
		}
	}

	@Override
	public boolean setConfiguration(String configId, String appIdParam, String data, boolean createIfNecessary) {
		try(Connection conn = getConnection()) {
			String sql = null;
			if (createIfNecessary) {
				//note ordering of cols is explicitly the same as the other SQL, so the parameters can be added in the same order
				sql = "INSERT INTO config (config_data, app_id, config_id)"
						+ " VALUES (?, ?, ?)"
						+ " ON DUPLICATE KEY UPDATE"
						+ " config_data = VALUES(config_data)";
			} else {
				sql = "UPDATE config "
						+ " SET config_data = ?"
						+ " WHERE app_id = ?"
						+ " AND config_id = ?";;

			}

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				int col = 1;
				statement.setString(col++, data);
				statement.setString(col++, appIdParam);
				statement.setString(col++, configId);

				//changed to return whether a row was updated
				int rows = statement.executeUpdate();

				return rows > 0;
				//statement.execute();
			}

		} catch (Exception e) {
			LOG.error("Error setting configuration ["+configId+"] for application ["+appIdParam+"]", e);
			return false;
		}
	}

	@Override
	public Map<String, String> getConfigurations(String appIdParam) {
		try (Connection conn = getConnection()) {

			String sql = " select config_id, config_data" +
							" from " +
							"    config" +
							" where app_id = ?";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setString(1, appIdParam);
				ResultSet rs = statement.executeQuery();

				Map<String, String> ret = new HashMap<>();

				while (rs.next()) {
					String id = rs.getString("config_id");
					String data = rs.getString("config_data");
					ret.put(id, data);
				}

				return ret;
			}

		} catch (Exception e) {
			LOG.error("Error getting configurations application [" + appIdParam + "]", e);
			return null;
		}
	}

	@Override
	public Set<String> getAppIds() {
		try (Connection conn = getConnection()) {

			String sql = "SELECT DISTINCT app_id FROM config";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {

				ResultSet rs = statement.executeQuery();

				Set<String> ret = new HashSet<>();
				while (rs.next()) {
					String appId = rs.getString(1);
					ret.add(appId);
				}
				return ret;
			}

		} catch (Exception e) {
			LOG.error("Error getting app IDs", e);
			return null;
		}

	}

	@Override
	public boolean deleteConfiguration(String configId, String appIdParam) {
		try(Connection conn = getConnection()) {
			String sql = "DELETE FROM config WHERE app_id = ? AND config_id = ?";

			try(PreparedStatement statement = conn.prepareStatement(sql)) {
				int col = 1;
				statement.setString(col++, appIdParam);
				statement.setString(col++, configId);

				int rows = statement.executeUpdate();
				return rows > 0;
			}

		} catch (Exception e) {
			LOG.error("Error setting configuration ["+configId+"] for application ["+appIdParam+"]", e);
			return false;
		}
	}

	@Override
	public List<ConfigHistory> getConfigurationHistory(String configId, String appIdParam) {

		try (Connection conn = getConnection()) {
			String sql = "SELECT config_data, dt_changed FROM config_history WHERE app_id = ? AND config_id = ? ORDER BY dt_changed ASC";

			try(PreparedStatement statement = conn.prepareStatement(sql)) {
				int col = 1;
				statement.setString(col++, appIdParam);
				statement.setString(col++, configId);

				List<ConfigHistory> ret = new ArrayList<>();
				String previousData = null;

				ResultSet rs = statement.executeQuery();
				while (rs.next()) {
					col = 1;
					String configData = rs.getString(col++);
					Date dtChanged = new java.util.Date(rs.getTimestamp(col++).getTime());

					ConfigHistory h = new ConfigHistory();
					h.setDtChanged(dtChanged);
					h.setChangedTo(configData);
					h.setChangedFrom(previousData);

					ret.add(h);

					previousData = configData;
				}

				return ret;
			}

		} catch (Exception e) {
			LOG.error("Error getting configuration history for config_id [" + configId + "] for appId [" + appIdParam + "]", e);
			return null;
		}
	}

	private Connection getConnection() throws SQLException, ClassNotFoundException {
		Map<String, String> envVars = System.getenv();

		Class.forName(envVars.getOrDefault(JDBC_CLASS_ENV_VAR, "org.postgresql.Driver"));

		Properties props = new Properties();

		props.setProperty("user", envVars.getOrDefault(JDBC_USER_ENV_VAR, "postgres"));
		props.setProperty("password", envVars.getOrDefault(JDBC_PASSWORD_ENV_VAR, ""));

		return DriverManager.getConnection(envVars.getOrDefault(JDBC_URL_ENV_VAR, "jdbc:postgresql://localhost:5432/config"), props);
	}
}
