# Valence i18n & Font Companion (valence-i18n)

[繁體中文](README.md) | **English**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1%2B-brightgreen.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric-0.15.0%2B-blue.svg)](https://fabricmc.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Author: cobbleme](https://img.shields.io/badge/Author-cobbleme-purple.svg)]()

> An independent **Companion Mod** for the [Valence](https://modrinth.com/mod/valence) client mod. It provides out-of-the-box 100% Traditional and Simplified Chinese localization, external language file hot-reloading, and customizable CJK font management with safe rollback mechanisms.

---

## ✨ Features

### 1. 🌐 100% Full CJK Localization
* **Dual CJK Built-in**: Full support for both **Traditional Chinese (`zh_tw`)** and **Simplified Chinese (`zh_cn`)**.
* **Zero Missing Translations**: Covers all 903+ translation keys across both core namespaces (`valence` and `valence_cord`):
  * **Valence Core**: 25+ dynamic skies with rich descriptions, style customization, totem size scaling, FPS performance tuner, full changelogs (v1.0.0 through v1.9.9), keybindings, and menus.
  * **Lyrics Cord Engine**: Step-by-step Spotify desktop connection tutorials and troubleshooting, Island top bar, 27 cosmetic hats and accessories, particle trails, and death effects.

### 2. 📂 External Custom Language Loader
* **Automatic Template Export**: On first startup (or by clicking the button in the in-game GUI), the complete merged official English dictionary is automatically extracted and exported to `.minecraft/config/valence_i18n/lang/en_us.json`.
* **External Overrides & Extensions**: Users can drop custom translation files (e.g., `zh_tw.json`, `zh_cn.json`, or new language codes like `ja_jp.json`) into `.minecraft/config/valence_i18n/lang/`. The mod prioritizes external files dynamically and supports instant reload via `F3 + T` or the in-game GUI without modifying any JAR files.

### 3. 🎨 Preset & External Font Management
* **Dual Default Fonts**:
  * **Traditional Chinese Preset**: `Inugami - HuaJia` (`huajia_tw.ttf`), elegant and refined.
  * **Simplified Chinese Preset**: `MYuppyGB-Medium` (`myu_cn.ttf`), clean and legible.
  * **Auto Strategy (`Auto`)**: Automatically picks the best matching CJK font based on the active Minecraft language, while preserving Valence's fancy English display fonts for western characters.
* **External Font Support**: Place any standard `.ttf` or `.otf` font file into `.minecraft/config/valence_i18n/fonts/`, and it will automatically appear in the font selection dropdown.
* **Safe Fallback & Rollback**: Built-in font validation tests rasterization before loading. If an external font is damaged or incompatible, the mod automatically reverts to the safe default font and shows a toast warning to prevent crashes.

### 4. 🖥️ Dedicated In-Game Configuration GUI
* **Quick Access**: Press keyboard **`]`** at any time in-game (customizable under "Options → Controls → Key Binds"), or access via the **Mod Menu** list.
* **Interactive Controls**: Cycle font options, view **real-time font rendering preview cards**, quickly open config directories, and use **[Reset to Default]** for instant one-click recovery.

---

## ⚠️ Known Issues & Testing Needed

* **Custom Font Rendering Issue**:  
  In current client test environments, **custom fonts may not yet properly render globally or may still fall back to vanilla Minecraft fonts**.  
  * **Technical Cause**: Minecraft 1.21.1+'s modern font pipeline (`FontManager` / `FontStorage` / STB-truetype rasterization) enforces strict resource resolution filtering and loading lifecycles on dynamic synthetic providers and multi-tier `reference` chains.
  * **Status**: Localization (language translation and loading) is 100% verified and functional. Community developers and testers are warmly invited to contribute, test, or open PRs regarding the font injection pipeline!

---

## 📥 Installation

### Requirements
* **Minecraft**: `1.21.1+`
* **Fabric Loader**: `>= 0.15.0`
* **Dependencies**: [Fabric API](https://modrinth.com/mod/fabric-api)
* **Required Mod**: Valence (1.9.9+ recommended)
* *(Optional)*: [Mod Menu](https://modrinth.com/mod/modmenu)

### Quick Start
1. Download `valence-i18n-1.0.0.jar`.
2. Place the JAR file into your `.minecraft/mods` folder.
3. Launch Minecraft. Valence will automatically display in your selected language.
4. Press **`]`** to open the customization panel.

---

## 📁 Directory Structure

After the first launch, the mod generates the following directory structure:

```text
.minecraft/config/valence_i18n/
├── config.json                     # Active font mode and scale settings
├── lang/                           # Directory for custom language JSON files
│   ├── README.txt                  # Usage guide
│   ├── en_us.json                  # Auto-exported official English reference dictionary
│   └── (Drop your custom zh_tw.json, zh_cn.json, etc. here)
└── fonts/                          # Directory for external custom fonts
    ├── README.txt                  # Font placement guide
    └── (Drop your custom .ttf / .otf font files here)
```

---

## 🛠️ Building from Source

This project uses standard Fabric Loom Gradle:

```bash
# Clone the repository
git clone https://github.com/cobbleme/valence-i18n.git
cd valence-i18n

# Compile and package (Requires JDK 21+)
./gradlew build
```

The compiled output will be located at `build/libs/valence-i18n-1.0.0.jar`.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).  
Mod code and Chinese translations are completely open-source. Embedded fonts remain under the copyright of their respective creators.

* **Author**: `cobbleme`
* **Issues & Pull Requests**: Contributions and feedback are welcome on GitHub!
