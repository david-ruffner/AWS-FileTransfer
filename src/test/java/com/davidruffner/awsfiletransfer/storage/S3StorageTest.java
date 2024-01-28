package com.davidruffner.awsfiletransfer.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
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
@ActiveProfiles(profiles = { "test", "local" })
public class S3StorageTest {

    @Autowired
    S3StorageFactory s3StorageFactory;

    private S3Storage s3Storage;
    private AmazonS3 s3Client;

    @BeforeEach
    public void setUp() {
        this.s3Storage = s3StorageFactory.getTestS3Storage();
        this.s3Client = s3Storage.getS3Client();
    }

    @Test
    public void testObjectExistsTrue() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("MyKey", dataStream);

        assertTrue(s3Storage.doesObjectExist("MyKey"));
        s3Storage.deleteObject("MyKey");
    }

    @Test
    public void testObjectExistsFalse() {
        assertFalse(s3Storage.doesObjectExist("NonExistentKey"));
    }

    @Test
    public void test_CRD_Object_Success() throws IOException {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream);

        S3StorageObject storageObject = s3Storage.getObject("NewFile");
        S3ObjectInputStream inputStream = storageObject.getInputStream();
        String downloadData = new String(inputStream.readAllBytes(),
                StandardCharsets.UTF_8);
        assertEquals(data, downloadData);
        dataStream.close();

        s3Storage.deleteObject("NewFile");
        assertFalse(s3Storage.doesObjectExist("NewFile"));
        s3Storage.deleteObject("NewFile");
    }

    @Test
    public void testUploadWithMetadataSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");

        s3Storage.uploadObject("NewFile", dataStream, metadata);

        S3StorageObject storageObject = s3Storage.getObject("NewFile");
        assertTrue(storageObject.hasMetadata());
        assertEquals("A description.", storageObject
                .getMetadata("user-description"));

        s3Storage.deleteObject("NewFile");
    }

    @Test
    public void testGetMetadata_NoMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream);

        assertThrows(RuntimeException.class, () -> {
            S3StorageObject storageObject = s3Storage.getObject("NewFile");
            storageObject.getMetadata("MyMetadata");
        });

        s3Storage.deleteObject("NewFile");
    }

    @Test
    void testGetMetadata_NonExistentMetaKey() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");

        s3Storage.uploadObject("NewFile", dataStream, metadata);

        S3StorageObject storageObject = s3Storage.getObject("NewFile");

        assertThrows(RuntimeException.class, () ->
                storageObject.getMetadata("non-existent"));

        s3Storage.deleteObject("NewFile");
    }

    @Test
    void testAddMetadataSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream);

        S3StorageObject storageObject = s3Storage.getObject("NewFile");
        assertFalse(storageObject.hasMetadata());

        s3Storage.addMetadata("NewFile", "user-description",
                "A description.");
        S3StorageObject downloadObj = s3Storage.getObject("NewFile");
        assertTrue(downloadObj.hasMetadata());
        assertEquals("A description.", downloadObj
                .getMetadata("user-description"));

        s3Storage.deleteObject("NewFile");
    }

    @Test
    void testAddMultiMetadataSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        s3Storage.uploadObject("NewFile", dataStream);

        S3StorageObject storageObject = s3Storage.getObject("NewFile");
        assertFalse(storageObject.hasMetadata());

        Map<String, String> metadata = Map.ofEntries(
                Map.entry("user-description", "A description."),
                Map.entry("another-tag", "Another tag.")
        );
        s3Storage.addMetadata("NewFile", metadata);

        S3StorageObject downloadObj = s3Storage.getObject("NewFile");
        assertTrue(downloadObj.hasMetadata());
        assertEquals("A description.", downloadObj
                .getMetadata("user-description"));
        assertEquals("Another tag.", downloadObj
                .getMetadata("another-tag"));

        s3Storage.deleteObject("NewFile");
    }

    @Test
    void testUpdateMetadataSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");

        s3Storage.uploadObject("NewFile", dataStream, metadata);

        S3StorageObject storageObject = s3Storage.getObject("NewFile");
        assertTrue(storageObject.hasMetadata());

        s3Storage.updateMetadata("NewFile", "user-description",
                "New value.");
        S3StorageObject downloadObj = s3Storage.getObject("NewFile");
        assertEquals("New value.", downloadObj
                .getMetadata("user-description"));

        s3Storage.deleteObject("NewFile");
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

        s3Storage.uploadObject("NewFile", dataStream, metadata);

        S3StorageObject storageObject = s3Storage.getObject("NewFile");
        assertTrue(storageObject.hasMetadata());

        Map<String, String> updatedMetadataMap = Map.ofEntries(
                Map.entry("user-description", "A new description."),
                Map.entry("another-tag", "A new tag.")
        );
        s3Storage.updateMetadata("NewFile", updatedMetadataMap);
        S3StorageObject downloadObj = s3Storage.getObject("NewFile");
        assertEquals("A new description.", downloadObj
                .getMetadata("user-description"));
        assertEquals("A new tag.", downloadObj
                .getMetadata("another-tag"));

        s3Storage.deleteObject("NewFile");
    }

    @Test
    void testDeleteMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");
        metadata.addUserMetadata("another-tag", "Another tag.");

        s3Storage.uploadObject("NewFile", dataStream, metadata);

        S3StorageObject storageObject = s3Storage.getObject("NewFile");
        assertTrue(storageObject.hasMetadata());

        s3Storage.deleteMetadata("NewFile", "user-description");
        S3StorageObject updatedObject = s3Storage.getObject("NewFile");
        assertTrue(updatedObject.hasMetadata());
        assertFalse(updatedObject.hasMetadataKey("user-description"));

        s3Storage.deleteObject("NewFile");
    }

    @Test
    void testDeleteAllMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");
        metadata.addUserMetadata("another-tag", "Another tag.");

        s3Storage.uploadObject("NewFile", dataStream, metadata);

        S3StorageObject storageObject = s3Storage.getObject("NewFile");
        assertTrue(storageObject.hasMetadata());

        s3Storage.deleteAllMetadata("NewFile");
        S3StorageObject updatedObject = s3Storage.getObject("NewFile");
        assertFalse(updatedObject.hasMetadata());

        s3Storage.deleteObject("NewFile");
    }

    @Test
    void testGetMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");

        s3Storage.uploadObject("NewFile", dataStream, metadata);

        assertEquals("A description.",
                s3Storage.getMetadata("NewFile", "user-description"));
        s3Storage.deleteObject("NewFile");
    }

    @Test
    void testGetAllMetadata() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("user-description", "A description.");
        metadata.addUserMetadata("another-tag", "Another tag.");

        s3Storage.uploadObject("NewFile", dataStream, metadata);
        assertEquals(2, s3Storage.getAllMetadata("NewFile").size());
        s3Storage.deleteObject("NewFile");
    }

    @Test
    public void testUploadObjectFailure() {
        assertThrows(RuntimeException.class, () ->
                s3Storage.uploadObject("NewFile", null));
    }

    @Test
    void testGetObjectNonExistentKey() {
        assertThrows(RuntimeException.class, () ->
                s3Storage.getObject("NonExistentKey"));
    }

    @Test
    void testRenameObjectSuccess() {
        String data = "Hello World!";
        InputStream dataStream = IOUtils.toInputStream(data,
                StandardCharsets.UTF_8);

        s3Storage.deleteObject("NewFile");
        s3Storage.uploadObject("NewFile", dataStream);

        s3Storage.renameObject("NewFile", "RenamedNewFile");
        assertTrue(s3Storage.doesObjectExist("RenamedNewFile"));
        assertFalse(s3Storage.doesObjectExist("NewFile"));
        s3Storage.deleteObject("RenamedNewFile");
    }

    @Test
    void testRenameObjectFailure() {
        assertThrows(RuntimeException.class, () -> s3Storage.renameObject(
                "NonExistentFile", "NonExistentFile"));
    }

    @Test
    void testImageUpload() throws IOException {
        File img = new File("src/test/resources/TestData/piano.jpeg");
        InputStream dataStream = new DataInputStream(new FileInputStream(img));
        s3Storage.uploadObject("ImgFile", dataStream);

        S3StorageObject storageObject = s3Storage.getObject("ImgFile");
        assertEquals(img.length(), storageObject.getInputStream().readAllBytes().length);
        s3Storage.deleteObject("ImgFile");
    }
}
