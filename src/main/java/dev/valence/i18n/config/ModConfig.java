package dev.valence.i18n.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "valence_i18n");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "config.json");

    // 字体选项："auto" (跟随语言自动), "default_tw" (花嫁), "default_cn" (MYuppy), "vanilla", 或外部字体文件名
    public String selectedFont = "auto";
    public double fontScale = 1.0;
    public boolean autoExportEnTemplate = true;

    private static ModConfig INSTANCE;

    public static ModConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static File getConfigDir() {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
        return CONFIG_DIR;
    }

    public static File getLangDir() {
        File dir = new File(getConfigDir(), "lang");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getFontsDir() {
        File dir = new File(getConfigDir(), "fonts");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static ModConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfig cfg = GSON.fromJson(reader, ModConfig.class);
                if (cfg != null) {
                    return cfg;
                }
            } catch (Exception e) {
                System.err.println("[Valence-i18n] Failed to load config, resetting to default: " + e.getMessage());
            }
        }
        ModConfig cfg = new ModConfig();
        cfg.save();
        return cfg;
    }

    public void save() {
        try {
            if (!CONFIG_DIR.exists()) {
                CONFIG_DIR.mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.err.println("[Valence-i18n] Failed to save config: " + e.getMessage());
        }
    }

    public void resetToDefault() {
        this.selectedFont = "auto";
        this.fontScale = 1.0;
        this.autoExportEnTemplate = true;
        save();
    }
}
