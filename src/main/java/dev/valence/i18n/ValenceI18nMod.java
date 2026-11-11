package dev.valence.i18n;

import dev.valence.i18n.config.LangManager;
import dev.valence.i18n.config.ModConfig;
import dev.valence.i18n.gui.FontConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ValenceI18nMod implements ClientModInitializer {
    public static final String MOD_ID = "valence_i18n";

    private static KeyBinding openConfigKey;

    @Override
    public void onInitializeClient() {
        System.out.println("[Valence-i18n] Initializing Valence Localization & Font Companion...");

        // 1. 初始化配置与语言导出
        ModConfig.get();
        LangManager.init();

        // 2. 注册快速打开字体配置面板的按键绑定 (默认 GLFW_KEY_RIGHT_BRACKET 即 ']')
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.valence_i18n.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                KeyBinding.Category.create(Identifier.of("valence_i18n", "main"))
        ));

        // 3. 注册按键检测事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new FontConfigScreen(null));
                }
            }
        });

        System.out.println("[Valence-i18n] Valence Localization & Font Companion initialized successfully!");
    }
}
