package com.davidruffner.awsfiletransfer.configuration.messaging;

public class FileTransferTopicsConfig {
    private String request;
    private String response;

    public String getRequestTopic() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponseTopic() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
