insert into config.config
(
	app_id, 
	config_id, 
	config_data
)
values
('sftpreader', 'url', 'jdbc:mysql://<HOSTNAME>:3306/sftpreader?useSSL=false'),
('sftpreader', 'username', '<USERNAME>'),
('sftpreader', 'password', '<PASSWORD>');