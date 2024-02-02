package com.davidruffner.awsfiletransfer.storage.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.davidruffner.awsfiletransfer.configuration.storage.S3Configuration;
import com.davidruffner.awsfiletransfer.storage.metadata.MetadataBase;
import com.davidruffner.awsfiletransfer.storage.metadata.S3Metadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component("S3Storage")
@Scope(value = "prototype")
public class S3Storage implements StorageBase {
    private AmazonS3 s3Client;

    public S3Storage(S3Configuration s3Config) {
        this.s3Client = s3Config.getS3Client();
    }

    private static final String S3_ERR_MSG = "S3 Storage Exception | %s";

    private void handleS3Exception(AmazonS3Exception ex, String keyName) throws RuntimeException {
        switch (ex.getErrorCode()) {
            case "NoSuchKey":
                throw new RuntimeException(String.format(S3_ERR_MSG,
                        "Key '" + keyName + "' was not found"));

            default:
                throw new RuntimeException(String.format(S3_ERR_MSG, ex.getMessage()));
        }
    }

    private void handleS3Exception(Exception ex) throws RuntimeException {
        throw new RuntimeException(String.format(S3_ERR_MSG, ex.getMessage()));
    }

    @Override
    public void uploadObject(String keyName, InputStream inputStream,
                             String containerName) throws RuntimeException {
        try {
            this.s3Client.putObject(containerName, keyName, inputStream, new ObjectMetadata());
        } catch (Exception ex) {
            // TODO: Log
            handleS3Exception(ex);
        }
    }

    @Override
    public void uploadObject(String keyName, InputStream inputStream,
                             String containerName, MetadataBase metadata) throws RuntimeException {
        try {
            ObjectMetadata s3Metadata = ((S3Metadata) metadata).getObjectMetadata();
            s3Client.putObject(containerName, keyName, inputStream, s3Metadata);
        } catch (Exception ex) {
            // TODO: Log
            handleS3Exception(ex);
        }
    }

    @Override
    public void renameObject(String oldKeyName, String newKeyName,
                             String containerName) throws RuntimeException {
        try {
            s3Client.copyObject(containerName, oldKeyName, containerName, newKeyName);
            s3Client.deleteObject(containerName, oldKeyName);
        } catch (AmazonS3Exception ex) {
            handleS3Exception(ex, oldKeyName);
        } catch (Exception ex) {
            handleS3Exception(ex);
        }
    }

    @Override
    public void moveObject(String keyName, String oldContainerName,
                           String newContainerName) throws RuntimeException {
        try {
            s3Client.copyObject(oldContainerName, keyName, newContainerName, keyName);
            s3Client.deleteObject(oldContainerName, keyName);
        } catch (AmazonS3Exception ex) {
            handleS3Exception(ex, keyName);
        } catch (Exception ex) {
            handleS3Exception(ex);
        }
    }

    @Override
    public void moveObject(String oldKeyName, String newKeyName, String oldContainerName,
                           String newContainerName) throws RuntimeException {
        try {
            s3Client.copyObject(oldContainerName, oldKeyName, newContainerName, newKeyName);
            s3Client.deleteObject(oldContainerName, oldKeyName);
        } catch (AmazonS3Exception ex) {
            handleS3Exception(ex, oldKeyName);
        } catch (Exception ex) {
            handleS3Exception(ex);
        }
    }

    @Override
    public S3StorageObject getObject(String keyName, String containerName) throws RuntimeException {
        try {
            return new S3StorageObject(s3Client.getObject(containerName, keyName),
                    s3Client, containerName);
        } catch (AmazonS3Exception ex) {
            handleS3Exception(ex, keyName);
            return null;
        } catch (Exception ex) {
            handleS3Exception(ex);
            return null;
        }
    }

    @Override
    public void deleteObject(String keyName, String containerName) throws RuntimeException {
        try {
            s3Client.deleteObject(containerName, keyName);
        } catch (Exception ex) {
            handleS3Exception(ex);
        }
    }

    @Override
    public void addMetadata(String objectKey, String containerName,
                            String metaKey, String metaValue) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey, containerName);
        storageObject.addMetadata(metaKey, metaValue);
    }

    @Override
    public void addMetadata(String objectKey, String containerName,
                            Map<String, String> metadata)
        throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey, containerName);
        storageObject.addMetadata(metadata);
    }

    @Override
    public void updateMetadata(String objectKey, String containerName,
                               String metaKey, String newValue) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey, containerName);
        storageObject.updateMetadata(metaKey, newValue);
    }

    @Override
    public void updateMetadata(String objectKey, String containerName,
                               Map<String, String> metadata)
        throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey, containerName);
        storageObject.updateMetadata(metadata);
    }

    @Override
    public void deleteMetadata(String objectKey, String containerName,
                               String metaKey) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey, containerName);
        storageObject.deleteMetadata(metaKey);
    }

    @Override
    public void deleteAllMetadata(String objectKey, String containerName) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey, containerName);
        storageObject.deleteAllMetadata();
    }

    @Override
    public String getMetadata(String objectKey, String containerName,
                              String metaKey) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey, containerName);
        return storageObject.getMetadata(metaKey);
    }

    @Override
    public Map<String, String> getAllMetadata(String objectKey, String containerName) throws RuntimeException {
        S3StorageObject storageObject = getObject(objectKey, containerName);
        return storageObject.retrieveMetadata().getUserMetadata();
    }

    @Override
    public Boolean doesObjectExist(String keyName, String containerName) {
        try {
            this.s3Client.getObject(containerName, keyName);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
