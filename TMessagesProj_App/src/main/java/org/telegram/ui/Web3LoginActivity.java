package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Web3 登录页面 (A02)
 *
 * 功能：
 * 1. Telegram 账号快捷登录
 * 2. 一键免密接入体验（两步弹窗：确认接入 + 进度显示）
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
     * 两步弹窗：确认接入 → 进度显示
     */
    private void quickAccessMode() {
        showQuickAccessConfirmDialog();
    }

    /**
     * 显示确认接入对话框（复刻 HTML modal-one-click）
     */
    private void showQuickAccessConfirmDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(getResId("dialog_quick_access_confirm", "layout"));
        dialog.setCancelable(true);

        Button btnCancel = dialog.findViewById(getResId("btn_cancel", "id"));
        Button btnConfirm = dialog.findViewById(getResId("btn_confirm", "id"));

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            showQuickAccessProgressDialog();
        });

        dialog.show();
    }

    /**
     * 显示进度对话框（复刻 HTML modal-login-progress）
     * 旋转圆环 + 进度条 0→100，完成后跳转
     */
    private void showQuickAccessProgressDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(getResId("dialog_quick_access_progress", "layout"));
        dialog.setCancelable(false);

        ImageView iconLoadingRing = dialog.findViewById(getResId("icon_loading_ring", "id"));
        ProgressBar progressBar = dialog.findViewById(getResId("progress_bar", "id"));

        dialog.show();

        // 旋转圆环动画
        Animation rotate = AnimationUtils.loadAnimation(this, getResId("rotate_infinite", "anim"));
        iconLoadingRing.startAnimation(rotate);

        // 进度条动画 0→100 (800ms)
        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        animator.setDuration(800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();

        // 1100ms 后关闭弹窗，显示 Toast，跳转
        new Handler().postDelayed(() -> {
            dialog.dismiss();
            Toast.makeText(this, getStringByName("quick_access_success_toast"), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LaunchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1100);
    }

    /**
     * 动态获取资源 ID（兼容多变体 namespace）
     */
    private int getResId(String name, String type) {
        return getResources().getIdentifier(name, type, getPackageName());
    }

    /**
     * 动态获取字符串资源
     */
    private String getStringByName(String name) {
        return getString(getResId(name, "string"));
    }

    @Override
    public void onBackPressed() {
        // 禁用返回键，用户必须选择登录方式
        // 如果需要允许返回到闪屏页，可以调用 super.onBackPressed();
    }
}
