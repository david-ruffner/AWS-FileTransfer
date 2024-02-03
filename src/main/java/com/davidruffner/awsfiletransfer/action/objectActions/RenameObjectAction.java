package com.davidruffner.awsfiletransfer.action.objectActions;

import com.davidruffner.awsfiletransfer.action.ActionBase;
import com.davidruffner.awsfiletransfer.action.ActionResponse;
import com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseBuilder;
import com.davidruffner.awsfiletransfer.storage.controllers.S3StorageObject;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageBase;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerFactory;
import com.davidruffner.awsfiletransfer.storage.controllers.StorageControllerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.FAIL;
import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.SUCCESS;

public class RenameObjectAction extends ActionBase {
    private String oldKeyName;
    private String newKeyName;

    private RenameObjectAction(RenameObjectActionBuilder.Steps builder) {
        super.containerName = builder.containerName;
        super.storageController = builder.storageController;

        this.oldKeyName = builder.oldKeyName;
        this.newKeyName = builder.newKeyName;
    }

    @Override
    protected ActionResponse doAction() {
        try {
            this.storageController.renameObject(this.oldKeyName,
                    this.newKeyName, this.containerName);

            return new ActionResponseBuilder(SUCCESS)
                    .withResponseMessage(String.format(
                            "File '%s' successfully renamed to '%s' " +
                                    "in container '%s'",
                            this.oldKeyName, this.newKeyName, this.containerName
                    ))
                    .build();
        } catch (RuntimeException ex) {
            return new ActionResponseBuilder(FAIL)
                    .withErrorMessage(ex.getMessage())
                    .build();
        }
    }

    @Component
    @Scope(value = "prototype")
    public static class RenameObjectActionBuilder {
        @Autowired
        private StorageControllerFactory storageControllerFactory;

        public StorageControllerStep newBuilder() {
            return new Steps(this.storageControllerFactory);
        }

        public interface StorageControllerStep {
            OldKeyNameStep storageController(StorageControllerType controller);
        }

        public interface OldKeyNameStep {
            NewKeyNameStep oldKeyName(String oldKeyName);
        }

        public interface NewKeyNameStep {
            ContainerNameStep newKeyName(String newKeyName);
        }

        public interface ContainerNameStep {
            ActionStep containerName(String containerName);
        }

        public interface ActionStep {
            ActionResponse doAction();
        }

        private static class Steps implements StorageControllerStep, OldKeyNameStep,
        NewKeyNameStep, ContainerNameStep, ActionStep {
            private StorageBase storageController;
            private StorageControllerFactory storageControllerFactory;
            private String oldKeyName;
            private String newKeyName;
            private String containerName;

            private Steps(StorageControllerFactory storageControllerFactory) {
                this.storageControllerFactory = storageControllerFactory;
            }

            public OldKeyNameStep storageController(StorageControllerType controllerType) {
                this.storageController =
                        this.storageControllerFactory.getStorageController(controllerType);
                return this;
            }

            public NewKeyNameStep oldKeyName(String oldKeyName) {
                this.oldKeyName = oldKeyName;
                return this;
            }

            public ContainerNameStep newKeyName(String newKeyName) {
                this.newKeyName = newKeyName;
                return this;
            }

            public ActionStep containerName(String containerName) {
                this.containerName = containerName;
                return this;
            }

            public ActionResponse doAction() {
                RenameObjectAction action = new RenameObjectAction(this);
                return action.doAction();
            }
        }
    }
}
