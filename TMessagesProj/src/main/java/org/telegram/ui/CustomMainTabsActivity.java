package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
        try {
            FileLog.d("CustomMainTabsActivity: Creating custom bottom navigation");
            // 获取父类的 contentView
            Field contentViewField = MainTabsActivity.class.getDeclaredField("contentView");
            contentViewField.setAccessible(true);
            FrameLayout contentView = (FrameLayout) contentViewField.get(this);

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
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(56)
            );
            params.gravity = Gravity.BOTTOM;
            contentView.addView(customBottomNav, params);

        } catch (NoSuchFieldException e) {
            FileLog.e("CustomMainTabsActivity: contentView field not found", e);
        } catch (IllegalAccessException e) {
            FileLog.e("CustomMainTabsActivity: Cannot access contentView field", e);
        } catch (Exception e) {
            FileLog.e("CustomMainTabsActivity: Error creating custom navigation", e);
        }
    }

    private void scrollToTab(int position) {
        try {
            // 获取 viewPager 并切换到指定位置
            Field viewPagerField = MainTabsActivity.class.getSuperclass().getDeclaredField("viewPager");
            viewPagerField.setAccessible(true);
            Object viewPager = viewPagerField.get(this);

            if (viewPager != null) {
                // 调用 scrollToPosition 方法
                viewPager.getClass().getMethod("scrollToPosition", int.class).invoke(viewPager, position);
            }
        } catch (Exception e) {
            FileLog.e("CustomMainTabsActivity: Error scrolling to tab", e);
        }
    }

    @Override
    protected void onViewPagerTabAnimationUpdate(boolean manual) {
        super.onViewPagerTabAnimationUpdate(manual);

        // 同步自定义导航栏的选中状态
        if (customBottomNav != null) {
            try {
                Field viewPagerField = MainTabsActivity.class.getSuperclass().getDeclaredField("viewPager");
                viewPagerField.setAccessible(true);
                Object viewPager = viewPagerField.get(this);

                if (viewPager != null) {
                    // 获取当前滑动位置
                    Float position = (Float) viewPager.getClass().getMethod("getPositionAnimated").invoke(viewPager);
                    if (position != null) {
                        customBottomNav.updatePosition(position);
                    }
                }
            } catch (Exception e) {
                // 忽略错误，不影响主要功能
            }
        }
    }
}
