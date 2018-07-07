package com.why.project.navutilsdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by HaiyuKing
 * Used 底部虚拟导航栏工具类
 */

public class NavUtils {
	private static final String TAG = NavUtils.class.getSimpleName();

	private NavUtils() {
		throw new RuntimeException("NavUtils cannot be initialized!");
	}

	/**
	 * 获取底部虚拟导航栏高度
	 * @param activity
	 * @return
	 */
	public static int getNavigationBarHeight(Context activity) {
		//方法2：有问题
		/*if (!checkDeviceHasNavigationBar(activity)) {
			return 0;
		}
		Resources resources = activity.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height",
				"dimen", "android");
		//获取NavigationBar的高度
		int height = resources.getDimensionPixelSize(resourceId);
		return height;*/

		//方法1
		boolean hasNavigationBar = navigationBarExist(scanForActivity(activity)) && !vivoNavigationGestureEnabled(activity);
		Log.e(TAG,"{getNavigationBarHeight}hasNavigationBar="+hasNavigationBar);
		if (!hasNavigationBar) {//如果不含有虚拟导航栏，则返回高度值0
			return 0;
		}
		Resources resources = activity.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height",
				"dimen", "android");
		//获取NavigationBar的高度
		int height = resources.getDimensionPixelSize(resourceId);
		return height;
	}

	/*========================================方法1======================================================*/
	/**
	 * 通过获取不同状态的屏幕高度对比判断是否有NavigationBar
	 * https://blog.csdn.net/u010042660/article/details/51491572
	 * https://blog.csdn.net/android_zhengyongbo/article/details/68941464*/
	public static boolean navigationBarExist(Activity activity) {
		WindowManager windowManager = activity.getWindowManager();
		Display d = windowManager.getDefaultDisplay();

		DisplayMetrics realDisplayMetrics = new DisplayMetrics();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			d.getRealMetrics(realDisplayMetrics);
		}

		int realHeight = realDisplayMetrics.heightPixels;
		int realWidth = realDisplayMetrics.widthPixels;

		DisplayMetrics displayMetrics = new DisplayMetrics();
		d.getMetrics(displayMetrics);

		int displayHeight = displayMetrics.heightPixels;
		int displayWidth = displayMetrics.widthPixels;
		return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
	}

	/**解决java.lang.ClassCastException: android.view.ContextThemeWrapper cannot be cast to android.app.Activity问题
	 * https://blog.csdn.net/yaphetzhao/article/details/49639097*/
	public static Activity scanForActivity(Context cont) {
		if (cont == null)
			return null;
		else if (cont instanceof Activity)
			return (Activity)cont;
		else if (cont instanceof ContextWrapper)
			return scanForActivity(((ContextWrapper)cont).getBaseContext());

		return null;
	}

	/**
	 * 获取vivo手机设置中的"navigation_gesture_on"值，判断当前系统是使用导航键还是手势导航操作
	 * @param context app Context
	 * @return false 表示使用的是虚拟导航键(NavigationBar)， true 表示使用的是手势， 默认是false
	 * https://blog.csdn.net/weelyy/article/details/79284332#更换部分被拉伸的图片资源文件
	 */
	public static boolean vivoNavigationGestureEnabled(Context context) {
		int val = Settings.Secure.getInt(context.getContentResolver(), "navigation_gesture_on", 0);
		return val != 0;
	}

	/*========================================方法2======================================================*/
	/**
	 * 检测是否有底部虚拟导航栏【有点儿问题，当隐藏虚拟导航栏后，打开APP，仍然判断显示了虚拟导航栏】
	 * @param context
	 * @return
	 */
	public static boolean checkDeviceHasNavigationBar(Context context) {
		boolean hasNavigationBar = false;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
		} catch (Exception e) {
		}
		return hasNavigationBar;
	}
}
