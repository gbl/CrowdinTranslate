/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.crowdintranslate.GradlePlugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 *
 * @author gbl
 */
public class CrowdinTranslatePlugin implements Plugin<Project> {

    public static CrowdinTranslateParameters parameters;

    @Override
    public void apply(Project project) {
        
        parameters = project.getExtensions()
                .create("crowdintranslate", CrowdinTranslateParameters.class);
        project.getTasks().create("downloadTranslations", DownloadTask.class);
    }
}
