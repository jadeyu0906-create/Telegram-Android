# 「我的」Tab 显示设置页 设计文档

**日期**: 2026-09-24
**版本**: 1.0
**状态**: 待实施

---

## 需求

把底部导航第 4 个 Tab「我的」的内容，从官方 `ProfileActivity`（个人资料页）改为官方 `SettingsActivity`（设置页）。

**约束**：
- Tab 文字保持「我的」、图标保持 👤（用户已确认）
- 不新增官方源码改动，全部在自定义类内完成
- 最小改动

---

## 现状

官方 `MainTabsActivity` 的 4 个 Tab 位置：

| position | 常量 | 官方内容 | 本项目当前 |
|----------|------|---------|-----------|
| 0 | `POSITION_CHATS` | DialogsActivity（聊天） | 复用官方 |
| 1 | `POSITION_CONTACTS` | ContactsActivity（联系人） | 复用官方 |
| 2 | `POSITION_CALLS_OR_SETTINGS` | Calls/Settings | **DiscoverPlaceholderFragment（发现）** |
| 3 | `POSITION_PROFILE` | ProfileActivity（个人资料） | 复用官方（**本次要改**） |

`CustomMainTabsActivity.createBaseFragmentAt()` 目前已重写 position 2 为「发现」，position 3 走 `super` 返回 `ProfileActivity`。

---

## 方案

在 `CustomMainTabsActivity.createBaseFragmentAt()` 增加 position 3 分支，返回 `SettingsActivity`：

```java
@Override
protected BaseFragment createBaseFragmentAt(int position) {
    // 第3个Tab（index=2）使用自定义发现页面
    if (position == 2) {  // POSITION_CALLS_OR_SETTINGS
        Bundle args = new Bundle();
        args.putBoolean("hasMainTabs", true);
        return new DiscoverPlaceholderFragment();
    }
    // 第4个Tab「我的」显示官方设置页
    if (position == 3) {  // POSITION_PROFILE
        Bundle args = new Bundle();
        args.putBoolean("hasMainTabs", true);
        return new SettingsActivity(args);
    }
    return super.createBaseFragmentAt(position);
}
```

---

## 关键点

1. **`SettingsActivity` 天然支持作为 Tab 嵌入**：它实现 `MainTabsActivity.TabFragmentDelegate`，读取 `hasMainTabs=true` 后自动隐藏返回键、为底部导航栏预留高度（`SettingsActivity.java:186/220`）。官方在关闭「通话Tab」时就是用 `SettingsActivity` 当 Tab（`MainTabsActivity.java:822-824`）。

2. **Profile 入口不丢失**：设置页顶部账号栏（头像+昵称）点击后仍进入 `ProfileActivity`，个人资料页只是从底部 Tab 移到了设置页顶部入口。

3. **`position == 3` 硬编码**：`POSITION_PROFILE` 是 `private static final`（`MainTabsActivity.java:96`），子类无法引用，沿用现有 `position == 2 // POSITION_CALLS_OR_SETTINGS` 的硬编码+注释风格。

4. **官方对 position 3 的特殊处理无副作用**：
   - `dropFragmentAtPosition(POSITION_PROFILE)`（`MainTabsActivity.java:739-740`）离开时销毁该 Fragment 省内存，官方 profile 同样如此。
   - `checkUi_fadeView` 的 profile 渐变效果对设置页无影响。

---

## 改动文件

- 修改：`TMessagesProj/src/main/java/org/telegram/ui/CustomMainTabsActivity.java`
- 仅新增一个 `if` 分支，无新文件，无官方源码改动。

---

## 测试点

- [ ] 点「我的」→ 显示设置页（非个人资料页）
- [ ] 设置页内各设置项可正常进入、返回
- [ ] 设置页顶部账号栏 → 进入个人资料页
- [ ] 切走再切回，设置页正常重建、无崩溃
- [ ] 4 个 Tab 切换、滑动手势均正常
- [ ] Tab 文字仍为「我的」、图标仍为 👤

---

## 变更历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-09-24 | 初始版本 |
