package de.guntram.mcmod.crowdintranslate.GradlePlugin;

import de.guntram.mcmod.crowdintranslate.CrowdinTranslate;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

public abstract class DownloadTask extends DefaultTask {
    @Input
    @Optional
    public abstract Property<String> getCrowdinProjectName();

    @Input
    public abstract Property<String> getMinecraftProjectName();

    @Input
    public abstract Property<String> getJsonSourceName();

    @Input
    public abstract Property<Boolean> getVerbose();

    @TaskAction
    public void action() {
        if (!getCrowdinProjectName().isPresent()) {
            System.err.println("No crowdin project name given, nothing downloaded");
            return;
        }
        String[] args = new String[ (getVerbose().get() ? 4 : 3) ];
        int argc = 0;
        if (getVerbose().get()) {
            args[argc++] = "-v";
        }
        String cpn = getCrowdinProjectName().get();
        String mpn = getMinecraftProjectName().get();
        args[argc++] = cpn;
        args[argc++] = (mpn == null ? cpn : mpn);
        args[argc++] = getJsonSourceName().get();

        CrowdinTranslate.main(args);
        this.setDidWork(true);
    }
}
