package com.davidruffner.awsfiletransfer.messaging.producer;

import com.davidruffner.awsfiletransfer.configuration.messaging.MessagingConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AwsFileTransferResponseProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName;

    public AwsFileTransferResponseProducer(KafkaTemplate<String, String> kafkaTemplate,
                                           MessagingConfiguration messagingConfiguration) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = messagingConfiguration.getFileTransferTopics().getResponseTopic();
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(this.topicName, message);
    }
}
