package com.davidruffner.awsfiletransfer.messaging.consumer;

import com.davidruffner.awsfiletransfer.action.ActionResponse;
import com.davidruffner.awsfiletransfer.action.objectActions.ObjectActionFactory;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequestMessage;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequestOuterClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class AwsFileTransferRequestConsumer {
    @Autowired
    ObjectActionFactory objectActionFactory;

    @KafkaListener(topics = "${messaging.fileTransferTopics.request}")
    public void listen(String message) {
        try {
            FileTransferRequestMessage ftrMsg =
                FileTransferRequestMessage.deserialize(message);

            ActionResponse response = objectActionFactory.callAction(ftrMsg.getPayload());
            System.out.printf("\nResponse Code: %s\n", response.getResponseCode());
            if (response.getResponseMessage().isPresent())
                System.out.printf("\nResponse Message: %s\n\n", response.getResponseMessage());
            else if (response.getErrorMessage().isPresent())
                System.out.printf("\nError Message: %s\n\n", response.getErrorMessage());
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "S3 File Transfer Exception | %s", ex.getMessage()
            ));
        }
    }


}
