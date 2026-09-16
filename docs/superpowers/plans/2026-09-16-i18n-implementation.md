# 多语言支持实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为闪屏广告页和 Web3 登录页添加多语言支持（英文、简体中文、繁体中文、韩文、日文）

**Architecture:** 使用 Android 标准资源系统（values-*/strings.xml），与 Telegram 官方多语言系统独立运行，未匹配语言自动回退到英文

**Tech Stack:** Android Resources, XML

---

## File Structure

### New Files (5 language directories)
```
TMessagesProj_App/src/main/res/
├── values/strings.xml                    # 英文（默认回退）- 新建
├── values-zh-rCN/strings.xml             # 简体中文 - 新建
├── values-zh-rTW/strings.xml             # 繁体中文 - 新建
├── values-ko/strings.xml                 # 韩文 - 新建
└── values-ja/strings.xml                 # 日文 - 新建
```

### Modified Files
```
TMessagesProj_App/src/main/res/
├── layout/activity_splash_ad.xml         # 替换硬编码文本为 @string/xxx
└── layout/activity_web3_login.xml        # 替换硬编码文本为 @string/xxx

TMessagesProj_App/src/main/java/org/telegram/ui/
└── SplashAdActivity.java:85              # 倒计时文本动态拼接
```

---

## Task 1: 创建英文字符串资源（默认）

**Files:**
- Create: `TMessagesProj_App/src/main/res/values/strings.xml`

- [ ] **Step 1: 创建 values 目录**

```bash
mkdir -p TMessagesProj_App/src/main/res/values
```

- [ ] **Step 2: 创建英文字符串资源文件**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Splash Ad Screen -->
    <string name="splash_skip_ad">Skip Ad</string>
    <string name="splash_official_tag">OFFICIAL</string>
    <string name="splash_usdt_tag">USDT</string>
    <string name="splash_cert_text">✓ UDuo Guarantee • $50M Reserve Fund</string>
    <string name="splash_title_brand">UDuo Guarantee ✕ Telegram</string>
    <string name="splash_title_slogan">Lightning-Fast Multi-Sig Notary Platform</string>
    <string name="splash_desc">Supports OTC, development, marketing &amp; traffic trading escrow\nSmart contract fund locking + official dispute arbitration</string>
    <string name="splash_feature_escrow">Multi-Sig Escrow</string>
    <string name="splash_feature_verify">Anti-Fraud Verification</string>
    <string name="splash_feature_arbitration">Official Arbitration</string>
    <string name="splash_button">Enter Telegram Web3 Login →</string>
    <string name="splash_footer">100%% funds secured by UDuo smart contracts &amp; reserve fund</string>

    <!-- Web3 Login Screen -->
    <string name="web3_title">Web3 × Telegram</string>
    <string name="web3_subtitle">Decentralized Web3 ecosystem meets lightweight social\nContinue with your existing account, contacts &amp; groups</string>
    <string name="web3_login_btn">Telegram Quick Login</string>
    <string name="web3_quick_access_btn">One-Click Guest Access</string>
    <string name="web3_footer">100%% funds secured by UDuo smart contracts &amp; reserve fund</string>
</resources>
```

保存到: `TMessagesProj_App/src/main/res/values/strings.xml`

- [ ] **Step 3: 验证 XML 格式正确**

```bash
xmllint --noout TMessagesProj_App/src/main/res/values/strings.xml
```

Expected: 无输出表示格式正确

- [ ] **Step 4: 提交**

```bash
git add TMessagesProj_App/src/main/res/values/strings.xml
git commit -m "feat(i18n): 添加英文字符串资源（默认回退语言）

- 闪屏广告页 12 条文本
- Web3 登录页 5 条文本

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

## Task 2: 创建简体中文字符串资源

**Files:**
- Create: `TMessagesProj_App/src/main/res/values-zh-rCN/strings.xml`

- [ ] **Step 1: 创建 values-zh-rCN 目录**

```bash
mkdir -p TMessagesProj_App/src/main/res/values-zh-rCN
```

- [ ] **Step 2: 创建简体中文字符串资源文件**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- 闪屏广告页 -->
    <string name="splash_skip_ad">跳过广告</string>
    <string name="splash_official_tag">OFFICIAL</string>
    <string name="splash_usdt_tag">USDT</string>
    <string name="splash_cert_text">✓ u多担保 • 5000万赔付准备金托底</string>
    <string name="splash_title_brand">u多担保 ✕ Telegram</string>
    <string name="splash_title_slogan">全网极速多签公证平台</string>
    <string name="splash_desc">支持 OTC、技术开发、营销公关与流量交易担保\n智能合约资金锁仓 + 官方争议仲裁保障</string>
    <string name="splash_feature_escrow">多签托管锁仓</string>
    <string name="splash_feature_verify">防骗真实性验真</string>
    <string name="splash_feature_arbitration">官方公正仲裁</string>
    <string name="splash_button">进入 Telegram Web3 登录页 →</string>
    <string name="splash_footer">100%% 资金由 u多担保 智能合约与赔付准备金双重托底</string>

    <!-- Web3 登录页 -->
    <string name="web3_title">Web3 × Telegram</string>
    <string name="web3_subtitle">去中心化 Web3 生态与轻量社交的融合体\n继续使用原有账号、联系人与群组</string>
    <string name="web3_login_btn">Telegram 账号快捷登录</string>
    <string name="web3_quick_access_btn">一键免密接入体验</string>
    <string name="web3_footer">100%% 资金由 u多担保 智能合约与赔付准备金双重托底</string>
</resources>
```

保存到: `TMessagesProj_App/src/main/res/values-zh-rCN/strings.xml`

- [ ] **Step 3: 验证 XML 格式正确**

```bash
xmllint --noout TMessagesProj_App/src/main/res/values-zh-rCN/strings.xml
```

Expected: 无输出表示格式正确

- [ ] **Step 4: 提交**

```bash
git add TMessagesProj_App/src/main/res/values-zh-rCN/strings.xml
git commit -m "feat(i18n): 添加简体中文字符串资源

- 适用于中国大陆用户
- 包含所有闪屏页和登录页文本

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

## Task 3: 创建繁体中文字符串资源

**Files:**
- Create: `TMessagesProj_App/src/main/res/values-zh-rTW/strings.xml`

- [ ] **Step 1: 创建 values-zh-rTW 目录**

```bash
mkdir -p TMessagesProj_App/src/main/res/values-zh-rTW
```

- [ ] **Step 2: 创建繁体中文字符串资源文件**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- 閃屏廣告頁 -->
    <string name="splash_skip_ad">跳過廣告</string>
    <string name="splash_official_tag">OFFICIAL</string>
    <string name="splash_usdt_tag">USDT</string>
    <string name="splash_cert_text">✓ u多擔保 • 5000萬賠付準備金託底</string>
    <string name="splash_title_brand">u多擔保 ✕ Telegram</string>
    <string name="splash_title_slogan">全網極速多簽公證平台</string>
    <string name="splash_desc">支援 OTC、技術開發、行銷公關與流量交易擔保\n智能合約資金鎖倉 + 官方爭議仲裁保障</string>
    <string name="splash_feature_escrow">多簽託管鎖倉</string>
    <string name="splash_feature_verify">防詐真實性驗真</string>
    <string name="splash_feature_arbitration">官方公正仲裁</string>
    <string name="splash_button">進入 Telegram Web3 登入頁 →</string>
    <string name="splash_footer">100%% 資金由 u多擔保 智能合約與賠付準備金雙重託底</string>

    <!-- Web3 登入頁 -->
    <string name="web3_title">Web3 × Telegram</string>
    <string name="web3_subtitle">去中心化 Web3 生態與輕量社交的融合體\n繼續使用原有帳號、聯絡人與群組</string>
    <string name="web3_login_btn">Telegram 帳號快捷登入</string>
    <string name="web3_quick_access_btn">一鍵免密接入體驗</string>
    <string name="web3_footer">100%% 資金由 u多擔保 智能合約與賠付準備金雙重託底</string>
</resources>
```

保存到: `TMessagesProj_App/src/main/res/values-zh-rTW/strings.xml`

- [ ] **Step 3: 验证 XML 格式正确**

```bash
xmllint --noout TMessagesProj_App/src/main/res/values-zh-rTW/strings.xml
```

Expected: 无输出表示格式正确

- [ ] **Step 4: 提交**

```bash
git add TMessagesProj_App/src/main/res/values-zh-rTW/strings.xml
git commit -m "feat(i18n): 添加繁体中文字符串资源

- 适用于台湾、香港用户
- 包含所有闪屏页和登录页文本

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

## Task 4: 创建韩文字符串资源

**Files:**
- Create: `TMessagesProj_App/src/main/res/values-ko/strings.xml`

- [ ] **Step 1: 创建 values-ko 目录**

```bash
mkdir -p TMessagesProj_App/src/main/res/values-ko
```

- [ ] **Step 2: 创建韩文字符串资源文件**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- 스플래시 광고 화면 -->
    <string name="splash_skip_ad">광고 건너뛰기</string>
    <string name="splash_official_tag">OFFICIAL</string>
    <string name="splash_usdt_tag">USDT</string>
    <string name="splash_cert_text">✓ UDuo 보증 • 5000만 달러 준비금 지원</string>
    <string name="splash_title_brand">UDuo 보증 ✕ Telegram</string>
    <string name="splash_title_slogan">초고속 다중 서명 공증 플랫폼</string>
    <string name="splash_desc">OTC, 기술 개발, 마케팅 및 트래픽 거래 에스크로 지원\n스마트 계약 자금 잠금 + 공식 분쟁 중재 보장</string>
    <string name="splash_feature_escrow">다중 서명 에스크로</string>
    <string name="splash_feature_verify">사기 방지 인증</string>
    <string name="splash_feature_arbitration">공식 중재</string>
    <string name="splash_button">Telegram Web3 로그인 페이지로 이동 →</string>
    <string name="splash_footer">100%% 자금은 UDuo 스마트 계약 및 준비금으로 보호됩니다</string>

    <!-- Web3 로그인 화면 -->
    <string name="web3_title">Web3 × Telegram</string>
    <string name="web3_subtitle">탈중앙화 Web3 생태계와 가벼운 소셜의 융합\n기존 계정, 연락처 및 그룹을 계속 사용하세요</string>
    <string name="web3_login_btn">Telegram 빠른 로그인</string>
    <string name="web3_quick_access_btn">원클릭 게스트 접속</string>
    <string name="web3_footer">100%% 자금은 UDuo 스마트 계약 및 준비금으로 보호됩니다</string>
</resources>
```

保存到: `TMessagesProj_App/src/main/res/values-ko/strings.xml`

- [ ] **Step 3: 验证 XML 格式正确**

```bash
xmllint --noout TMessagesProj_App/src/main/res/values-ko/strings.xml
```

Expected: 无输出表示格式正确

- [ ] **Step 4: 提交**

```bash
git add TMessagesProj_App/src/main/res/values-ko/strings.xml
git commit -m "feat(i18n): 添加韩文字符串资源

- 适用于韩国用户
- 包含所有闪屏页和登录页文本

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

## Task 5: 创建日文字符串资源

**Files:**
- Create: `TMessagesProj_App/src/main/res/values-ja/strings.xml`

- [ ] **Step 1: 创建 values-ja 目录**

```bash
mkdir -p TMessagesProj_App/src/main/res/values-ja
```

- [ ] **Step 2: 创建日文字符串资源文件**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- スプラッシュ広告画面 -->
    <string name="splash_skip_ad">広告をスキップ</string>
    <string name="splash_official_tag">OFFICIAL</string>
    <string name="splash_usdt_tag">USDT</string>
    <string name="splash_cert_text">✓ UDuo保証 • 5000万ドル準備金サポート</string>
    <string name="splash_title_brand">UDuo保証 ✕ Telegram</string>
    <string name="splash_title_slogan">超高速マルチシグ公証プラットフォーム</string>
    <string name="splash_desc">OTC、技術開発、マーケティング、トラフィック取引エスクローをサポート\nスマートコントラクト資金ロック + 公式紛争調停保証</string>
    <string name="splash_feature_escrow">マルチシグエスクロー</string>
    <string name="splash_feature_verify">詐欺防止認証</string>
    <string name="splash_feature_arbitration">公式調停</string>
    <string name="splash_button">Telegram Web3ログインページへ →</string>
    <string name="splash_footer">100%%の資金はUDuoスマートコントラクトと準備金で保護されています</string>

    <!-- Web3ログイン画面 -->
    <string name="web3_title">Web3 × Telegram</string>
    <string name="web3_subtitle">分散型Web3エコシステムと軽量ソーシャルの融合\n既存のアカウント、連絡先、グループを引き続き使用</string>
    <string name="web3_login_btn">Telegramクイックログイン</string>
    <string name="web3_quick_access_btn">ワンクリックゲストアクセス</string>
    <string name="web3_footer">100%%の資金はUDuoスマートコントラクトと準備金で保護されています</string>
</resources>
```

保存到: `TMessagesProj_App/src/main/res/values-ja/strings.xml`

- [ ] **Step 3: 验证 XML 格式正确**

```bash
xmllint --noout TMessagesProj_App/src/main/res/values-ja/strings.xml
```

Expected: 无输出表示格式正确

- [ ] **Step 4: 提交**

```bash
git add TMessagesProj_App/src/main/res/values-ja/strings.xml
git commit -m "feat(i18n): 添加日文字符串资源

- 适用于日本用户
- 包含所有闪屏页和登录页文本

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

## Task 6: 修改闪屏广告页布局（替换硬编码文本）

**Files:**
- Modify: `TMessagesProj_App/src/main/res/layout/activity_splash_ad.xml:24, 89, 90, 107, 124, 139, 150, 162, 198, 225, 251, 273, 285`

- [ ] **Step 1: 替换跳过按钮文本（第24行）**

修改前:
```xml
android:text="跳过广告 3s"
```

修改后:
```xml
android:text="@string/splash_skip_ad"
```

**注意**: 倒计时数字将在 Java 代码中动态拼接

- [ ] **Step 2: 替换 USDT 标签文本（第89行）**

修改前:
```xml
android:text="USDT"
```

修改后:
```xml
android:text="@string/splash_usdt_tag"
```

- [ ] **Step 3: 替换 OFFICIAL 标签文本（第107行）**

修改前:
```xml
android:text="OFFICIAL"
```

修改后:
```xml
android:text="@string/splash_official_tag"
```

- [ ] **Step 4: 替换认证标签文本（第124行）**

修改前:
```xml
android:text="✓ u多担保 • 5000万赔付准备金托底"
```

修改后:
```xml
android:text="@string/splash_cert_text"
```

- [ ] **Step 5: 替换品牌标题文本（第139行）**

修改前:
```xml
android:text="u多担保 ✕ Telegram"
```

修改后:
```xml
android:text="@string/splash_title_brand"
```

- [ ] **Step 6: 替换宣传标语文本（第150行）**

修改前:
```xml
android:text="全网极速多签公证平台"
```

修改后:
```xml
android:text="@string/splash_title_slogan"
```

- [ ] **Step 7: 替换描述文字（第162行）**

修改前:
```xml
android:text="支持 OTC、技术开发、营销公关与流量交易担保\n智能合约资金锁仓 + 官方争议仲裁保障"
```

修改后:
```xml
android:text="@string/splash_desc"
```

- [ ] **Step 8: 替换特性1文本（第198行）**

修改前:
```xml
android:text="多签托管锁仓"
```

修改后:
```xml
android:text="@string/splash_feature_escrow"
```

- [ ] **Step 9: 替换特性2文本（第225行）**

修改前:
```xml
android:text="防骗真实性验真"
```

修改后:
```xml
android:text="@string/splash_feature_verify"
```

- [ ] **Step 10: 替换特性3文本（第251行）**

修改前:
```xml
android:text="官方公正仲裁"
```

修改后:
```xml
android:text="@string/splash_feature_arbitration"
```

- [ ] **Step 11: 替换进入按钮文本（第273行）**

修改前:
```xml
android:text="进入 Telegram Web3 登录页 →"
```

修改后:
```xml
android:text="@string/splash_button"
```

- [ ] **Step 12: 替换底部说明文本（第285行）**

修改前:
```xml
android:text="100% 资金由 u多担保 智能合约与赔付准备金双重托底"
```

修改后:
```xml
android:text="@string/splash_footer"
```

- [ ] **Step 13: 验证布局文件 XML 格式**

```bash
xmllint --noout TMessagesProj_App/src/main/res/layout/activity_splash_ad.xml
```

Expected: 无输出表示格式正确

- [ ] **Step 14: 提交**

```bash
git add TMessagesProj_App/src/main/res/layout/activity_splash_ad.xml
git commit -m "refactor(i18n): 闪屏页布局使用字符串资源引用

- 替换所有硬编码文本为 @string/xxx
- 支持多语言自动切换

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

## Task 7: 修改 Web3 登录页布局（替换硬编码文本）

**Files:**
- Modify: `TMessagesProj_App/src/main/res/layout/activity_web3_login.xml:43, 52-53, 80, 100, 115`

- [ ] **Step 1: 替换页面标题文本（第43行）**

修改前:
```xml
android:text="Web3 × Telegram"
```

修改后:
```xml
android:text="@string/web3_title"
```

- [ ] **Step 2: 替换副标题文本（第52-53行）**

修改前:
```xml
android:text="去中心化 Web3 生态与轻量社交的融合体\n继续使用原有账号、联系人与群组"
```

修改后:
```xml
android:text="@string/web3_subtitle"
```

- [ ] **Step 3: 替换 Telegram 登录按钮文本（第80行）**

修改前:
```xml
android:text="Telegram 账号快捷登录"
```

修改后:
```xml
android:text="@string/web3_login_btn"
```

- [ ] **Step 4: 替换快速接入按钮文本（第100行）**

修改前:
```xml
android:text="一键免密接入体验"
```

修改后:
```xml
android:text="@string/web3_quick_access_btn"
```

- [ ] **Step 5: 替换底部说明文本（第115行）**

修改前:
```xml
android:text="100% 资金由 u多担保 智能合约与赔付准备金双重托底"
```

修改后:
```xml
android:text="@string/web3_footer"
```

- [ ] **Step 6: 验证布局文件 XML 格式**

```bash
xmllint --noout TMessagesProj_App/src/main/res/layout/activity_web3_login.xml
```

Expected: 无输出表示格式正确

- [ ] **Step 7: 提交**

```bash
git add TMessagesProj_App/src/main/res/layout/activity_web3_login.xml
git commit -m "refactor(i18n): Web3登录页布局使用字符串资源引用

- 替换所有硬编码文本为 @string/xxx
- 支持多语言自动切换

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

## Task 8: 修改 SplashAdActivity 代码（倒计时文本动态拼接）

**Files:**
- Modify: `TMessagesProj_App/src/main/java/org/telegram/ui/SplashAdActivity.java:85`

- [ ] **Step 1: 修改倒计时文本拼接逻辑**

修改前（第85行）:
```java
skipButton.setText("跳过广告 " + seconds + "s");
```

修改后:
```java
skipButton.setText(getString(R.string.splash_skip_ad) + " " + seconds + "s");
```

**说明**: 使用 `getString(R.string.splash_skip_ad)` 获取当前语言的"跳过广告"文本，然后拼接倒计时秒数

- [ ] **Step 2: 验证代码语法正确**

```bash
./gradlew TMessagesProj_App:compileAfatDebugJavaWithJavac
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: 提交**

```bash
git add TMessagesProj_App/src/main/java/org/telegram/ui/SplashAdActivity.java
git commit -m "refactor(i18n): 闪屏页倒计时文本支持多语言

- 使用 getString() 动态获取当前语言的文本
- 保持倒计时秒数动态拼接

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>"
```

---

## Task 9: 编译测试

**Files:**
- Test: Build system

- [ ] **Step 1: 清理构建缓存**

```bash
./gradlew clean
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 2: 编译 Debug 版本**

```bash
./gradlew TMessagesProj_App:assembleAfatDebug
```

Expected: BUILD SUCCESSFUL
输出: `TMessagesProj_App/build/outputs/apk/afat/debug/app-afat-debug.apk`

- [ ] **Step 3: 检查 APK 中是否包含所有语言资源**

```bash
unzip -l TMessagesProj_App/build/outputs/apk/afat/debug/app-afat-debug.apk | grep "res/values"
```

Expected output (包含以下目录):
```
res/values/strings.xml
res/values-zh-rCN/strings.xml
res/values-zh-rTW/strings.xml
res/values-ko/strings.xml
res/values-ja/strings.xml
```

- [ ] **Step 4: 验证通过，无需提交**

编译测试不产生代码变更，无需 commit

---

## Task 10: 功能测试（语言切换验证）

**Files:**
- Test: Runtime behavior

- [ ] **Step 1: 安装 APK 到测试设备**

```bash
adb install -r TMessagesProj_App/build/outputs/apk/afat/debug/app-afat-debug.apk
```

Expected: Success

- [ ] **Step 2: 测试简体中文**

1. 修改设备系统语言为简体中文（中国）
2. 启动应用
3. 验证闪屏页所有文本显示简体中文：
   - 跳过按钮: "跳过广告 30s"
   - 认证标签: "✓ u多担保 • 5000万赔付准备金托底"
   - 主标题: "u多担保 ✕ Telegram"
   - 副标题: "全网极速多签公证平台"
   - 特性标签: "多签托管锁仓", "防骗真实性验真", "官方公正仲裁"
   - 按钮: "进入 Telegram Web3 登录页 →"
4. 点击按钮进入 Web3 登录页
5. 验证登录页所有文本显示简体中文：
   - 标题: "Web3 × Telegram"
   - 副标题: "去中心化 Web3 生态与轻量社交的融合体..."
   - 按钮: "Telegram 账号快捷登录", "一键免密接入体验"

Expected: 所有文本均为简体中文

- [ ] **Step 3: 测试繁体中文**

1. 修改设备系统语言为繁体中文（台湾）
2. 重启应用
3. 验证闪屏页所有文本显示繁体中文（"跳過廣告", "u多擔保", "多簽託管鎖倉" 等）
4. 验证登录页所有文本显示繁体中文（"帳號快捷登入" 等）

Expected: 所有文本均为繁体中文

- [ ] **Step 4: 测试韩文**

1. 修改设备系统语言为韩文
2. 重启应用
3. 验证闪屏页所有文本显示韩文（"광고 건너뛰기", "UDuo 보증" 等）
4. 验证登录页所有文本显示韩文（"빠른 로그인" 等）

Expected: 所有文本均为韩文

- [ ] **Step 5: 测试日文**

1. 修改设备系统语言为日文
2. 重启应用
3. 验证闪屏页所有文本显示日文（"広告をスキップ", "UDuo保証" 等）
4. 验证登录页所有文本显示日文（"クイックログイン" 等）

Expected: 所有文本均为日文

- [ ] **Step 6: 测试英文**

1. 修改设备系统语言为英文
2. 重启应用
3. 验证闪屏页所有文本显示英文（"Skip Ad", "UDuo Guarantee" 等）
4. 验证登录页所有文本显示英文（"Telegram Quick Login" 等）

Expected: 所有文本均为英文

- [ ] **Step 7: 测试未匹配语言回退**

1. 修改设备系统语言为法文（或德文、西班牙文等未支持的语言）
2. 重启应用
3. 验证闪屏页和登录页所有文本显示英文（默认回退）

Expected: 所有文本均为英文（values/ 资源）

- [ ] **Step 8: 测试 Telegram 官方页面不受影响**

1. 保持设备语言为简体中文
2. 从 Web3 登录页点击"Telegram 账号快捷登录"
3. 进入 Telegram 官方登录页
4. 验证官方页面仍使用 Telegram 自己的多语言系统（应显示中文"你的电话号码"等官方翻译）

Expected: Telegram 官方页面使用自己的多语言系统，不受影响

- [ ] **Step 9: 测试倒计时功能正常**

1. 保持任意语言设置
2. 重启应用
3. 验证跳过按钮文本每秒更新（30s → 29s → 28s...）
4. 验证倒计时结束后自动跳转

Expected: 倒计时功能正常，文本动态更新

- [ ] **Step 10: 测试通过，无需提交**

功能测试不产生代码变更，无需 commit

---

## Task 11: 最终提交

**Files:**
- All modified files

- [ ] **Step 1: 查看所有变更**

```bash
git status
```

Expected output:
```
On branch master
nothing to commit, working tree clean
```

所有变更应该已经在之前的步骤中分别提交

- [ ] **Step 2: 查看提交历史**

```bash
git log --oneline -10
```

Expected: 显示以下 8 个提交（从新到旧）:
1. refactor(i18n): 闪屏页倒计时文本支持多语言
2. refactor(i18n): Web3登录页布局使用字符串资源引用
3. refactor(i18n): 闪屏页布局使用字符串资源引用
4. feat(i18n): 添加日文字符串资源
5. feat(i18n): 添加韩文字符串资源
6. feat(i18n): 添加繁体中文字符串资源
7. feat(i18n): 添加简体中文字符串资源
8. feat(i18n): 添加英文字符串资源（默认回退语言）

- [ ] **Step 3: 确认所有测试通过**

回顾测试结果：
- ✅ 编译测试通过（Task 9）
- ✅ 5 种语言显示正确（Task 10 Step 2-6）
- ✅ 未匹配语言回退到英文（Task 10 Step 7）
- ✅ Telegram 官方页面不受影响（Task 10 Step 8）
- ✅ 倒计时功能正常（Task 10 Step 9）

- [ ] **Step 4: 实施完成**

多语言支持已成功实施！

---

## Self-Review Checklist

**Spec Coverage:**
- ✅ Task 1-5: 创建 5 种语言的字符串资源（规格第 4 节）
- ✅ Task 6-7: 修改布局文件使用字符串资源引用（规格第 5 节）
- ✅ Task 8: 修改 Java 代码支持动态文本拼接（规格第 6 节）
- ✅ Task 9: 编译测试（规格第 7.1 节）
- ✅ Task 10: 功能测试（规格第 7.2-7.3 节）

**Placeholder Scan:**
- ✅ 无 TBD、TODO、待定等占位符
- ✅ 所有代码块完整可执行
- ✅ 所有文件路径精确

**Type Consistency:**
- ✅ 所有 string key 保持一致（splash_skip_ad, web3_title 等）
- ✅ 所有方法调用一致（getString(R.string.xxx)）
- ✅ 所有文件路径格式一致（values-zh-rCN/ 等）

---

## Execution Notes

- 每个 Task 都可以独立执行
- 编译错误会在 Task 8 Step 2 被发现
- 运行时错误会在 Task 10 被发现
- 所有提交消息遵循 Conventional Commits 规范
- 推荐使用 subagent-driven-development 方式执行
