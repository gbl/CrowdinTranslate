CrowdinTranslate - a library to add CrowdIn Internationalization to your mods
=============================================================================

CrowdinTranslate is a library that's intended to make Internationalization as 
easy as possible in your mods. The jar file is, at the same time:

- A main program which you can use to easily download translations from
CrowdIn, and distribute these translations to the correct file names in the
correct folder

- A Fabric library mod which you can use in your own mods which downloads
updated translations, and makes them available in a resource pack, to your
users, so you don't have to publish a new version of your mod, and people
don't need to redownload, when new translations appear

- A Java library that you can just shade in from your Forge mods, with the same
functionality as for the Fabric mod



## Getting started

Create a CrowdIn project, (if possible, use the same project name as your mod id).
Upload your en_us.json, get it translated, build the project. More detailled 
info below.

## Manual usage:

Run `java -jar crowdintranslate-<version>.jar <projectname>` from the main
mod directory to download translations and distribute them
to `src/main/resources/assets/<projectname>/lang/`.

If you weren't able to use your modid for your crowdin project, run
`java -jar crowdintranslate-<version>.jar <projectname> <modid>` instead.

## Automatic usage:

(this needs some more info to use with Forge, and how to get the version number)

Add this to your build.gradle:

```
repositories {
	maven {
		url = "https://minecraft.guntram.de/maven/"
	}
}
dependencies {
    modImplementation "de.guntram.mcmod:crowdin-translate:1.0"
    include "de.guntram.mcmod:crowdin-translate:1.0"
}
```

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

This will download the translations from `https://crowdin.com/project/projectname`
to `assets/modid/lang`.



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
