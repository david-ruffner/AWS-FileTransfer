package com.davidruffner.awsfiletransfer.configuration.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "messaging")
public class MessagingConfiguration {
    private FileTransferTopicsConfig fileTransferTopics;

    public FileTransferTopicsConfig getFileTransferTopics() {
        return fileTransferTopics;
    }

    public void setFileTransferTopics(FileTransferTopicsConfig fileTransferTopics) {
        this.fileTransferTopics = fileTransferTopics;
    }
}
