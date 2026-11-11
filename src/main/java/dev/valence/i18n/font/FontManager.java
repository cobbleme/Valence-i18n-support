package dev.valence.i18n.font;

import dev.valence.i18n.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FontManager {
    public static final String FONT_AUTO = "auto";
    public static final String FONT_DEFAULT_TW = "default_tw";
    public static final String FONT_DEFAULT_CN = "default_cn";
    public static final String FONT_VANILLA = "vanilla";

    private static boolean rollbackTriggered = false;

    public static List<String> getAvailableFontOptions() {
        List<String> list = new ArrayList<>();
        list.add(FONT_AUTO);
        list.add(FONT_DEFAULT_TW);
        list.add(FONT_DEFAULT_CN);
        list.add(FONT_VANILLA);

        File fontsDir = ModConfig.getFontsDir();
        File[] files = fontsDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".ttf") || lower.endsWith(".otf");
        });

        if (files != null) {
            for (File f : files) {
                list.add("external:" + f.getName());
            }
        }
        return list;
    }

    /**
     * 判断当前应激活哪个字体资源路径
     */
    public static String getActiveFontResourcePath() {
        String choice = ModConfig.get().selectedFont;

        if (FONT_VANILLA.equals(choice)) {
            return null; // 不注入自定义 CJK 字体，使用 Minecraft 原版回退
        }

        if (FONT_AUTO.equals(choice)) {
            String gameLang = MinecraftClient.getInstance().getLanguageManager().getLanguage();
            if (gameLang != null && (gameLang.equalsIgnoreCase("zh_tw") || gameLang.equalsIgnoreCase("zh_hk") || gameLang.equalsIgnoreCase("zh_mo"))) {
                return "valence_i18n:font/huajia_tw.ttf";
            } else {
                return "valence_i18n:font/myu_cn.ttf";
            }
        }

        if (FONT_DEFAULT_TW.equals(choice)) {
            return "valence_i18n:font/huajia_tw.ttf";
        }

        if (FONT_DEFAULT_CN.equals(choice)) {
            return "valence_i18n:font/myu_cn.ttf";
        }

        if (choice.startsWith("external:")) {
            String filename = choice.substring("external:".length());
            File externalFile = new File(ModConfig.getFontsDir(), filename);
            if (externalFile.exists() && validateFont(externalFile)) {
                return "valence_i18n:font/external_" + filename;
            } else {
                System.err.println("[Valence-i18n] External font failed validation, rolling back: " + filename);
                triggerSafeRollback("External font file corrupted or invalid: " + filename);
                return getActiveFontResourcePath();
            }
        }

        return "valence_i18n:font/myu_cn.ttf";
    }

    /**
     * 预检字体文件是否可被解析
     */
    public static boolean validateFont(File fontFile) {
        if (!fontFile.exists() || fontFile.length() < 1024) {
            return false;
        }
        try (InputStream in = new FileInputStream(fontFile)) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            // 测试基本字符能否光栅化
            return font.canDisplay('A') || font.canDisplay('中');
        } catch (Exception e) {
            System.err.println("[Valence-i18n] Font validation exception: " + e.getMessage());
            return false;
        }
    }

    /**
     * 安全回滚逻辑
     */
    public static synchronized void triggerSafeRollback(String reason) {
        if (rollbackTriggered) return;
        rollbackTriggered = true;

        System.err.println("[Valence-i18n] !!! SAFE ROLLBACK TRIGGERED: " + reason);
        ModConfig.get().selectedFont = FONT_AUTO;
        ModConfig.get().save();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            client.execute(() -> {
                client.inGameHud.getChatHud().addMessage(
                    Text.translatable("valence_i18n.warn.rollback")
                );
            });
        }
    }

    public static void resetRollbackFlag() {
        rollbackTriggered = false;
    }
}
