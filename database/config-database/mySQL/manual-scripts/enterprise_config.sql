INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('enterprise', 'admin_database', '{
  "driverClass" : "com.mysql.cj.jdbc.Driver",
	"username" : "<USERNAME>",
	"password" : "<PASSWORD>",
	"url"	: "jdbc:mysql://<HOSTNAME>:3306/enterprise_admin?useSSL=false",
	"pseudonymised": false
}');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('enterprise','patient_database','{
  "driverClass" : "com.mysql.cj.jdbc.Driver",
	"username" : "<USERNAME>",
	"password" : "<PASSWORD>",
	"url"	: "jdbc:mysql://<HOSTNAME>:3306/enterprise_data_pseudonymised?useSSL=false",
	"pseudonymised": false
}');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('enterprise','keycloak','{
  "realm": "endeavour",
  "realm-public-key": "<KEY>",
  "auth-server-url": "https://<HOSTNAME>/auth",
  "ssl-required": "external",
  "resource": "eds-ui",
  "public-client": true
}');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('enterprise','<SPECIFIC_ENTERPRISE>','{
	"driverClass" : "com.mysql.cj.jdbc.Driver",
	"enterprise_username": "<USERNAME>",
	"enterprise_password": "<PASSWORD>",
	"enterprise_url": "jdbc:mysql://<HOSTNAME>:3306/enterprise_data_pseudonymised?useSSL=false",
	"pseudonymised": true,
	"salt": "<SALT>",
	"transform_url": "jdbc:mysql://<HOSTNAME>:3306/<TRANSFORM_DB>",
	"transform_username": "<USERNAME>",
	"transform_password": "<PASSWORD>"
}');