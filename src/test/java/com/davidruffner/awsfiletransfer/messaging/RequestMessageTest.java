package com.davidruffner.awsfiletransfer.messaging;

import com.davidruffner.awsfiletransfer.configuration.storage.S3Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = { "test", "local" })
public class RequestMessageTest {
    private static final String TOPIC_NAME = "AWS_FILE_TRANSFER";
    private static final String GROUP_ID = "my-group";

    @Autowired
    S3Configuration s3Configuration;

    @Test
    void testS3Config() throws Exception {

    }

    @Test
    void testUploadAction() {

    }

    //    @Test
//    void testRequestMessageReceiveAndHandle() throws InvalidProtocolBufferException {
//        FileTransferRequest ftr = FileTransferRequest.newBuilder()
//                .setFileName("MyFile")
//                .setBucketName("inventory-tracker-data")
//                .setData(ByteString.copyFrom("Hello World".getBytes(StandardCharsets.UTF_8)))
//                .build();
//
//        String enc = Base64.getEncoder().encodeToString(ftr.toByteArray());
//        awsFileTransferRequestProducer.sendMessage(enc);
//    }
//
//    @Test
//    void testBuildRequestWithoutFileName() {
//        Assertions.assertThrows(UninitializedMessageException.class, () ->
//                FileTransferRequest.newBuilder().setBucketName("MyBucket").build());
//    }
//
//    @Test
//    void testWeights() {
//        System.out.printf("\nActive Profile: %s\n", profilesConfiguration.getActiveProfile());
////        for (Map.Entry<String, Integer> ent : profilesConfiguration.getWeights().entrySet()) {
////            System.out.printf("\nProfile: %s | Weight: %d\n", ent.getKey(), ent.getValue());
////        }
//    }
}
