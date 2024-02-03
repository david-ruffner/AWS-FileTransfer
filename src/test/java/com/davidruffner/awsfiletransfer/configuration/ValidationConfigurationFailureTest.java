package com.davidruffner.awsfiletransfer.configuration;

import com.davidruffner.awsfiletransfer.configuration.validation.ValidationConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles(value = { "InvalidProfile" })
public class ValidationConfigurationFailureTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void testConfigValueNotLoaded() {
        assertThrows(RuntimeException.class, () ->
                applicationContext.getBean("ValidationConfiguration"));
    }
}
