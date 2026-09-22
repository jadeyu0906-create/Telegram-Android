package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;

/**
 * @custom 自定义二开代码
 * 自定义Tab视图 - 黑金配色
 */
public class CustomTabView extends FrameLayout {

    // 黑金配色
    private static final int COLOR_SELECTED = 0xFFCFFF55;     // 荧光绿选中
    private static final int COLOR_UNSELECTED = 0xFF666666;   // 灰色未选中

    private TextView iconView;
    private TextView textView;
    private boolean isSelected = false;

    public CustomTabView(Context context) {
        super(context);
        init();
    }

    private void init() {
        // 容器布局（垂直）
        LinearLayout content = new LinearLayout(getContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);

        // 图标（使用emoji）
        iconView = new TextView(getContext());
        iconView.setTextSize(24);
        iconView.setGravity(Gravity.CENTER);
        iconView.setTextColor(COLOR_UNSELECTED);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
            AndroidUtilities.dp(28),
            AndroidUtilities.dp(28)
        );
        content.addView(iconView, iconParams);

        // 文字
        textView = new TextView(getContext());
        textView.setTextSize(10);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(COLOR_UNSELECTED);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        );
        textParams.topMargin = AndroidUtilities.dp(2);
        content.addView(textView, textParams);

        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        );
        addView(content, contentParams);
    }

    public void setTabIcon(String emoji) {
        iconView.setText(emoji);
    }

    public void setTabText(String text) {
        textView.setText(text);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        int color = selected ? COLOR_SELECTED : COLOR_UNSELECTED;
        iconView.setTextColor(color);
        textView.setTextColor(color);
    }

    public boolean isTabSelected() {
        return isSelected;
    }
}
