## ViewInspect
通过Hook页面view树的方式，捕捉View的信息，并显示在可拖动的浮窗上。主要解决开发者接触不熟悉的功能模块或者控件时，很难定位代码所在工程中的位置。可以通过这个知道控件的id、类型、所在页面，快速定位到代码位置
## 功能特性
- 实时hook当前页面的所有view，触摸到所要检查的view，这个view的id和类型、当前页面等信息就即时显示在可拖动的悬浮窗上
- 可以检查代码中后续动态添加的view
- 轻量，作用于运行时，无侵入性，可放心接入
## 效果截图
<img src="https://github.com/moshaoxia/ViewFinder/blob/master/WechatIMG38-tuya.jpeg" width="300px" height="600px">

## 使用方式

```
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
```

## Thanks
https://github.com/leotyndale/EnFloatingView

使用了部分这个库的代码作为悬浮窗显示
