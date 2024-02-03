package com.davidruffner.awsfiletransfer.action.objectActions;

import com.davidruffner.awsfiletransfer.action.ActionBase;
import com.davidruffner.awsfiletransfer.action.ActionResponse;
import com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseBuilder;
import com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageBase;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerFactory;
import com.davidruffner.awsfiletransfer.storage.metadata.MetadataBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Optional;

import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.FAIL;
import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.SUCCESS;

public class UploadObjectAction extends ActionBase {
    private InputStream inputStream;
    private Optional<MetadataBase> metadata;

    private UploadObjectAction(Builder.Steps builder) {
        super.storageController = builder.storageController;
        super.keyName = builder.keyName;
        super.containerName = builder.containerName;
        super.validationConfiguration = builder.validationConfiguration;

        this.inputStream = builder.inputStream;
        this.metadata = builder.metadata;
    }

    @Override
    protected ActionResponse verifyParams() {
        try (ActionResponse verifyKeyName = super.verifyKeyName()) {
            if (null != verifyKeyName)
                return verifyKeyName;
        }

        try (ActionResponse verifyContainerName = super.verifyContainerName()) {
            if (null != verifyContainerName)
                return verifyContainerName;
        }

        try {
            if (null == this.inputStream || this.inputStream.available() == 0) {
                return new ActionResponseBuilder(FAIL)
                    .withErrorMessage("Given inputStream was either null or empty")
                    .build();
            }
        } catch (Exception ex) {
            return new ActionResponseBuilder(FAIL)
                .withErrorMessage(String.format("File Transfer Exception | %s",
                    ex.getMessage()))
                .build();
        }

        if (this.metadata.isPresent()) {
            try (ActionResponse verifyMetadata = validationConfiguration
                    .verifyMetadataMap(this.metadata.get().getMetadataMap())) {
                if (null != verifyMetadata)
                    return verifyMetadata;
            }
        }

        return null;
    }

    @Override
    protected ActionResponse defineAction() {
        try {
            if (this.metadata.isPresent()) {
                super.storageController.uploadObject(super.keyName, this.inputStream,
                        super.containerName, this.metadata.get());
            } else {
                super.storageController.uploadObject(super.keyName, this.inputStream,
                        super.containerName);
            }

            return new ActionResponseBuilder(SUCCESS)
                    .withResponseMessage(String.format(
                            "File '%s' successfully uploaded to container '%s'",
                            super.keyName, super.containerName))
                    .build();
        } catch (RuntimeException ex) {
            return new ActionResponseBuilder(FAIL)
                    .withErrorMessage(ex.getMessage())
                    .build();
        }
    }

    @Component(value = "UploadObjectActionBuilder")
    @Scope(value = "prototype")
    public static class Builder {
        @Autowired
        private StorageControllerFactory storageControllerFactory;

        @Autowired
        private ValidationConfiguration validationConfiguration;

        public StorageControllerStep newBuilder() {
            return new Steps(this.storageControllerFactory,
                    this.validationConfiguration);
        }

        public interface StorageControllerStep {
            KeyNameStep storageController(FileTransferRequest.StorageControllerType controller);
        }

        public interface KeyNameStep {
            ContainerNameStep keyName(String keyName);
        }

        public interface ContainerNameStep {
            Builder.InputStreamStep containerName(String containerName);
        }

        public interface InputStreamStep {
            OptionalStep inputStream(InputStream inputStream);
        }

        public interface OptionalStep {
            OptionalStep withMetadata(MetadataBase metadata);
            ActionResponse doAction();
        }

        private static class Steps implements StorageControllerStep, KeyNameStep, ContainerNameStep,
                InputStreamStep, OptionalStep {
            private StorageBase storageController;
            private StorageControllerFactory storageControllerFactory;
            private ValidationConfiguration validationConfiguration;
            private String keyName;
            private String containerName;
            private InputStream inputStream;
            private Optional<MetadataBase> metadata = Optional.empty();

            private Steps(StorageControllerFactory storageControllerFactory,
                          ValidationConfiguration validationConfiguration) {
                this.storageControllerFactory = storageControllerFactory;
                this.validationConfiguration = validationConfiguration;
            }

            public KeyNameStep storageController(FileTransferRequest.StorageControllerType controller) {
                this.storageController =
                        this.storageControllerFactory.getStorageController(controller);
                return this;
            }

            public ContainerNameStep keyName(String keyName) {
                this.keyName = keyName;
                return this;
            }

            public InputStreamStep containerName(String containerName) {
                this.containerName = containerName;
                return this;
            }

            public OptionalStep inputStream(InputStream inputStream) {
                this.inputStream = inputStream;
                return this;
            }

            public OptionalStep withMetadata(MetadataBase metadata) {
                this.metadata = Optional.of(metadata);
                return this;
            }

            public ActionResponse doAction() {
                UploadObjectAction action = new UploadObjectAction(this);
                return action.doAction();
            }
        }
    }
}
