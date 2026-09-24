# Telegram Android 二开项目要求文档

> 本文档是本项目的**唯一**需求与开发规范来源，整合了原分散的设计、计划、测试、同步等文档。

**上游仓库**：https://github.com/DrKLO/Telegram
**本项目仓库**：https://github.com/jadeyu0906-create/Telegram-Android
**基础版本**：Telegram 12.10.1 (7038)
**技术栈**：Java / Kotlin，Gradle 8.x

---

## 1. 项目概述

基于 Telegram Android 官方客户端二次开发，集成 Web3 功能与自定义黑金 UI（品牌「u多担保」）。

### 1.1 已实现功能

- 闪屏广告页（品牌宣传 + 3s 倒计时自动跳转）
- Web3 登录页（Telegram 快捷登录 + 一键免密接入）
- 一键免密接入弹窗（两步弹窗：确认 + 进度演示）
- 自定义底部导航 4 Tab（聊天 / 联系人 / 发现 / 我的）
- 多语言支持（中英日韩，5 种语言）
- 多渠道变体（Google Play / 华为 / 独立版）

---

## 2. 项目结构

```
Telegram-Android/
├── TMessagesProj/                    # 官方核心库（⚠️ 尽量不修改官方源码）
│   ├── src/main/java/org/telegram/ui/
│   │   ├── CustomMainTabsActivity.java       # @custom 自定义主 Tab 容器
│   │   ├── DiscoverPlaceholderFragment.java  # @custom 「发现」占位页
│   │   └── Components/
│   │       ├── CustomTabView.java            # @custom 单个 Tab 视图
│   │       └── CustomBottomNavigationView.java # @custom 底部导航栏
│   └── src/main/java/org/telegram/ui/LaunchActivity.java  # ⚠️ 已修改 4 处
│
├── TMessagesProj_App/                # 🎨 自定义代码主目录
│   ├── src/main/java/org/telegram/ui/
│   │   ├── SplashAdActivity.java            # 闪屏广告页
│   │   └── Web3LoginActivity.java           # Web3 登录页 + 一键免密弹窗
│   └── src/main/res/
│       ├── layout/                          # activity_splash_ad.xml 等
│       ├── values*/strings.xml              # 5 种语言自定义字符串
│       ├── drawable/  anim/                 # 图标与动画资源
│
├── TMessagesProj_AppHuawei/          # 华为变体
└── TMessagesProj_AppStandalone/      # 独立版变体
```

### 2.1 代码放置原则（关键约束）

| 修改类型 | 放置位置 | 冲突风险 |
|---------|---------|---------|
| 新增 Activity/自定义类 | `TMessagesProj_App/src/main/java/` | ✅ 无 |
| 新增布局/资源 | `TMessagesProj_App/src/main/res/` | ✅ 无 |
| **必须被官方代码引用的类** | `TMessagesProj/src/main/java/`（继承/重写） | 无（新文件） |
| 修改官方类 | ❌ 禁止内联大改，只能单行入口点 + `@custom` 注释 | 🔴 高 |

> ⚠️ **模块依赖方向**：`TMessagesProj_App` 依赖 `TMessagesProj`（单向）。`LaunchActivity` 在 `TMessagesProj`，无法引用 `TMessagesProj_App` 的类，因此 `CustomMainTabsActivity` 等被官方入口引用的类**必须**放在 `TMessagesProj`。

---

## 3. 功能需求

### 3.1 闪屏广告页（SplashAdActivity）

- 全屏沉浸式，品牌宣传（u多担保）
- **倒计时 3s** 自动跳转；「跳过」按钮与「进入」按钮均可跳转
- 跳转目标：`LaunchActivity`（自动加载自定义底部导航）
- 禁用返回键，防止开屏时退出
- 平滑淡入淡出过渡

### 3.2 Web3 登录页 + 一键免密接入弹窗（Web3LoginActivity）

- 两个入口：Telegram 快捷登录 / 一键免密接入体验
- **一键免密接入**：两步弹窗
  1. 确认弹窗（可取消，留在当前页）
  2. 进度弹窗（不可取消，4 步模拟进度，总时长 3.2s）
     - 设备指纹采集 → AI 风控验证 → 临时账号生成 → 配置同步
     - 完成后 Toast 提示并跳转 `LaunchActivity`
- 后端接口预留，当前为 UI 演示版本（必定成功）

### 3.3 自定义底部导航 4 Tab

| 顺序 | Tab | 图标 | 内容 |
|------|-----|------|------|
| 1 | 聊天 | 💬 | `DialogsActivity`（官方聊天列表） |
| 2 | 联系人 | 👥 | `ContactsActivity`（官方联系人） |
| 3 | 发现 | 🔍 | `DiscoverPlaceholderFragment`（自定义占位） |
| 4 | 我的 | 👤 | `SettingsActivity`（官方设置页，替代 Profile） |

**实现方式**：`CustomMainTabsActivity extends MainTabsActivity`，重写 `createBaseFragmentAt(int)`：
- `position == 2`（`POSITION_CALLS_OR_SETTINGS`）→ 发现占位页
- `position == 3`（`POSITION_PROFILE`）→ `SettingsActivity`（`hasMainTabs=true`）
- 其余走官方实现

**要点**：
- 隐藏官方导航栏（反射隐藏 `tabsViewWrapper`），注入自定义 `CustomBottomNavigationView`
- 点击/滑动均同步自定义导航栏选中态（`onViewPagerTabAnimationUpdate` 直接访问 `viewPager`，**不用反射**避免卡顿）
- 底部导航栏高度 = `56dp + 系统底部手势条高度`（`onApplyWindowInsets` 处理，避免小米手势条遮挡）
- 「我的」显示设置页，个人资料仍可通过设置页顶部账号栏进入

### 3.4 多语言支持

- 支持 5 种语言：英文（默认回退）、简体中文、繁体中文、韩文、日文
- 通过 Android 标准资源目录实现，与官方 `LocaleController` 独立、互不干扰
- 未匹配语言自动回退英文

---

## 4. 样式规范（黑金配色）

> 所有自定义 UI 硬编码色值，不依赖官方 `Theme` 系统，主题切换不影响。

| 用途 | 色值 |
|------|------|
| 背景色（导航栏/发现页） | `#000000` |
| 选中色（Tab/按钮/进度） | `#CFFF55`（荧光绿） |
| 未选中色（Tab/文字） | `#666666` |
| 分割线 | `#292929` |
| 弹窗背景（确认） | `#1A1A1A` |
| 弹窗背景（进度） | `#0E0E10` |
| 进度条背景 | `#333333` |
| 标题文字 | `#FFFFFF` |

色值常量位置：`CustomTabView`（选中/未选中色）、`CustomBottomNavigationView`（背景/分割线色）。

---

## 5. 多语言字符串规范

### 5.1 资源目录

```
TMessagesProj_App/src/main/res/
├── values/            # 英文（默认回退）
├── values-zh-rCN/     # 简体中文
├── values-zh-rTW/     # 繁体中文
├── values-ko/         # 韩文
└── values-ja/         # 日文
```

### 5.2 String Key 清单

**闪屏页**：`splash_skip_ad`、`splash_official_tag`、`splash_usdt_tag`、`splash_cert_text`、`splash_title_brand`、`splash_title_slogan`、`splash_desc`、`splash_feature_escrow`、`splash_feature_verify`、`splash_feature_arbitration`、`splash_button`、`splash_footer`

**Web3 登录页**：`web3_title`、`web3_subtitle`、`web3_login_btn`、`web3_quick_access_btn`、`web3_footer`

**一键免密弹窗**：`quick_access_dialog_title`、`quick_access_dialog_message`、`quick_access_dialog_confirm`、`quick_access_dialog_cancel`、`quick_access_progress_title`、`quick_access_step1~4`、`quick_access_success_toast`

**底部导航**：`tab_chats`、`tab_contacts`、`tab_discover`、`tab_mine`、`discover_placeholder_title`、`discover_placeholder_message`

### 5.3 新增文本步骤

1. 在 `values/strings.xml` 加 key（英文）
2. 在其余 4 个语言目录加相同 key 的翻译
3. 布局/代码引用 `@string/key` 或 `getString(R.string.key)`

---

## 6. 开发规范

### 6.1 注释要求

所有自定义代码标注 `@custom 自定义二开代码`；官方源码内联改动处标注修改原因与冲突解决方式。

### 6.2 Git 提交规范

```
feat     新功能    fix      修复 bug    chore    依赖/同步
refactor 重构      docs     文档        style    格式调整
```

### 6.3 禁止事项

- ❌ 不直接内联大改 `TMessagesProj/src/main/java/org/telegram/` 官方源码
- ❌ 不删除官方 Activity/Service 注册
- ❌ 不提交敏感信息（API Key、keystore）

---

## 7. 官方源码改动登记表

> 每新增一处官方源码改动，必须在此登记。同步时照表核对。

| 文件 | 改动位置 | 改动内容 | 冲突解法 |
|------|---------|---------|---------|
| `TMessagesProj/.../ui/LaunchActivity.java` | 592 / 1199 / 3315 / 3332 | `new MainTabsActivity()` → `new CustomMainTabsActivity()` | 保留 `@custom` 版，删 upstream 的 `new MainTabsActivity()` |
| `TMessagesProj/.../ui/LoginActivity.java` | 1612 / 1628 | 同上（登录成功跳转主界面） | 同上 |
| `TMessagesProj/.../ui/TwoStepVerificationSetupActivity.java` | 1317 / 2157 | 同上（两步验证设置完成） | 同上 |
| `TMessagesProj/.../ui/TwoStepVerificationActivity.java` | 1301 | 同上（两步验证完成） | 同上 |

**检索规则**：合并后全局搜索 `new MainTabsActivity()`，确认官方是否有新增创建点，逐处替换为 `new CustomMainTabsActivity()`。

**新增文件清单**（无冲突）：
- `TMessagesProj`：`CustomMainTabsActivity`、`DiscoverPlaceholderFragment`、`Components/CustomTabView`、`Components/CustomBottomNavigationView`
- `TMessagesProj_App`：`SplashAdActivity`、`Web3LoginActivity`、`res/values*/strings.xml`、`res/layout/*`、`res/drawable/*`、`res/anim/*`

---

## 8. Upstream 同步与冲突解决

### 8.1 核心结论

- **新增文件永不冲突**；只有「修改官方文件」（见第 7 节登记表，4 个文件）可能冲突，且冲突只在官方也改到同一行时发生。
- **条件编译（BuildVars 开关）和注释都不能减少冲突**——它们解决的是「功能可开关」和「冲突后快速认出」。减少冲突只能靠：改官方越少、每处越窄、能放新文件就不改官方。

### 8.2 同步流程

```bash
git fetch upstream
git merge upstream/master           # 或 git rebase（线性历史）
# 解决冲突：保留 @custom 版（新子模块需 git submodule update --init --recursive）
git add <冲突文件>
git commit
# 全局搜索 new MainTabsActivity() 确认无遗漏新建点
./gradlew :TMessagesProj:compileDebugJavaWithJavac
```

### 8.3 强烈建议：开启 git rerere

```bash
git config rerere.enabled true
```

记录并自动重放冲突解决结果，冲突越多越省时。

### 8.4 同步频率

- 每月一次（避免超过 3 个月导致大量冲突）；重大安全更新立即同步。

---

## 9. 构建与测试

### 9.1 构建

```bash
./gradlew installAfatDebug                 # 编译并安装 debug 版
./gradlew :TMessagesProj:compileDebugJavaWithJavac   # 快速编译验证
```

APK 输出：`TMessagesProj/build/outputs/apk/afat/debug/`

### 9.2 功能测试清单

- [ ] 闪屏页显示正常，3s 倒计时自动跳转，跳过/进入按钮可用
- [ ] 底部 4 Tab 显示（聊天/联系人/发现/我的），黑金配色正确
- [ ] Tab 点击/滑动切换流畅，选中态正确高亮
- [ ] 「发现」显示黑色占位页
- [ ] 「我的」显示设置页，各设置项可进入，账号栏可进个人资料
- [ ] 横竖屏切换、快速切换不崩溃
- [ ] 系统深浅色主题切换，导航栏颜色不变
- [ ] 5 种语言切换正确，未匹配语言回退英文

---

## 10. 变更历史

| 日期 | 说明 |
|------|------|
| 2026-09-24 | 整合所有分散文档为本文档；「我的」Tab 改为设置页；闪屏倒计时改为 3s |
| 2026-09-23 | 底部导航 Tab 功能实施完成 |
| 2026-09-16 | 多语言、一键免密接入弹窗设计完成 |
