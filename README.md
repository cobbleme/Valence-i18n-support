# Valence i18n & Font Companion (valence-i18n)

**繁體中文** | [English](README_EN.md)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1%2B-brightgreen.svg)](https://www.minecraft.net/)
[![Fabric Loader](https://img.shields.io/badge/Fabric-0.15.0%2B-blue.svg)](https://fabricmc.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Author: cobbleme](https://img.shields.io/badge/Author-cobbleme-purple.svg)]()

> **Valence 模组的独立扩展伴侣（Companion Mod）**，提供开箱即用的全量繁简中文本地化、外置语言配置动态加载、以及个性化中文字体包切换与安全回滚机制。

---

## ✨ 核心特性 (Features)

### 1. 🌐 100% 全量精细汉化 (Full Localization)
* **双语内置**：完整内置**繁体中文 (`zh_tw`)** 与 **简体中文 (`zh_cn`)**。
* **全覆盖无遗漏**：涵盖 Valence 两个核心命名空间（`valence` 与 `valence_cord` 共计 903+ 词条）：
  * **Valence 核心**：25+ 款动态天空名称与细致说明、材质风格、图腾弹出尺寸调节、性能优化面板、全版本更新日志 (v1.0.0 ~ v1.9.9)、按键绑定等。
  * **Lyrics Cord 歌词引擎**：Spotify 桌面连接全套图文教程与错误排查指南、灵动顶栏、27 款个性装扮与帽子、足迹拖尾粒子、击败消散特效。

### 2. 📂 外置自定义语言加载 (External Custom Languages)
* **自动导出参考模板**：首次启动或在设置面板中点击按钮，会自动将 Valence 官方合并后的完整英文词条导出至 `.minecraft/config/valence_i18n/lang/en_us.json`。
* **外置覆盖与增补**：玩家可直接在 `config/valence_i18n/lang/` 目录下放置自定义 JSON（如 `zh_tw.json` 或 `ja_jp.json`），游戏即时优先读取，支持通过 `F3 + T` 或面板按钮热重载，无需拆解任何 JAR 包。

### 3. 🎨 双预设字体与外置字体管理 (Font Management)
* **内置双预设字体**：
  * **繁体中文预设**：`犬神志-花嫁r` (`huajia_tw.ttf`)，优雅华丽。
  * **简体中文预设**：`MYuppyGB-Medium` (`myu_cn.ttf`)，圆润清晰。
  * **智能自动策略 (`Auto`)**：根据客户端当前语言环境自动智能匹配预设字体，英文保留 Valence 原版炫酷艺术字体。
* **外置字体扩展**：支持用户将任意第三方 `.ttf` 或 `.otf` 字体放入 `.minecraft/config/valence_i18n/fonts/`，游戏内配置菜单将自动检索并列出。
* **失败安全回滚 (Safe Rollback)**：若检测到外置字体文件损坏或不兼容，模组将自动回滚至安全预设字体并弹出提示，避免客户端闪退。

### 4. 🖥️ 专属图形设置界面 (In-Game GUI)
* **快捷唤起**：在游戏内任意时刻按下键盘 **`]`** 键（可在“选项 → 控制 → Valence 漢化與字型”中自定义），或通过 **Mod Menu** 模组列表点击设置齿轮进入。
* **直观操作**：支持字体循环切换、**实时文字渲染预览**、快捷打开外置配置文件夹、以及**【一键恢复预设】**。

---

## ⚠️ 已知问题与测试说明 (Known Issues & Testing Needed)

* **自订字体未生效问题**：  
  目前在部分客户端运行环境下，**自订字体的实际全局渲染仍可能未正常生效或表现为原版回退字体**。  
  * **原因说明**：由于 Minecraft 1.21.1+ 的底层字体管线（`FontManager` / `FontStorage` / STB-truetype 光栅化）对动态虚拟提供者（Synthetic Font Providers）和多层 `reference` 继承链存在严格的加载时序与资源过滤机制，当前环境尚缺乏更广泛机型的深入联调测试。
  * **后续计划**：语言汉化功能已 100% 稳定运行。关于字体链路的注入策略，欢迎社区开发者共同研究、测试或提交 PR 完善！

---

## 📥 安装与使用 (Installation)

### 环境要求
* **Minecraft**: `1.21.1+`
* **Fabric Loader**: `>= 0.15.0`
* **前置依赖**: [Fabric API](https://modrinth.com/mod/fabric-api)
* **主模组**: Valence (建议 1.9.9+)
* *(推荐搭配)*: [Mod Menu](https://modrinth.com/mod/modmenu)

### 安装步骤
1. 下载最新构建的 `valence-i18n-1.0.0.jar`。
2. 将 JAR 文件放入您的游戏 `.minecraft/mods` 文件夹中。
3. 启动游戏，界面与 Valence 设置已自动呈现为中文。
4. 按下 **`]`** 键调出个性化设置面板。

---

## 📁 目录结构说明 (Directory Structure)

模组启动后，会在 `.minecraft/` 下自动建立如下结构：

```text
.minecraft/config/valence_i18n/
├── config.json                     # 记录当前选中的字体模式、缩放比例等
├── lang/                           # 自定义语言文件目录
│   ├── README.txt                  # 使用指引
│   ├── en_us.json                  # 自动导出的完整官方英文参考词典
│   └── (可放置自定义 zh_tw.json, zh_cn.json 等)
└── fonts/                          # 自定义字体文件目录
    ├── README.txt                  # 字体放置指引
    └── (可放入任意第三方 .ttf / .otf 字体文件)
```

---

## 🛠️ 源码构建 (Building from Source)

本项目采用标准的 Fabric Loom Gradle 构建系统：

```bash
# 克隆仓库
git clone https://github.com/cobbleme/valence-i18n.git
cd valence-i18n

# 执行编译与打包 (需要 JDK 21+)
./gradlew build
```

编译输出的 JAR 文件将生成于 `build/libs/valence-i18n-1.0.0.jar`。

---

## 📄 开源许可证 (License)

本项目采用 [MIT License](LICENSE) 开源许可证。  
所有内置汉化文本与模组源码完全开源。内置字体版权归原字体作者所有。

* **作者**：`cobbleme`
* **反馈与贡献**：欢迎在 GitHub Issues 提交问题与建议！
