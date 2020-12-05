CrowdinTranslate - a library to add CrowdIn Internationalization to your mods
=============================================================================

CrowdinTranslate is a library that's intended to make Internationalization as 
easy as possible in your mods. The jar file is, at the same time:

- A main program which you can use to easily download translations from
CrowdIn, and distribute these translations to the correct file names in the
correct folder

- A gradle plugin to automate getting translations from your build process

- A Fabric library mod which you can use in your own mods which downloads
updated translations, and makes them available in a resource pack, to your
users, so you don't have to publish a new version of your mod, and people
don't need to redownload, when new translations appear

- A Java library that you can just shade in from your Forge mods, with the same
functionality as for the Fabric mod (yet to be implemented ...)



## Getting started

Create a CrowdIn project, (if possible, use the same project name as your mod id).
Upload your en_us.json, get it translated, build the project. More detailed 
info below.

## Manual usage:

Run `java -jar crowdintranslate-<version>.jar <projectname>` from the main
mod directory to download translations and distribute them
to `src/main/resources/assets/<projectname>/lang/`.

If you weren't able to use your modid for your crowdin project, run
`java -jar crowdintranslate-<version>.jar <projectname> <modid>` instead.

## Automatic usage:

In your build.gradle, at the very top (before `plugins`), add this:

```
buildscript {
    dependencies {
        classpath 'de.guntram.mcmod:crowdin-translate:1.3+1.16'
    }
    repositories {
        maven {
            name = 'CrowdinTranslate source'
            url = "https://minecraft.guntram.de/maven/"
        }
    }
}
```

(note that you can use this no matter which Minecraft version you're compiling
for, even if the maven version number says 1.16).

Then, somewhere later (after plugins) add:

```
apply plugin: 'de.guntram.mcmod.crowdin-translate'
crowdintranslate.crowdinProjectName = '<modid>'
crowdintranslate.minecraftProjectName = '<modid>'
crowdintranslate.verbose = false
```

You can omit the minecraftProjectName if the ids are the same, and you can
set verbose to true to see more about what's happening in the build process.

This will give you a new gradle task: `gradle downloadTranslations` fetches 
all translations to your src/main/resources/assets/<modid>/lang directory.

To do this automatically when you build the project, add something like this
to the end of your build.gradle:

```
build {
        dependsOn downloadTranslations
}
```

## Have your mod automatically check for new translations

That way your users can get new translations automatically, without
you re-publishing your mod, and them having to re-download it.

Add this to your build.gradle:

```
repositories {
	maven {
		url = "https://minecraft.guntram.de/maven/"
	}
}
dependencies {
    modImplementation "de.guntram.mcmod:crowdin-translate:<version>"
    include "de.guntram.mcmod:crowdin-translate:<version>"
}
```

where `version` is currently either `1.3+1.16` or `1.3+1.17-alpha20w49a`.
(The `1.3+1.16` version actually works for all versions from 1.15.2 to 20w48a,
20w49a introduced an incompatible change.)

and this to your ClientModInitializer:

```
CrowdinTranslate.downloadTranslations("modid");
```

for example

```
public class MyModClass implements ClientModInitializer 
{
    static public final String MODID="modid";
    @Override
    public void onInitializeClient() {
        CrowdinTranslate.downloadTranslations(MODID);
    }
}
```

If your CrowdIn project name does not match your Minecraft Mod ID, you need
to use the two parameter form with CrowdIn name first, and mod id second:

```
CrowdinTranslate.downloadTranslations("projectname", "modid");
```

This will download the translations from
`https://crowdin.com/project/projectname`
to `assets/modid/lang`.

### What if I have the translation files for several mods in the same crowdin project?

Since version 1.3, you can override the translation source name that
crowdin-translate checks for. So, if your mods are named foo, bar, and baz,
you can have one single crowdin project that has them all, and have file names
`foo.json`, `bar.json` and `thisisnotbaz.json` for your source.

Assuming your crowdin project name is `allmymods`,
adjust the above use cases like this:

* manual usage:
```
java -jar crowdintranslate-<version>.jar allmymods foo foo
java -jar crowdintranslate-<version>.jar allmymods bar bar
java -jar crowdintranslate-<version>.jar allmymods baz thisisnotbaz
```

* usage in gradle: add a 'jsonSourceName' parameter

```
crowdintranslate.jsonSourceName = 'thisisnotbaz'
```

* usage in your `ClientModInitializer`: use the 3 argument call:

```
CrowdinTranslate.downloadTranslations("allmymods", "baz", "thisisnotbaz");
```

### Getting started
(this needs some redoing)
- Create an account on CrowdIn (https://crowdin.com)
- Optional but recommended: apply for a open source membership so you can start multiple projects, for free
- Create a project. This will ask for a project name, and a project address. 
If possible, select your address so the identifier matches your mod id
(the mod `foobar` should have `https://crowdin.com/project/foobar`).
- Switch the source language from English to English, United States. This is not
100% neccesary, but will make things easier, especially if your original json
file is named `en_us.json`.
- Add the target languages you want to use. (In a future version of CrowdinTranslate,
there will be an easy way to consistently set the languages for a collection 
of mods)
- Once your project is created, upload your en_us.json. Then, do some translations,
or get people to do that for you.
