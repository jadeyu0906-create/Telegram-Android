package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;

/**
 * @custom 自定义二开代码
 * 主Tab容器Activity - 包装 LaunchActivity 并添加底部导航
 */
public class MainTabContainerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 直接启动官方的 LaunchActivity
        // 底部导航功能暂时通过其他方式实现
        Intent intent = new Intent(this, LaunchActivity.class);

        // 传递所有intent数据
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                intent.putExtras(getIntent().getExtras());
            }
            intent.setAction(getIntent().getAction());
            intent.setData(getIntent().getData());
        }

        startActivity(intent);
        finish();
    }
}
