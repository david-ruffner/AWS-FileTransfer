package com.davidruffner.awsfiletransfer.storage.controllers;

import com.davidruffner.awsfiletransfer.storage.metadata.MetadataBase;

import java.io.InputStream;
import java.util.Map;

public interface StorageBase {
    void uploadObject(String keyName, InputStream inputStream,
                             String containerName) throws RuntimeException;

    void uploadObject(String keyName, InputStream inputStream,
                             String containerName, MetadataBase metadata) throws RuntimeException;

    void renameObject(String oldKeyName, String newKeyName,
                             String containerName) throws RuntimeException;

    void moveObject(String keyName, String oldContainerName,
                           String newContainerName) throws RuntimeException;

    void moveObject(String oldKeyName, String newKeyName, String oldContainerName,
                           String newContainerName) throws RuntimeException;

    S3StorageObject getObject(String keyName, String containerName) throws RuntimeException;

    void deleteObject(String keyName, String containerName) throws RuntimeException;

    void addMetadata(String objectKey, String containerName,
                            String metaKey, String metaValue) throws RuntimeException;

    void addMetadata(String objectKey, String containerName,
                            Map<String, String> metadata) throws RuntimeException;

    void updateMetadata(String objectKey, String containerName,
                               String metaKey, String newValue) throws RuntimeException;

    void updateMetadata(String objectKey, String containerName,
                               Map<String, String> metadata) throws RuntimeException;

    void deleteMetadata(String objectKey, String containerName,
                               String metaKey) throws RuntimeException;

    void deleteAllMetadata(String objectKey, String containerName) throws RuntimeException;

    String getMetadata(String objectKey, String containerName,
                              String metaKey) throws RuntimeException;

    Map<String, String> getAllMetadata(String objectKey, String containerName) throws RuntimeException;

    Boolean doesObjectExist(String keyName, String containerName);
}
