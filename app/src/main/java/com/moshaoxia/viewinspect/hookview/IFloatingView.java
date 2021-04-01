package com.moshaoxia.viewinspect.hookview;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

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

    interface FloatingCallback {
        void onTriggerHook();

        void onShowChild(View v);

        void onShowParent(View v);
    }


}
