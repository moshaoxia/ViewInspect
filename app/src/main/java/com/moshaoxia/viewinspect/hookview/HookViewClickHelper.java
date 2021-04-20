package com.moshaoxia.viewinspect.hookview;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * @author mohaiyang
 * Date：2021/3/23
 * Email: 345079386@qq.com
 * Description:
 * 1、获取当前Activity
 * 2、遍历当前window下所有DecorView下子view 并hook它们的点击事件
 */

public class HookViewClickHelper {

    private static final String TAG = "HookViewClickHelper";

    public static void hookViewClick(View view) {
        try {
            Class<?> viewClass = Class.forName("android.view.View");
            Method getListenerInfo = viewClass.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfoObj = getListenerInfo.invoke(view);
            Class<?> infoClass = Class.forName("android.view.View$ListenerInfo");
            Field field = infoClass.getDeclaredField("mOnTouchListener");
            field.setAccessible(true);
            View.OnTouchListener onTouchListener = (View.OnTouchListener) field.get(listenerInfoObj);
            if (onTouchListener == null) {
                view.setOnTouchListener(new OnTouchListenerProxy(null));
                Log.d(TAG, "hookViewClick onClickListener = null");
                return;
            } else if (onTouchListener instanceof OnTouchListenerProxy) {
                Log.d(TAG, "hookViewClick view = " + view + "already hooked");
                return;
            }
            OnTouchListenerProxy proxy = new OnTouchListenerProxy(onTouchListener);
            field.set(listenerInfoObj, proxy);
        } catch (Exception e) {
            Log.e(TAG, "hookViewClick view = " + view + " error:", e);
        }

    }

    public static void hookCurrentActivity() {
        View decorView = ViewInspectUtil.getRunningActivity().getWindow().getDecorView();
        hookViews(decorView);
        ViewInspectUtil.clearViewBorder(decorView);
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

    public static class OnTouchListenerProxy implements View.OnTouchListener {
        public static boolean intercept = false;
        private View.OnTouchListener touchListener;
        private static Interceptor interceptor;
        private static View targetView = null;

        public static void setInterceptor(Interceptor interceptor) {
            OnTouchListenerProxy.interceptor = interceptor;
        }

        public OnTouchListenerProxy(View.OnTouchListener clickListener) {
            this.touchListener = clickListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (interceptor != null) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (targetView == null) {
                        targetView = v;
                    }
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    if (targetView != null) {
                        LinkedList<View> views = ViewInspectUtil.buildTarget2RootChain(targetView);
                        ViewInspectUtil.clearViewBorder(views.peekLast());
                        interceptor.onTouch(views);
                    }
                    targetView = null;
                    if (intercept) {
                        return true;
                    }
                }
            }
            if (touchListener != null) {
                return touchListener.onTouch(v, event);
            }
            return false;
        }

        public interface Interceptor {
            void onTouch(LinkedList<View> v);
        }
    }
}