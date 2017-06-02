INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-user-manager', 'keycloak', '{
  "realm": "endeavour",
  "realm-public-key": "<KEY>",
  "auth-server-url": "https://<HOSTNAME>/auth",
  "ssl-required": "external",
  "resource": "eds-user-manager",
  "public-client": true
}' );

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-user-manager', 'keycloak_proxy', '{
 "user" : "eds-ui",
 "pass" : "<PASSWORD>",
 "url" : "https://<HOSTNAME>/auth"
}');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-user-manager', 'application', '{ "appUrl" : "http://<HOSTNAME>" }');