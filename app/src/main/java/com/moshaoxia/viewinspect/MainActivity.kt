package com.moshaoxia.viewinspect

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.moshaoxia.viewinspect.hookview.FloatingView
import com.moshaoxia.viewinspect.hookview.HookViewClickHelper
import com.moshaoxia.viewinspect.hookview.Utils
import com.moshaoxia.viewinspect.hookview.IFloatingView

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val hello = findViewById<TextView>(R.id.tv_hello)
        val btnHook = findViewById<TextView>(R.id.btnHook)
        btnHook.setOnClickListener {
            HookViewClickHelper.hookViewClick(btnHook)
        }
        findViewById<TextView>(R.id.btnText).setOnClickListener {

            Toast.makeText(this, "Hello!", Toast.LENGTH_SHORT).show()

        }

        findViewById<LinearLayout>(R.id.ll_root).setOnTouchListener { v, event ->
            Log.i(TAG, "ll_root onTouch")
            false
        }

        findViewById<TextView>(R.id.hookViewGroup).setOnClickListener {
            HookViewClickHelper.hookViewClick(findViewById(R.id.ll_root))
        }
        HookViewClickHelper.OnTouchListenerProxy.setInterceptor {
            FloatingView.get().updateViews(it)
            val v = it.first
            showViewInfo(v)
        }

        findViewById<TextView>(R.id.addFloat).setOnClickListener {
            FloatingView.get().show()
            //下面这两行代码可以封装到内部去，使用者自己决定
            HookViewClickHelper.hookCurrentActivity()
            FloatingView.get().updateLockStatus(HookViewClickHelper.OnTouchListenerProxy.intercept)
            FloatingView.get().setFloatingCallback(object : IFloatingView.FloatingCallback {
                override fun onTriggerHook() {
                    HookViewClickHelper.hookCurrentActivity()

                }

                override fun onShowChild(v: View) {
                    showViewInfo(v)
                }

                override fun onShowParent(v: View) {
                    showViewInfo(v)
                }

                override fun onLockSwitch() {
                    HookViewClickHelper.OnTouchListenerProxy.intercept = !HookViewClickHelper.OnTouchListenerProxy.intercept
                    FloatingView.get().updateLockStatus(HookViewClickHelper.OnTouchListenerProxy.intercept)
                }
            })

        }
    }

    private fun showViewInfo(v: View) {
        val view = FloatingView.get().view
        if (view != null && view.isVisible) {
            //这个地方还可以自定义所在页面信息
            val name = Utils.getRunningActivity().javaClass.simpleName
            val viewInfo = Utils.getViewInfo(v)
            FloatingView.get().updateViewInfo("$viewInfo\n$name")
        }
    }

}