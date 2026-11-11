package dev.valence.i18n.pack;

import dev.valence.i18n.config.LangManager;
import dev.valence.i18n.config.ModConfig;
import dev.valence.i18n.font.FontManager;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VirtualI18nResourcePack implements ResourcePack {
    private final ResourcePackInfo info;

    public VirtualI18nResourcePack(ResourcePackInfo info) {
        this.info = info;
    }

    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        if (segments.length == 1 && "pack.mcmeta".equals(segments[0])) {
            String mcmeta = "{\"pack\":{\"pack_format\":34,\"description\":\"Valence i18n & Font Dynamic Pack\"}}";
            return () -> new ByteArrayInputStream(mcmeta.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return null;
        }

        String namespace = id.getNamespace();
        String path = id.getPath();

        // 1. 处理语言文件：assets/<namespace>/lang/<langCode>.json
        if (path.startsWith("lang/") && path.endsWith(".json")) {
            String langCode = path.substring("lang/".length(), path.length() - ".json".length());
            if ("valence".equals(namespace) || "valence_cord".equals(namespace) || "valence_i18n".equals(namespace)) {
                InputStream langStream = LangManager.getLanguageStream(namespace, langCode);
                if (langStream != null) {
                    return () -> langStream;
                }
            }
        }

        // 2. 处理外部字体流：valence_i18n:font/external_<filename>
        if ("valence_i18n".equals(namespace) && path.startsWith("font/external_")) {
            String filename = path.substring("font/external_".length());
            File fontFile = new File(ModConfig.getFontsDir(), filename);
            if (fontFile.exists() && fontFile.isFile()) {
                return () -> new FileInputStream(fontFile);
            }
        }

        // 3. 处理默认字体注入：覆盖 minecraft:default 字体链以支持选中字体
        if ("minecraft".equals(namespace) && "font/default.json".equals(path)) {
            String activeFont = FontManager.getActiveFontResourcePath();
            if (activeFont != null) {
                String fontJson = "{\n" +
                        "  \"providers\": [\n" +
                        "    {\n" +
                        "      \"type\": \"ttf\",\n" +
                        "      \"file\": \"" + activeFont + "\",\n" +
                        "      \"size\": 11.0,\n" +
                        "      \"oversample\": 4.0\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"type\": \"reference\",\n" +
                        "      \"id\": \"minecraft:include/space\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"type\": \"reference\",\n" +
                        "      \"id\": \"minecraft:include/default\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
                return () -> new ByteArrayInputStream(fontJson.getBytes(StandardCharsets.UTF_8));
            }
        }

        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return;
        }

        // 1. 字体资源索引发现
        if ("minecraft".equals(namespace) && "font".equals(prefix)) {
            Identifier id = Identifier.of("minecraft", "font/default.json");
            InputSupplier<InputStream> supplier = open(type, id);
            if (supplier != null) {
                consumer.accept(id, supplier);
            }
        }

        // 2. 语言资源索引发现
        if (("valence".equals(namespace) || "valence_cord".equals(namespace) || "valence_i18n".equals(namespace)) && "lang".equals(prefix)) {
            List<String> langCodes = new ArrayList<>(List.of("zh_cn", "zh_tw", "en_us"));

            File langDir = ModConfig.getLangDir();
            File[] externalFiles = langDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (externalFiles != null) {
                for (File f : externalFiles) {
                    String name = f.getName();
                    String code = name.substring(0, name.length() - ".json".length());
                    if (!langCodes.contains(code)) {
                        langCodes.add(code);
                    }
                }
            }

            for (String code : langCodes) {
                Identifier id = Identifier.of(namespace, "lang/" + code + ".json");
                InputSupplier<InputStream> supplier = open(type, id);
                if (supplier != null) {
                    consumer.accept(id, supplier);
                }
            }
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        if (type == ResourceType.CLIENT_RESOURCES) {
            return Set.of("valence", "valence_cord", "valence_i18n", "minecraft");
        }
        return Collections.emptySet();
    }

    @Override
    public <T> T parseMetadata(ResourceMetadataSerializer<T> serializer) {
        return null;
    }

    @Override
    public ResourcePackInfo getInfo() {
        return this.info;
    }

    @Override
    public void close() {
    }
}
