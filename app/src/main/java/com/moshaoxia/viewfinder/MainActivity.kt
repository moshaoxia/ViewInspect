package com.moshaoxia.viewfinder

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.moshaoxia.viewfinder.hookview.FloatingView
import com.moshaoxia.viewfinder.hookview.HookViewClickHelper
import com.moshaoxia.viewfinder.hookview.ContextUtil

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
            hello.setOnClickListener {
                Toast.makeText(this, "Text", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<LinearLayout>(R.id.ll_root).setOnTouchListener { v, event ->
            Log.i(TAG, "ll_root onTouch")
            false
        }

        findViewById<TextView>(R.id.hookViewGroup).setOnClickListener {
            HookViewClickHelper.hookViewClick(findViewById(R.id.ll_root))
        }
        HookViewClickHelper.OnTouchListenerProxy.setInterceptor {
            val view = FloatingView.get().view
            if (view != null && view.isVisible) {
                //这个地方还可以自定义所在页面信息
                val name = ContextUtil.getRunningActivity().javaClass.simpleName
                val viewInfo = ContextUtil.getViewInfo(it)
                FloatingView.get().view.findViewById<TextView>(R.id.viewInfo).text ="$viewInfo\n$name"
            }
        }

        findViewById<TextView>(R.id.addFloat).setOnClickListener {
            FloatingView.get().show()
            //下面这两行代码可以封装到内部去，使用者自己决定
            HookViewClickHelper.hookCurrentActivity()
            FloatingView.get().setItemClickListener {
                HookViewClickHelper.hookCurrentActivity()
            }
        }
    }


}