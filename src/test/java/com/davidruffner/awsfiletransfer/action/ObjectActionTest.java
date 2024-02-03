package com.davidruffner.awsfiletransfer.action;

import com.davidruffner.awsfiletransfer.action.objectActions.*;
import com.davidruffner.awsfiletransfer.action.objectActions.DeleteObjectAction.DeleteObjectActionBuilder;
import com.davidruffner.awsfiletransfer.action.objectActions.MoveObjectAction.MoveObjectActionBuilder;
import com.davidruffner.awsfiletransfer.action.objectActions.ObjectExistsAction.ObjectExistsActionBuilder;
import com.davidruffner.awsfiletransfer.action.objectActions.RenameObjectAction.RenameObjectActionBuilder;
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

import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.SUCCESS;
import static com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerType.S3;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = { "local" })
public class ObjectActionTest {

    private static final String BUCKET_NAME = "inventory-tracker-data";
    private static final String SECOND_BUCKET_NAME = "inventory-tracker-data-2";

    @Autowired
    UploadObjectAction.Builder uploadBuilder;

    @Autowired
    GetObjectAction.Builder getBuilder;

    @Autowired
    RenameObjectActionBuilder renameBuilder;

    @Autowired
    MoveObjectActionBuilder moveBuilder;

    @Autowired
    DeleteObjectActionBuilder deleteBuilder;

    @Autowired
    ObjectExistsActionBuilder existsBuilder;

    @Autowired
    S3Storage s3Storage;

    @Test
    public void testUploadAction() throws IOException {
        File dataFile = new File("src/test/resources/TestData/piano.jpeg");
        InputStream dataStream = new FileInputStream(dataFile);

        ActionResponse actionResponse = uploadBuilder.newBuilder()
                .storageController(S3)
                .keyName("NewFile.png")
                .containerName(BUCKET_NAME)
                .inputStream(dataStream)
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseMessage().isPresent());

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

        ActionResponse actionResponse = uploadBuilder.newBuilder()
                .storageController(S3)
                .keyName("NewFile.txt")
                .containerName(BUCKET_NAME)
                .inputStream(dataStream)
                .withMetadata(metadata)
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseMessage().isPresent());

        S3StorageObject obj = s3Storage.getObject("NewFile.txt", BUCKET_NAME);
        assertEquals("This is a description.", obj.getMetadata("user-description"));
        s3Storage.deleteObject("NewFile.txt", BUCKET_NAME);
    }

    @Test
    void testGetObjectAction() throws IOException {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME);

        ActionResponse actionResponse = getBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseMessage().isPresent());
        String compareString = new String(actionResponse.getStorageObject().get()
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(data, compareString);

        s3Storage.deleteObject("MyFile", BUCKET_NAME);
    }

    @Test
    void testRenameObjectAction() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME);

        ActionResponse actionResponse = renameBuilder.newBuilder()
                .storageController(S3)
                .oldKeyName("MyFile")
                .newKeyName("MyRenamedFile")
                .containerName(BUCKET_NAME)
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseMessage().isPresent());
        assertFalse(s3Storage.doesObjectExist("MyFile", BUCKET_NAME));
        assertTrue(s3Storage.doesObjectExist("MyRenamedFile", BUCKET_NAME));

        s3Storage.deleteObject("MyRenamedFile", BUCKET_NAME);
    }

    @Test
    void testMoveObjectWithoutRename() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME);

        ActionResponse actionResponse = moveBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .oldContainerName(BUCKET_NAME)
                .newContainerName(SECOND_BUCKET_NAME)
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseMessage().isPresent());
        System.out.printf("\nMove Object Without Rename Test Response: %s\n",
                actionResponse.getResponseMessage().get());

        assertFalse(s3Storage.doesObjectExist("MyFile", BUCKET_NAME));
        assertTrue(s3Storage.doesObjectExist("MyFile", SECOND_BUCKET_NAME));
        s3Storage.deleteObject("MyFile", SECOND_BUCKET_NAME);
    }

    @Test
    void testMoveObjectWithRename() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME);

        ActionResponse actionResponse = moveBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .oldContainerName(BUCKET_NAME)
                .newContainerName(SECOND_BUCKET_NAME)
                .withNewKeyName("RenamedFile")
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseMessage().isPresent());
        System.out.printf("\n\nMove Object With Rename Test Response: %s\n\n",
                actionResponse.getResponseMessage().get());

        assertFalse(s3Storage.doesObjectExist("MyFile", BUCKET_NAME));
        assertFalse(s3Storage.doesObjectExist("MyFile", SECOND_BUCKET_NAME));
        assertTrue(s3Storage.doesObjectExist("RenamedFile", SECOND_BUCKET_NAME));
        s3Storage.deleteObject("RenamedFile", SECOND_BUCKET_NAME);
    }

    @Test
    void testDeleteObjectAction() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME);
        assertTrue(s3Storage.doesObjectExist("MyFile", BUCKET_NAME));

        ActionResponse actionResponse = deleteBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseMessage().isPresent());
        System.out.printf("\n\nDelete Object Response: %s\n\n",
                actionResponse.getResponseMessage().get());
        assertFalse(s3Storage.doesObjectExist("MyFile", BUCKET_NAME));
    }

    @Test
    void testObjectExistsAction() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME);

        ActionResponse actionResponse = existsBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .doAction();
        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseFlag().isPresent());
        assertTrue(actionResponse.getResponseFlag().get());

        s3Storage.deleteObject("MyFile", BUCKET_NAME);

        ActionResponse deleteResponse = existsBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .doAction();
        assertEquals(SUCCESS, deleteResponse.getResponseCode());
        assertTrue(deleteResponse.getResponseFlag().isPresent());
        assertFalse(deleteResponse.getResponseFlag().get());
    }
}
