package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Web3 登录页面 (A02)
 *
 * 功能：
 * 1. Telegram 账号快捷登录
 * 2. 一键免密接入体验
 *
 * 设计参考：web3_telegram_super_app_prototype.html (screen-login)
 *
 * @author Jade Yu
 * @date 2026-01-XX
 * @custom 自定义二开代码，不影响官方更新
 */
public class Web3LoginActivity extends Activity {

    private Button btnTelegramLogin;
    private Button btnQuickAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 动态加载布局资源
        int layoutId = getResources().getIdentifier("activity_web3_login", "layout", getPackageName());
        setContentView(layoutId);

        initViews();
        setupListeners();
    }

    private void initViews() {
        // 动态获取视图ID
        int btnTelegramLoginId = getResources().getIdentifier("btn_telegram_login", "id", getPackageName());
        int btnQuickAccessId = getResources().getIdentifier("btn_quick_access", "id", getPackageName());

        btnTelegramLogin = findViewById(btnTelegramLoginId);
        btnQuickAccess = findViewById(btnQuickAccessId);
    }

    private void setupListeners() {
        // Telegram 账号快捷登录
        btnTelegramLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithTelegram();
            }
        });

        // 一键免密接入体验
        btnQuickAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickAccessMode();
            }
        });
    }

    /**
     * Telegram 账号登录
     * 通过设置过期的登录状态，让 LaunchActivity 跳过 IntroActivity，
     * 同时让 LoginActivity 自动重置为第一步（输入手机号）
     */
    private void loginWithTelegram() {
        // 设置一个"过期的"登录状态（超过 24 小时）
        // 这样：
        // 1. LaunchActivity 会检测到 currentViewNum != 0，显示 LoginActivity 而不是 IntroActivity
        // 2. LoginActivity 会检测到状态过期（时间戳距今超过 24 小时），自动重置为 VIEW_PHONE_INPUT (0)
        long expiredTimestamp = (System.currentTimeMillis() / 1000) - (25 * 60 * 60); // 25 小时前

        getSharedPreferences("logininfo2", MODE_PRIVATE)
            .edit()
            .clear()
            .putInt("currentViewNum", 1)  // 设为非 0，跳过 IntroActivity
            .putInt("open", (int) expiredTimestamp)  // 设置过期时间戳
            .apply();

        // 跳转到 LaunchActivity
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 一键免密接入体验模式
     * TODO: 后续实现 Web3 钱包连接逻辑
     */
    private void quickAccessMode() {
        // 暂时也跳转到 LaunchActivity
        // 后续可以实现游客模式或 Web3 钱包连接
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // 禁用返回键，用户必须选择登录方式
        // 如果需要允许返回到闪屏页，可以调用 super.onBackPressed();
    }
}
