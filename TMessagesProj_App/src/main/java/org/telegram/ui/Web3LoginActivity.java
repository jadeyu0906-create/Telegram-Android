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
     * 跳转到官方 LaunchActivity 进行正常登录流程
     */
    private void loginWithTelegram() {
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
