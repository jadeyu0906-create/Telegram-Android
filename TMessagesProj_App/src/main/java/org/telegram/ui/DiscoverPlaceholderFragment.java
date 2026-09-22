package org.telegram.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;

/**
 * @custom 自定义二开代码
 * 发现页面占位符 Fragment
 * 临时占位，待后续实现具体功能
 */
public class DiscoverPlaceholderFragment extends BaseFragment {

    @Override
    public View createView(Context context) {
        // 使用代码创建占位视图，避免资源ID问题
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(0xFF000000); // 黑色背景
        layout.setPadding(
            AndroidUtilities.dp(24),
            AndroidUtilities.dp(24),
            AndroidUtilities.dp(24),
            AndroidUtilities.dp(24)
        );

        // 标题
        TextView title = new TextView(context);
        title.setText(getDiscoverText(context, "discover_placeholder_title", "Discover"));
        title.setTextColor(0xFFFFFFFF); // 白色
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        // 副标题
        TextView message = new TextView(context);
        message.setText(getDiscoverText(context, "discover_placeholder_message", "Under development"));
        message.setTextColor(0xFFA1A1A1); // 灰色
        message.setTextSize(16);
        message.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        messageParams.topMargin = AndroidUtilities.dp(16);
        message.setLayoutParams(messageParams);
        layout.addView(message);

        fragmentView = layout;
        return fragmentView;
    }

    /**
     * 获取文本（带多语言支持）
     */
    private String getDiscoverText(Context context, String key, String defaultValue) {
        try {
            int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());
            if (resId != 0) {
                return context.getString(resId);
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return defaultValue;
    }
}
