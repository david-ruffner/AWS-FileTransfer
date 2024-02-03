package com.davidruffner.awsfiletransfer.action.objectActions;

import com.davidruffner.awsfiletransfer.action.ActionBase;
import com.davidruffner.awsfiletransfer.action.ActionResponse;
import com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseBuilder;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageBase;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerFactory;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerType;
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

        this.inputStream = builder.inputStream;
        this.metadata = builder.metadata;
    }

    @Override
    protected ActionResponse doAction() {
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

    @Component
    @Scope(value = "prototype")
    public static class Builder {
        @Autowired
        private StorageControllerFactory storageControllerFactory;

        public StorageControllerStep newBuilder() {
            return new Steps(this.storageControllerFactory);
        }

        public interface StorageControllerStep {
            KeyNameStep storageController(StorageControllerType controller);
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
            private String keyName;
            private String containerName;
            private InputStream inputStream;
            private Optional<MetadataBase> metadata = Optional.empty();

            private Steps(StorageControllerFactory storageControllerFactory) {
                this.storageControllerFactory = storageControllerFactory;
            }

            public KeyNameStep storageController(StorageControllerType controller) {
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
