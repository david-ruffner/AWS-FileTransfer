package com.davidruffner.awsfiletransfer.messaging.entities;

import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequestOuterClass.FileTransferRequest.ACTION_TYPE;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequestOuterClass.FileTransferRequest.CONTROLLER_NAME;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequestOuterClass.FileTransferRequest.METADATA_ACTION_TYPE;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileTransferRequest {
    private String keyName;
    private String containerName;
    private StorageControllerType controllerType;
    private ActionType actionType;

    Optional<MetadataActionType> metadataActionType = Optional.empty();
    Optional<String> newContainerName = Optional.empty();
    Optional<String> newKeyName = Optional.empty();
    Optional<String> metadataKey = Optional.empty();
    Optional<String> newMetadataValue = Optional.empty();
    Optional<Map<String, String>> metadataMap = Optional.empty();
    Optional<InputStream> data = Optional.empty();

    private void initObject(FileTransferRequestOuterClass
                                    .FileTransferRequest outerClass) throws JsonProcessingException {
        this.keyName = outerClass.getKeyName();
        this.containerName = outerClass.getContainerName();
        this.controllerType = StorageControllerType
                .valueOf(outerClass.getControllerName().name());
        this.actionType = ActionType.valueOf(outerClass.getActionType().name());

        if (outerClass.hasMetaDataActionType())
            this.metadataActionType = Optional.of(MetadataActionType.valueOf(
                outerClass.getMetaDataActionType().name()));
        if (outerClass.hasNewContainerName())
            this.newContainerName = Optional.of(outerClass.getNewContainerName());
        if (outerClass.hasNewKeyName())
            this.newKeyName = Optional.of(outerClass.getNewKeyName());
        if (outerClass.hasMetaDataKey())
            this.metadataKey = Optional.of(outerClass.getMetaDataKey());
        if (outerClass.hasNewMetaDataValue())
            this.newMetadataValue = Optional.of(outerClass.getNewMetaDataValue());
        if (outerClass.hasData())
            this.data = Optional.of(new ByteArrayInputStream(
                    outerClass.getData().toByteArray()));

        if (outerClass.hasMetadataMap()) {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {};

            HashMap<String, String> metadataMap =
                mapper.readValue(outerClass.getMetadataMap(), typeRef);
            this.metadataMap = Optional.of(metadataMap);
        }
    }

    public FileTransferRequest() {}

    public FileTransferRequest(byte[] inputBytes) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(inputBytes);
            initObject(FileTransferRequestOuterClass.FileTransferRequest
                    .parseFrom(decodedBytes));
        } catch (IOException ex) {
            throw new RuntimeException(String.format("File Storage Exception | %s",
                    ex.getMessage()));
        }
    }

    public FileTransferRequest(String inputStr) {
        try {
            byte[] decodedBytes = Base64.getDecoder()
                    .decode(inputStr.trim().getBytes(UTF_8));
            initObject(FileTransferRequestOuterClass.FileTransferRequest
                    .parseFrom(decodedBytes));
        } catch (IOException ex) {
            throw new RuntimeException(String.format("File Storage Exception | %s",
                    ex.getMessage()));
        }
    }

    public FileTransferRequest(InputStream inputStream) {
        try {
            String readValue = new String(inputStream.readAllBytes(), UTF_8).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(readValue);

            initObject(FileTransferRequestOuterClass.FileTransferRequest
                    .parseFrom(decodedBytes));
        } catch (IOException ex) {
            throw new RuntimeException(String.format("File Storage Exception | %s",
                    ex.getMessage()));
        }
    }

    public static String encode(FileTransferRequest ftr) throws IOException {
        FileTransferRequestOuterClass.FileTransferRequest.Builder outerBuilder =
                FileTransferRequestOuterClass.FileTransferRequest.newBuilder()
                .setKeyName(ftr.getKeyName())
                .setContainerName(ftr.getContainerName())
                .setControllerName(CONTROLLER_NAME.valueOf(ftr.getControllerType().label))
                .setActionType(ACTION_TYPE.valueOf(ftr.getActionType().label));

        if (ftr.getMetadataActionType().isPresent())
            outerBuilder.setMetaDataActionType(METADATA_ACTION_TYPE.valueOf(
                    ftr.getMetadataActionType().get().label));
        if (ftr.getNewContainerName().isPresent())
            outerBuilder.setNewContainerName(ftr.getNewContainerName().get());
        if (ftr.getNewKeyName().isPresent())
            outerBuilder.setNewKeyName(ftr.getNewKeyName().get());
        if (ftr.getMetadataKey().isPresent())
            outerBuilder.setMetaDataKey(ftr.getMetadataKey().get());
        if (ftr.getNewMetadataValue().isPresent())
            outerBuilder.setNewMetaDataValue(ftr.getNewMetadataValue().get());
        if (ftr.getData().isPresent())
            outerBuilder.setData(ByteString.copyFrom(
                    ftr.getData().get().readAllBytes()));

        if (ftr.getMetadataMap().isPresent()) {
            String flatMap = new ObjectMapper()
                    .writeValueAsString(ftr.getMetadataMap().get());
            outerBuilder.setMetadataMap(flatMap);
        }

        FileTransferRequestOuterClass.FileTransferRequest outerFtr
                = outerBuilder.build();

        byte[] encoded = Base64.getEncoder().encode(outerFtr.toByteArray());
        return new String(encoded, UTF_8);
    }

    public String getKeyName() {
        return keyName;
    }

    public String getContainerName() {
        return containerName;
    }

    public StorageControllerType getControllerType() {
        return controllerType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public Optional<MetadataActionType> getMetadataActionType() {
        return metadataActionType;
    }

    public Optional<String> getNewContainerName() {
        return newContainerName;
    }

    public Optional<String> getNewKeyName() {
        return newKeyName;
    }

    public Optional<String> getMetadataKey() {
        return metadataKey;
    }

    public Optional<String> getNewMetadataValue() {
        return newMetadataValue;
    }

    public Optional<Map<String, String>> getMetadataMap() {
        return metadataMap;
    }

    public Optional<InputStream> getData() {
        return data;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public void setControllerType(StorageControllerType controllerType) {
        this.controllerType = controllerType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public void setMetadataActionType(MetadataActionType metadataActionType) {
        this.metadataActionType = Optional.of(metadataActionType);
    }

    public void setNewContainerName(String newContainerName) {
        this.newContainerName = Optional.of(newContainerName);
    }

    public void setNewKeyName(String newKeyName) {
        this.newKeyName = Optional.of(newKeyName);
    }

    public void setMetadataKey(String metadataKey) {
        this.metadataKey = Optional.of(metadataKey);
    }

    public void setNewMetadataValue(String newMetadataValue) {
        this.newMetadataValue = Optional.of(newMetadataValue);
    }

    public void setMetadataMap(Map<String, String> metadataMap) {
        this.metadataMap = Optional.of(metadataMap);
    }

    public void setData(InputStream data) {
        this.data = Optional.of(data);
    }

    public enum StorageControllerType {
        S3("S3");

        public final String label;

        private StorageControllerType(String label) { this.label = label; }

        private static final Map<StorageControllerType, String> beanTypeMap = Map.ofEntries(
                Map.entry(S3, "S3Storage")
        );

        public static String getBeanName(StorageControllerType controllerType) {
            if (!beanTypeMap.containsKey(controllerType)) {
                throw new RuntimeException(String.format(
                        "File Transfer Exception | Controller type: '%s' doesn't have a bean",
                        controllerType.label));
            }

            return beanTypeMap.get(controllerType);
        }
    }

    public enum ActionType {
        UPLOAD("UPLOAD"),
        GET("GET"),
        MOVE("MOVE"),
        OBJECT_EXISTS("OBJECT_EXISTS"),
        OBJECT_METADATA("OBJECT_METADATA"),
        RENAME("RENAME"),
        DELETE("DELETE");

        public final String label;

        private ActionType(String label) {
            this.label = label;
        }

        private static final Map<ActionType, String> actionBeanMap = Map.ofEntries(
                Map.entry(UPLOAD, "UploadObjectActionBuilder"),
                Map.entry(GET, "GetObjectActionBuilder"),
                Map.entry(MOVE, "MoveObjectActionBuilder"),
                Map.entry(OBJECT_EXISTS, "ObjectExistsActionBuilder"),
                Map.entry(OBJECT_METADATA, "ObjectMetadataActionBuilder"),
                Map.entry(RENAME, "RenameObjectActionBuilder"),
                Map.entry(DELETE, "DeleteObjectActionBuilder")
        );

        public static String getBeanName(ActionType actionType) {
            if (!actionBeanMap.containsKey(actionType))
                throw new RuntimeException(String.format("" +
                        "File Transfer Exception | Action Bean '%s' doesn't exist",
                        actionType.label));

            return actionBeanMap.get(actionType);
        }
    }

    public enum MetadataActionType {
        ADD_METADATA("ADD_METADATA"),
        UPDATE_METADATA("UPDATE_METADATA"),
        DELETE_METADATA("DELETE_METADATA"),
        DELETE_ALL_METADATA("DELETE_ALL_METADATA"),
        GET_METADATA("GET_METADATA"),
        GET_ALL_METADATA("GET_ALL_METADATA");

        public final String label;

        private MetadataActionType(String label) { this.label = label; }
    }
}
