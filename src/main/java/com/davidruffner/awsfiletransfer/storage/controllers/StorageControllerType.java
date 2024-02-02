package com.davidruffner.awsfiletransfer.storage.controllers;

public enum StorageControllerType {
    S3("S3Storage");

    public final String beanName;

    private StorageControllerType(String beanName) { this.beanName = beanName; }
}
