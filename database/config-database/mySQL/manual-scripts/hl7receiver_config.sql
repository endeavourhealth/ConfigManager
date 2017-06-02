insert into config.config
(
	app_id, 
	config_id, 
	config_data
)
values
('hl7receiver', 'url', '"jdbc:mysql://<HOSTNAME>:3306/hl7receiver'),
('hl7receiver', 'username', '<USERNAME>'),
('hl7receiver', 'password', '<PASSWORD>');
