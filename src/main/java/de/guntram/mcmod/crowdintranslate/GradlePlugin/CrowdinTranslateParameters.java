package de.guntram.mcmod.crowdintranslate.GradlePlugin;

import org.gradle.api.provider.Property;

public abstract class CrowdinTranslateParameters {
    public void setCrowdinProjectname(String s) {
        getCrowdinProjectName().set(s);
    }

    public abstract Property<String> getCrowdinProjectName();

    public abstract Property<String> getMinecraftProjectName();

    public abstract Property<String> getJsonSourceName();

    public abstract Property<Boolean> getVerbose();
}
