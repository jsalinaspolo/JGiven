package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.tngtech.jgiven.config.ConfigValue;
import com.tngtech.jgiven.impl.TestUtil.JGivenLogHandler;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Map<String, String> systemPropertiesBackup = new HashMap<>();
    private final JGivenLogHandler handler = new JGivenLogHandler();
    private CharSink jgivenConfig;

    @Before
    public void setupPropertiesFile() throws Exception {
        Logger.getLogger(Config.class.getName()).addHandler(handler);
        File configFile = temporaryFolder.newFile();
        jgivenConfig = Files.asCharSink(configFile, Charsets.UTF_8, FileWriteMode.APPEND);
        setSystemProperty("jgiven.config.path", configFile.getAbsolutePath());
        setSystemProperty("jgiven.report.dir", null);
    }

    @After
    public void teardown() {
        Logger.getLogger(Config.class.getName()).removeHandler(handler);
    }

    @Test
    public void disabledReportsLogAMessage() {
        setSystemProperty("jgiven.report.enabled", "false");

        JGivenLogHandler.resetEvents();
        Config.logReportEnabled();

        assertThat(JGivenLogHandler.containsLoggingEvent("Please note that the report generation is turned off.",
                                                                Level.INFO)).isTrue();
    }

    @Test
    public void enabledReportsDontLogAMessage() {
        setSystemProperty("jgiven.report.enabled", "true");

        JGivenLogHandler.resetEvents();
        Config.logReportEnabled();

        assertThat(JGivenLogHandler.containsLoggingEvent("Please note that the report generation is turned off.",
                                                            Level.INFO)).isFalse();
    }

    @Test
    public void dryRunEnabledLogsAMessage() {
        setSystemProperty("jgiven.report.dry-run", "true");

        JGivenLogHandler.resetEvents();
        Config.logDryRunEnabled();

        assertThat(JGivenLogHandler.containsLoggingEvent("Dry Run enabled.",
                Level.INFO)).isTrue();
    }

    @Test
    public void dryRunDisabledDoesntLogAMessage() {
        setSystemProperty("jgiven.report.dry-run", "false");

        JGivenLogHandler.resetEvents();
        Config.logDryRunEnabled();

        assertThat(JGivenLogHandler.containsLoggingEvent("Dry Run enabled.",
                Level.INFO)).isFalse();
    }

    @Test
    public void configValuesHaveDefaults() throws Exception {
        Config underTest = createNewTestInstance();

        assertThat(underTest.isReportEnabled()).isTrue();
        assertThat(underTest.getReportDir()).get().extracting(File::getPath).isEqualTo("jgiven-reports");
        assertThat(underTest.textColorEnabled()).extracting(Enum::name).isEqualTo("AUTO");
        assertThat(underTest.filterStackTrace()).isTrue();
    }

    @Test
    public void configFileValuesAreRecognized() throws Exception {
        File reportPath = temporaryFolder.newFolder();
        jgivenConfig.write("jgiven.report.enabled=false\n");
        jgivenConfig.write("jgiven.report.dir="
            + reportPath.getAbsolutePath().replace("\\", "/") + "\n");
        jgivenConfig.write("jgiven.report.text=false\n");
        jgivenConfig.write("jgiven.report.text.color=true\n");
        jgivenConfig.write("jgiven.report.filterStackTrace=false\n");

        Config underTest = createNewTestInstance();

        assertThat(underTest.isReportEnabled()).isFalse();
        assertThat(underTest.getReportDir()).contains(reportPath);
        assertThat(underTest.textReport()).isFalse();
        assertThat(underTest.textColorEnabled()).isEqualTo(ConfigValue.TRUE);
        assertThat(underTest.filterStackTrace()).isFalse();
    }

    @Test
    public void testCommandLinePropertiesTakePrecedenceOverConfigFile() throws Exception {
        jgivenConfig.write("jgiven.report.enabled=false\n");
        setSystemProperty("jgiven.report.enabled", "true");

        Config underTest = createNewTestInstance();

        assertThat(underTest.isReportEnabled()).isTrue();
    }

    @After
    public void cleanupSystemProperties() {
        systemPropertiesBackup.entrySet()
            .stream()
            .peek(entry -> System.clearProperty(entry.getKey()))
            .filter(entry -> entry.getValue() != null)
            .forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    private static Config createNewTestInstance() throws Exception {
        Constructor<Config> constructor = Config.class.getDeclaredConstructor();
        try {
            constructor.setAccessible(true);
            return constructor.newInstance();
        } finally {
            constructor.setAccessible(false);
        }
    }

    private void setSystemProperty(String key, String value) {
        String originalValue = System.getProperty(key);
        if (!systemPropertiesBackup.containsKey(key)) {
            systemPropertiesBackup.put(key, originalValue);
        }
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }
}
