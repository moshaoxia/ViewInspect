package com.moshaoxia.varietystore

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.moshaoxia.varietystore.hookview.FloatingMagnetView
import com.moshaoxia.varietystore.hookview.FloatingView
import com.moshaoxia.varietystore.hookview.HookViewClickHelper
import com.moshaoxia.varietystore.hookview.utils.FloatUtil

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

        findViewById<TextView>(R.id.hookViewGroup).setOnClickListener {
            HookViewClickHelper.hookViewClick(findViewById(R.id.ll_root))
        }
        HookViewClickHelper.OnClickListenerProxy.setInterceptor {
            FloatingView.get().view.findViewById<TextView>(R.id.viewInfo).text = FloatUtil.getViewInfo(it)
        }

        findViewById<TextView>(R.id.addFloat).setOnClickListener {
            FloatingView.get().show()
            val floatRoot =
                FloatingView.get().view.findViewById<FloatingMagnetView>(R.id.floatRoot)
            Log.i(TAG, "name: float root = $floatRoot")
            FloatingView.get().setItemClickListener {
                HookViewClickHelper.hookCurrentActivity()
            }
        }
    }



}