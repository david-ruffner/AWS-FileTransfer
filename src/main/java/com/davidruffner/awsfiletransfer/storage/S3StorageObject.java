package com.davidruffner.awsfiletransfer.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.util.HashMap;
import java.util.Map;

public class S3StorageObject {
    String objectKey;
    S3ObjectInputStream inputStream;
    ObjectMetadata metadata;

    AmazonS3 s3Client;
    String bucketName;

    public S3StorageObject(S3Object s3Object, AmazonS3 s3Client,
                           String bucketName) {
        this.objectKey = s3Object.getKey();
        this.inputStream = s3Object.getObjectContent();
        this.metadata = s3Object.getObjectMetadata();
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public S3ObjectInputStream getInputStream() {
        return inputStream;
    }

    public String getMetadata(String metadataKey) throws RuntimeException {
        if (!hasMetadata())
            throw new RuntimeException(String.format("S3 Storage Exception | " +
                    "Object '%s' doesn't have metadata", this.objectKey));

        Map<String, String> userMetadata = this.metadata.getUserMetadata();
        if (!userMetadata.containsKey(metadataKey))
            throw new RuntimeException(String.format("S3 Storage Exception | " +
                    "Object '%s' doesn't have metadata key '%s'",
                    objectKey, metadataKey));

        return userMetadata.get(metadataKey);
    }

    public ObjectMetadata retrieveMetadata() {
        if (!hasMetadata())
            throw new RuntimeException(String.format("S3 Storage Exception | " +
                    "Object '%s' doesn't have metadata", this.objectKey));

        return this.metadata;
    }

    public void addMetadata(String keyName, String value) throws RuntimeException {
        this.metadata.addUserMetadata(keyName, value);
        syncChanges();
    }

    public void addMetadata(Map<String, String> metadataMap) throws RuntimeException {
        this.metadata.setUserMetadata(metadataMap);
        syncChanges();
    }

    public void updateMetadata(String keyName, String newValue) throws RuntimeException {
        Map<String, String> metadataMap = this.metadata.getUserMetadata();
        if (!metadataMap.containsKey(keyName))
            throw new RuntimeException(String.format("S3 Storage Exception | " +
                            "Object '%s' doesn't have metadata key '%s'",
                    objectKey, keyName));

        metadataMap.put(keyName, newValue);
        this.metadata.setUserMetadata(metadataMap);
        syncChanges();
    }

    public void updateMetadata(Map<String, String> metadataMap) throws RuntimeException {
        this.metadata.setUserMetadata(metadataMap);
        syncChanges();
    }

    public void deleteMetadata(String keyName) throws RuntimeException {
        Map<String, String> metadataMap = this.metadata.getUserMetadata();
        if (!metadataMap.containsKey(keyName))
            throw new RuntimeException(String.format("S3 Storage Exception | " +
                            "Object '%s' doesn't have metadata key '%s'",
                    objectKey, keyName));

        metadataMap.remove(keyName);
        this.metadata.setUserMetadata(metadataMap);
        syncChanges();
    }

    public void deleteAllMetadata() throws RuntimeException {
        this.metadata.setUserMetadata(new HashMap<>());
        syncChanges();
    }

    public void syncChanges() throws RuntimeException {
        this.s3Client.putObject(this.bucketName, this.objectKey,
                this.getInputStream(), hasMetadata() ?
                        this.retrieveMetadata() : new ObjectMetadata());
    }

    public Boolean hasMetadata() {
        return !this.metadata.getUserMetadata().isEmpty();
    }

    public Boolean hasMetadataKey(String metaKey) {
        return !this.metadata.getUserMetadata().isEmpty() &&
                this.metadata.getUserMetadata().containsKey(metaKey);
    }
}
