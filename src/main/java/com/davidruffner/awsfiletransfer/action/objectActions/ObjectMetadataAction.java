package com.davidruffner.awsfiletransfer.action.objectActions;

import com.davidruffner.awsfiletransfer.action.ActionBase;
import com.davidruffner.awsfiletransfer.action.ActionResponse;
import com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseBuilder;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageBase;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerFactory;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.FAIL;
import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.SUCCESS;

public class ObjectMetadataAction extends ActionBase {

    public enum MetadataActionType {
        ADD_METADATA,
        UPDATE_METADATA,
        DELETE_METADATA,
        DELETE_ALL_METADATA,
        GET_METADATA,
        GET_ALL_METADATA
    }

    private MetadataActionType metadataActionType;
    private Optional<String> metadataKey;
    private Optional<String> newMetadataValue;
    private Optional<Map<String, String>> metadataMap;

    private ObjectMetadataAction(ObjectMetadataActionBuilder.Steps builder) {
        super.keyName = builder.keyName;
        super.containerName = builder.containerName;
        super.storageController = builder.storageController;

        this.metadataActionType = builder.metadataActionType;
        this.metadataKey = builder.metadataKey;
        this.newMetadataValue = builder.newMetadataValue;
        this.metadataMap = builder.metadataMap;
    }

    private boolean verifyParams() {
        return switch (this.metadataActionType) {
            case ADD_METADATA, UPDATE_METADATA -> (this.metadataKey.isPresent()
                    && this.newMetadataValue.isPresent()) ||
                    (this.metadataMap.isPresent());

            case GET_METADATA, DELETE_METADATA -> this.metadataKey.isPresent();

            default -> true;
        };
    }

    @Override
    protected ActionResponse doAction() {
        if (!verifyParams()) {
            return new ActionResponseBuilder(FAIL)
                    .withErrorMessage(String.format(
                            "Not all required params provided for " +
                                    "metadata action type: '%s'",
                            this.metadataActionType))
                    .build();
        }

        try {
            String responseMessage = "";

            switch (this.metadataActionType) {
                case ADD_METADATA -> {
                    if (this.metadataKey.isPresent()) {
                        this.storageController.addMetadata(this.keyName, this.containerName,
                                this.metadataKey.get(), this.newMetadataValue.get());
                        responseMessage = String.format("Metadata key '%s' with value '%s' " +
                                "added to file '%s' in container '%s'", this.metadataKey.get(),
                                this.newMetadataValue.get(), this.keyName, this.containerName);
                    } else {
                        this.storageController.addMetadata(this.keyName, this.containerName,
                                this.metadataMap.get());
                        responseMessage = String.format("Metadata map '%s' added to file " +
                                "'%s' in container '%s'", this.metadataMap.get(),
                                this.keyName, this.containerName);
                    }
                }

                case UPDATE_METADATA -> {
                    if (this.metadataKey.isPresent()) {
                        this.storageController.updateMetadata(this.keyName, this.containerName,
                                this.metadataKey.get(), this.newMetadataValue.get());
                        responseMessage = String.format("Metadata key '%s' updated to new value '%s' " +
                                "for file '%s' in container '%s'", this.metadataKey.get(),
                                this.newMetadataValue.get(), this.keyName, this.containerName);
                    } else {
                        this.storageController.updateMetadata(this.keyName, this.containerName,
                                this.metadataMap.get());
                        responseMessage = String.format("Metadata map '%s' set for file " +
                                "'%s' in container '%s'", this.metadataMap.get(),
                                this.keyName, this.containerName);
                    }
                }

                case DELETE_METADATA -> {
                    this.storageController.deleteMetadata(this.keyName, this.containerName,
                            this.metadataKey.get());
                    responseMessage = String.format("Metadata key '%s' deleted for file '%s' " +
                            "in container '%s'", this.metadataKey.get(),
                            this.keyName, this.containerName);
                }

                case DELETE_ALL_METADATA -> {
                    this.storageController.deleteAllMetadata(this.keyName, this.containerName);
                    responseMessage = String.format("All metadata for file '%s' in container " +
                            "'%s' was deleted", this.keyName, this.containerName);
                }

                case GET_METADATA -> {
                    String metadataValue = this.storageController.getMetadata(this.keyName,
                            this.containerName, this.metadataKey.get());
                    return new ActionResponseBuilder(SUCCESS)
                            .withMetadataValue(this.metadataKey.get(), metadataValue)
                            .build();
                }

                case GET_ALL_METADATA -> {
                    Map<String, String> metadataMap = this.storageController.getAllMetadata(
                            this.keyName, this.containerName);
                    return new ActionResponseBuilder(SUCCESS)
                            .withMetadataMap(metadataMap)
                            .build();
                }
            }

            return new ActionResponseBuilder(SUCCESS)
                    .withResponseMessage(responseMessage)
                    .build();
        } catch (RuntimeException ex) {
            return new ActionResponseBuilder(FAIL)
                    .withErrorMessage(ex.getMessage())
                    .build();
        }
    }

    @Component
    @Scope(value = "prototype")
    public static class ObjectMetadataActionBuilder {
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
            MetadataActionTypeStep containerName(String containerName);
        }

        public interface MetadataActionTypeStep {
            OptionalStep metadataActionType(MetadataActionType metadataActionType);
        }

        public interface OptionalStep {
            OptionalStep withMetadataKey(String metadataKey);
            OptionalStep withNewMetadataValue(String newMetadataValue);
            OptionalStep withMetadataMap(Map<String, String> metadataMap);
            ActionResponse doAction();
        }

        private static class Steps implements StorageControllerStep, KeyNameStep,
        ContainerNameStep, MetadataActionTypeStep, OptionalStep {
            private StorageBase storageController;
            private StorageControllerFactory storageControllerFactory;
            private String keyName;
            private String containerName;
            private MetadataActionType metadataActionType;
            private Optional<String> metadataKey = Optional.empty();
            private Optional<String> newMetadataValue = Optional.empty();
            private Optional<Map<String, String>> metadataMap = Optional.empty();

            private Steps(StorageControllerFactory storageControllerFactory) {
                this.storageControllerFactory = storageControllerFactory;
            }

            public KeyNameStep storageController(StorageControllerType controllerType) {
                this.storageController =
                        this.storageControllerFactory.getStorageController(controllerType);
                return this;
            }

            public ContainerNameStep keyName(String keyName) {
                this.keyName = keyName;
                return this;
            }

            public MetadataActionTypeStep containerName(String containerName) {
                this.containerName = containerName;
                return this;
            }

            public OptionalStep metadataActionType(MetadataActionType metadataActionType) {
                this.metadataActionType = metadataActionType;
                return this;
            }

            public OptionalStep withMetadataKey(String metadataKey) {
                this.metadataKey = Optional.of(metadataKey);
                return this;
            }

            public OptionalStep withNewMetadataValue(String newMetadataValue) {
                this.newMetadataValue = Optional.of(newMetadataValue);
                return this;
            }

            public OptionalStep withMetadataMap(Map<String, String> metadataMap) {
                this.metadataMap = Optional.of(metadataMap);
                return this;
            }

            public ActionResponse doAction() {
                ObjectMetadataAction action = new ObjectMetadataAction(this);
                return action.doAction();
            }
        }
    }
}
