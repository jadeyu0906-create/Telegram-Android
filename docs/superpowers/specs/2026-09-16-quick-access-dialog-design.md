# 一键免密接入弹窗功能设计文档

**日期**: 2026-09-16
**作者**: Claude Opus 4.6
**状态**: 待审核

---

## 1. 概述

为 Web3LoginActivity 添加"一键免密接入体验"弹窗功能，包括两步弹窗（确认接入 + 进度显示）和多语言支持。

### 目标
- 实现两步弹窗流程：确认接入 → 进度显示
- 模拟异步进度（UI 演示版本，不对接真实后端）
- 支持 5 种语言（英文、简体中文、繁体中文、韩文、日文）
- 与现有页面风格保持一致（深色主题 + 荧光绿配色）

### 非目标
- 不实现真实的后端 API 对接（预留接口位置，等后端 ready 后替换）
- 不实现错误处理和重试逻辑（UI 演示版本必定成功）
- 不实现取消进度功能（进度弹窗不可取消）

---

## 2. 用户交互流程

```
用户在 Web3LoginActivity
    ↓
点击"一键免密接入体验"按钮
    ↓
弹窗1: 确认接入对话框
    ├─ 点击"取消" → 关闭弹窗，留在当前页面
    └─ 点击"确认接入"
        ↓
    弹窗2: 进度显示对话框
        ├─ Step 1 (0.0s - 0.8s): 设备指纹采集完成 ✓
        ├─ Step 2 (0.8s - 1.6s): AI 风控验证通过 ✓
        ├─ Step 3 (1.6s - 2.4s): 临时账号生成中... ✓
        ├─ Step 4 (2.4s - 3.2s): 配置同步完成 ✓
        └─ 进度条: 0% → 100%
            ↓
    3.2s 后自动关闭弹窗
        ↓
    显示 Toast: "免密接入成功，已登录 Telegram 账号！"
        ↓
    跳转到 LaunchActivity (Telegram 主界面)
```

---

## 3. 架构设计

### 3.1 组件结构

```
Web3LoginActivity
├── quickAccessMode() 方法（现有，需修改）
│   └── 调用 showQuickAccessConfirmDialog()
│
├── showQuickAccessConfirmDialog()
│   ├── 创建 AlertDialog（确认弹窗）
│   └── 点击"确认接入" → showQuickAccessProgressDialog()
│
└── showQuickAccessProgressDialog()
    ├── 创建自定义 Dialog（进度弹窗）
    ├── 使用 Handler 模拟异步进度
    ├── 更新 4 个步骤状态 + 进度条
    └── 完成后跳转 + 显示 Toast
```

### 3.2 对话框类型

**确认弹窗：**
- 类型：`AlertDialog`（Android 原生）
- 样式：深色主题
- 可取消：点击外部或返回键可取消

**进度弹窗：**
- 类型：自定义 `Dialog`
- 样式：自定义布局（`dialog_quick_access_progress.xml`）
- 不可取消：`setCancelable(false)`，禁用返回键

---

## 4. UI 设计

### 4.1 确认弹窗设计

**布局：**
```
┌────────────────────────────────┐
│  一键免密接入体验                │ ← 标题
├────────────────────────────────┤
│  无需注册或输入密码，使用设备    │
│  指纹 + AI 动态风控直接生成临   │
│  时账号体验完整功能              │ ← 说明文字
├────────────────────────────────┤
│      [取消]      [确认接入]      │ ← 按钮
└────────────────────────────────┘
```

**颜色方案：**
- 背景色：#1A1A1A（深灰）
- 标题文字：#FFFFFF（白色）
- 说明文字：#CCCCCC（浅灰）
- 取消按钮：#666666（灰色）
- 确认按钮：#CFFF55（荧光绿）

---

### 4.2 进度弹窗设计

**布局：**
```
┌────────────────────────────────┐
│         🛡️                      │ ← 旋转加载图标（用户盾牌）
│    (旋转动画)                   │
├────────────────────────────────┤
│  正在创建临时账号                │ ← 标题
├────────────────────────────────┤
│  ✓ 设备指纹采集完成              │ ← Step 1
│  ✓ AI 风控验证通过              │ ← Step 2
│  ⏳ 临时账号生成中...           │ ← Step 3（进行中）
│  ○ 配置同步完成                 │ ← Step 4（未开始）
├────────────────────────────────┤
│  ▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░ 50%      │ ← 进度条
└────────────────────────────────┘
```

**步骤状态：**
- 未开始：灰色圆圈 ○ + 灰色文字
- 进行中：旋转加载图标 ⏳ + 荧光绿文字
- 已完成：绿色勾选 ✓ + 白色文字

**颜色方案：**
- 背景色：#0E0E10（超深色）
- 边框：#CFFF55 透明度 30%
- 标题文字：#FFFFFF（白色）
- 荧光绿：#CFFF55
- 灰色：#666666
- 进度条背景：#333333
- 进度条前景：#CFFF55

---

## 5. 动画设计

### 5.1 旋转图标动画

**顶部用户盾牌图标：**
- 类型：无限旋转动画（Rotate Animation）
- 持续时间：1.5s/圈
- 插值器：LinearInterpolator（匀速）
- 方向：顺时针

### 5.2 步骤状态动画

**时间轴（总时长 3.2s）：**

| 时间 | Step 1 | Step 2 | Step 3 | Step 4 | 进度条 |
|------|--------|--------|--------|--------|--------|
| 0.0s | ⏳ 进行中 | ○ 未开始 | ○ 未开始 | ○ 未开始 | 0% |
| 0.8s | ✓ 完成 | ⏳ 进行中 | ○ 未开始 | ○ 未开始 | 25% |
| 1.6s | ✓ 完成 | ✓ 完成 | ⏳ 进行中 | ○ 未开始 | 50% |
| 2.4s | ✓ 完成 | ✓ 完成 | ✓ 完成 | ⏳ 进行中 | 75% |
| 3.2s | ✓ 完成 | ✓ 完成 | ✓ 完成 | ✓ 完成 | 100% |

**步骤切换动画：**
- 图标切换：淡入淡出（Alpha 动画，200ms）
- 文字颜色：平滑过渡（Color 动画，200ms）

### 5.3 进度条动画

**ProgressBar 配置：**
- 类型：Horizontal ProgressBar
- 样式：自定义 Drawable（圆角矩形）
- 动画：平滑增长（使用 ObjectAnimator）
- 每个步骤增加 25%，持续时间 800ms

---

## 6. 多语言资源

### 6.1 字符串清单

**确认弹窗（4条）：**
- `quick_access_dialog_title` - "一键免密接入体验"
- `quick_access_dialog_message` - "无需注册或输入密码，使用设备指纹 + AI 动态风控直接生成临时账号体验完整功能"
- `quick_access_dialog_confirm` - "确认接入"
- `quick_access_dialog_cancel` - "取消"

**进度弹窗（6条）：**
- `quick_access_progress_title` - "正在创建临时账号"
- `quick_access_step1` - "设备指纹采集完成"
- `quick_access_step2` - "AI 风控验证通过"
- `quick_access_step3` - "临时账号生成中..."
- `quick_access_step4` - "配置同步完成"
- `quick_access_success_toast` - "免密接入成功，已登录 Telegram 账号！"

**总计：10 条字符串 × 5 种语言 = 50 条**

### 6.2 完整翻译

#### 英文（values/strings.xml）

```xml
<!-- Quick Access Dialogs -->
<string name="quick_access_dialog_title">One-Click Guest Access</string>
<string name="quick_access_dialog_message">No registration or password required. Generate a temporary account instantly using device fingerprint + AI risk control to experience all features.</string>
<string name="quick_access_dialog_confirm">Confirm Access</string>
<string name="quick_access_dialog_cancel">Cancel</string>

<string name="quick_access_progress_title">Creating Temporary Account</string>
<string name="quick_access_step1">Device fingerprint collected</string>
<string name="quick_access_step2">AI risk control passed</string>
<string name="quick_access_step3">Generating temporary account...</string>
<string name="quick_access_step4">Configuration synced</string>
<string name="quick_access_success_toast">Access successful, logged into Telegram!</string>
```

#### 简体中文（values-zh-rCN/strings.xml）

```xml
<!-- 一键免密接入弹窗 -->
<string name="quick_access_dialog_title">一键免密接入体验</string>
<string name="quick_access_dialog_message">无需注册或输入密码，使用设备指纹 + AI 动态风控直接生成临时账号体验完整功能</string>
<string name="quick_access_dialog_confirm">确认接入</string>
<string name="quick_access_dialog_cancel">取消</string>

<string name="quick_access_progress_title">正在创建临时账号</string>
<string name="quick_access_step1">设备指纹采集完成</string>
<string name="quick_access_step2">AI 风控验证通过</string>
<string name="quick_access_step3">临时账号生成中...</string>
<string name="quick_access_step4">配置同步完成</string>
<string name="quick_access_success_toast">免密接入成功，已登录 Telegram 账号！</string>
```

#### 繁体中文（values-zh-rTW/strings.xml）

```xml
<!-- 一鍵免密接入彈窗 -->
<string name="quick_access_dialog_title">一鍵免密接入體驗</string>
<string name="quick_access_dialog_message">無需註冊或輸入密碼，使用裝置指紋 + AI 動態風控直接生成臨時帳號體驗完整功能</string>
<string name="quick_access_dialog_confirm">確認接入</string>
<string name="quick_access_dialog_cancel">取消</string>

<string name="quick_access_progress_title">正在建立臨時帳號</string>
<string name="quick_access_step1">裝置指紋採集完成</string>
<string name="quick_access_step2">AI 風控驗證通過</string>
<string name="quick_access_step3">臨時帳號生成中...</string>
<string name="quick_access_step4">配置同步完成</string>
<string name="quick_access_success_toast">免密接入成功，已登入 Telegram 帳號！</string>
```

#### 韩文（values-ko/strings.xml）

```xml
<!-- 원클릭 게스트 접속 대화상자 -->
<string name="quick_access_dialog_title">원클릭 게스트 접속</string>
<string name="quick_access_dialog_message">등록이나 비밀번호 없이 장치 지문 + AI 위험 통제를 사용하여 임시 계정을 즉시 생성하고 모든 기능을 체험하세요.</string>
<string name="quick_access_dialog_confirm">접속 확인</string>
<string name="quick_access_dialog_cancel">취소</string>

<string name="quick_access_progress_title">임시 계정 생성 중</string>
<string name="quick_access_step1">장치 지문 수집 완료</string>
<string name="quick_access_step2">AI 위험 통제 통과</string>
<string name="quick_access_step3">임시 계정 생성 중...</string>
<string name="quick_access_step4">구성 동기화 완료</string>
<string name="quick_access_success_toast">접속 성공, Telegram에 로그인되었습니다!</string>
```

#### 日文（values-ja/strings.xml）

```xml
<!-- ワンクリックゲストアクセスダイアログ -->
<string name="quick_access_dialog_title">ワンクリックゲストアクセス</string>
<string name="quick_access_dialog_message">登録やパスワード不要。デバイス指紋 + AIリスク管理で一時アカウントを即座に生成し、全機能を体験できます。</string>
<string name="quick_access_dialog_confirm">アクセス確認</string>
<string name="quick_access_dialog_cancel">キャンセル</string>

<string name="quick_access_progress_title">一時アカウント作成中</string>
<string name="quick_access_step1">デバイス指紋収集完了</string>
<string name="quick_access_step2">AIリスク管理通過</string>
<string name="quick_access_step3">一時アカウント生成中...</string>
<string name="quick_access_step4">設定同期完了</string>
<string name="quick_access_success_toast">アクセス成功、Telegramにログインしました！</string>
```

---

## 7. 技术实现

### 7.1 文件结构

**新增文件：**
```
TMessagesProj_App/src/main/res/
├── layout/
│   └── dialog_quick_access_progress.xml    # 进度弹窗布局
├── drawable/
│   ├── ic_user_shield.xml                  # 用户盾牌图标
│   ├── ic_check_circle.xml                 # 勾选图标
│   ├── ic_loading_circle.xml               # 加载圆圈图标
│   ├── progress_bar_bg.xml                 # 进度条背景
│   └── progress_bar_fg.xml                 # 进度条前景
└── anim/
    └── rotate_infinite.xml                 # 无限旋转动画
```

**修改文件：**
```
TMessagesProj_App/src/main/java/org/telegram/ui/
└── Web3LoginActivity.java                  # 添加弹窗逻辑

TMessagesProj_App/src/main/res/values*/
└── strings.xml                             # 添加 10 条新字符串（5种语言）
```

### 7.2 代码结构

**Web3LoginActivity.java 新增方法：**

```java
/**
 * 显示确认接入对话框
 */
private void showQuickAccessConfirmDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
    builder.setTitle(getString(R.string.quick_access_dialog_title));
    builder.setMessage(getString(R.string.quick_access_dialog_message));
    builder.setPositiveButton(getString(R.string.quick_access_dialog_confirm), (dialog, which) -> {
        dialog.dismiss();
        showQuickAccessProgressDialog();
    });
    builder.setNegativeButton(getString(R.string.quick_access_dialog_cancel), (dialog, which) -> {
        dialog.dismiss();
    });
    builder.show();
}

/**
 * 显示进度对话框
 */
private void showQuickAccessProgressDialog() {
    // 创建自定义 Dialog
    Dialog progressDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    int layoutId = getResources().getIdentifier("dialog_quick_access_progress", "layout", getPackageName());
    progressDialog.setContentView(layoutId);
    progressDialog.setCancelable(false);

    // 获取布局中的视图
    TextView step1 = progressDialog.findViewById(...);
    TextView step2 = progressDialog.findViewById(...);
    TextView step3 = progressDialog.findViewById(...);
    TextView step4 = progressDialog.findViewById(...);
    ProgressBar progressBar = progressDialog.findViewById(...);

    progressDialog.show();

    // 使用 Handler 模拟异步进度
    Handler handler = new Handler();

    // Step 1: 0.8s
    handler.postDelayed(() -> {
        updateStepStatus(step1, true);
        updateProgress(progressBar, 25);
    }, 800);

    // Step 2: 1.6s
    handler.postDelayed(() -> {
        updateStepStatus(step2, true);
        updateProgress(progressBar, 50);
    }, 1600);

    // Step 3: 2.4s
    handler.postDelayed(() -> {
        updateStepStatus(step3, true);
        updateProgress(progressBar, 75);
    }, 2400);

    // Step 4: 3.2s
    handler.postDelayed(() -> {
        updateStepStatus(step4, true);
        updateProgress(progressBar, 100);

        // 关闭弹窗，显示 Toast，跳转
        handler.postDelayed(() -> {
            progressDialog.dismiss();
            Toast.makeText(this, getString(R.string.quick_access_success_toast), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LaunchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 500);
    }, 3200);
}

/**
 * 更新步骤状态
 */
private void updateStepStatus(TextView stepView, boolean completed) {
    // 更改图标和文字颜色
}

/**
 * 更新进度条
 */
private void updateProgress(ProgressBar progressBar, int progress) {
    ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", progress);
    animator.setDuration(800);
    animator.setInterpolator(new DecelerateInterpolator());
    animator.start();
}
```

### 7.3 进度弹窗布局设计

**dialog_quick_access_progress.xml：**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="#CC000000"
    android:gravity="center"
    android:padding="24dp">

    <!-- 弹窗容器 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:background="@drawable/dialog_background"
        android:padding="24dp"
        android:gravity="center">

        <!-- 顶部旋转图标 -->
        <ImageView
            android:id="@+id/icon_loading"
            android:layout_width="64dp"
            android:layout_height="64dp"
            android:src="@drawable/ic_user_shield"
            android:tint="#CFFF55"
            android:layout_marginBottom="16dp" />

        <!-- 标题 -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/quick_access_progress_title"
            android:textColor="#FFFFFF"
            android:textSize="16sp"
            android:textStyle="bold"
            android:layout_marginBottom="24dp" />

        <!-- 步骤1 -->
        <LinearLayout
            android:id="@+id/step1_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginBottom="12dp">

            <ImageView
                android:id="@+id/step1_icon"
                android:layout_width="20dp"
                android:layout_height="20dp"
                android:src="@drawable/ic_loading_circle"
                android:tint="#CFFF55" />

            <TextView
                android:id="@+id/step1_text"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="12dp"
                android:text="@string/quick_access_step1"
                android:textColor="#CFFF55"
                android:textSize="14sp" />
        </LinearLayout>

        <!-- 步骤2、3、4 类似结构 -->
        <!-- ... -->

        <!-- 进度条 -->
        <ProgressBar
            android:id="@+id/progress_bar"
            android:layout_width="match_parent"
            android:layout_height="8dp"
            android:layout_marginTop="16dp"
            style="?android:attr/progressBarStyleHorizontal"
            android:progressDrawable="@drawable/progress_bar_bg"
            android:max="100"
            android:progress="0" />
    </LinearLayout>
</LinearLayout>
```

---

## 8. 测试计划

### 8.1 功能测试

**测试场景：**

| 场景 | 操作 | 预期结果 |
|------|------|----------|
| 显示确认弹窗 | 点击"一键免密接入体验"按钮 | 显示确认对话框，标题和文字正确显示 |
| 取消接入 | 点击"取消"按钮 | 弹窗关闭，留在 Web3LoginActivity |
| 确认接入 | 点击"确认接入"按钮 | 确认弹窗关闭，进度弹窗显示 |
| 进度动画 | 观察进度弹窗 | 4 个步骤依次完成，进度条平滑增长 |
| 完成跳转 | 等待 3.2s | 弹窗关闭，Toast 显示，跳转到 LaunchActivity |
| 返回键禁用 | 在进度弹窗按返回键 | 无响应，弹窗不关闭 |

### 8.2 多语言测试

**测试场景：**

| 系统语言 | 预期结果 |
|----------|----------|
| 简体中文 (zh-CN) | 所有文字显示简体中文 |
| 繁体中文 (zh-TW) | 所有文字显示繁体中文 |
| 韩文 (ko) | 所有文字显示韩文 |
| 日文 (ja) | 所有文字显示日文 |
| 英文 (en) | 所有文字显示英文 |
| 其他语言 | 回退到英文显示 |

### 8.3 动画测试

**测试内容：**
- 旋转图标平滑旋转，无卡顿
- 步骤状态切换平滑，图标和文字颜色过渡自然
- 进度条增长平滑，无跳变
- 总时长精确为 3.2s ± 0.1s

---

## 9. 后续扩展

### 9.1 预留接口位置

**当后端 API ready 后，需要替换的位置：**

```java
// 当前：模拟进度
handler.postDelayed(() -> {
    updateStepStatus(step1, true);
    updateProgress(progressBar, 25);
}, 800);

// 替换为：真实 API 调用
apiService.collectDeviceFingerprint(new Callback() {
    @Override
    public void onSuccess() {
        updateStepStatus(step1, true);
        updateProgress(progressBar, 25);
        // 继续下一步
    }

    @Override
    public void onError(String error) {
        // 错误处理
    }
});
```

### 9.2 错误处理（未来实现）

**错误场景：**
- 网络请求失败 → 显示错误提示，提供重试按钮
- API 返回错误 → 显示具体错误信息
- 超时 → 显示超时提示，提供重试按钮

### 9.3 取消功能（未来实现）

**用户需求：**
- 在进度弹窗添加"取消"按钮
- 点击后中断进度，返回登录页
- 需要后端支持取消 API

---

## 10. 设计决策记录

### 10.1 为什么使用两步弹窗？

**原因：**
1. 遵循 HTML demo 的设计
2. 给用户二次确认机会，避免误操作
3. 符合 Android Material Design 规范

### 10.2 为什么进度弹窗不可取消？

**原因：**
1. 模拟真实场景：账号创建过程不应中断
2. UI 演示版本没有取消逻辑
3. 总时长仅 3.2s，用户等待时间可接受

### 10.3 为什么选择 3.2s 总时长？

**原因：**
1. HTML demo 参考时长
2. 每步 0.8s，用户可以清楚看到进度变化
3. 不会太快（看不清）或太慢（等待焦虑）

### 10.4 为什么使用 Handler 而不是 RxJava/Coroutine？

**原因：**
1. Telegram 项目可能未引入这些库
2. Handler 是 Android 原生 API，无需额外依赖
3. 简单的延迟任务，Handler 足够

---

## 11. 实施清单

- [ ] 添加 5 种语言的字符串资源（10 条 × 5 = 50 条）
- [ ] 创建进度弹窗布局文件（`dialog_quick_access_progress.xml`）
- [ ] 创建图标资源（用户盾牌、勾选、加载圆圈）
- [ ] 创建进度条样式资源（背景、前景）
- [ ] 创建旋转动画资源（`rotate_infinite.xml`）
- [ ] 修改 `Web3LoginActivity.java`，添加弹窗逻辑
- [ ] 编译测试（验证 XML 格式和代码语法）
- [ ] 功能测试（5 种语言 × 6 个场景）
- [ ] 动画测试（验证平滑度和时长）
- [ ] 提交 git（提交信息：`feat: 添加一键免密接入弹窗功能`）

---

## 12. 参考资料

- HTML Demo: `/Users/jade/code/code/telegram/web3_telegram_super_app_prototype.html`
- Android Dialog 官方文档: https://developer.android.com/guide/topics/ui/dialogs
- Android Animation 官方文档: https://developer.android.com/guide/topics/graphics/view-animation
- Material Design - Dialogs: https://material.io/components/dialogs
