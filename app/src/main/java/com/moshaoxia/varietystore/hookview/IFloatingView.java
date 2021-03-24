package com.moshaoxia.varietystore.hookview;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Yunpeng Li on 2018/3/15.
 */

public interface IFloatingView {


    FloatingView attach(Activity activity);

    FloatingView attach(FrameLayout container);

    FloatingView detach(Activity activity);

    FloatingView detach(FrameLayout container);

    FloatingMagnetView getView();

    void show();

    FloatingView remove();

    void setItemClickListener(View.OnClickListener listener);

}
