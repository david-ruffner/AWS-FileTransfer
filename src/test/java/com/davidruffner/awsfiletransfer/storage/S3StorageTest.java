package com.davidruffner.awsfiletransfer.storage;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

// TODO: Remove local from active profiles
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = { "local" })
public class S3StorageTest {

    private static final String BUCKET_NAME = "inventory-tracker-data";
    private static final String SECOND_BUCKET_NAME = "inventory-tracker-data-2";

    @Autowired
    private S3Storage s3Storage;

    @Autowired
    UploadObjectAction.Builder builder;

    @Test
    public void testObjectExistsTrue() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME);

        assertTrue(s3Storage.doesObjectExist("NewFile", BUCKET_NAME));
        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    public void testObjectExistsFalse() {
        assertFalse(s3Storage.doesObjectExist("NonExistentKey",
                BUCKET_NAME));
    }

    @Test
    public void test_CRD_Object_Success() throws IOException {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME);

        S3StorageObject storageObject = s3Storage.getObject("NewFile",
                BUCKET_NAME);
        S3ObjectInputStream inputStream = storageObject.getInputStream();
        String downloadData = new String(inputStream.readAllBytes(),
                StandardCharsets.UTF_8);
        assertEquals(data, downloadData);
        dataStream.close();

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
        assertFalse(s3Storage.doesObjectExist("NewFile", BUCKET_NAME));
    }

    @Test
    public void testMoveObjectWithoutRename() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME);

        s3Storage.moveObject("NewFile", BUCKET_NAME, SECOND_BUCKET_NAME);
        assertFalse(s3Storage.doesObjectExist("NewFile", BUCKET_NAME));
        assertTrue(s3Storage.doesObjectExist("NewFile", SECOND_BUCKET_NAME));
        s3Storage.deleteObject("NewFile", SECOND_BUCKET_NAME);
    }

    @Test
    public void testMoveObjectWithRename() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME);

        s3Storage.moveObject("NewFile", "MovedFile", BUCKET_NAME, SECOND_BUCKET_NAME);
        assertFalse(s3Storage.doesObjectExist("NewFile", BUCKET_NAME));
        assertTrue(s3Storage.doesObjectExist("MovedFile", SECOND_BUCKET_NAME));
        s3Storage.deleteObject("MovedFile", SECOND_BUCKET_NAME);
    }

    @Test
    public void testUploadWithMetadataSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");

        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME, new S3Metadata(metadata));

        S3StorageObject storageObject = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertTrue(storageObject.hasMetadata());
        assertEquals("A description.", storageObject
                .getMetadata("user-description"));

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    public void testGetMetadata_NoMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME);

        assertThrows(RuntimeException.class, () -> {
            S3StorageObject storageObject = s3Storage.getObject("NewFile", BUCKET_NAME);
            storageObject.getMetadata("MyMetadata");
        });

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    void testGetMetadata_NonExistentMetaKey() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");

        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME, new S3Metadata(metadata));

        S3StorageObject storageObject = s3Storage.getObject("NewFile", BUCKET_NAME);

        assertThrows(RuntimeException.class, () ->
                storageObject.getMetadata("non-existent"));

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    void testAddMetadataSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME);

        S3StorageObject storageObject = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertFalse(storageObject.hasMetadata());

        s3Storage.addMetadata("NewFile", BUCKET_NAME,
                "user-description","A description.");
        S3StorageObject downloadObj = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertTrue(downloadObj.hasMetadata());
        assertEquals("A description.", downloadObj
                .getMetadata("user-description"));

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    void testAddMultiMetadataSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME);

        S3StorageObject storageObject = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertFalse(storageObject.hasMetadata());

        Map<String, String> metadata = Map.ofEntries(
                Map.entry("user-description", "A description."),
                Map.entry("another-tag", "Another tag.")
        );
        s3Storage.addMetadata("NewFile", BUCKET_NAME, metadata);

        S3StorageObject downloadObj = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertTrue(downloadObj.hasMetadata());
        assertEquals("A description.", downloadObj
                .getMetadata("user-description"));
        assertEquals("Another tag.", downloadObj
                .getMetadata("another-tag"));

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    void testUpdateMetadataSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");

        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME, new S3Metadata(metadata));

        S3StorageObject storageObject = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertTrue(storageObject.hasMetadata());

        s3Storage.updateMetadata("NewFile", BUCKET_NAME,
                "user-description", "New value.");
        S3StorageObject downloadObj = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertEquals("New value.", downloadObj
                .getMetadata("user-description"));

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    void testUpdateMultiMetadataSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        Map<String, String> metadataMap = Map.ofEntries(
                Map.entry("user-description", "A description."),
                Map.entry("another-tag", "Another tag.")
        );
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setUserMetadata(metadataMap);

        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME, new S3Metadata(metadata));

        S3StorageObject storageObject = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertTrue(storageObject.hasMetadata());

        Map<String, String> updatedMetadataMap = Map.ofEntries(
                Map.entry("user-description", "A new description."),
                Map.entry("another-tag", "A new tag.")
        );
        s3Storage.updateMetadata("NewFile", BUCKET_NAME, updatedMetadataMap);
        S3StorageObject downloadObj = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertEquals("A new description.", downloadObj
                .getMetadata("user-description"));
        assertEquals("A new tag.", downloadObj
                .getMetadata("another-tag"));

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    void testDeleteMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");
        metadata.addUserMetadata("another-tag", "Another tag.");

        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME, new S3Metadata(metadata));

        S3StorageObject storageObject = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertTrue(storageObject.hasMetadata());

        s3Storage.deleteMetadata("NewFile", BUCKET_NAME,"user-description");
        S3StorageObject updatedObject = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertTrue(updatedObject.hasMetadata());
        assertFalse(updatedObject.hasMetadataKey("user-description"));

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    void testDeleteAllMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");
        metadata.addUserMetadata("another-tag", "Another tag.");

        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME, new S3Metadata(metadata));

        S3StorageObject storageObject = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertTrue(storageObject.hasMetadata());

        s3Storage.deleteAllMetadata("NewFile", BUCKET_NAME);
        S3StorageObject updatedObject = s3Storage.getObject("NewFile", BUCKET_NAME);
        assertFalse(updatedObject.hasMetadata());

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    void testGetMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");

        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME, new S3Metadata(metadata));

        assertEquals("A description.",
                s3Storage.getMetadata("NewFile", BUCKET_NAME,"user-description"));
        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    void testGetAllMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");
        metadata.addUserMetadata("another-tag", "Another tag.");

        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME, new S3Metadata(metadata));
        assertEquals(2, s3Storage.getAllMetadata("NewFile", BUCKET_NAME).size());
        s3Storage.deleteObject("NewFile", BUCKET_NAME);
    }

    @Test
    public void testUploadObjectFailure() {
        assertThrows(RuntimeException.class, () ->
                s3Storage.uploadObject("NewFile", null, BUCKET_NAME));
    }

    @Test
    void testGetObjectNonExistentKey() {
        assertThrows(RuntimeException.class, () ->
                s3Storage.getObject("NonExistentKey", BUCKET_NAME));
    }

    @Test
    void testRenameObjectSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);

        s3Storage.deleteObject("NewFile", BUCKET_NAME);
        s3Storage.uploadObject("NewFile", dataStream, BUCKET_NAME);

        s3Storage.renameObject("NewFile", "RenamedNewFile", BUCKET_NAME);
        assertTrue(s3Storage.doesObjectExist("RenamedNewFile", BUCKET_NAME));
        assertFalse(s3Storage.doesObjectExist("NewFile", BUCKET_NAME));
        s3Storage.deleteObject("RenamedNewFile", BUCKET_NAME);
    }

    @Test
    void testRenameObjectFailure() {
        assertThrows(RuntimeException.class, () -> s3Storage.renameObject(
                "NonExistentFile", "NonExistentFile", BUCKET_NAME));
    }

    @Test
    void testImageUpload() throws IOException {
        File img = new File("src/test/resources/TestData/piano.jpeg");
        InputStream dataStream = new DataInputStream(new FileInputStream(img));
        s3Storage.uploadObject("ImgFile", dataStream, BUCKET_NAME);

        S3StorageObject storageObject = s3Storage.getObject("ImgFile", BUCKET_NAME);
        assertEquals(img.length(), storageObject.getInputStream().readAllBytes().length);
        s3Storage.deleteObject("ImgFile", BUCKET_NAME);
    }
}
