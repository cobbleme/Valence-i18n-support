package dev.valence.i18n.gui;

import dev.valence.i18n.config.LangManager;
import dev.valence.i18n.config.ModConfig;
import dev.valence.i18n.font.FontManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.List;

public class FontConfigScreen extends Screen {
    private final Screen parent;
    private CyclingButtonWidget<String> fontSelector;

    public FontConfigScreen(Screen parent) {
        super(Text.translatable("valence_i18n.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 35;

        // 1. 语言管理部分
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("valence_i18n.lang.open_folder"),
                b -> Util.getOperatingSystem().open(ModConfig.getLangDir())
        ).dimensions(centerX - 155, y, 150, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("valence_i18n.lang.export_en"),
                b -> {
                    LangManager.checkAndExportEnTemplate(true);
                    if (this.client != null && this.client.player != null) {
                        this.client.inGameHud.getChatHud().addMessage(Text.translatable("valence_i18n.lang.exported_toast"));
                    }
                }
        ).dimensions(centerX + 5, y, 150, 20).build());

        y += 28;

        // 2. 字体选择按钮
        List<String> fontOptions = FontManager.getAvailableFontOptions();
        String currentSelection = ModConfig.get().selectedFont;
        if (!fontOptions.contains(currentSelection)) {
            currentSelection = FontManager.FONT_AUTO;
        }

        this.fontSelector = CyclingButtonWidget.<String>builder(val -> {
            if (FontManager.FONT_AUTO.equals(val)) return Text.translatable("valence_i18n.font.select").append(": ").append(Text.translatable("valence.auto"));
            if (FontManager.FONT_DEFAULT_TW.equals(val)) return Text.translatable("valence_i18n.font.default_tw");
            if (FontManager.FONT_DEFAULT_CN.equals(val)) return Text.translatable("valence_i18n.font.default_cn");
            if (FontManager.FONT_VANILLA.equals(val)) return Text.translatable("valence_i18n.font.vanilla");
            if (val.startsWith("external:")) return Text.literal("外置: " + val.substring("external:".length()));
            return Text.literal(val);
        }, currentSelection)
        .values(fontOptions)
        .build(centerX - 155, y, 310, 20, Text.translatable("valence_i18n.font.select"), (btn, value) -> {
            ModConfig.get().selectedFont = value;
            ModConfig.get().save();
        });

        this.addDrawableChild(this.fontSelector);

        y += 28;

        // 3. 打开字体文件夹按钮
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("valence_i18n.btn.open_fonts"),
                b -> Util.getOperatingSystem().open(ModConfig.getFontsDir())
        ).dimensions(centerX - 155, y, 310, 20).build());

        y += 85;

        // 4. 底部功能按钮：一键恢复默认、保存并应用重载、完成
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("valence_i18n.btn.reset_default"),
                b -> {
                    if (this.client != null) {
                        this.client.setScreen(new ConfirmScreen(
                                confirmed -> {
                                    if (confirmed) {
                                        ModConfig.get().resetToDefault();
                                        FontManager.resetRollbackFlag();
                                        if (this.client != null) {
                                            this.client.reloadResources();
                                        }
                                    }
                                    if (this.client != null) {
                                        this.client.setScreen(this);
                                    }
                                },
                                Text.translatable("valence_i18n.btn.reset_default"),
                                Text.translatable("valence_i18n.btn.reset_confirm")
                        ));
                    }
                }
        ).dimensions(centerX - 155, this.height - 28, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("valence_i18n.btn.apply"),
                b -> {
                    ModConfig.get().save();
                    if (this.client != null) {
                        this.client.reloadResources();
                    }
                }
        ).dimensions(centerX - 50, this.height - 28, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("gui.done"),
                b -> this.close()
        ).dimensions(centerX + 55, this.height - 28, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;

        // 绘制标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, 12, 0xFFFFFF);

        // 绘制当前语言提示
        String currentLang = (this.client != null && this.client.getLanguageManager() != null)
                ? this.client.getLanguageManager().getLanguage() : "unknown";
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("valence_i18n.lang.current", currentLang),
                centerX, 25, 0xAAAAAA
        );

        // 绘制实时字体预览卡片
        int previewBoxY = 120;
        int previewBoxWidth = 310;
        int previewBoxHeight = 56;
        int boxLeft = centerX - previewBoxWidth / 2;

        context.fill(boxLeft, previewBoxY, boxLeft + previewBoxWidth, previewBoxY + previewBoxHeight, 0x88000000);
        // 绘制 1px 细边框
        int borderColor = 0xFF555555;
        context.fill(boxLeft, previewBoxY, boxLeft + previewBoxWidth, previewBoxY + 1, borderColor);
        context.fill(boxLeft, previewBoxY + previewBoxHeight - 1, boxLeft + previewBoxWidth, previewBoxY + previewBoxHeight, borderColor);
        context.fill(boxLeft, previewBoxY, boxLeft + 1, previewBoxY + previewBoxHeight, borderColor);
        context.fill(boxLeft + previewBoxWidth - 1, previewBoxY, boxLeft + previewBoxWidth, previewBoxY + previewBoxHeight, borderColor);

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("valence_i18n.font.preview_title"),
                boxLeft + 8, previewBoxY + 6, 0xFFAA00
        );
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("valence_i18n.font.preview_text1"),
                boxLeft + 8, previewBoxY + 22, 0xFFFFFF
        );
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("valence_i18n.font.preview_text2"),
                boxLeft + 8, previewBoxY + 38, 0xDDDDDD
        );
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
