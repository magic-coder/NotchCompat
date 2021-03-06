package ff.driven.self.notch

import android.graphics.Rect
import android.support.annotation.RequiresApi
import android.view.Window
import java.lang.reflect.Field
import java.util.*

internal class HwNotchScreenSupport : INotchScreenSupport {
    @RequiresApi(api = 26)
    override fun hasNotchInScreen(window: Window): Boolean {
        return try {
            if (hwNotchSizeUtil == null) {
                val cl = window.context.classLoader
                hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            }
            val get = hwNotchSizeUtil!!.getMethod("hasNotchInScreen")
            get.invoke(null) as Boolean
        } catch (ignored: Exception) {
            false
        }
    }

    @RequiresApi(api = 26)
    override fun getNotchSize(window: Window): List<Rect> {
        val result = ArrayList<Rect>()
        val rect = Rect()
        try {
            val context = window.context
            if (hwNotchSizeUtil == null) {
                val cl = context.classLoader
                hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            }
            val get = hwNotchSizeUtil!!.getMethod("getNotchSize")
            val ret = get.invoke(null) as IntArray
            rect.left = ret[0]
            rect.bottom = ret[1]
            rect.right = context.resources.displayMetrics.widthPixels - rect.left
            rect.top = 0
            result.add(rect)
            return result
        } catch (ignored: Exception) {
            return result
        }
    }

    @RequiresApi(api = 26)
    override fun setWindowLayoutAroundNotch(window: Window) {
        val layoutParams = window.attributes
        try {
            if (hwLayoutParamsFlags == null) {
                hwLayoutParamsFlags = layoutParams.javaClass.getDeclaredField("hwFlags")
                hwLayoutParamsFlags!!.isAccessible = true
            }
            val old = hwLayoutParamsFlags!!.get(layoutParams) as Int
            hwLayoutParamsFlags!!.set(layoutParams, old or HW_FLAG_NOTCH_SUPPORT)
        } catch (ignored: Exception) {
        }
    }

    @RequiresApi(api = 26)
    override fun setWindowLayoutBlockNotch(window: Window) {
        val layoutParams = window.attributes
        try {
            if (hwLayoutParamsFlags == null) {
                hwLayoutParamsFlags = layoutParams.javaClass.getDeclaredField("hwFlags")
                hwLayoutParamsFlags!!.isAccessible = true
            }
            val old = hwLayoutParamsFlags!!.get(layoutParams) as Int
            hwLayoutParamsFlags!!.set(layoutParams, old and HW_FLAG_NOTCH_SUPPORT.inv())
        } catch (ignored: Exception) {
        }
    }

    companion object {
        //华为刘海屏全屏显示FLAG
        private const val HW_FLAG_NOTCH_SUPPORT = 0x00010000

        private var hwNotchSizeUtil: Class<*>? = null
        private var hwLayoutParamsFlags: Field? = null
    }
}
