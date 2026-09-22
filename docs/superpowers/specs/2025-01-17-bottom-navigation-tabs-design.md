# Telegram Android 底部导航 Tab 改造设计文档

**日期**: 2025-01-17
**版本**: 1.0
**状态**: ✅ 已实施 (2026-09-23)

---

## 📋 需求概述

在 Telegram Android 二开项目中实现自定义底部导航，包含 4 个 Tab：

1. **Chats（聊天）** - 复用官方聊天列表
2. **Contacts（联系人）** - 复用官方联系人列表
3. **Discover（发现）** - 自定义占位页面
4. **Mine（我的）** - 复用官方 Profile 页面

**核心约束**:
- ✅ 所有自定义代码必须在 `TMessagesProj_App/` 目录
- ⚠️ 仅允许修改官方源码 **1行代码**（启动入口）
- ✅ 样式采用黑金配色（黑色背景 + 荧光绿 #CFFF55）

---

## 🏗️ 架构分析

### 官方实现

Telegram 官方已有完整的 Tab 实现：`MainTabsActivity`

**核心类结构**:
```
MainTabsActivity (extends ViewPagerActivity)
├── ViewPager (滑动容器)
├── MainTabsLayout (底部导航栏容器)
├── GlassTabView[] (5个Tab视图)
└── BaseFragment[] (4个页面Fragment)
```

**官方的 4 个 Tab**:
- Position 0: Chats (DialogsActivity)
- Position 1: Contacts (ContactsActivity)
- Position 2: Calls/Settings (CallLogActivity 或 SettingsActivity)
- Position 3: Profile (ProfileActivity)

**关键发现**:
- `createBaseFragmentAt(int position)` 方法负责创建每个位置的 Fragment
- Tab 视图通过 `GlassTabView.createMainTab()` 创建
- 第 3 个 Tab (POSITION_CALLS_OR_SETTINGS) 根据用户配置显示 Calls 或 Settings

---

## 🎯 实现方案

### 方案选择：继承 + 最小修改

**策略**: 继承 `MainTabsActivity` 并重写关键方法，仅修改第 3 个 Tab 的行为和样式。

### 核心修改点

#### 1. 创建自定义 MainTabsActivity

**文件位置**: `TMessagesProj_App/src/main/java/org/telegram/ui/CustomMainTabsActivity.java`

**核心逻辑**:
```java
public class CustomMainTabsActivity extends MainTabsActivity {

    @Override
    protected BaseFragment createBaseFragmentAt(int position) {
        // 重写第3个Tab，返回自定义的发现页面
        if (position == POSITION_CALLS_OR_SETTINGS) {
            Bundle args = new Bundle();
            args.putBoolean("hasMainTabs", true);
            return new DiscoverPlaceholderFragment(args);
        }
        // 其他Tab使用官方实现
        return super.createBaseFragmentAt(position);
    }

    @Override
    public View createView(Context context) {
        View view = super.createView(context);

        // 自定义第3个Tab的样式（替换官方的 GlassTabView）
        customizeTabView();

        return view;
    }

    private void customizeTabView() {
        // 将第3个Tab的文本改为"发现"
        // 将图标改为自定义图标
        // 注意：这里需要访问父类的 tabs[] 数组
    }
}
```

#### 2. 修改官方启动入口（唯一修改官方源码的地方）

**文件**: `TMessagesProj/src/main/java/org/telegram/ui/LaunchActivity.java`

**修改位置**: 4 处（全局替换）

**原代码**:
```java
MainTabsActivity mainTabsActivity = new MainTabsActivity();
```

**修改为**:
```java
// @custom 自定义二开代码 - 使用自定义Tab容器
// 修改原因：实现自定义底部导航（4个Tab：聊天/联系人/发现/我的）
// 修改日期：2025-01-17
// 冲突解决：搜索 "new MainTabsActivity()" 并全部替换为 "new CustomMainTabsActivity()"
MainTabsActivity mainTabsActivity = new CustomMainTabsActivity();
```

**修改位置详情**:
1. **第 586 行** - `onCreate()` 方法，首次创建 MainTabsActivity
2. **第 1188 行** - `switchToAccount()` 方法参数类型声明
3. **第 1191 行** - `switchToAccount()` 方法实现，切换账号时创建
4. **第 3300 行** - 处理深链接时创建
5. **第 3313 行** - 处理 intent 时创建

**注意事项**:
- 所有标注必须使用完全相同的格式
- 便于搜索和批量替换
- 合并冲突时，优先保留自定义代码

---

## 🎨 样式定制

### 挑战：完全替换官方的玻璃模糊风格

官方使用 `GlassTabView`，具有：
- 模糊背景效果（BlurredBackgroundDrawable）
- 圆角玻璃材质
- 动态颜色

**用户要求**：必须 100% 还原黑金配色设计稿，不能使用官方的模糊玻璃效果。

**定制方案**：完全自定义底部导航栏

由于必须 100% 还原设计稿，我们需要：
1. **完全隐藏官方的 `MainTabsLayout` 和 `tabsView`**
2. **创建全新的自定义底部导航栏**
3. **拦截 ViewPager 的滑动事件，同步自定义导航栏状态**

#### 实现步骤

**步骤 1：隐藏官方导航栏**
```java
@Override
public View createView(Context context) {
    View view = super.createView(context);

    // 隐藏官方的 tabsView
    try {
        Field tabsViewWrapperField = MainTabsActivity.class.getDeclaredField("tabsViewWrapper");
        tabsViewWrapperField.setAccessible(true);
        FrameLayout tabsViewWrapper = (FrameLayout) tabsViewWrapperField.get(this);
        tabsViewWrapper.setVisibility(View.GONE);
    } catch (Exception e) {
        FileLog.e(e);
    }

    return view;
}
```

**步骤 2：创建自定义底部导航栏**
```java
private void createCustomBottomNavigation() {
    // 创建自定义导航栏
    customBottomNav = new CustomBottomNavigationView(getContext());
    customBottomNav.setOnTabSelectedListener(position -> {
        // 切换 ViewPager
        if (viewPager != null) {
            viewPager.scrollToPosition(position);
        }
    });

    // 添加到 contentView
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        AndroidUtilities.dp(56)
    );
    params.gravity = Gravity.BOTTOM;
    contentView.addView(customBottomNav, params);
}
```

**步骤 3：监听 ViewPager 滑动，同步导航栏**
```java
@Override
protected void onViewPagerTabAnimationUpdate(boolean manual) {
    super.onViewPagerTabAnimationUpdate(manual);

    // 同步自定义导航栏的选中状态
    if (customBottomNav != null && viewPager != null) {
        float position = viewPager.getPositionAnimated();
        customBottomNav.updatePosition(position);
    }
}
```

---

## 📦 组件清单

### 需要创建的文件

#### 1. CustomMainTabsActivity.java
**路径**: `TMessagesProj_App/src/main/java/org/telegram/ui/CustomMainTabsActivity.java`

**职责**:
- 继承 `MainTabsActivity`
- 重写 `createBaseFragmentAt()` 返回自定义发现页面
- 重写 `createView()` 定制第 3 个 Tab 的样式

**复杂度**: 中等
**风险**: 需要处理反射访问父类私有字段

#### 2. DiscoverPlaceholderFragment.java
**路径**: `TMessagesProj_App/src/main/java/org/telegram/ui/DiscoverPlaceholderFragment.java`

**职责**:
- 继承 `BaseFragment`
- 显示占位界面："发现页面，待开发"

**复杂度**: 低
**状态**: 已实现

#### 3. CustomTabView.java（必需）
**路径**: `TMessagesProj_App/src/main/java/org/telegram/ui/Components/CustomTabView.java`

**职责**:
- 完全自定义的 Tab 视图
- 实现黑金配色（黑色背景 #000000 + 荧光绿选中 #CFFF55）
- 不依赖 `GlassTabView`

**复杂度**: 中等

#### 4. CustomBottomNavigationView.java（必需）
**路径**: `TMessagesProj_App/src/main/java/org/telegram/ui/Components/CustomBottomNavigationView.java`

**职责**:
- 底部导航栏容器
- 管理 4 个 `CustomTabView`
- 处理点击事件和状态同步
- 黑金配色样式

**复杂度**: 中等

---

## 🔍 关键技术点

### 1. 反射访问父类字段

**问题**: `MainTabsActivity` 的 `tabs[]` 数组是 `public`，但 `tabsView` 是 `private`

**解决方案**:
```java
Field tabsViewField = MainTabsActivity.class.getDeclaredField("tabsView");
tabsViewField.setAccessible(true);
MainTabsLayout tabsView = (MainTabsLayout) tabsViewField.get(this);
```

### 2. Tab 动画兼容性

官方的 `GlassTabView` 使用 `TabAnimation` 枚举：
- `CHATS` - 聊天动画
- `CONTACTS` - 联系人动画
- `CALLS` - 通话动画
- `SETTINGS` - 设置动画

**自定义发现 Tab 的动画**:
- 选项 A: 复用 `SETTINGS` 动画
- 选项 B: 创建新的动画类型（需要修改 `GlassTabView`）
- **推荐**: 选项 A，使用现有动画

### 3. Fragment 生命周期

**注意事项**:
- `DiscoverPlaceholderFragment` 必须正确实现 `createView()`
- 必须设置 `args.putBoolean("hasMainTabs", true)` 以正确集成

### 4. 多语言支持

**已完成**: 字符串资源已在第一次实现中添加
- `values/strings.xml`
- `values-zh-rCN/strings.xml`
- `values-zh-rTW/strings.xml`
- `values-ja/strings.xml`
- `values-ko/strings.xml`

---

## ⚠️ 风险评估

### 高风险点

#### 1. LaunchActivity 修改冲突
**风险等级**: 🔴 高
**描述**: 修改官方 `LaunchActivity.java` 的 4 处代码
**缓解措施**:
- 使用明确的代码注释标记：`@custom 自定义二开代码`
- 在合并前搜索所有 `new MainTabsActivity()` 的位置
- 建立自动化脚本检测冲突

#### 2. 反射访问失败
**风险等级**: 🟡 中
**描述**: 未来官方可能重构 `MainTabsActivity` 的内部结构
**缓解措施**:
- 添加 try-catch 处理
- 提供降级方案（使用官方样式）
- 记录详细日志

#### 3. 样式兼容性
**风险等级**: 🟢 低（已解决）
**描述**: ~~黑金配色可能与官方主题系统冲突~~
**解决方案**:
- ✅ 完全隐藏官方的 `MainTabsLayout`
- ✅ 创建独立的自定义底部导航栏
- ✅ 硬编码颜色值，不依赖 `Theme.getColor()` 系统
- ✅ 100% 还原设计稿

### 中风险点

#### 4. ViewPager 滑动手势
**风险等级**: 🟢 低
**描述**: 自定义 Fragment 可能影响滑动体验
**缓解措施**:
- 实现 `TabFragmentDelegate` 接口
- 正确处理 `canParentTabsSlide()` 方法

---

## 📝 实施步骤

### 阶段 1: 核心功能实现

1. **创建 CustomMainTabsActivity**
   - 继承 `MainTabsActivity`
   - 重写 `createBaseFragmentAt()`
   - 验证第 3 个 Tab 显示自定义页面

2. **修改 LaunchActivity 启动入口**
   - 全局搜索 `new MainTabsActivity()`
   - 替换为 `new CustomMainTabsActivity()`
   - 编译验证

3. **测试基础功能**
   - 启动应用，验证 4 个 Tab 是否正常显示
   - 切换 Tab，验证页面是否正确加载
   - 测试滑动手势

### 阶段 2: 样式定制

4. **定制第 3 个 Tab 的样式**
   - 实现 `customizeTabView()` 方法
   - 替换或修改 `GlassTabView`
   - 应用黑金配色

5. **测试样式兼容性**
   - 切换系统深色/浅色模式
   - 验证颜色显示正确
   - 检查动画流畅性

### 阶段 3: 优化和测试

6. **处理边缘情况**
   - 多账户切换
   - 横屏/竖屏切换
   - 分屏模式

7. **性能优化**
   - 减少反射调用
   - 优化 Fragment 创建逻辑

---

## 🧪 测试计划

### 功能测试

- [ ] 应用启动，默认显示 Chats Tab
- [ ] 点击 Contacts Tab，切换到联系人页面
- [ ] 点击 Discover Tab，显示占位页面
- [ ] 点击 Mine Tab，显示 Profile 页面
- [ ] 左右滑动手势切换 Tab
- [ ] Tab 切换动画流畅

### 样式测试

- [ ] 第 3 个 Tab 显示"发现"文字
- [ ] 选中状态显示荧光绿（#CFFF55）
- [ ] 未选中状态显示灰色
- [ ] 背景为黑色（#000000）

### 兼容性测试

- [ ] Android 8.0 - 14 系统版本
- [ ] 小屏手机（< 5.5 寸）
- [ ] 平板模式
- [ ] 分屏模式

### 回归测试

- [ ] 官方的聊天、联系人、设置功能正常
- [ ] 通知、消息推送正常
- [ ] 多账户切换正常

---

## 📊 技术债务

### 已知限制

1. **必须修改官方源码 1 处**
   - 位置：`LaunchActivity.java`
   - 影响：每次上游更新需要手动合并

2. **依赖反射访问**
   - 可能在未来版本失效
   - 需要持续维护

3. **样式定制有限**
   - 受限于 `GlassTabView` 的结构
   - 可能无法 100% 还原设计稿

### 未来改进方向

1. **探索 Xposed Hook 方案**
   - 运行时替换 `MainTabsActivity`
   - 完全避免修改官方源码

2. **完全自定义底部导航**
   - 不依赖 `MainTabsActivity`
   - 完全控制样式和行为
   - 代价：维护成本高

3. **提交 PR 到官方**
   - 将自定义功能贡献回上游
   - 成为官方功能的一部分

---

## ✅ 验收标准

### 必须满足

- [ ] 4 个 Tab 正常显示和切换
- [ ] 第 3 个 Tab 显示自定义发现页面
- [ ] 其他 3 个 Tab 复用官方功能
- [ ] 所有自定义代码在 `TMessagesProj_App/`（除 LaunchActivity 4 处修改）
- [ ] 样式 100% 还原黑金配色设计稿
  - 背景色：#000000（纯黑）
  - 选中色：#CFFF55（荧光绿）
  - 未选中色：#666666（灰色）
  - 分割线：#292929（深灰）
- [ ] 编译无错误，运行无崩溃
- [ ] 通过基础功能测试
- [ ] LaunchActivity 修改处有清晰标注，便于冲突解决

### 不接受

- [ ] 样式只有 80-90% 还原度
- [ ] 保留官方的模糊玻璃效果
- [ ] 降级使用官方样式（仅在反射失败时临时降级）

---

## 📚 参考资料

- [Telegram Android 源码](https://github.com/DrKLO/Telegram)
- `MainTabsActivity.java` - 官方 Tab 实现
- `ViewPagerActivity.java` - ViewPager 基类
- `BaseFragment.java` - Fragment 基类
- `GlassTabView.java` - Tab 视图组件

---

## 🔄 变更历史

| 版本 | 日期 | 作者 | 变更说明 |
|------|------|------|----------|
| 1.0  | 2025-01-17 | Claude | 初始版本 |

---

**审核人**: @jade
**下一步**: 待审核通过后，开始编写实施计划
