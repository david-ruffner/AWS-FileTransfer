package com.davidruffner.awsfiletransfer.action;

import com.davidruffner.awsfiletransfer.storage.controllers.S3StorageObject;

import java.util.Map;
import java.util.Optional;

public class ActionResponse implements AutoCloseable {
    private static final String ERR_MSG = "File Transfer Exception | %s";

    @Override
    public void close() {

    }

    public enum ActionResponseCode {
        SUCCESS,
        FAIL,
        PENDING
    }

    private ActionResponseCode responseCode;

    private Optional<String> responseMessage = Optional.empty();
    private Optional<String> errorMessage = Optional.empty();
    private Optional<Map<String, String>> metadataMap = Optional.empty();
    private Optional<Boolean> responseFlag = Optional.empty();
    private Optional<S3StorageObject> storageObject = Optional.empty();

    private ActionResponse(ActionResponseBuilder actionResponseBuilder) {
        this.responseCode = actionResponseBuilder.responseCode;
        this.responseMessage = actionResponseBuilder.responseMessage;
        this.errorMessage = actionResponseBuilder.errorMessage;
        this.metadataMap = actionResponseBuilder.metadataMap;
        this.responseFlag = actionResponseBuilder.responseFlag;
        this.storageObject = actionResponseBuilder.storageObject;
    }

    public ActionResponseCode getResponseCode() {
        return responseCode;
    }

    public Optional<String> getResponseMessage() {
        return responseMessage;
    }

    public Optional<String> getErrorMessage() {
        return errorMessage;
    }

    public Optional<Map<String, String>> getMetadataMap() {
        return metadataMap;
    }

    public Optional<Boolean> getResponseFlag() {
        return responseFlag;
    }

    public Optional<S3StorageObject> getStorageObject() {
        return storageObject;
    }

    public static class ActionResponseBuilder {
        private ActionResponseCode responseCode;
        private Optional<String> responseMessage = Optional.empty();
        private Optional<String> errorMessage = Optional.empty();
        private Optional<Map<String, String>> metadataMap = Optional.empty();
        private Optional<Boolean> responseFlag = Optional.empty();
        private Optional<S3StorageObject> storageObject = Optional.empty();

        public ActionResponseBuilder(ActionResponseCode responseCode) {
            this.responseCode = responseCode;
        }

        public ActionResponseBuilder withResponseMessage(String responseMessage) {
            this.responseMessage = Optional.of(responseMessage);
            return this;
        }

        public ActionResponseBuilder withErrorMessage(String errorMessage) {
            this.errorMessage = Optional.of(String.format(ERR_MSG, errorMessage));
            return this;
        }

        public ActionResponseBuilder withMetadataValue(String key, String value) {
            this.metadataMap = Optional.of(Map.ofEntries(
                    Map.entry(key, value)
            ));
            return this;
        }

        public ActionResponseBuilder withMetadataMap(Map<String, String> metadataMap) {
            this.metadataMap = Optional.of(metadataMap);
            return this;
        }

        public ActionResponseBuilder withResponseFlag(Boolean responseFlag) {
            this.responseFlag = Optional.of(responseFlag);
            return this;
        }

        public ActionResponseBuilder withStorageObject(S3StorageObject storageObject) {
            this.storageObject = Optional.of(storageObject);
            return this;
        }

        public ActionResponse build() {
            return new ActionResponse(this);
        }
    }
}
