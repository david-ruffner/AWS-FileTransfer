package com.davidruffner.awsfiletransfer.messaging.consumer;

import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequestOuterClass;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class AwsFileTransferRequestConsumer {
    @KafkaListener(topics = "${messaging.fileTransferTopics.request}")
    public void listen(String message) {
        try {
            FileTransferRequestOuterClass.FileTransferRequest ftr_dec =
                    FileTransferRequestOuterClass.FileTransferRequest
                            .parseFrom(Base64.getDecoder().decode(message));

            System.out.printf("\nFile Name: %s\n", ftr_dec.getFileName());
            System.out.printf("Bucket Name: %s\n", ftr_dec.getBucketName());

            String dataStr = ftr_dec.hasData() ?
                    ftr_dec.getData().toStringUtf8() : "No Data";
            System.out.printf("Data: %s\n", dataStr);
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "S3 File Transfer Exception | %s", ex.getMessage()
            ));
        }
    }


}
