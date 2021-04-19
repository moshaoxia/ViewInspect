package com.moshaoxia.viewinspect.hookview;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * Created by Yunpeng Li on 2018/3/15.
 * @Modification by moshaoxia on 2021/3/23
 */

public interface IFloatingView {


    FloatingView attach(Activity activity);

    FloatingView attach(FrameLayout container);

    FloatingView detach(Activity activity);

    FloatingView detach(FrameLayout container);

    FloatingMagnetView getView();

    FloatingView show();

    FloatingView remove();

    void setFloatingCallback(FloatingCallback listener);

    void updateViews(LinkedList<View> views);

    void updateViewInfo(@NotNull String info);

    /**
     * locked标记View树都被拦截，更新🔐的图标
     * @param locked
     */
    void updateLockStatus(boolean locked);

    interface FloatingCallback {
        void onTriggerHook();

        void onShowChild(View v);

        void onShowParent(View v);

        void onLockSwitch();
    }


}
