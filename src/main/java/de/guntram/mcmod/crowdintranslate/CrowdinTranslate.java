package de.guntram.mcmod.crowdintranslate;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CrowdinTranslate extends Thread {
    
    private static final Map<String, String> mcCodetoCrowdinCode;
    /* The directory to download to. This is used in the mod; main will overwrite this.  */
    private static String rootDir = "ModTranslations";
    private static boolean thisIsAMod = true;
    private static final Set<String> registeredMods;
    
    static {
        mcCodetoCrowdinCode = new HashMap<>();
        registeredMods = new HashSet<>();

        add("af_za", "af");
        add("ar_sa", "ar");
        add("ast_es", "ast");
        add("az_az", "az");
        add("ba_ru", "ba");
        //add("bar", "bar");			// Bavaria
        add("be_by", "be");
        add("bg_bg", "bg");
        add("br_fr", "br-FR");
        //add("brb", "brb");			// Brabantian
        add("bs_ba", "bs");
        add("ca_es", "ca");
        add("cs_cz", "cs");
        add("cy_gb", "cy");
        add("da_dk", "da");
        add("de_at", "de-AT");
        add("de_ch", "de-CH");
        add("de_de", "de");
        add("el_gr", "el");
        add("en_au", "de-AT");
        add("en_ca", "en-CA");
        add("en_gb", "en-GB");
        add("en_nz", "en-NZ");
        add("en_pt", "en-PT");
        add("en_ud", "en-UD");
        add("en_us", "en-US");
        //add("enp", "enp");			// Anglish
        //add("enws", "enws");			// Shakespearean English
        add("eo_uy", "eo");
        add("es_ar", "es-AR");
        add("es_cl", "es-CL");
        add("es_ec", "es-EC");
        add("es_es", "es-ES");
        add("es_mx", "es-MX");
        add("es_uy", "es-UY");
        add("es_ve", "es-VE");
        //add("esan", "esan");			// Andalusian
        add("et_ee", "et");
        add("eu_es", "eu");
        add("fa_ir", "fa");
        add("fi_fi", "fi");
        add("fil_ph", "fil");
        add("fo_fo", "fo");
        add("fr_ca", "fr-CA");
        add("fr_fr", "fr");
        add("fra_de", "fra-DE");
        add("fy_nl", "fy-NL");
        add("ga_ie", "ga-IE");
        add("gd_gb", "gd");
        add("gl_es", "gl");
        add("haw_us", "haw");
        add("he_il", "he");
        add("hi_in", "hi");
        add("hr_hr", "hr");
        add("hu_hu", "hu");
        add("hy_am", "hy-AM");
        add("id_id", "id");
        add("ig_ng", "ig");
        add("io_en", "ido");
        add("is_is", "is");
        //add("isv", "isv");			// Interslavic
        add("it_it", "it");
        add("ja_jp", "ja");
        add("jbo_en", "jbo");
        add("ka_ge", "ka");
        add("kk_kz", "kk");
        add("kn_in", "kn");
        add("ko_kr", "ko");
        //add("ksh", "ksh");			// Ripuarian
        add("kw_gb", "kw");
        add("la_la", "la-LA");
        add("lb_lu", "lb");
        add("li_li", "li");
        add("lol_us", "lol");
        add("lt_lt", "lt");
        add("lv_lv", "lv");
        //add("lzh", "lzh");			// Classical Chinese
        add("mk_mk", "mk");
        add("mn_mn", "mn");
        add("ms_my", "ms");
        add("mt_mt", "mt");
        add("nds_de", "nds");
        add("nl_be", "nl-BE");
        add("nl_nl", "nl");
        add("nn_no", "nn-NO");
        add("no_no‌", "no");
        add("oc_fr", "oc");
        //add("ovd", "ovd");			// Elfdalian
        add("pl_pl", "pl");
        add("pt_br", "pt-BR");
        add("pt_pt", "pt-PT");
        add("qya_aa", "qya-AA");
        add("ro_ro", "ro");
        //add("rpr", "rpr");			// Russian (pre-revolutionary)
        add("ru_ru", "ru");
        add("se_no", "se");
        add("sk_sk", "sk");
        add("sl_si", "sl");
        add("so_so", "so");
        add("sq_al", "sq");
        add("sr_sp", "sr");
        add("sv_se", "sv-SE");
        //add("sxu", "sxu");			// Upper Saxon German
        //add("szl", "szl");			// Silesian
        add("ta_in", "ta");
        add("th_th", "th");
        add("tl_ph", "tl");
        add("tlh_aa", "tlh-AA");
        add("tr_tr", "tr");
        add("tt_ru", "tt-RU");
        add("uk_ua", "uk");
        add("val_es", "val-ES");
        add("vec_it", "vec");
        add("vi_vn", "vi");
        add("yi_de", "yi");
        add("yo_ng", "yo");
        add("zh_cn", "zh-CN");
        add("zh_hk", "zh-HK");
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
        downloadTranslations(crowdinProjectName, minecraftProjectName, null, verbose);
    }

    public static void downloadTranslations(String crowdinProjectName, String minecraftProjectName, String sourcefileOverride) {
        downloadTranslations(crowdinProjectName, minecraftProjectName, sourcefileOverride, false);
    }

    public static void downloadTranslations(String crowdinProjectName, String minecraftProjectName, String sourcefileOverride, boolean verbose) {
        
        registeredMods.add(minecraftProjectName);
        if (thisIsAMod && projectDownloadedRecently(minecraftProjectName)) {
            return;
        }
        CrowdinTranslate runner = new CrowdinTranslate(crowdinProjectName, minecraftProjectName);
        if (verbose) {
            runner.setVerbose();
        }
        if (sourcefileOverride != null) {
            runner.setSourceFileOverride(sourcefileOverride);
        }
        runner.start();
        if (!thisIsAMod) {
            try {
                runner.join(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void forceClose(Closeable c) {
        try {
            c.close();
        } catch (IOException ex) {
        }
    }
    
    public static Set<String> registeredMods() {
        return registeredMods;
    }
    
    public static String getRootDir() {
        return rootDir;
    }
    
    private String crowdinProjectName, minecraftProjectName;
    private Optional<String> sourceFileOverride = Optional.empty();
    private boolean verbose;
    
    private CrowdinTranslate(String crowdinProjectName, String minecraftProjectName) {
        this.crowdinProjectName = crowdinProjectName;
        this.minecraftProjectName = minecraftProjectName;
        verbose = false;
    }
    
    private void setVerbose() {
        verbose = true;
    }
    
    private void setSourceFileOverride(String name) {
        if (name == null) {
            sourceFileOverride = Optional.empty();
        } else if (name.toLowerCase().endsWith((".json"))) {
            sourceFileOverride = Optional.of(name);
        } else {
            sourceFileOverride = Optional.of(name+".json");
        }
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
        if (thisIsAMod) {
            markDownloadedNow(minecraftProjectName);
        }
    }
        
    private Map<String, byte[]> getCrowdinTranslations(String projectName) throws IOException {    
        ZipInputStream zis = null;
        Pattern pattern = Pattern.compile("^([a-z]{2}(-[A-Z]{2})?)/(.+\\.json)$");
        Map<String, byte[]> zipContents = new HashMap<>();

        try {
            URL url = new URL("https://crowdin.com/backend/download/project/"+projectName+".zip");
            if (verbose) {
                System.out.println("Trying to download "+url);
            }
            zis = new ZipInputStream(url.openStream());
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String path = entry.getName();
                Matcher matcher = pattern.matcher(path);
                if (matcher.matches()) {
                    String crowdinLang = matcher.group(1);
                    String origFileName = matcher.group(3);
                    if (sourceFileOverride.isPresent() && !sourceFileOverride.get().equals(origFileName)) {
                        if (verbose) {
                            System.out.println("Ignoring "+path+", we're looking for "+sourceFileOverride.get());
                        }
                        continue;
                    }
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
                forceClose(zis);
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
    
    private void saveBufferToJsonFile(byte[] buffer, String filename) {
        
        File file = new File(filename);
        try (FileOutputStream stream = new FileOutputStream(filename)) {
            stream.write(buffer);
        } catch (IOException ex) {
            System.err.println("failed to write "+filename);
            System.err.println("absolute path is "+file.getAbsolutePath());
            ex.printStackTrace(System.err);
        }
    }
    
    private static boolean projectDownloadedRecently(String projectName) {
        File file =  new File(rootDir, projectName+".timestamp");
        if (file.exists() && file.lastModified() > System.currentTimeMillis() - 86400 * 3000) {
            return true;
        }
        return false;
    }
    
    private static void markDownloadedNow(String projectName) {
        File file =  new File(rootDir, projectName+".timestamp");
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException ex) {
            // bad luck, we'll just check again next time.
        }
        file.setLastModified(System.currentTimeMillis());
    }

    public static void main(String[] args) {
        boolean verbose = false;
        int startArg = 0;
        
        rootDir = "src/main/resources";
        thisIsAMod = false;
        if (args.length > 0 && args[0].equals("-v")) {
            verbose = true;
            ++startArg;
        }
        if (args.length == startArg+1) {
            downloadTranslations(args[startArg], args[startArg], verbose);
        } else if (args.length == startArg+2) {
            downloadTranslations(args[startArg], args[startArg+1], verbose);
        } else if (args.length == startArg+3) {
            downloadTranslations(args[startArg], args[startArg+1], args[startArg+2], verbose);
        }
        else {
            System.out.println("Usage: CrowdinTranslate [-v] crowdin_project_name [minecraft_project_name]");
            System.out.println("\t-v enables verbose logging");
            System.out.println("\tGet the translations from crowdin.com/project/name\n\tand write them to assets/project/lang");
            System.out.println("\tThe second parameter is only neccesary if the crowdin project name\n\tdoesn't match the minecraft project name");
        }
    }
}
