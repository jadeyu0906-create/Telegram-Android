package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.CustomBottomNavigationView;

import java.lang.reflect.Field;

/**
 * @custom 自定义二开代码
 * 自定义主Tab容器Activity
 * 继承官方MainTabsActivity，替换为黑金配色的自定义底部导航
 */
public class CustomMainTabsActivity extends MainTabsActivity {

    private CustomBottomNavigationView customBottomNav;

    @Override
    protected BaseFragment createBaseFragmentAt(int position) {
        // 第3个Tab（index=2）使用自定义发现页面
        if (position == 2) {  // POSITION_CALLS_OR_SETTINGS
            Bundle args = new Bundle();
            args.putBoolean("hasMainTabs", true);
            return new DiscoverPlaceholderFragment();
        }
        // 其他Tab使用官方实现
        return super.createBaseFragmentAt(position);
    }

    @Override
    public View createView(Context context) {
        FileLog.d("CustomMainTabsActivity: createView called");
        View view = super.createView(context);

        // 隐藏官方的导航栏
        hideOfficialTabsView();

        // 创建自定义导航栏
        createCustomBottomNavigation(context);

        FileLog.d("CustomMainTabsActivity: createView completed");
        return view;
    }

    private void hideOfficialTabsView() {
        try {
            FileLog.d("CustomMainTabsActivity: Attempting to hide official tabs");
            // 反射访问父类的 tabsViewWrapper 字段
            Field tabsViewWrapperField = MainTabsActivity.class.getDeclaredField("tabsViewWrapper");
            tabsViewWrapperField.setAccessible(true);
            FrameLayout tabsViewWrapper = (FrameLayout) tabsViewWrapperField.get(this);

            if (tabsViewWrapper != null) {
                tabsViewWrapper.setVisibility(View.GONE);
                FileLog.d("CustomMainTabsActivity: Official tabs hidden successfully");
            } else {
                FileLog.e("CustomMainTabsActivity: tabsViewWrapper is null");
            }
        } catch (NoSuchFieldException e) {
            FileLog.e("CustomMainTabsActivity: tabsViewWrapper field not found", e);
        } catch (IllegalAccessException e) {
            FileLog.e("CustomMainTabsActivity: Cannot access tabsViewWrapper field", e);
        } catch (Exception e) {
            FileLog.e("CustomMainTabsActivity: Error hiding official tabs", e);
        }
    }

    private void createCustomBottomNavigation(Context context) {
        FileLog.d("CustomMainTabsActivity: Creating custom bottom navigation");

        // contentView 是父类 ViewPagerActivity 的 protected 字段，可以直接访问
        if (contentView == null) {
            FileLog.e("CustomMainTabsActivity: contentView is null");
            return;
        }

        FileLog.d("CustomMainTabsActivity: contentView found, creating CustomBottomNavigationView");
        // 创建自定义导航栏
        customBottomNav = new CustomBottomNavigationView(context);
        customBottomNav.setOnTabSelectedListener(new CustomBottomNavigationView.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                scrollToTab(position);
            }
        });

        // 添加到底部
        // 高度 = Tab高度(56dp) + 系统底部手势条/导航栏高度，避免被小米手势条遮挡、点不到
        int bottomInset = AndroidUtilities.navigationBarHeight;
        customBottomNav.setPadding(0, 0, 0, bottomInset);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            AndroidUtilities.dp(56) + bottomInset
        );
        params.gravity = Gravity.BOTTOM;
        contentView.addView(customBottomNav, params);

        FileLog.d("CustomMainTabsActivity: Custom navigation added successfully");
    }

    private void scrollToTab(int position) {
        // viewPager 是 ViewPagerActivity 的 protected 字段，直接访问，避免反射带来的卡顿
        if (viewPager == null) {
            return;
        }
        // 与官方点击逻辑一致：滑动过程中忽略点击，已在目标页时忽略
        if (viewPager.isManualScrolling() || viewPager.isTouch()) {
            return;
        }
        if (viewPager.getCurrentPosition() == position) {
            return;
        }
        viewPager.scrollToPosition(position);
    }

    @Override
    protected void onViewPagerTabAnimationUpdate(boolean manual) {
        super.onViewPagerTabAnimationUpdate(manual);

        // 同步自定义导航栏的选中状态（viewPager 为 protected 字段，直接访问）
        if (customBottomNav != null && viewPager != null) {
            customBottomNav.updatePosition(viewPager.getPositionAnimated());
        }
    }

    @Override
    protected WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        WindowInsetsCompat result = super.onApplyWindowInsets(v, insets);

        // 根据系统窗口 insets 精确设置底部导航栏的底部内边距，避免手势条遮挡
        if (customBottomNav != null) {
            Insets systemInsets = AndroidUtilities.getDefaultWindowInsets(insets, false);
            int bottomInset = systemInsets.bottom;
            customBottomNav.setPadding(0, 0, 0, bottomInset);

            ViewGroup.LayoutParams lp = customBottomNav.getLayoutParams();
            int height = AndroidUtilities.dp(56) + bottomInset;
            if (lp.height != height) {
                lp.height = height;
                customBottomNav.setLayoutParams(lp);
            }
        }

        return result;
    }
}
