package com.proofpoint.anomalytics.blackhole;

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Map;

import static com.proofpoint.configuration.testing.ConfigAssertions.assertFullMapping;
import static com.proofpoint.configuration.testing.ConfigAssertions.assertRecordedDefaults;
import static com.proofpoint.configuration.testing.ConfigAssertions.recordDefaults;
import static com.proofpoint.experimental.testing.ValidationAssertions.assertFailsValidation;

public class TestBlackholeConfig
{
    @Test
    public void testDefaults()
    {
        assertRecordedDefaults(recordDefaults(BlackholeConfig.class)
                .setSamplingRate(BigDecimal.ZERO)
                .setServiceAnnouncement(null)
        );
    }

    @Test
    public void testExplicitPropertyMappings()
    {
        Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("blackhole.sample-rate", "0.5")
                .put("blackhole.announcement", "blackhole")
                .build();

        BlackholeConfig expected = new BlackholeConfig()
                .setSamplingRate(BigDecimal.valueOf(0.5))
                .setServiceAnnouncement("blackhole");

        assertFullMapping(properties, expected);
    }

    @Test
    public void testInvalidSampleRate()
    {
        assertFailsValidation(new BlackholeConfig().setSamplingRate(null), "samplingRate", "may not be null", NotNull.class);
        assertFailsValidation(new BlackholeConfig().setSamplingRate(BigDecimal.valueOf(-1)), "samplingRate", "must be greater than or equal to 0", Min.class);
        assertFailsValidation(new BlackholeConfig().setSamplingRate(BigDecimal.valueOf(1.5)), "samplingRate", "must be less than or equal to 1", Max.class);
    }

    @Test
    public void testInvalidAnnouncement()
    {
        assertFailsValidation(new BlackholeConfig(), "serviceAnnouncement", "may not be null", NotNull.class);
        assertFailsValidation(new BlackholeConfig().setServiceAnnouncement("black-hole"), "serviceAnnouncement", "is invalid", Pattern.class);
    }
}
