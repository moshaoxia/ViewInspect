package com.moshaoxia.varietystore.hookview.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by Yunpeng Li on 2018/11/8.
 */
public class FloatUtil {
    private static final String TAG = "FloatUtil";
    private static final Application INSTANCE;

    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            e.printStackTrace();
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                e.printStackTrace();
            }
        } finally {
            INSTANCE = app;
        }
    }

    public static Application getApp() {
        return INSTANCE;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Activity getRunningActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread")
                    .invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            ArrayMap activities = (ArrayMap) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Didn't find the running activity");
    }

    public static int dip2Px(float dp) {
        try {
            return (int) (convertDpToPixel(dp, getApp()) + 0.5);
        } catch (Exception ex) {
            Log.e("ResolutionUtils", "Empty Catch on convertDpToPixel", ex);
        }

        return -1;
    }

    public static String getViewInfo(View v) {
        try {
            String name = v.getResources().getResourceEntryName(v.getId());
            return name + " : " + v.getClass().getSimpleName();
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "getViewName not found");
        }
        return v.getClass().getSimpleName();
    }

    private static float convertDpToPixel(float dp, Context context) {

        try {
            if (context == null) {
                return dp;
            }
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            return px;
        } catch (Exception ex) {
            Log.e(TAG, "Empty Catch on convertDpToPixel", ex);
        }

        return -1;
    }
}
