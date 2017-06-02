INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-ui', 'keycloak', '{
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
('eds-ui', 'keycloak_proxy', '{
 "user" : "eds-ui",
 "pass" : "<PASSWORD>",
 "url" : "https://<HOSTNAME>/auth"
}');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-ui', 'application', '{ "appUrl" : "http://<HOSTNAME>" }');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-ui', 'logbackDb','{
   "url" : "jdbc:mysql://<HOSTNAME>:3306/logback",
   "username" : "<USERNAME>",
   "password" : "<PASSWORD>"
}');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-ui', 'OrganisationManagerDB','{
   "url" : "jdbc:mysql://<HOSTNAME>:3306/OrganisationManager",
   "username" : "<USERNAME>",
   "password" : "<PASSWORD>"
}');

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('eds-ui', 'GoogleMapsAPI','{
   "url" : "https://maps.googleapis.com/maps/api/geocode/json?address=",
   "apiKey" : "<API_KEY>"
}');