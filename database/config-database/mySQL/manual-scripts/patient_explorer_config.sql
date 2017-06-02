INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-patient-explorer', 'keycloak', '{
  "realm": "endeavour",
  "realm-public-key": "<KEY>",
  "auth-server-url": "https://<HOSTNAME>/auth",
  "ssl-required": "external",
  "resource": "eds-ui",
  "public-client": true
}' );

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-patient-explorer', 'keycloak_proxy_user', 'eds-ui' );

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-patient-explorer', 'keycloak_proxy_pass', '<PASSWORD>' );

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-patient-explorer', 'enterprise-lite-db', 'jdbc:mysql://<HOSTNAME>:3306/enterprise_data?user=<USERNAME>&password=<PASSWORD>');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-patient-explorer', 'application', '{ "appUrl" : "http://<HOSTNAME>" }');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-patient-explorer', 'enterprise-lite','{
    "username" : "<USERNAME>",
    "password" : "<PASSWORD>",
    "ui-username" : "<USERNAME>",
    "ui-password" : "<PASSWORD>",
    "url"    : "jdbc:mysql://<HOSTNAME>:3306/enterprise_data"
}');