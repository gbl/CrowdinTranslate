package de.guntram.mcmod.crowdintranslate;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CrowdinTranslate extends Thread {
    
    private static final Map<String, String> mcCodetoCrowdinCode;
    /* The directory to download to. This is used in the mod; main will overwrite this.  */
    private static String rootDir = "resourcepacks/ModTranslations";
    private static boolean thisIsAMod = true;
    private static boolean extractionDone = false;
    
    static {
        mcCodetoCrowdinCode = new HashMap<>();

        add("cs_cz", "cs");
        add("de_de", "de");
        add("el_gr", "el");
        add("es_ar", "es-ES");
        add("es_cl", "es-ES");
        add("es_ec", "es-ES");
        add("es_es", "es-ES");
        add("es_mx", "es-ES");
        add("es_uy", "es-ES");
        add("es_ve", "es-ES");
        add("fi_fi", "fi");
        add("fr_fr", "fr");
        add("it_it", "it");
        add("ja_jp", "ja");
        add("ko_kr", "ko");
        add("nl_nl", "nl");
        add("no_no", "no");
        add("pl_pl", "pl");
        add("pt_br", "pt-PT");
        add("pt_pt", "pt-PT");
        add("ro_ro", "ro");
        add("ru_ru", "ru");
        add("sr_sp", "sr");
        add("sv_se", "sv-SE");
        add("tr_tr", "tr");
        add("zh_cn", "zh-CN");
        add("zh_tw", "zh-TW");
    }
    
    private static void add(String mc, String ci) {
        mcCodetoCrowdinCode.put(mc, ci);
    }
    
    public static void downloadTranslations(String projectName) {
        downloadTranslations(projectName, projectName);
    }
        
    public static void downloadTranslations(String crowdinProjectName, String minecraftProjectName) {
        downloadTranslations(crowdinProjectName, minecraftProjectName, false);
    }
        
    public static void downloadTranslations(String crowdinProjectName, String minecraftProjectName, boolean verbose) {
        
        // To make sure we won't run in trouble should Fabric ever initialize
        // several mods at once, do this synchronized. But don't have mods
        // unneccesarily wait for others if they won't extract anyway.

        if (!extractionDone) {
            synchronized(mcCodetoCrowdinCode) {
                if (!extractionDone && thisIsAMod) {
                    extractionDone = true;
                    new File(rootDir).mkdirs();
                    File icon = new File(rootDir, "pack.png");
                    if (!icon.exists()) {
                        extractFromSelf("pack.png");
                    }
                    if (!(new File(rootDir, "disable.mcmeta")).exists()) {
                        extractFromSelf("pack.mcmeta");
                    }
                    extractFromSelf("README.txt");
                }
                extractionDone = true;
            }
        }

        CrowdinTranslate runner = new CrowdinTranslate(crowdinProjectName, minecraftProjectName);
        if (true || verbose) {
            runner.setVerbose();
        }
        runner.start();
    }
    
    private static void extractFromSelf(String name) {
        InputStream is = null;
        ZipInputStream zis = null;
        CodeSource src = CrowdinTranslate.class.getProtectionDomain().getCodeSource();
        URL jar = src.getLocation();
        if (src != null) {
            try {
                is = jar.openStream();
                zis = new ZipInputStream(is);
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equalsIgnoreCase(name)) {
                        extractZipEntry(zis, name);
                        break;
                    }
                }
                zis.close();
            } catch (IOException ex) {
            } finally {
                if (zis != null) {
                    forceClose(zis);
                }
                if (is != null) {
                    forceClose(is);
                }
            }
        }
    }

    private static void extractZipEntry(ZipInputStream zipStream, String outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(new File(rootDir, outputFile))) {
            byte[] buf=new byte[16384];
            int length;
            while ((length=zipStream.read(buf, 0, buf.length))>=0) {
                fos.write(buf, 0, length);
            }
        } catch (IOException ex) {
            throw ex;
        }
    }
    
    private static void forceClose(Closeable c) {
        try {
            c.close();
        } catch (IOException ex) {
        }
    }
    
    private String crowdinProjectName, minecraftProjectName;
    private boolean verbose;
    
    private CrowdinTranslate(String crowdinProjectName, String minecraftProjectName) {
        this.crowdinProjectName = crowdinProjectName;
        this.minecraftProjectName = minecraftProjectName;
        verbose = false;
    }
    
    private void setVerbose() {
        verbose = true;
    }
    
    @Override
    public void run() {
        Map<String, byte[]> translations;
        try {
            translations = getCrowdinTranslations(crowdinProjectName);
        } catch (IOException ex) {
            System.err.println("Exception when downloading translations");
            ex.printStackTrace(System.err);
            return;
        }
        
        String assetDir = rootDir+File.separatorChar+"assets"+File.separatorChar
                                +minecraftProjectName+File.separatorChar+"lang";
        new File(assetDir).mkdirs();

        for (Map.Entry<String, String> entry: mcCodetoCrowdinCode.entrySet()) {
            byte[] buffer = translations.get(entry.getValue());
            if (buffer != null) {
                String filePath = assetDir+File.separatorChar+entry.getKey()+".json";
                if (verbose) {
                    System.out.println("writing "+buffer.length+" bytes from \""+entry.getValue()+"\" to MC file "+filePath);
                }
                saveBufferToJsonFile(buffer, filePath);
            }
        }
    }
        
    private Map<String, byte[]> getCrowdinTranslations(String projectName) throws IOException {    
        ZipInputStream zis = null;
        Pattern pattern = Pattern.compile("^([a-z]{2}(-[A-Z]{2})?)/(.+\\.json)$");
        Map<String, byte[]> zipContents = new HashMap<>();

        try {
            URL url = new URL("https://crowdin.com/backend/download/project/"+projectName+".zip");
            zis = new ZipInputStream(url.openStream());
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String path = entry.getName();
                Matcher matcher = pattern.matcher(path);
                if (matcher.matches()) {
                    String crowdinLang = matcher.group(1);
                    String origFileName = matcher.group(3);
                    if (verbose) {
                        System.out.println("Found translation \""+crowdinLang+"\" for file "+origFileName);
                    }
                    if (entry.getSize() > 10_000_000) {
                        // This is mainly a guard against broken files that
                        // could exhaust our memory.
                        throw new IOException("file too large: "+entry.getName()+": "+entry.getSize());
                    }
                    byte[] zipFileContent = getZipStreamContent(zis, (int) entry.getSize());
                    if (zipContents.containsKey(crowdinLang)) {
                        System.err.println("More than one file for "+crowdinLang+", ignoring "+origFileName);
                        continue;
                    }
                    zipContents.put(matcher.group(1), zipFileContent);
                }
            }
        } catch (IOException ex) {
            if (zis != null) {
                tryClose(zis);
            }
            throw ex;
        }
        return zipContents;
    }
    
    private byte[] getZipStreamContent(InputStream is, int size) throws IOException {
        byte[] buf = new byte[size];
        int toRead = size;
        int totalRead = 0, readNow;
        
        while (toRead > 0) {
            if ((readNow = is.read(buf, totalRead, toRead)) <= 0) {
                throw new IOException("premature end of stream");
            };
            totalRead += readNow;
            toRead -= readNow;
        }
        return buf;
    }
    
    private void tryClose(Closeable o) {
        try {
            o.close();
        } catch (Exception ex) {
        }
    }
    
    private void saveBufferToJsonFile(byte[] buffer, String filename) {
        
        File file = new File(filename);
        try (FileOutputStream stream = new FileOutputStream(filename)) {
            stream.write(buffer);
        } catch (IOException ex) {
            System.err.println("failed to write "+filename);
        }
    }

    public static void main(String[] args) {
        boolean verbose = false;
        int startArg = 0;
        
        rootDir = ".";
        thisIsAMod = false;
        if (args.length > 0 && args[0].equals("-v")) {
            verbose = true;
            ++startArg;
        }
        if (args.length == startArg+1) {
            downloadTranslations(args[startArg], args[startArg], verbose);
        } else if (args.length == startArg+2) {
            downloadTranslations(args[startArg], args[startArg+1], verbose);
        }
        else {
            System.out.println("Usage: CrowdinTranslate [-v] crowdin_project_name [minecraft_project_name]");
            System.out.println("\t-v enables verbose logging");
            System.out.println("\tGet the translations from crowdin.com/project/name\n\tand write them to assets/project/lang");
            System.out.println("\tThe second parameter is only neccesary if the crowdin project name\n\tdoesn't match the minecraft project name");
        }
    }
}
