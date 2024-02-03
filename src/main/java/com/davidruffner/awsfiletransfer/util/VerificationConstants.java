package com.davidruffner.awsfiletransfer.util;

import java.util.regex.Pattern;

public class VerificationConstants {
    public static final Pattern VALID_KEY_NAME = Pattern.compile("^[A-Za-z0-9!@#$%^\\-_.?=+~<>\\s]{1,50}$");
}
