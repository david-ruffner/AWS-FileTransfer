package com.davidruffner.awsfiletransfer.messaging;

import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequestMessage;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequestOuterClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest.ActionType.UPLOAD;
import static com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest.StorageControllerType.S3;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = { "test", "local" })
public class AwsFileTransferRequestConsumerTest {

    @Test
    void testProtoEncode() throws IOException {
        File encodedFile = new File("src/test/resources/" +
                "TestData/file_transfer_request_encoded.txt");
        FileTransferRequest ftr = new FileTransferRequest(new FileInputStream(encodedFile));

        String encoded = FileTransferRequest.encode(ftr);
        System.out.printf("\nEncoded: %s\n", encoded);

        FileTransferRequest decodedFtr = new FileTransferRequest(encoded);
        System.out.printf("\nKey Name: %s\n", decodedFtr.getKeyName());
        System.out.printf("\nContainer Name: %s\n", decodedFtr.getContainerName());
        System.out.printf("\nController Name: %s\n", decodedFtr.getControllerType());
        System.out.printf("\nAction Type: %s\n", decodedFtr.getActionType());
        System.out.printf("\nMetadata Action Type: %s\n", decodedFtr.getMetadataActionType().get());
        System.out.printf("\nNew Container Name: %s\n", decodedFtr.getNewContainerName().get());
        System.out.printf("\nNew Key Name: %s\n", decodedFtr.getNewKeyName().get());
        System.out.printf("\nMeta Data Key: %s\n", decodedFtr.getMetadataKey().get());
        System.out.printf("\nNew Meta Data Value: %s\n\n", decodedFtr.getNewMetadataValue().get());

        System.out.println("Map Values:\n");
        decodedFtr.getMetadataMap().get().forEach((k, v) ->
                System.out.printf("Key: %s | Value: %s\n", k, v));
        System.out.println("\n\n");
    }

    @Test
    void testFTRMessage() throws JsonProcessingException, FileNotFoundException {
        File encodedFile = new File("src/test/resources/" +
                "TestData/file_transfer_request_encoded.txt");
        FileTransferRequest ftr = new FileTransferRequest(new FileInputStream(encodedFile));

        FileTransferRequestMessage ftrMsg =
                new FileTransferRequestMessage("My Token", ftr);

        String serialized = FileTransferRequestMessage.serialize(ftrMsg);
        System.out.printf("\nSerialized Msg: %s\n\n", serialized);

        FileTransferRequestMessage newFtrMsg =
                FileTransferRequestMessage.deserialize(serialized);
        System.out.printf("\nToken: %s\n", newFtrMsg.getToken());
        System.out.printf("\nTimestamp: %s\n", newFtrMsg.getTimestamp());
        System.out.printf("\nPayload Key Name: %s\n", newFtrMsg.getPayload().getKeyName());
        System.out.printf("\nPayload Container Name: %s\n\n",
                newFtrMsg.getPayload().getContainerName());
    }

    @Test
    void testProtoFunctions() throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader(
//                "src/test/resources/TestData/" +
//                        "file_transfer_request_decoded"));
//        StringBuilder sb = new StringBuilder();
//        String line = null;
//        String ls = System.lineSeparator();
//
//        while ((line = reader.readLine()) != null) {
//            sb.append(line);
//            sb.append(ls);
//        }
//
//        sb.deleteCharAt(sb.length() - 1);
//        reader.close();

        File encodedFile = new File("src/test/resources/" +
                "TestData/file_transfer_request_encoded.txt");
        InputStream inputStream = new FileInputStream(encodedFile);

        FileTransferRequest ftr = new FileTransferRequest(inputStream);
        System.out.printf("\nKey Name: %s\n", ftr.getKeyName());
        System.out.printf("\nContainer Name: %s\n", ftr.getContainerName());
        System.out.printf("\nController Name: %s\n", ftr.getControllerType());
        System.out.printf("\nAction Type: %s\n", ftr.getActionType());

//        System.out.println(sb);
//        System.out.printf("\nOutput: %s\n", sb);
    }
}
