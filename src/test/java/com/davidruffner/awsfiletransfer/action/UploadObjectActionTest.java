package com.davidruffner.awsfiletransfer.action;

import com.davidruffner.awsfiletransfer.storage.controllers.S3Storage;
import com.davidruffner.awsfiletransfer.storage.controllers.S3StorageObject;
import com.davidruffner.awsfiletransfer.storage.metadata.S3Metadata;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerType.S3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = { "local" })
public class UploadObjectActionTest {

    private static final String BUCKET_NAME = "inventory-tracker-data";

    @Autowired
    UploadObjectAction.Builder builder;

    @Autowired
    S3Storage s3Storage;

    @Test
    public void testUploadAction() throws IOException {
        File dataFile = new File("src/test/resources/TestData/piano.jpeg");
        InputStream dataStream = new FileInputStream(dataFile);

        builder.newBuilder()
                .storageController(S3)
                .keyName("NewFile.png")
                .containerName(BUCKET_NAME)
                .inputStream(dataStream)
                .doAction();

        S3StorageObject obj = s3Storage.getObject("NewFile.png", BUCKET_NAME);
        assertEquals(dataFile.length(), obj.getInputStream().readAllBytes().length);
        s3Storage.deleteObject("NewFile.png", BUCKET_NAME);
    }

    @Test
    public void testUploadActionWithMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);

        S3Metadata metadata = new S3Metadata(Map.ofEntries(
                Map.entry("user-description", "This is a description.")
        ));

        builder.newBuilder()
                .storageController(S3)
                .keyName("NewFile.txt")
                .containerName(BUCKET_NAME)
                .inputStream(dataStream)
                .withMetadata(metadata)
                .doAction();

        S3StorageObject obj = s3Storage.getObject("NewFile.txt", BUCKET_NAME);
        assertEquals("This is a description.", obj.getMetadata("user-description"));
        s3Storage.deleteObject("NewFile.txt", BUCKET_NAME);
    }
}
