/*
	Schema V1.0: Initial schema
*/

create table config
(
	app_id	varchar(100) not null,
	config_id varchar(100) not null,
	config_data text not null,
	constraint config_config_app_id_config_id_pk primary key (app_id, config_id)
);
