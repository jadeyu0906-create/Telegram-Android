# Telegram Android 二开项目开发指南

## 📖 项目说明

本项目是基于 [Telegram Android 官方客户端](https://github.com/DrKLO/Telegram) 进行二次开发的定制版本，集成 Web3 功能和自定义 UI。

- **上游仓库**：https://github.com/DrKLO/Telegram
- **本项目仓库**：https://github.com/jadeyu0906-create/Telegram-Android
- **基础版本**：Telegram 12.10.1 (7038)
- **开发语言**：Java / Kotlin
- **构建工具**：Gradle 8.x

---

## 🎯 二开功能清单

### 已实现功能
- ✅ 自定义闪屏广告页（SplashAdActivity）
- ✅ Web3 登录页面（规划中）
- ✅ 多渠道支持（Google Play、华为、独立版）

### 规划功能
- 🔲 Web3 钱包集成
- 🔲 去中心化身份验证
- 🔲 智能合约交互
- 🔲 u多担保公证系统

---

## 📁 项目结构说明

```
Telegram-Android/
├── TMessagesProj/                    # 官方核心库（⚠️ 尽量不修改）
│   ├── src/main/java/org/telegram/  # 官方源码
│   ├── config/                       # 配置文件（✅ 可修改）
│   │   ├── debug/AndroidManifest.xml
│   │   ├── debug/AndroidManifest_SDK23.xml
│   │   ├── release/AndroidManifest.xml
│   │   └── release/AndroidManifest_SDK23.xml
│   └── build.gradle
│
├── TMessagesProj_App/                # 🎨 自定义代码主目录（✅ 重点开发区）
│   ├── src/main/java/
│   │   └── org/telegram/ui/
│   │       ├── SplashAdActivity.java      # 闪屏页
│   │       └── Web3LoginActivity.java     # Web3 登录页（待开发）
│   ├── src/main/res/
│   │   ├── layout/
│   │   │   ├── activity_splash_ad.xml
│   │   │   └── activity_web3_login.xml
│   │   └── drawable/                      # 自定义图标资源
│   └── src/main/AndroidManifest.xml       # App 特定配置
│
├── TMessagesProj_AppHuawei/          # 华为版本变体
├── TMessagesProj_AppStandalone/      # 独立版本变体
└── build.gradle                       # 根构建配置
```

---

## 🛠️ 开发规范

### 1. 代码放置原则

| 修改类型 | 放置位置 | 原因 | 冲突风险 |
|---------|---------|------|---------|
| **新增 Activity/Service** | `TMessagesProj_App/src/main/java/` | 完全隔离，不影响官方代码 | ✅ 无 |
| **新增布局/资源** | `TMessagesProj_App/src/main/res/` | 资源独立管理 | ✅ 无 |
| **Activity 注册** | `TMessagesProj/config/*/AndroidManifest.xml` | 全局生效（所有变体） | ⚠️ 低 |
| **修改官方类** | ❌ **禁止直接修改**，应继承/扩展 | 避免合并冲突 | 🔴 高 |
| **核心逻辑修改** | 使用反射/Hook/依赖注入 | 保持可维护性 | ⚠️ 中 |

### 2. 命名规范

- **自定义 Activity**：使用描述性名称，如 `Web3LoginActivity`、`SplashAdActivity`
- **包名**：统一使用 `org.telegram.ui` 或创建独立包 `com.yourcompany.custom`
- **资源文件**：加前缀避免冲突，如 `web3_login_button.xml`、`ic_custom_logo.xml`

### 3. Git 提交规范

```bash
# 提交格式
git commit -m "feat: 添加 Web3 登录页面"
git commit -m "fix: 修复闪屏页倒计时问题"
git commit -m "chore: 更新到 Telegram 12.11.0"

# 提交类型
feat     - 新功能
fix      - 修复 bug
chore    - 更新依赖/同步上游
refactor - 重构代码
docs     - 文档更新
style    - 代码格式调整
```

### 4. 注释要求

```java
/**
 * Web3 登录页面
 *
 * 功能：
 * 1. Telegram 账号快捷登录
 * 2. 一键免密接入
 * 3. Web3 钱包连接
 *
 * @author YourName
 * @date 2026-01-XX
 * @custom 自定义二开代码，不影响官方更新
 */
public class Web3LoginActivity extends Activity {
    // ...
}
```

---

## 🔄 上游同步流程

### 配置 Upstream（首次）

```bash
# 1. 添加官方仓库为 upstream
git remote add upstream https://github.com/DrKLO/Telegram.git

# 2. 验证配置
git remote -v
# origin    https://github.com/jadeyu0906-create/Telegram-Android.git (fetch)
# origin    https://github.com/jadeyu0906-create/Telegram-Android.git (push)
# upstream  https://github.com/DrKLO/Telegram.git (fetch)
# upstream  https://github.com/DrKLO/Telegram.git (push)
```

### 定期同步步骤（推荐每月一次）

#### 方案 A：Rebase 方式（推荐，保持线性历史）

```bash
# 1. 确保工作区干净
git status
git add .
git commit -m "chore: 同步前保存当前工作"

# 2. 拉取官方最新代码
git fetch upstream

# 3. 查看官方更新内容
git log HEAD..upstream/master --oneline

# 4. Rebase 到官方最新版本
git rebase upstream/master

# 5. 如果有冲突，逐个解决
# 冲突文件通常是：
# - TMessagesProj/config/debug/AndroidManifest.xml
# - TMessagesProj/config/release/AndroidManifest.xml
# - gradle.properties

# 6. 解决冲突后继续
git add <冲突文件>
git rebase --continue

# 7. 推送到远程（需要强制推送）
git push origin master --force-with-lease

# 8. 验证编译
./gradlew TMessagesProj_App:assembleDebug
```

#### 方案 B：Merge 方式（保留完整历史）

```bash
# 1. 拉取官方最新代码
git fetch upstream

# 2. 合并官方代码
git merge upstream/master

# 3. 解决冲突（如果有）
git add <冲突文件>
git commit -m "chore: 合并 Telegram 官方更新"

# 4. 推送到远程
git push origin master

# 5. 验证编译
./gradlew clean
./gradlew TMessagesProj_App:assembleDebug
```

### 冲突处理指南

#### 常见冲突场景 1：AndroidManifest.xml

```xml
<<<<<<< HEAD (你的修改)
    <!-- 开屏广告 Activity -->
    <activity
        android:name="org.telegram.ui.SplashAdActivity"
        android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN"/>
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity>
=======
    <!-- 官方新增的 Service -->
    <service
        android:name="org.telegram.messenger.NewOfficialService"
        android:exported="false"/>
>>>>>>> upstream/master (官方更新)
```

**解决方式**：保留双方的修改
```xml
    <!-- 开屏广告 Activity -->
    <activity
        android:name="org.telegram.ui.SplashAdActivity"
        android:theme="@android:style/Theme.Black.NoTitleBar.Fullscreen"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN"/>
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity>

    <!-- 官方新增的 Service -->
    <service
        android:name="org.telegram.messenger.NewOfficialService"
        android:exported="false"/>
```

#### 常见冲突场景 2：gradle.properties

```properties
<<<<<<< HEAD
APP_VERSION_NAME=12.10.1
=======
APP_VERSION_NAME=12.11.0
>>>>>>> upstream/master
```

**解决方式**：使用官方版本号
```properties
APP_VERSION_NAME=12.11.0
```

#### 常见冲突场景 3：依赖版本冲突

```gradle
<<<<<<< HEAD
implementation 'androidx.core:core:1.12.0'
=======
implementation 'androidx.core:core:1.13.0'
>>>>>>> upstream/master
```

**解决方式**：使用官方最新版本
```gradle
implementation 'androidx.core:core:1.13.0'
```

---

## ✅ 同步后验证清单

```bash
# 1. 清理构建缓存
./gradlew clean

# 2. 编译所有变体
./gradlew TMessagesProj_App:assembleDebug
./gradlew TMessagesProj_AppHuawei:assembleDebug
./gradlew TMessagesProj_AppStandalone:assembleDebug

# 3. 运行测试（如果有）
./gradlew test

# 4. 安装测试
adb install -r TMessagesProj_App/build/outputs/apk/afat/debug/app-afat-debug.apk

# 5. 功能验证
# - 启动应用，检查闪屏页是否正常显示
# - 倒计时/跳过按钮是否正常工作
# - 登录流程是否正常
# - 自定义功能是否完好
```

---

## 🚨 注意事项

### ⚠️ 禁止操作
1. ❌ **不要直接修改** `TMessagesProj/src/main/java/org/telegram/` 下的官方源码
2. ❌ **不要删除** 官方的 Activity/Service 注册
3. ❌ **不要修改** 官方的包名和签名配置（除非你知道后果）
4. ❌ **不要提交** 敏感信息（API Key、签名文件）到 Git

### ✅ 推荐做法
1. ✅ 所有自定义代码放在 `TMessagesProj_App/` 目录
2. ✅ 使用继承/扩展方式修改官方类
3. ✅ 定期同步 upstream（建议每月一次）
4. ✅ 在 `.gitignore` 中排除敏感文件
5. ✅ 为自定义代码添加详细注释
6. ✅ 同步前创建备份分支

### 🔐 敏感文件管理

确保 `.gitignore` 包含：
```gitignore
# API Keys
API_KEYS
local.properties
*.jks
*.keystore

# 签名配置
release.keystore
debug.keystore
```

---

## 🏗️ 构建配置

### 本地构建

```bash
# Debug 版本
./gradlew TMessagesProj_App:assembleAfatDebug

# Release 版本
./gradlew TMessagesProj_App:assembleAfatRelease

# 华为版本
./gradlew TMessagesProj_AppHuawei:assembleAfatDebug

# 独立版本
./gradlew TMessagesProj_AppStandalone:assembleDebug
```

### API Keys 配置

创建 `API_KEYS` 文件（参考 `API_KEYS_EXAMPLE`）：
```
APP_ID = 你的应用ID
APP_HASH = 你的应用Hash
MAPS_API_KEY = 地图API密钥
```

---

## 📚 参考资源

### 官方文档
- [Telegram API](https://core.telegram.org/api)
- [Telegram Android 源码](https://github.com/DrKLO/Telegram)

### 知名二开项目
- [Nekogram](https://github.com/Nekogram/Nekogram) - 功能增强版
- [Telegram-FOSS](https://github.com/Telegram-FOSS-Team/Telegram-FOSS) - 开源纯净版
- [NekoX](https://github.com/NekoX-Dev/NekoX) - 多功能定制版

### 开发工具
- [Android Studio](https://developer.android.com/studio)
- [Git](https://git-scm.com/)
- [Gradle](https://gradle.org/)

---


## 📄 许可证

本项目基于 [GPL-2.0](LICENSE) 许可证，继承自 Telegram 官方客户端。

---

**最后更新**：2026-01-XX
