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

import java.util.Optional;

import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.FAIL;
import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.SUCCESS;

public class MoveObjectAction extends ActionBase {
    private String oldContainerName;
    private String newContainerName;
    private Optional<String> newKeyName;

    private MoveObjectAction(MoveObjectActionBuilder.Steps builder) {
        super.keyName = builder.keyName;
        super.storageController = builder.storageController;

        this.oldContainerName = builder.oldContainerName;
        this.newContainerName = builder.newContainerName;
        this.newKeyName = builder.newKeyName;
    }

    @Override
    protected ActionResponse doAction() {
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

    @Component
    @Scope(value = "prototype")
    public static class MoveObjectActionBuilder {
        @Autowired
        private StorageControllerFactory storageControllerFactory;

        public StorageControllerStep newBuilder() {
            return new Steps(this.storageControllerFactory);
        }

        public interface StorageControllerStep {
            KeyNameStep storageController(StorageControllerType controller);
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
            private String keyName;
            private String oldContainerName;
            private String newContainerName;
            private Optional<String> newKeyName = Optional.empty();

            private Steps(StorageControllerFactory storageControllerFactory) {
                this.storageControllerFactory = storageControllerFactory;
            }

            public KeyNameStep storageController(StorageControllerType controllerType) {
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
