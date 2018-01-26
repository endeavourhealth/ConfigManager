INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('messaging-api', 'keycloak', '{
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
('messaging-api', 'aws', '{
  "s3-bucket": "<S3BUCKETNAME>",
  "access-key-id": "<ACCESSKEYID>",
  "secret-access-key": "<SECRETACCESSKEY>",
  "region": "<REGION>",
  "keypath-prefix: "<S3 KEYPATH PREFIX>"
}' );

INSERT INTO config.config
(app_id, config_id, config_data)
VALUES
('messaging-api', 'api-configuration', '<?xml version="1.0" encoding="UTF-8"?>
<ApiConfiguration xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xsi:noNamespaceSchemaLocation="../../../../eds-messaging-core/src/main/resources/ApiConfiguration.xsd">
    <GetData>
        <Pipeline>
            <ValidateMessageType/>
        </Pipeline>
    </GetData>
    <GetDataAsync>
        <Pipeline>
            <ValidateMessageType/>
        </Pipeline>
    </GetDataAsync>
    <PostMessage>
        <Pipeline>
            <ValidateMessageType/>
        </Pipeline>
    </PostMessage>
    <PostMessageAsync>
        <Pipeline>
            <OpenEnvelope/>
            <PostMessageToLog>
                <EventType>Receive</EventType>
            </PostMessageToLog>
            <DetermineRelevantProtocolIds/>
            <ValidateSender/>
            <ValidateMessageType/>
            <PostMessageToLog>
                <EventType>Validate</EventType>
            </PostMessageToLog>
            <PostMessageToExchange>
                <Exchange>EdsInbound</Exchange>
                <RoutingHeader>SenderLocalIdentifier</RoutingHeader>
            </PostMessageToExchange>
            <ReturnResponseAcknowledgement/>
        </Pipeline>
    </PostMessageAsync>
</ApiConfiguration>');