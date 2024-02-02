package com.davidruffner.awsfiletransfer.action;

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

public class GetObjectAction extends ActionBase {

    private GetObjectAction(Builder.Steps builder) {
        super.storageController = builder.storageController;
        super.keyName = builder.keyName;
        super.containerName = builder.containerName;
    }

    @Override
    protected ActionResponse doAction() {
        try {
            S3StorageObject storageObject = super.storageController
                    .getObject(super.keyName, super.containerName);

            return new ActionResponseBuilder(SUCCESS)
                    .withResponseMessage(String.format(
                            "File '%s' successfully retrieved from container '%s'",
                            super.keyName, super.containerName))
                    .withStorageObject(storageObject)
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
            ActionStep containerName(String containerName);
        }

        public interface ActionStep {
            ActionResponse doAction();
        }

        private static class Steps implements StorageControllerStep, KeyNameStep, ContainerNameStep,
        ActionStep {
            private StorageBase storageController;
            private StorageControllerFactory storageControllerFactory;
            private String keyName;
            private String containerName;

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

            public ActionStep containerName(String containerName) {
                this.containerName = containerName;
                return this;
            }

            public ActionResponse doAction() {
                GetObjectAction action = new GetObjectAction(this);
                return action.doAction();
            }
        }
    }
}
