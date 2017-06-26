package org.alg.elasticsearch.plugin.topk;

import org.alg.elasticsearch.search.aggregations.topk.TopKBuilder;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

import java.io.IOException;
import java.nio.file.Path;

public class TopKPluginConfiguration {

    private final Settings settings, customSettings;

    public static final Setting<String> TEST_SETTING =
            new Setting<String>("test", "default_value",
                    (value) -> value, Setting.Property.Dynamic);

    public TopKPluginConfiguration(Environment env) {
        // The directory part of the location matches the artifactId of this plugin
        Path path = env.configFile().resolve(TopKBuilder.ARTIFACT_ID + "/settings.yml");
        try {
            customSettings = Settings.builder().loadFromPath(path).build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load settings, giving up", e);
        }

        settings = env.settings();

        // asserts for tests
        assert customSettings != null;
        assert TEST_SETTING.get(customSettings) != null;
    }

    public String getTestConfig() {
        return TEST_SETTING.get(customSettings);
    }

    public Settings getSettings() {
        return settings;
    }
}