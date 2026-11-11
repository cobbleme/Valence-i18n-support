package dev.valence.i18n.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LangManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void init() {
        ensureDirectoriesAndReadme();
        if (ModConfig.get().autoExportEnTemplate) {
            checkAndExportEnTemplate(false);
        }
    }

    private static void ensureDirectoriesAndReadme() {
        File langDir = ModConfig.getLangDir();
        File readme = new File(langDir, "README.txt");
        if (!readme.exists()) {
            try (FileWriter writer = new FileWriter(readme, StandardCharsets.UTF_8)) {
                writer.write("====================================================\n" +
                        "  Valence i18n 自定义外置语言包说明\n" +
                        "====================================================\n\n" +
                        "1. 模板参考：\n" +
                        "   本目录下的 'en_us.json' 是从原版 Valence 完整提取并合并的英文词条模板。\n" +
                        "   请不要直接在 en_us.json 上修改（避免下次被覆盖）。\n\n" +
                        "2. 自定义翻译：\n" +
                        "   若需要自定义修改某个语言，复制一份并重命名为对应的 Minecraft 语言代码，例如：\n" +
                        "     - 简体中文: zh_cn.json\n" +
                        "     - 繁体中文: zh_tw.json\n" +
                        "     - 日文:     ja_jp.json\n" +
                        "   放入本目录后，游戏在切换到该语言时会优先读取本目录的文件！\n\n" +
                        "3. 即时生效：\n" +
                        "   修改完 JSON 文件后，在游戏中按下 F3+T 重新加载资源，或在 Valence 字体设置界面点击「即时重载」即可。\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File fontsDir = ModConfig.getFontsDir();
        File fontReadme = new File(fontsDir, "README.txt");
        if (!fontReadme.exists()) {
            try (FileWriter writer = new FileWriter(fontReadme, StandardCharsets.UTF_8)) {
                writer.write("====================================================\n" +
                        "  Valence 自定义外置字体目录\n" +
                        "====================================================\n\n" +
                        "1. 支持格式：\n" +
                        "   将任意标准的 TrueType 字体 (.ttf) 或 OpenType 字体 (.otf) 复制到此文件夹中。\n\n" +
                        "2. 游戏内选择：\n" +
                        "   在游戏内打开【Valence 语言与字体个性化设置】界面，下拉框会自动列出本目录中的所有字体。\n\n" +
                        "3. 安全机制：\n" +
                        "   如果外部字体损坏或发生兼容性异常，模组会自动安全回滚至预设字体，确保游戏不会闪退。\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkAndExportEnTemplate(boolean force) {
        File target = new File(ModConfig.getLangDir(), "en_us.json");
        if (target.exists() && !force) {
            return false;
        }

        try {
            JsonObject combined = new JsonObject();
            combined.addProperty("_comment", "Valence 完整参考英文词条模板 (由 valence_i18n 自动导出)");

            // 从本模组内置资源中读取原版的完整 en_us
            loadResourceIntoJson("/assets/valence/lang/en_us.json", combined);
            loadResourceIntoJson("/assets/valence_cord/lang/en_us.json", combined);

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8)) {
                GSON.toJson(combined, writer);
            }
            System.out.println("[Valence-i18n] Exported reference en_us.json to " + target.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.err.println("[Valence-i18n] Failed to export en_us.json: " + e.getMessage());
            return false;
        }
    }

    private static void loadResourceIntoJson(String resourcePath, JsonObject target) {
        try (InputStream in = LangManager.class.getResourceAsStream(resourcePath)) {
            if (in != null) {
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                    for (String key : obj.keySet()) {
                        target.add(key, obj.get(key));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Valence-i18n] Could not read internal resource " + resourcePath + ": " + e.getMessage());
        }
    }

    /**
     * 获取外置或内置合并后的指定命名空间和语言文件的输入流
     */
    public static InputStream getLanguageStream(String namespace, String langCode) {
        // 1. 先检查 config/valence_i18n/lang/<langCode>.json 是否存在外部覆盖
        File externalFile = new File(ModConfig.getLangDir(), langCode + ".json");
        JsonObject externalData = null;
        if (externalFile.exists() && externalFile.isFile()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(externalFile), StandardCharsets.UTF_8)) {
                externalData = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                System.err.println("[Valence-i18n] Error reading external lang file " + externalFile.getName() + ": " + e.getMessage());
            }
        }

        // 2. 加载内置的对应语言文件
        String internalPath = "/assets/" + namespace + "/lang/" + langCode + ".json";
        JsonObject result = new JsonObject();
        loadResourceIntoJson(internalPath, result);

        // 3. 如果有外部数据，合并进去（按 key 前缀匹配分流给对应的 namespace）
        if (externalData != null) {
            for (String key : externalData.keySet()) {
                if (key.startsWith("_")) continue;
                if ("valence_cord".equals(namespace)) {
                    if (key.startsWith("neonlyrics.")) {
                        result.add(key, externalData.get(key));
                    }
                } else if ("valence".equals(namespace)) {
                    if (!key.startsWith("neonlyrics.") && !key.startsWith("valence_i18n.")) {
                        result.add(key, externalData.get(key));
                    }
                } else if ("valence_i18n".equals(namespace)) {
                    if (key.startsWith("valence_i18n.")) {
                        result.add(key, externalData.get(key));
                    }
                }
            }
        }

        if (result.size() == 0) {
            return null;
        }

        String jsonStr = GSON.toJson(result);
        return new ByteArrayInputStream(jsonStr.getBytes(StandardCharsets.UTF_8));
    }
}
