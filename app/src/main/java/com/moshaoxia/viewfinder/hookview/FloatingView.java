package com.moshaoxia.viewfinder.hookview;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.moshaoxia.viewfinder.R;

import java.lang.ref.WeakReference;


/**
 * @ClassName FloatingView
 * @Description 悬浮窗管理器
 * @Author Yunpeng Li
 * @Creation 2018/3/15 下午5:05
 * @Mender Yunpeng Li
 * @Modification by moshaoxia on 2021/3/23
 */
public class FloatingView implements IFloatingView {
    private static final String TAG = "FloatingView";
    private FloatingMagnetView mEnFloatingView;
    private static volatile FloatingView mInstance;
    private WeakReference<FrameLayout> mContainer;
    private Activity attachedActivity;
    private View.OnClickListener clickListener;
    private int marginLeftRight = 15;
    private int marginBottom = 300;
    private ViewGroup.LayoutParams mLayoutParams = getParams();

    private FloatingView() {
        registerActivity();
    }

    public static FloatingView get() {
        if (mInstance == null) {
            synchronized (FloatingView.class) {
                if (mInstance == null) {
                    mInstance = new FloatingView();
                }
            }
        }
        return mInstance;
    }

    @Override
    public FloatingView remove() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mEnFloatingView == null) {
                    return;
                }
                if (ViewCompat.isAttachedToWindow(mEnFloatingView) && getContainer() != null) {
                    getContainer().removeView(mEnFloatingView);

                }
                mEnFloatingView = null;
            }
        });
        return this;
    }

    @Override
    public void setItemClickListener(View.OnClickListener listener) {
        clickListener = listener;
    }

    private void ensureFloatingView() {
        synchronized (this) {
            if (mEnFloatingView == null) {
                return;
            }
            mEnFloatingView.setLayoutParams(mLayoutParams);
            addViewToWindow(mEnFloatingView);
        }
    }

    public FloatingView add() {
        ensureFloatingView();
        return this;
    }

    @Override
    public FloatingView attach(Activity activity) {
        attachedActivity = activity;
        attach(getActivityRoot(activity));
        return this;
    }

    @Override
    public FloatingView attach(FrameLayout container) {
        if (container == null || mEnFloatingView == null) {
            mContainer = new WeakReference<>(container);
            return this;
        }
        if (mEnFloatingView.getParent() == container) {
            return this;
        }
        if (mEnFloatingView.getParent() != null) {
            ((ViewGroup) mEnFloatingView.getParent()).removeView(mEnFloatingView);
        }
        mContainer = new WeakReference<>(container);
        container.addView(mEnFloatingView);
        return this;
    }

    @Override
    public FloatingView detach(Activity activity) {
        detach(getActivityRoot(activity));
        attachedActivity = null;
        return this;
    }

    @Override
    public FloatingView detach(FrameLayout container) {
        if (mEnFloatingView != null && container != null && ViewCompat.isAttachedToWindow(mEnFloatingView)) {
            container.removeView(mEnFloatingView);
        }
        if (getContainer() == container) {
            mContainer = null;
        }
        return this;
    }

    @Override
    public FloatingMagnetView getView() {
        return mEnFloatingView;
    }

    @Override
    public FloatingView show() {
        if (mEnFloatingView == null) {
            if (attachedActivity == null) {
                attachedActivity = ContextUtil.getRunningActivity();
            }
            mEnFloatingView = (FloatingMagnetView) LayoutInflater.from(attachedActivity).inflate(R.layout.layout_float, null);
            mEnFloatingView.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove();
                }
            });
            mEnFloatingView.findViewById(R.id.hookTrigger).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onClick(v);
                    }
                }
            });
        }
        add().attach(attachedActivity);
        return this;
    }

    public FloatingView customView(FloatingMagnetView viewGroup) {
        mEnFloatingView = viewGroup;
        return this;
    }

    public FloatingView layoutParams(ViewGroup.LayoutParams params) {
        mLayoutParams = params;
        if (mEnFloatingView != null) {
            mEnFloatingView.setLayoutParams(params);
        }
        return this;
    }

    private void addViewToWindow(final View view) {
        if (getContainer() == null) {
            return;
        }
        if (getContainer().indexOfChild(view) >= 0) {
            Log.w(TAG, "addViewToWindow has been add");
            return;
        }
        getContainer().addView(view);
    }

    private FrameLayout getContainer() {
        if (mContainer == null) {
            return null;
        }
        return mContainer.get();
    }

    private FrameLayout.LayoutParams getParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.setMargins(marginLeftRight, params.topMargin, marginLeftRight, marginBottom);
        return params;
    }

    private FrameLayout getActivityRoot(Activity activity) {
        if (activity == null) {
            return null;
        }
        try {
            return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void registerActivity() {
        ContextUtil.getApp()
                .registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                    }

                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {
                        attach(activity);
                    }

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {
                        detach(activity);
                    }

                    @Override
                    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                    }

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity) {

                    }
                });
    }
}