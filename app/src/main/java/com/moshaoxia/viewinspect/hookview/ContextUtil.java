package com.moshaoxia.viewinspect.hookview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

/**
 * @author moshaoxia
 * Date：2021/3/23
 * Email:345079386@qq.com
 * Description:
 */
public class ContextUtil {
    private static final String TAG = "Util";
    private static Application instance;

    /**
     * 可直接替换为在Application#attachBaseContext获取时注入
     *
     */
    public static Application getApp() {
        if (instance != null) {
            return instance;
        }
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                e.printStackTrace();
            }
        } finally {
            instance = app;
        }
        return instance;
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


    public static String getViewInfo(View v) {
        try {
            String name = v.getResources().getResourceEntryName(v.getId());
            return name + " : " + v.getClass().getSimpleName();
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "getViewName not found");
        }
        return v.getClass().getSimpleName();
    }

}
