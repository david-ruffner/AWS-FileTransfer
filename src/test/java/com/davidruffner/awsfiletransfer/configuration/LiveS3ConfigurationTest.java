package com.davidruffner.awsfiletransfer.configuration;

import com.amazonaws.regions.Regions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = { LiveS3Configuration.class })
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class LiveS3ConfigurationTest {

    @Autowired
    LiveS3Configuration liveS3Configuration;

    @Test
    public void testGetAccessKey() {
        assertNotNull(liveS3Configuration.getAccessKey());
    }

    @Test
    public void testGetSecretKey() {
        assertNotNull(liveS3Configuration.getSecretKey());
    }

    @Test
    public void testGetWhenConfigNotReady() {
        ReflectionTestUtils.setField(liveS3Configuration, "accessKey", null);
        assertThrows(RuntimeException.class, () -> liveS3Configuration.getAccessKey());
    }

    @Test
    public void testGetURL() {
        assertNotNull(liveS3Configuration.getURL("MyKey"));
    }

    @Test
    public void testGetRegionName() {
        Regions region = liveS3Configuration.getRegionName();
        assertNotNull(region);
    }

    @Test
    public void testGetInvalidRegionName() {
        ReflectionTestUtils.setField(liveS3Configuration, "regionName", "InvalidRegion");
        assertThrows(RuntimeException.class, () -> liveS3Configuration.getRegionName());
    }

    @Test
    public void testGetBucketName() {
        assertNotNull(liveS3Configuration.getBucketName());
    }
}
