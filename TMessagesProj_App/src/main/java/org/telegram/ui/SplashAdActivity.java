package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

/**
 * 开屏广告 Activity
 * u多担保品牌宣传页面，带倒计时自动跳转功能
 */
public class SplashAdActivity extends Activity {

    private static final int COUNTDOWN_SECONDS = 30;
    private CountDownTimer countDownTimer;

    private TextView skipButton;
    private View logoContainer;
    private Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置全屏沉浸式
        setupFullScreen();

        // 加载布局
        setContentView(getResources().getIdentifier("activity_splash_ad", "layout", getPackageName()));

        // 初始化视图
        initViews();

        // 启动动画
        startAnimations();

        // 启动倒计时
        startCountdown();

        // 设置点击事件
        setupClickListeners();
    }

    private void setupFullScreen() {
        // 隐藏状态栏和导航栏，全屏显示
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void initViews() {
        skipButton = findViewById(getResources().getIdentifier("skip_button", "id", getPackageName()));
        logoContainer = findViewById(getResources().getIdentifier("logo_container", "id", getPackageName()));
        enterButton = findViewById(getResources().getIdentifier("enter_button", "id", getPackageName()));
    }

    private void startAnimations() {
        if (logoContainer != null) {
            // Logo 缩放动画
            Animation scaleAnim = AnimationUtils.loadAnimation(
                this,
                getResources().getIdentifier("splash_logo_scale", "anim", getPackageName())
            );
            logoContainer.startAnimation(scaleAnim);
        }
    }

    private void startCountdown() {
        if (skipButton == null) {
            return;
        }

        countDownTimer = new CountDownTimer(COUNTDOWN_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                int skipAdResId = getResources().getIdentifier("splash_skip_ad", "string", getPackageName());
                skipButton.setText(getString(skipAdResId) + " " + seconds + "s");
            }

            @Override
            public void onFinish() {
                // 倒计时结束，自动跳转
                goToMainApp();
            }
        }.start();
    }

    private void setupClickListeners() {
        // 跳过按钮点击
        if (skipButton != null) {
            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMainApp();
                }
            });
        }

        // 进入按钮点击
        if (enterButton != null) {
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMainApp();
                }
            });
        }
    }

    /**
     * 跳转到主Tab容器页面
     */
    private void goToMainApp() {
        // 取消倒计时
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        try {
            // @custom 自定义二开代码 - 跳转到官方LaunchActivity
            // LaunchActivity会自动加载CustomMainTabsActivity（带自定义底部导航）
            Intent intent = new Intent(this, LaunchActivity.class);

            // 传递原始的 intent 数据（如果有的话）
            if (getIntent() != null && getIntent().getExtras() != null) {
                intent.putExtras(getIntent().getExtras());
            }

            // 传递原始的 action 和 data
            if (getIntent() != null) {
                intent.setAction(getIntent().getAction());
                intent.setData(getIntent().getData());
            }

            startActivity(intent);
            finish();

            // 平滑过渡动画
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } catch (Exception e) {
            e.printStackTrace();
            // 如果出错，直接结束当前页面
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 清理倒计时
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onBackPressed() {
        // 禁用返回键，防止用户在开屏广告时退出
        // 如果需要允许返回，可以调用 super.onBackPressed()
    }
}
