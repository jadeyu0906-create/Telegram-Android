package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

/**
 * @custom 自定义二开代码
 * 自定义底部导航栏 - 黑金配色
 */
public class CustomBottomNavigationView extends LinearLayout {

    // 黑金配色
    private static final int COLOR_BG = 0xFF000000;           // 纯黑背景
    private static final int COLOR_DIVIDER = 0xFF292929;      // 分割线
    private static final int TAB_COUNT = 4;                   // Tab数量

    private CustomTabView[] tabs;
    private int selectedPosition = 0;
    private Paint dividerPaint;
    private OnTabSelectedListener listener;

    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    public CustomBottomNavigationView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setBackgroundColor(COLOR_BG);
        setWeightSum(TAB_COUNT);
        setWillNotDraw(false);

        dividerPaint = new Paint();
        dividerPaint.setColor(COLOR_DIVIDER);
        dividerPaint.setStrokeWidth(AndroidUtilities.dp(1));

        createTabs();
    }

    private void createTabs() {
        tabs = new CustomTabView[TAB_COUNT];

        // Tab 0: Chats
        tabs[0] = createTab("💬", getTabText("tab_chats", "Chats"), 0);

        // Tab 1: Contacts
        tabs[1] = createTab("👥", getTabText("tab_contacts", "Contacts"), 1);

        // Tab 2: Discover
        tabs[2] = createTab("🔍", getTabText("tab_discover", "Discover"), 2);

        // Tab 3: Mine
        tabs[3] = createTab("👤", getTabText("tab_mine", "Mine"), 3);

        // 默认选中第一个
        setSelectedTab(0);
    }

    private CustomTabView createTab(String emoji, String text, final int position) {
        CustomTabView tab = new CustomTabView(getContext());
        tab.setTabIcon(emoji);
        tab.setTabText(text);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            0,
            LayoutParams.MATCH_PARENT,
            1.0f
        );
        tab.setLayoutParams(params);

        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedTab(position);
                if (listener != null) {
                    listener.onTabSelected(position);
                }
            }
        });

        addView(tab);
        return tab;
    }

    private String getTabText(String key, String defaultValue) {
        try {
            // 注意: 资源查找在初始化时仅调用4次，性能影响可忽略
            int resId = getContext().getResources().getIdentifier(key, "string", getContext().getPackageName());
            if (resId != 0) {
                return getContext().getString(resId);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return defaultValue;
    }

    public void setSelectedTab(int position) {
        if (position < 0 || position >= tabs.length) {
            return;
        }

        // 取消之前选中的
        if (selectedPosition >= 0 && selectedPosition < tabs.length) {
            tabs[selectedPosition].setSelected(false);
        }

        // 选中新的
        selectedPosition = position;
        tabs[position].setSelected(true);
    }

    public void updatePosition(float position) {
        // ViewPager滑动时的连续位置更新
        // 可以实现渐变动画，暂时简化处理
        int nearestPosition = Math.round(position);
        if (nearestPosition != selectedPosition) {
            setSelectedTab(nearestPosition);
        }
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制顶部分割线
        canvas.drawLine(0, 0, getWidth(), 0, dividerPaint);
    }
}
