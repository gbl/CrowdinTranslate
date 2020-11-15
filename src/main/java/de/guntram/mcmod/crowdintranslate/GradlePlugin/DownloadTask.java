package de.guntram.mcmod.crowdintranslate.GradlePlugin;

import de.guntram.mcmod.crowdintranslate.CrowdinTranslate;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.tasks.TaskAction;

public class DownloadTask extends AbstractTask {
    @TaskAction
    public void action() {
        CrowdinTranslateParameters parms = CrowdinTranslatePlugin.parameters;
        if (parms.getCrowdinProjectName() == null) {
            System.err.println("No crowdin project name given, nothing downloaded");
            return;
        }
        String[] args = new String[ (parms.getVerbose() ? 4 : 3) ];
        int argc = 0;
        if (parms.getVerbose()) {
            args[argc++] = "-v";
        }
        String cpn = parms.getCrowdinProjectName();
        String mpn = parms.getMinecraftProjectName();
        args[argc++] = cpn;
        args[argc++] = (mpn == null ? cpn : mpn);
        args[argc++] = parms.getJsonSourceName();

        CrowdinTranslate.main(args);
        this.setDidWork(true);
    }
}
