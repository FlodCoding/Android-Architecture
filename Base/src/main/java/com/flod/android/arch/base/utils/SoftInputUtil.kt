package com.flod.android.arch.base.utils

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-13
 * UseDes:
 *
 */
object SoftInputUtil {

    fun touchOutsideHideSoftInput(activity: Activity, event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = activity.currentFocus
            if (isShouldHideSoftInput(view, event)) {
                hideSoftInput(activity)
            }
        }
    }


    private fun isShouldHideSoftInput(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val leftTop = intArrayOf(0, 0)
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }


    private fun hideSoftInput(activity: Activity) {
        val view = activity.window.peekDecorView() ?: return
        val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}