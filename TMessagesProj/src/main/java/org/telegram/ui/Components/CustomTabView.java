package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.ColorStateList;
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

    private ImageView iconView;
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

        // 图标（使用ImageView）
        iconView = new ImageView(getContext());
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
            AndroidUtilities.dp(24),
            AndroidUtilities.dp(24)
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

        // 启用无障碍支持
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    public void setTabIcon(int iconResId) {
        iconView.setImageResource(iconResId);
        // 尝试应用颜色状态列表
        try {
            int colorResId = getContext().getResources().getIdentifier("tab_icon_color", "color", getContext().getPackageName());
            if (colorResId != 0) {
                ColorStateList colorStateList = getContext().getResources().getColorStateList(colorResId);
                iconView.setImageTintList(colorStateList);
            }
        } catch (Exception e) {
            // 如果颜色状态列表不可用，手动设置颜色
            iconView.setColorFilter(isSelected ? COLOR_SELECTED : COLOR_UNSELECTED);
        }
    }

    public void setTabText(String text) {
        textView.setText(text);
        setContentDescription(text);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        int color = selected ? COLOR_SELECTED : COLOR_UNSELECTED;
        textView.setTextColor(color);

        // 更新图标颜色
        iconView.setSelected(selected);
        iconView.setColorFilter(color);

        // 更新文字粗细
        textView.setTypeface(null, selected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        invalidate();
    }

    public boolean isTabSelected() {
        return isSelected;
    }
}
