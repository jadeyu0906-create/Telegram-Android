# Upstream 同步与冲突解决规范

**目的**：本项目是 Telegram Android 的二开 fork（`upstream` = `https://github.com/DrKLO/Telegram.git`）。随着自定义改动增多，本文档规范如何快速、安全地同步官方源码并解决冲突。

---

## 1. 改动分类

所有改动分两类，同步时的表现完全不同：

| 类型 | 表现 | 同步结果 |
|------|------|---------|
| **新增文件**（自定义类/资源） | upstream 不存在这些文件 | 永不冲突，自动保留 |
| **修改官方文件**（内联改动） | upstream 也有该文件 | 官方若改到同一行才冲突 |

**核心原则**：能放新文件就放新文件（继承/重写），绝不在官方文件里内联写大段逻辑。入口点（如 `LaunchActivity` 的实例化）是逃不掉的最小改动。

---

## 2. 官方源码改动登记表

> 每次新增官方源码改动，必须在此登记。同步时照表核对。

| 文件 | 改动位置（行号） | 改动内容 | 冲突解法 |
|------|----------------|---------|---------|
| `TMessagesProj/src/main/java/org/telegram/ui/LaunchActivity.java` | 589 / 1196 / 3312 / 3329 | `new MainTabsActivity()` → `new CustomMainTabsActivity()` | 保留 `@custom` 版，删掉 upstream 的 `new MainTabsActivity()` |

**检索规则**：合并后全局搜索 `new MainTabsActivity()`，确认官方是否有新增的第 N 处创建点，逐处替换为 `new CustomMainTabsActivity()`。

---

## 3. 自定义改动清单（新增文件，无冲突）

### 3.1 核心模块 `TMessagesProj`

| 文件 | 说明 |
|------|------|
| `org/telegram/ui/CustomMainTabsActivity.java` | 自定义主 Tab 容器（继承 `MainTabsActivity`） |
| `org/telegram/ui/DiscoverPlaceholderFragment.java` | 「发现」占位页 |
| `org/telegram/ui/Components/CustomTabView.java` | 单个 Tab 视图（黑金配色） |
| `org/telegram/ui/Components/CustomBottomNavigationView.java` | 底部导航栏容器 |

> ⚠️ 说明：这些类**必须**在 `TMessagesProj`（而非 `TMessagesProj_App`），因为 `LaunchActivity` 在 `TMessagesProj`，且 `TMessagesProj_App` 依赖 `TMessagesProj`（单向依赖），官方代码无法反向引用 App 模块的类。

### 3.2 App 模块 `TMessagesProj_App`

| 文件 | 说明 |
|------|------|
| `org/telegram/ui/SplashAdActivity.java` | 开屏广告页 |
| `res/values*/strings.xml`（5 份：default/zh-rCN/zh-rTW/ja/ko） | 自定义多语言 key（`tab_chats` 等） |
| `res/layout/activity_splash_ad.xml` 等 | 开屏/占位布局 |
| `res/anim/` | 开屏动画 |

---

## 4. 同步流程（标准操作）

```bash
git fetch upstream
git merge upstream/master          # 或 git rebase upstream/master
```

1. 冲突只会出现在**登记表里的「修改官方文件」**（目前仅 `LaunchActivity.java`）。新增文件不会冲突。
2. 解决 `LaunchActivity` 冲突：保留 `@custom` 版，删掉 upstream 的 `new MainTabsActivity()`。
3. 全局搜索 `new MainTabsActivity()`，确认官方没有新增创建点。
4. 编译验证：
   ```bash
   ./gradlew :TMessagesProj:compileDebugJavaWithJavac
   ```

---

## 5. 冲突解决规则（机械操作）

每处 `@custom` 改动都是「单行 + 明确注释」，解决方式固定：

```
冲突块内：
  upstream 侧：new MainTabsActivity()
  我方侧    ：new CustomMainTabsActivity()  // 保留这行 + @custom 注释
```

**一律保留我方 `@custom` 版本**。除非官方对该行做了重大重构（此时需人工评估是否需要重写自定义逻辑）。

---

## 6. 强烈建议：开启 git rerere

`rerere` 会记录每次冲突的解决结果，下次同样冲突自动重放，只需 `git add` 确认：

```bash
git config rerere.enabled true
```

冲突越多，省的时间越多。这是应对「未来改动越来越多」最有效的工具。

---

## 7. 面向未来改动的分层原则

| 优先级 | 做法 | 冲突风险 |
|--------|------|---------|
| 1（首选） | 新增文件 + 继承/重写 | 零 |
| 2（必要入口） | 官方文件内单行改动 + `@custom` 注释 + 登记 | 低（仅同行冲突） |
| 3（可开关功能） | `BuildVars` 运行时开关 | 同 2（判断点仍需改官方代码） |

> 条件编译（`BuildVars` 开关）和注释**都不能减少冲突**，它们解决的是「功能可开关」和「冲突后快速认出」。减少冲突只能靠「改官方越少、每处越窄、能放新文件就不改官方」。

---

## 8. 变更历史

| 日期 | 说明 |
|------|------|
| 2026-09-24 | 初始版本，登记底部导航 Tab 改动的冲突解法 |
