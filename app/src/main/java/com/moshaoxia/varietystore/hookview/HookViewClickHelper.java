package com.moshaoxia.varietystore.hookview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.moshaoxia.varietystore.hookview.utils.FloatUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author mohaiyang
 * Date：2021/3/23
 * Email: mohaiyang@yy.com
 * Description:
 * 1、获取当前Activity
 * 2、遍历当前window下所有DecorView下子view 并hook它们的点击事件
 */

public class HookViewClickHelper {

    private static final String TAG = "HookViewClickHelper";
    public static boolean hookAll = true;

    public static HookViewClickHelper getInstance() {
        return HookViewClickHelperHolder.sHookViewClickHelper;
    }

    private static class HookViewClickHelperHolder {
        private static final HookViewClickHelper sHookViewClickHelper = new HookViewClickHelper();
    }

    private HookViewClickHelper() {
    }

    public static void hookViewClick(View view) {
        try {
            Class<?> viewClass = Class.forName("android.view.View");
            Method getListenerInfo = viewClass.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfoObj = getListenerInfo.invoke(view);
            Class<?> infoClass = Class.forName("android.view.View$ListenerInfo");
            Field field = infoClass.getDeclaredField("mOnClickListener");
            field.setAccessible(true);
            View.OnClickListener onClickListener = (View.OnClickListener) field.get(listenerInfoObj);
            if (onClickListener == null && hookAll && !(view instanceof NotHook)) {
                view.setOnClickListener(new OnClickListenerProxy(null));
                Log.d(TAG, "hookViewClick onClickListener = null");
                return;
            } else if (onClickListener instanceof OnClickListenerProxy) {
                Log.d(TAG, "hookViewClick view = " + view + "already hooked");
                return;
            }
            OnClickListenerProxy proxy = new OnClickListenerProxy(onClickListener);
            field.set(listenerInfoObj, proxy);
        } catch (Exception e) {
            Log.e(TAG, "hookViewClick view = " + view + " error:", e);
        }

    }

    public static void hookCurrentActivity() {
        View decorView = FloatUtil.getRunningActivity().getWindow().getDecorView();
        hookViews(decorView);
    }

    public static void hookViews(View view) {
        if (view instanceof NotHook) {
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            hookViewClick(vp);
            for (int i = 0; i < vp.getChildCount(); i++) {
                View child = vp.getChildAt(i);
                hookViews(child);
            }
        } else {
            hookViewClick(view);
        }
    }



    public interface NotHook {

    }

    public static class OnClickListenerProxy implements View.OnClickListener {

        private View.OnClickListener clickListener;
        private static Interceptor interceptor;

        public static void setInterceptor(Interceptor interceptor) {
            OnClickListenerProxy.interceptor = interceptor;
        }

        public OnClickListenerProxy() {
        }

        public OnClickListenerProxy(View.OnClickListener clickListener) {
            this.clickListener = clickListener;
        }



        @Override
        public void onClick(View v) {
            if (interceptor != null) {
                interceptor.onClickBefore(v);
            }
            if (clickListener != null) {
                clickListener.onClick(v);
            }
        }

        public interface Interceptor {
            void onClickBefore(View v);
        }
    }
}