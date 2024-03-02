package de.guntram.mcmod.crowdintranslate.GradlePlugin;

import de.guntram.mcmod.crowdintranslate.CrowdinTranslate;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class DownloadTask extends DefaultTask {
    @TaskAction
    public void action() {
        CrowdinTranslateParameters parms = CrowdinTranslatePlugin.parameters;
        if (parms.getCrowdinProjectName() == null) {
            System.err.println("No crowdin project name given, nothing downloaded");
            return;
        }
        String[] args = new String[ (parms.getVerbose().get() ? 4 : 3) ];
        int argc = 0;
        if (parms.getVerbose().get()) {
            args[argc++] = "-v";
        }
        String cpn = parms.getCrowdinProjectName().get();
        String mpn = parms.getMinecraftProjectName().get();
        args[argc++] = cpn;
        args[argc++] = (mpn == null ? cpn : mpn);
        args[argc++] = parms.getJsonSourceName().get();

        CrowdinTranslate.main(args);
        this.setDidWork(true);
    }
}
