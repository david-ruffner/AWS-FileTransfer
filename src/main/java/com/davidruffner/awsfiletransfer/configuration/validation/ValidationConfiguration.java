package com.davidruffner.awsfiletransfer.configuration.validation;

import com.davidruffner.awsfiletransfer.action.ActionResponse;
import com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.davidruffner.awsfiletransfer.action.ActionResponse.ActionResponseCode.FAIL;
import static com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration.ValidationType.*;

@Configuration
@ConfigurationProperties(prefix = "validation")
public class ValidationConfiguration {
    public enum ValidationType {
        KEY_NAME,
        CONTAINER_NAME,
        METADATA_VALUE
    }

    private Optional<Pattern> validKeyName = Optional.empty();
    private Optional<Pattern> validContainerName = Optional.empty();
    private Optional<Pattern> validMetadataValue = Optional.empty();
    private Map<ValidationType, Optional<Pattern>> validationTypeMap = new HashMap<>();

    public void setValidKeyName(String validKeyName) {
        try {
            this.validKeyName = Optional.of(Pattern.compile(validKeyName));
            validationTypeMap.put(KEY_NAME, this.validKeyName);
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "File Transfer Exception | %s", ex.getMessage()
            ));
        }
    }

    public void setValidContainerName(String validContainerName) {
        try {
            this.validContainerName = Optional.of(Pattern.compile(validContainerName));
            validationTypeMap.put(CONTAINER_NAME, this.validContainerName);
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "File Transfer Exception | %s", ex.getMessage()
            ));
        }
    }

    public void setValidMetadataValue(String validMetadataValue) {
        try {
            this.validMetadataValue = Optional.of(Pattern.compile(validMetadataValue));
            validationTypeMap.put(METADATA_VALUE, this.validMetadataValue);
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "File Transfer Exception | %s", ex.getMessage()
            ));
        }
    }

    public boolean verifyKeyName(String keyName) throws RuntimeException {
        if (validKeyName.isEmpty()) {
            throw new RuntimeException("File Transfer Exception | " +
                    "validKeyName property not loaded in configuration");
        }

        return validKeyName.get().matcher(keyName).matches();
    }

    public boolean verifyContainerName(String containerName) throws RuntimeException {
        if (validContainerName.isEmpty())
            throw new RuntimeException("File Transfer Exception | " +
                    "validContainerName property not loaded in configuration");

        return validContainerName.get().matcher(containerName).matches();
    }

    public ActionResponse verifyMetadataMap(Map<String, String> metadataMap) {
        ActionResponse actionResponse = null;

        for (Map.Entry<String, String> entry : metadataMap.entrySet()) {
            if (!verifyKeyName(entry.getKey()))
                actionResponse = new ActionResponseBuilder(FAIL)
                    .withErrorMessage(String.format("Given metadata keyName '%s' is invalid. %s",
                            entry.getKey(), generateErrorMessage(KEY_NAME)))
                    .build();

            if (!verifyMetadataValue(entry.getValue()))
                actionResponse = new ActionResponseBuilder(FAIL)
                        .withErrorMessage(String.format("Given metadata value '%s' is invalid. %s",
                                entry.getValue(), generateErrorMessage(METADATA_VALUE)))
                        .build();
        }

        return actionResponse;
    }

    public boolean verifyMetadataValue(String metadataValue) throws RuntimeException {
        if (validMetadataValue.isEmpty())
            throw new RuntimeException("File Transfer Exception | " +
                    "validMetadataValue property not loaded in configuration");

        return validMetadataValue.get().matcher(metadataValue).matches();
    }

    public String generateErrorMessage(ValidationType validationType) {
        if (validationTypeMap.get(validationType).isEmpty())
            throw new RuntimeException("File Transfer Exception | " +
                    "validationTypeMap not loaded");

        String patternStr = validationTypeMap.get(validationType).get().toString();
        int allowedFormats = 0;
        int currentFormatIndex = 0;

        boolean uppercaseAllowed = patternStr.contains("A-Z");
        boolean lowercaseAllowed = patternStr.contains("a-z");
        boolean numbersAllowed = patternStr.contains("0-9");
        boolean whitespaceAllowed = patternStr.contains("\\s");
        int minLength = 0;
        int maxLength = 0;

        Pattern lengthPatt = Pattern.compile("\\{(\\d+),(\\d+)\\}");
        Matcher lengthMatcher = lengthPatt.matcher(patternStr);
        if (lengthMatcher.find()) {
            minLength = Integer.parseInt(lengthMatcher.group(1));
            maxLength = Integer.parseInt(lengthMatcher.group(2));
        }

        List<String> allowedSpecialChars = new ArrayList<>();
        if (patternStr.contains("\\\\"))
            allowedSpecialChars.add("\\");
        if (patternStr.contains("\\/"))
            allowedSpecialChars.add("/");

        Arrays.stream(patternStr.split("\\\\")).forEach(
            c -> {
                String ch = c.split("]")[0];
                if (ch.length() == 1 && !Pattern.compile("^[A-Za-z0-9\\s]$")
                        .matcher(ch).matches() && !allowedSpecialChars.contains(ch)) {
                    allowedSpecialChars.add(ch);
                }
            });

        if (uppercaseAllowed)
            allowedFormats++;
        if (lowercaseAllowed)
            allowedFormats++;
        if (numbersAllowed)
            allowedFormats++;
        if (whitespaceAllowed)
            allowedFormats++;
        if (!allowedSpecialChars.isEmpty())
            allowedFormats++;

        StringBuilder errMsg = new StringBuilder();
        errMsg.append(String.format("Parameter must be between %d and %d characters and " +
                "can only include ", minLength, maxLength));

        if (uppercaseAllowed && lowercaseAllowed) {
            currentFormatIndex++;
            errMsg.append(currentFormatIndex < allowedFormats ?
                    "uppercase/lowercase letters, " : "and uppercase/lowercase letters.");
        } else {
            if (!lowercaseAllowed && uppercaseAllowed) {
                currentFormatIndex++;
                errMsg.append((currentFormatIndex < allowedFormats) ?
                        "uppercase letters, " : "and uppercase letters.");
            } else if (lowercaseAllowed) {
                currentFormatIndex++;
                errMsg.append(currentFormatIndex < allowedFormats ?
                        "lowercase letters, " : "and lowercase letters.");
            }
        }

        if (numbersAllowed) {
            currentFormatIndex++;
            errMsg.append(currentFormatIndex < allowedFormats ?
                    "numbers, " : "and numbers.");
        }

        if (whitespaceAllowed) {
            currentFormatIndex++;
            errMsg.append(currentFormatIndex < allowedFormats ?
                    "whitespace, " : "and whitespace.");
        }

        if (!allowedSpecialChars.isEmpty()) {
            errMsg.append("and the following characters: ");

            for (int i = 0; i < allowedSpecialChars.size(); i++) {
                errMsg.append(i < allowedSpecialChars.size() ?
                        String.format("%s ", allowedSpecialChars.get(i))
                        : allowedSpecialChars.get(i));
            }
        }

        return errMsg.toString();
    }
}
