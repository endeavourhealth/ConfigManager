use config;

drop trigger if exists after_config_insert;
drop trigger if exists after_config_update;
drop trigger if exists after_config_delete;


create table config
(
	app_id	varchar(100) not null,
	config_id varchar(100) not null,
	config_data text not null,
	constraint config_config_app_id_config_id_pk primary key (app_id, config_id)
);

create table config_history
(
	app_id	varchar(100) not null,
	config_id varchar(100) not null,
	config_data text, -- allow nulls
    transaction_type varchar(100) not null,
    dt_changed datetime(3) not null
);

DELIMITER $$
CREATE TRIGGER after_config_insert
  AFTER INSERT ON config
  FOR EACH ROW
  BEGIN
    INSERT INTO config_history (
		app_id,
        config_id,
        config_data,
        transaction_type,
        dt_changed
    ) VALUES (
		NEW.app_id,
        NEW.config_id,
        NEW.config_data,
        'insert',
        now()
    );
  END$$
DELIMITER ;


DELIMITER $$
CREATE TRIGGER after_config_update
  AFTER UPDATE ON config
  FOR EACH ROW
  BEGIN
    INSERT INTO config_history (
		app_id,
        config_id,
        config_data,
        transaction_type,
        dt_changed
    ) VALUES (
		NEW.app_id,
        NEW.config_id,
        NEW.config_data,
        'update',
        now()
    );
  END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER after_config_delete
  AFTER DELETE ON config
  FOR EACH ROW
  BEGIN
    INSERT INTO config_history (
		app_id,
        config_id,
        config_data,
        transaction_type,
        dt_changed
    ) VALUES (
		OLD.app_id,
        OLD.config_id,
        null,
        'delete',
        now()
    );
  END$$
DELIMITER ;