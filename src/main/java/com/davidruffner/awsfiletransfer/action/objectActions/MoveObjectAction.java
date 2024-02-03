package com.davidruffner.awsfiletransfer.action.objectActions;

import com.davidruffner.awsfiletransfer.action.ActionBase;
import com.davidruffner.awsfiletransfer.action.ActionResponse;
import com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseBuilder;
import com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration;
import com.davidruffner.awsfiletransfer.messaging.entities.FileTransferRequest;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageBase;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.FAIL;
import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.SUCCESS;
import static com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration.ValidationType.CONTAINER_NAME;
import static com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration.ValidationType.KEY_NAME;

public class MoveObjectAction extends ActionBase {
    private String oldContainerName;
    private String newContainerName;
    private Optional<String> newKeyName;

    private MoveObjectAction(MoveObjectActionBuilder.Steps builder) {
        super.keyName = builder.keyName;
        super.storageController = builder.storageController;
        super.validationConfiguration = builder.validationConfiguration;

        this.oldContainerName = builder.oldContainerName;
        this.newContainerName = builder.newContainerName;
        this.newKeyName = builder.newKeyName;
    }

    @Override
    protected ActionResponse verifyParams() {
        try (ActionResponse verifyKey = super.verifyKeyName()) {
            if (null != verifyKey)
                return verifyKey;
        }

        if (!validationConfiguration.verifyContainerName(this.oldContainerName))
            return new ActionResponseBuilder(FAIL)
                    .withErrorMessage(String.format("Given oldContainerName '%s' is invalid. %s",
                            this.oldContainerName, validationConfiguration
                                    .generateErrorMessage(CONTAINER_NAME)))
                    .build();

        if (!validationConfiguration.verifyContainerName(this.newContainerName))
            return new ActionResponseBuilder(FAIL)
                    .withErrorMessage(String.format("Given newContainerName '%s' is invalid. %s",
                            this.newContainerName, validationConfiguration
                                    .generateErrorMessage(CONTAINER_NAME)))
                    .build();

        if (this.newKeyName.isPresent() && !validationConfiguration.verifyKeyName(this.newKeyName.get()))
            return new ActionResponseBuilder(FAIL)
                    .withErrorMessage(String.format("Given newKeyName '%s' is invalid. %s",
                            this.newKeyName.get(), validationConfiguration
                                    .generateErrorMessage(KEY_NAME)))
                    .build();

        return null;
    }

    @Override
    protected ActionResponse defineAction() {
        try {
            if (this.newKeyName.isPresent()) {
                this.storageController.moveObject(this.keyName, this.newKeyName.get(),
                        this.oldContainerName, this.newContainerName);

                return new ActionResponseBuilder(SUCCESS)
                        .withResponseMessage(String.format(
                                "File '%s' in container '%s' was moved to " +
                                        "new container '%s' with new name '%s'",
                                this.keyName, this.oldContainerName,
                                this.newContainerName, this.newKeyName.get()))
                        .build();
            } else {
                this.storageController.moveObject(this.keyName,
                        this.oldContainerName, this.newContainerName);

                return new ActionResponseBuilder(SUCCESS)
                        .withResponseMessage(String.format(
                                "File '%s' in container '%s' was moved to " +
                                        "new container '%s'",
                                this.keyName, this.oldContainerName, this.newContainerName))
                        .build();
            }
        } catch (RuntimeException ex) {
            return new ActionResponseBuilder(FAIL)
                    .withErrorMessage(ex.getMessage())
                    .build();
        }
    }

    @Component(value = "MoveObjectActionBuilder")
    @Scope(value = "prototype")
    public static class MoveObjectActionBuilder {
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
            OldContainerNameStep keyName(String keyName);
        }

        public interface OldContainerNameStep {
            NewContainerNameStep oldContainerName(String oldContainerName);
        }

        public interface NewContainerNameStep {
            OptionalStep newContainerName(String newContainerName);
        }

        public interface OptionalStep {
            OptionalStep withNewKeyName(String newKeyName);
            ActionResponse doAction();
        }

        private static class Steps implements StorageControllerStep, KeyNameStep,
        OldContainerNameStep, NewContainerNameStep, OptionalStep {
            private StorageBase storageController;
            private StorageControllerFactory storageControllerFactory;
            private ValidationConfiguration validationConfiguration;
            private String keyName;
            private String oldContainerName;
            private String newContainerName;
            private Optional<String> newKeyName = Optional.empty();

            private Steps(StorageControllerFactory storageControllerFactory,
                          ValidationConfiguration validationConfiguration) {
                this.storageControllerFactory = storageControllerFactory;
                this.validationConfiguration = validationConfiguration;
            }

            public KeyNameStep storageController(FileTransferRequest.StorageControllerType controllerType) {
                this.storageController =
                        this.storageControllerFactory.getStorageController(controllerType);
                return this;
            }

            public OldContainerNameStep keyName(String keyName) {
                this.keyName = keyName;
                return this;
            }

            public NewContainerNameStep oldContainerName(String oldContainerName) {
                this.oldContainerName = oldContainerName;
                return this;
            }

            public OptionalStep newContainerName(String newContainerName) {
                this.newContainerName = newContainerName;
                return this;
            }

            public OptionalStep withNewKeyName(String newKeyName) {
                this.newKeyName = Optional.of(newKeyName);
                return this;
            }

            public ActionResponse doAction() {
                MoveObjectAction action = new MoveObjectAction(this);
                return action.doAction();
            }
        }
    }
}
