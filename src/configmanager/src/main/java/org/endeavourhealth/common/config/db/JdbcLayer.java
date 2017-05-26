package org.endeavourhealth.common.config.db;

import org.endeavourhealth.common.config.ConfigManagerEnvVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JdbcLayer implements DatabaseLayer {
	private static final Logger LOG = LoggerFactory.getLogger(JdbcLayer.class);

	@Override
	public void createDatabaseConnection() {
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
	public boolean setConfiguration(String configId, String appIdParam, String data) {
		try(Connection conn = getConnection()) {
			String sql =
					" update config c" +
							" set config_data = ? " +
							" where c.app_id = ?" +
							" and   c.config_id = ?";

			try(PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setString(1, data);
				statement.setString(2, appIdParam);
				statement.setString(3, configId);

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

			String sql =
					" select config_id, config_data" +
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

	private Connection getConnection() throws SQLException, ClassNotFoundException {
		Map<String, String> envVars = System.getenv();

		Class.forName(envVars.getOrDefault(ConfigManagerEnvVars.JDBC_CLASS_ENV_VAR, "org.postgresql.Driver"));

		Properties props = new Properties();

		props.setProperty("user", envVars.getOrDefault(ConfigManagerEnvVars.JDBC_USER_ENV_VAR, "postgres"));
		props.setProperty("password", envVars.getOrDefault(ConfigManagerEnvVars.JDBC_PASSWORD_ENV_VAR, ""));

		return DriverManager.getConnection(envVars.getOrDefault(ConfigManagerEnvVars.JDBC_URL_ENV_VAR, "jdbc:postgresql://localhost:5432/config"), props);
	}
}
