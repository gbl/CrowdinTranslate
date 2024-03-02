/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.crowdintranslate.GradlePlugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

/**
 *
 * @author gbl
 */
public class CrowdinTranslatePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        final CrowdinTranslateParameters parameters = project.getExtensions()
                .create("crowdintranslate", CrowdinTranslateParameters.class);
        parameters.getVerbose().convention(false);

        project.getTasks().register("downloadTranslations", DownloadTask.class).configure(task -> {
            task.getCrowdinProjectName().set(parameters.getCrowdinProjectName());
            task.getMinecraftProjectName().set(parameters.getMinecraftProjectName());
            task.getJsonSourceName().set(parameters.getJsonSourceName());
            task.getVerbose().set(parameters.getVerbose());
        });
    }
}
