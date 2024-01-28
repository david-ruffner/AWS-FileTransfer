package com.davidruffner.awsfiletransfer.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = { ConfigDataApplicationContextInitializer.class })
@ActiveProfiles(profiles = { "test" })
@EnableConfigurationProperties(value = { TestS3Configuration.class })
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TestS3ConfigurationTest {

    @Autowired
    TestS3Configuration testS3Configuration;

    @Test
    void testGetURL() {
        assertNotNull(testS3Configuration.getURL("MyFile"));
        System.out.printf("\nURL: %s\n", testS3Configuration.getURL("MyFile"));
    }
}
