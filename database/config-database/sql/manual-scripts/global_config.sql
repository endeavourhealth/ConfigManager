INSERT INTO config
(app_id, config_id, config_data)
VALUES
('global', 'cassandra', '{
 "node" : [
    "127.0.0.1"
  ]
 }' );


INSERT INTO config
(app_id, config_id, config_data)
VALUES
('global', 'logback', '<?xml version="1.0" encoding="UTF-8"?>
<configuration>

  <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{dd MMM HH:mm:ss.SSS} [%thread] %-5level %logger{10}:%-3line - %msg%n
      </pattern>
    </encoder>
  </appender>

  <appender name="db" class="ch.qos.logback.classic.db.DBAppender">
    <!--<connectionSource class="ch.qos.logback.core.db.DriverManagerConnectionSource">-->
    <connectionSource class="org.endeavourhealth.common.config.LogbackPooledConnectionSource">
      <driverClass>org.postgresql.Driver</driverClass>
      <url>jdbc:postgresql://localhost:5432/logback</url>
      <user>postgres</user>
      <password></password>
    </connectionSource>
  </appender>

  <!-- wrap the DB appender in an async one, that means it won''t block when logging to the DB -->
  <appender name="db_async" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="db" />
    <discardingThreshold>0</discardingThreshold>
    <includeCallerData>true</includeCallerData>
  </appender>

  <!-- file logging uses this element to name log files using the time-->
  <timestamp key="bySecond" datePattern="yyyyMMdd''T''HHmmss"/>

  <!-- file logging uses this element to locate the log files in a folder using a java system property-->
  <property scope="context" name="LOGBACK_FOLDER" value="${location.of.the.log.folder}" />

  <!--
  <appender name="file" class="ch.qos.logback.core.FileAppender">
    <file>/var/log/${LOGBACK_FOLDER}/log-${bySecond}.txt</file>
    <encoder>
      <pattern>%d{dd MMM HH:mm:ss.SSS} [%thread] %-5level %logger{10}:%-3line - %msg%n
      </pattern>
    </encoder>
  </appender>
  -->

  <!--================================-->
  <!--logging settings for development-->
  <!--================================-->

  <!-- only want ERRORs from these packages -->
  <logger name="ch.qos.logback" level="ERROR"/>
  <logger name="com.mchange" level="ERROR"/>
  <logger name="com.datastax" level="ERROR"/>
  <logger name="org.hibernate" level="ERROR"/>
  <logger name="io.netty" level="ERROR"/>
  <logger name="com.zaxxer" level="WARN"/>

  <!-- enable TRACE logging for Endeavour code -->
  <logger name="org.endeavourhealth" level="TRACE"/>

  <!-- only log to stdout with INFO level -->
  <root level="INFO">
    <appender-ref ref="stdout" />
    <appender-ref ref="db_async" />
    <!--appender-ref ref="file" /-->
  </root>

  <!-- specify a shutdown hook for logging, so all loggers are flushed before app exit -->
  <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook">
	<!-- 8-Feb-2017 Jonny R Added delay to ensure we capture log messages during shutdown -->
  	<delay>5000</delay>
  </shutdownHook>

</configuration>' );



INSERT INTO config
(app_id, config_id, config_data)
VALUES
('global', 'rabbit', '{
	"username" : "guest",
	"password" : "guest",
	"nodes"	: "127.0.0.1:5672",
	"managementPortOffset" : "10000"
}');

INSERT INTO config
(app_id, config_id, config_data)
VALUES
('global', 'routings', '[
  {
    "exchangeName": "EdsInbound",
    "regex": ".*",
    "routeKey": "All",
    "description": "All inbound"
  },
  {
    "exchangeName": "EdsProtocol",
    "regex": ".*",
    "routeKey": "All",
    "description": "All protocols"
  },
  {
    "exchangeName": "EdsTransform",
    "regex": ".*",
    "routeKey": "All",
    "description": "All outbound transform"
  },
  {
    "exchangeName": "EdsSubscriber",
    "regex": ".*",
    "routeKey": "All",
    "description": "All outbound subscriber"
  }
]');

INSERT INTO config
(app_id, config_id, config_data)
VALUES
('global', 'coding','{
   "url" : "jdbc:postgresql://localhost:5432/coding",
   "username" : "postgres",
   "password" : ""
}');

INSERT INTO config
(app_id, config_id, config_data)
VALUES
('global', 'reference_db','{
   "url" : "jdbc:postgresql://localhost:5432/reference",
   "username" : "postgres",
   "password" : ""
}');

INSERT INTO config
(app_id, config_id, config_data)
VALUES
('global', 'eds_db', '{
   "url" : "jdbc:postgresql://localhost:5432/eds",
   "username" : "postgres",
   "password" : ""
}' );