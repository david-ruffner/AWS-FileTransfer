package com.davidruffner.awsfiletransfer.configuration;

import com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration.ValidationType.*;
import static com.davidruffner.awsfiletransfer.util.VerificationConstants.VALID_KEY_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles(value = { "test" })
public class ValidationConfigurationTest {

    @Autowired
    ValidationConfiguration validationConfiguration;

    @Test
    void testValidKeyName() {
        assertTrue(validationConfiguration.verifyKeyName("My-Test-file_009"));
        assertTrue(validationConfiguration.verifyKeyName("My Test File.png"));
        assertFalse(validationConfiguration.verifyKeyName("(fdaf"));
    }

    @Test
    void testValidContainerName() {
        assertTrue(validationConfiguration.verifyContainerName("My_container_09-A"));
        assertFalse(validationConfiguration.verifyContainerName(" My cont a$in"));
    }

    @Test
    void testValidMetadataValue() {
        String metaValue1 = """
                My metadata value blah bla.fdsa842828f32#@$@482342.
                
                    This is a test
                """;

        String metaValue2 = """
                My metadata value blah bla.fdsa842828f32#@$@482342.
                
                    This is a test
                `
                """;

        assertTrue(validationConfiguration.verifyMetadataValue(metaValue1));
        assertFalse(validationConfiguration.verifyMetadataValue(metaValue2));
    }

    @Test
    void testErrMsg() {
        validationConfiguration.generateErrorMessage(KEY_NAME);

        validationConfiguration.generateErrorMessage(CONTAINER_NAME);

        validationConfiguration.generateErrorMessage(METADATA_VALUE);
    }
}
