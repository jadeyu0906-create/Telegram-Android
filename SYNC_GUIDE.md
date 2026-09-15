# 🔄 Telegram Upstream 同步快速指南

## 快速同步命令（复制粘贴）

### 方式一：Rebase（推荐）

```bash
# 1. 保存当前工作
git add .
git commit -m "chore: 同步前保存"

# 2. 拉取并同步
git fetch upstream
git rebase upstream/master

# 3. 如有冲突，解决后：
git add .
git rebase --continue

# 4. 推送（需要强制推送）
git push origin master --force-with-lease

# 5. 验证编译
./gradlew clean
./gradlew TMessagesProj_App:assembleDebug
```

### 方式二：Merge（保留历史）

```bash
# 1. 拉取并合并
git fetch upstream
git merge upstream/master

# 2. 如有冲突，解决后：
git add .
git commit -m "chore: 合并 Telegram 官方更新"

# 3. 推送
git push origin master

# 4. 验证编译
./gradlew clean
./gradlew TMessagesProj_App:assembleDebug
```

---

## 🔥 冲突处理速查表

### 常见冲突文件

| 文件 | 冲突原因 | 处理方式 |
|-----|---------|---------|
| `TMessagesProj/config/*/AndroidManifest.xml` | 我们添加了自定义 Activity | 保留双方修改 |
| `gradle.properties` | 版本号更新 | 使用官方版本号 |
| `build.gradle` | 依赖更新 | 使用官方依赖版本 |
| `TMessagesProj_App/src/main/*` | 我们的自定义代码 | ✅ 不会冲突 |

### AndroidManifest.xml 冲突解决模板

```xml
<!-- ✅ 正确：保留双方的修改 -->

<!-- 我们的自定义 Activity -->
<activity
    android:name="org.telegram.ui.SplashAdActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
    </intent-filter>
</activity>

<!-- 官方新增的组件（保留） -->
<service
    android:name="org.telegram.messenger.OfficialNewService"
    android:exported="false"/>
```

---

## ⚠️ 紧急回滚

如果同步后出现严重问题：

```bash
# 1. 查看最近的提交
git log --oneline -10

# 2. 回滚到同步前的提交
git reset --hard <同步前的commit-hash>

# 3. 强制推送
git push origin master --force

# 4. 重新尝试同步（检查问题）
```

---

## 📅 同步频率建议

- ✅ **每月同步一次**：保持与官方版本接近，减少冲突积累
- ⚠️ **重大更新立即同步**：安全补丁、重要功能
- ❌ **避免长期不同步**：超过 3 个月会导致大量冲突

---

## 🎯 同步检查清单

```bash
# 同步前
□ git status 确认工作区干净
□ 备份当前分支：git branch backup-$(date +%Y%m%d)
□ 查看官方更新内容：git log HEAD..upstream/master

# 同步中
□ 仔细检查每个冲突文件
□ 保留我们的自定义功能
□ 采用官方的版本号和依赖

# 同步后
□ ./gradlew clean
□ 编译所有变体成功
□ 安装 APK 测试功能
□ 验证自定义功能正常
□ 更新 CHANGELOG.md
```

---

## 📞 遇到问题？

1. 查看完整文档：[DEVELOPMENT.md](DEVELOPMENT.md)
2. 查看 Git 历史：`git log --oneline --graph`
3. 参考知名二开项目：Nekogram、Telegram-FOSS

---

**上次同步时间**：2026-01-XX（更新此日期）
**当前官方版本**：12.10.1 (7038)
**下次同步建议**：2026-02-XX
