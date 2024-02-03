package com.davidruffner.awsfiletransfer.action;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.davidruffner.awsfiletransfer.action.objectActions.DeleteObjectAction.DeleteObjectActionBuilder;
import com.davidruffner.awsfiletransfer.action.objectActions.GetObjectAction;
import com.davidruffner.awsfiletransfer.action.objectActions.MoveObjectAction.MoveObjectActionBuilder;
import com.davidruffner.awsfiletransfer.action.objectActions.ObjectExistsAction.ObjectExistsActionBuilder;
import com.davidruffner.awsfiletransfer.action.objectActions.ObjectMetadataAction.ObjectMetadataActionBuilder;
import com.davidruffner.awsfiletransfer.action.objectActions.RenameObjectAction.RenameObjectActionBuilder;
import com.davidruffner.awsfiletransfer.action.objectActions.UploadObjectAction;
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

import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.FAIL;
import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.SUCCESS;
import static com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest.MetadataActionType.*;
import static com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest.StorageControllerType.S3;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = { "test", "local" })
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
    ObjectMetadataActionBuilder metadataBuilder;

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
    void testUploadAction_InvalidKeyName() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);

        try (ActionResponse actionResponse = uploadBuilder.newBuilder()
                .storageController(S3)
                .keyName("(InvalidKey).png")
                .containerName(BUCKET_NAME)
                .inputStream(dataStream)
                .doAction()) {
            assertEquals(FAIL, actionResponse.getResponseCode());
            assertTrue(actionResponse.getErrorMessage().isPresent());
            System.out.printf("\nUpload Invalid Key Err Msg: %s\n",
                    actionResponse.getErrorMessage().get());
        }
    }

    @Test
    void testUploadAction_InvalidContainerName() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);

        try (ActionResponse actionResponse = uploadBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile.png")
                .containerName("Invalid$Bucket#Name")
                .inputStream(dataStream)
                .doAction()) {
            assertEquals(FAIL, actionResponse.getResponseCode());
            assertTrue(actionResponse.getErrorMessage().isPresent());
            System.out.printf("\nUpload Invalid Container Err Msg: %s\n",
                    actionResponse.getErrorMessage().get());
        }
    }

    @Test
    void testUploadAction_InvalidInputStream() {
        String data = "";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);

        try (ActionResponse actionResponse = uploadBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile.png")
                .containerName(BUCKET_NAME)
                .inputStream(dataStream)
                .doAction()) {
            assertEquals(FAIL, actionResponse.getResponseCode());
            assertTrue(actionResponse.getErrorMessage().isPresent());
            System.out.printf("\nUpload Invalid Input Stream Err Msg: %s\n",
                    actionResponse.getErrorMessage().get());
        }
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
    public void testUploadAction_InvalidMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME);

        Map<String, String> invalidMetadataKeyMap = Map.ofEntries(
                Map.entry("(InvalidKey)", "This is a description.")
        );

        Map<String, String> invalidMetadataValMap = Map.ofEntries(
                Map.entry("MyFile", "")
        );

        try (ActionResponse invalidMetaKey = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("NewFile.txt")
                .containerName(BUCKET_NAME)
                .metadataActionType(ADD_METADATA)
                .withMetadataMap(invalidMetadataKeyMap)
                .doAction()) {
            assertEquals(FAIL, invalidMetaKey.getResponseCode());
            assertTrue(invalidMetaKey.getErrorMessage().isPresent());
            System.out.printf("\nUpload Invalid Meta Key Err Msg: %s\n",
                    invalidMetaKey.getErrorMessage().get());
        }

        try (ActionResponse invalidMetaKey = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("NewFile.txt")
                .containerName(BUCKET_NAME)
                .metadataActionType(ADD_METADATA)
                .withMetadataMap(invalidMetadataValMap)
                .doAction()) {
            assertEquals(FAIL, invalidMetaKey.getResponseCode());
            assertTrue(invalidMetaKey.getErrorMessage().isPresent());
            System.out.printf("\nUpload Invalid Meta Val Err Msg: %s\n\n",
                    invalidMetaKey.getErrorMessage().get());
        }

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

    @Test
    void testAddObjectMetadata_SingleValue() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME);

        S3StorageObject downloadedObj = s3Storage.getObject("MyFile", BUCKET_NAME);
        assertFalse(downloadedObj.hasMetadata());

        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(ADD_METADATA)
                .withMetadataKey("user-description")
                .withNewMetadataValue("A description")
                .doAction();
        assertEquals(SUCCESS, actionResponse.getResponseCode());

        S3StorageObject updatedObj = s3Storage.getObject("MyFile", BUCKET_NAME);
        assertTrue(updatedObj.hasMetadata());
        assertEquals("A description", updatedObj.getMetadata("user-description"));

        s3Storage.deleteObject("MyFile", BUCKET_NAME);
    }

    @Test
    void testAddObjectMetadataMap() {
        Map<String, String> originalMetadata = Map.ofEntries(
                Map.entry("user-description", "A description."),
                Map.entry("user-tag", "A tag value")
        );

        Map<String, String> newMetadataMap = Map.ofEntries(
                Map.entry("new-tag", "A new tag value"),
                Map.entry("user-description", "A new description.")
        );

        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setUserMetadata(originalMetadata);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME, new S3Metadata(metadata));

        assertEquals(2, s3Storage.getAllMetadata("MyFile", BUCKET_NAME).size());
        assertEquals("A description.", s3Storage.getMetadata("MyFile",
                BUCKET_NAME, "user-description"));

        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(ADD_METADATA)
                .withMetadataMap(newMetadataMap)
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseMessage().isPresent());
        assertEquals(3, s3Storage.getAllMetadata("MyFile", BUCKET_NAME).size());
        assertEquals("A new description.", s3Storage.getMetadata("MyFile",
                BUCKET_NAME, "user-description"));
        System.out.printf("\n\nUser Metadata After Map Update: %s\n\n",
                s3Storage.getAllMetadata("MyFile", BUCKET_NAME));

        s3Storage.deleteObject("MyFile", BUCKET_NAME);
    }

    @Test
    void testAddMetadataWithoutRequiredParams() {
        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(ADD_METADATA)
                .doAction();

        assertEquals(FAIL, actionResponse.getResponseCode());
        assertTrue(actionResponse.getErrorMessage().isPresent());
        System.out.printf("\n\nAdd Metadata Without Required Params Error Msg: %s\n\n",
                actionResponse.getErrorMessage().get());
    }

    @Test
    void testAddMetadataWithPartialRequiredParams() {
        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(ADD_METADATA)
                .withMetadataKey("MetaKey")
                .doAction();

        assertEquals(FAIL, actionResponse.getResponseCode());
        assertTrue(actionResponse.getErrorMessage().isPresent());
        System.out.printf("\n\nAdd Metadata Without Required Params Error Msg: %s\n\n",
                actionResponse.getErrorMessage().get());
    }

    @Test
    void testUpdateObjectMetadata_SingleValue() {
        Map<String, String> originalMetadata = Map.ofEntries(
                Map.entry("user-description", "A description")
        );

        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME,
                new S3Metadata(originalMetadata));

        S3StorageObject downloadedObj = s3Storage.getObject("MyFile", BUCKET_NAME);
        assertEquals("A description", downloadedObj.getMetadata("user-description"));

        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(UPDATE_METADATA)
                .withMetadataKey("user-description")
                .withNewMetadataValue("A new description")
                .doAction();
        assertEquals(SUCCESS, actionResponse.getResponseCode());

        S3StorageObject updatedObj = s3Storage.getObject("MyFile", BUCKET_NAME);
        assertEquals("A new description", updatedObj.getMetadata("user-description"));

        s3Storage.deleteObject("MyFile", BUCKET_NAME);
    }

    @Test
    void testUpdateObjectMetadataMap() {
        Map<String, String> originalMetadata = Map.ofEntries(
                Map.entry("user-description", "A description."),
                Map.entry("user-tag", "A tag value")
        );

        Map<String, String> newMetadataMap = Map.ofEntries(
                Map.entry("new-tag", "A new tag value"),
                Map.entry("new-tag-2", "A new second tag value.")
        );

        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME,
                new S3Metadata(originalMetadata));

        assertEquals(2, s3Storage.getAllMetadata("MyFile", BUCKET_NAME).size());
        assertEquals("A description.", s3Storage.getMetadata("MyFile",
                BUCKET_NAME, "user-description"));

        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(UPDATE_METADATA)
                .withMetadataMap(newMetadataMap)
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getResponseMessage().isPresent());
        assertEquals(2, s3Storage.getAllMetadata("MyFile", BUCKET_NAME).size());
        assertEquals("A new tag value", s3Storage.getMetadata("MyFile",
                BUCKET_NAME, "new-tag"));
        System.out.printf("\n\nUser Metadata After Map Update: %s\n\n",
                s3Storage.getAllMetadata("MyFile", BUCKET_NAME));

        s3Storage.deleteObject("MyFile", BUCKET_NAME);
    }

    @Test
    void testDeleteMetadata_SingleValue() {
        Map<String, String> originalMetadata = Map.ofEntries(
                Map.entry("user-description", "A description")
        );

        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME,
                new S3Metadata(originalMetadata));

        S3StorageObject downloadedObj = s3Storage.getObject("MyFile", BUCKET_NAME);
        assertTrue(downloadedObj.hasMetadata());

        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(DELETE_METADATA)
                .withMetadataKey("user-description")
                .doAction();

        S3StorageObject updatedObj = s3Storage.getObject("MyFile", BUCKET_NAME);
        assertFalse(updatedObj.hasMetadata());

        s3Storage.deleteObject("MyFile", BUCKET_NAME);
    }

    @Test
    void testDeleteMetadata_Map() {
        Map<String, String> originalMetadata = Map.ofEntries(
                Map.entry("user-description", "A description")
        );

        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME,
                new S3Metadata(originalMetadata));

        S3StorageObject downloadedObj = s3Storage.getObject("MyFile", BUCKET_NAME);
        assertTrue(downloadedObj.hasMetadata());

        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(DELETE_ALL_METADATA)
                .doAction();

        S3StorageObject updatedObj = s3Storage.getObject("MyFile", BUCKET_NAME);
        assertFalse(updatedObj.hasMetadata());

        s3Storage.deleteObject("MyFile", BUCKET_NAME);
    }

    @Test
    void testGetMetadata_SingleValue() {
        Map<String, String> originalMetadata = Map.ofEntries(
                Map.entry("user-description", "A description")
        );

        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME,
                new S3Metadata(originalMetadata));

        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(GET_METADATA)
                .withMetadataKey("user-description")
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getMetadataMap().isPresent());
        assertEquals("A description",
                actionResponse.getMetadataMap().get().get("user-description"));

        s3Storage.deleteObject("MyFile", BUCKET_NAME);
    }

    @Test
    void testGetMetadata_Map() {
        Map<String, String> originalMetadata = Map.ofEntries(
                Map.entry("user-description", "A description"),
                Map.entry("user-tag", "A user tag value")
        );

        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyFile", dataStream, BUCKET_NAME,
                new S3Metadata(originalMetadata));

        ActionResponse actionResponse = metadataBuilder.newBuilder()
                .storageController(S3)
                .keyName("MyFile")
                .containerName(BUCKET_NAME)
                .metadataActionType(GET_ALL_METADATA)
                .doAction();

        assertEquals(SUCCESS, actionResponse.getResponseCode());
        assertTrue(actionResponse.getMetadataMap().isPresent());
        assertEquals("A description",
                actionResponse.getMetadataMap().get().get("user-description"));
        assertEquals("A user tag value",
                actionResponse.getMetadataMap().get().get("user-tag"));

        s3Storage.deleteObject("MyFile", BUCKET_NAME);
    }
}
