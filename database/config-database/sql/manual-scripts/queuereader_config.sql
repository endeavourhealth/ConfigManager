INSERT INTO config
(app_id, config_id, config_data)
VALUES
('queuereader', 'inbound', '<?xml version="1.0" encoding="UTF-8"?>
<QueueReaderConfiguration xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../../../eds-messaging-core/src/main/resources/QueueReaderConfiguration.xsd">
    <Queue>EdsInbound-All</Queue>
    <Pipeline>
        <PostMessageToLog>
            <EventType>Transform_Start</EventType>
        </PostMessageToLog>
        <MessageTransformInbound>
            <SharedStoragePath>C:\SFTPData</SharedStoragePath>
            <FilingThreadLimit>10</FilingThreadLimit>
        </MessageTransformInbound>
        <PostMessageToLog>
            <EventType>Transform_End</EventType>
        </PostMessageToLog>
        <PostMessageToExchange>
            <Exchange>EdsProtocol</Exchange>
            <RoutingHeader>SenderLocalIdentifier</RoutingHeader>
            <MulticastHeader>BatchIds</MulticastHeader>
        </PostMessageToExchange>
    </Pipeline>
</QueueReaderConfiguration>');

INSERT INTO config
(app_id, config_id, config_data)
VALUES
('queuereader', 'protocol', '<?xml version="1.0" encoding="UTF-8"?>
<QueueReaderConfiguration xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../../../eds-messaging-core/src/main/resources/QueueReaderConfiguration.xsd">
    <Queue>EdsProtocol-All</Queue>
    <Pipeline>
        <RunDataDistributionProtocols/>
        <PostMessageToExchange>
            <Exchange>EdsTransform</Exchange>
            <RoutingHeader>SenderLocalIdentifier</RoutingHeader>
            <MulticastHeader>TransformBatch</MulticastHeader>
        </PostMessageToExchange>
    </Pipeline>
</QueueReaderConfiguration>');


INSERT INTO config
(app_id, config_id, config_data)
VALUES
('queuereader', 'transform', '<?xml version="1.0" encoding="UTF-8"?>
<QueueReaderConfiguration xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../../../eds-messaging-core/src/main/resources/QueueReaderConfiguration.xsd">
    <Queue>EdsTransform-All</Queue>
    <Pipeline>
        <MessageTransformOutbound/>
        <PostMessageToExchange>
            <Exchange>EdsSubscriber</Exchange>
            <RoutingHeader>SenderLocalIdentifier</RoutingHeader>
            <MulticastHeader>SubscriberBatch</MulticastHeader>
        </PostMessageToExchange>
    </Pipeline>
</QueueReaderConfiguration>');

INSERT INTO config
(app_id, config_id, config_data)
VALUES
('queuereader', 'subscriber', '<?xml version="1.0" encoding="UTF-8"?>
<QueueReaderConfiguration xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../../../../eds-messaging-core/src/main/resources/QueueReaderConfiguration.xsd">
    <Queue>EdsSubscriber-All</Queue>
    <Pipeline>
        <PostToSubscriberWebService/>
    </Pipeline>
</QueueReaderConfiguration>');

