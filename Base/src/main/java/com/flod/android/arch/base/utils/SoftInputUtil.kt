@file:Suppress("unused")

package com.flod.android.arch.base.utils

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment


fun Activity.touchOutsideHideSoftInput(event: MotionEvent) {
    if (event.action == MotionEvent.ACTION_DOWN) {
        val view = currentFocus
        if (isShouldHideSoftInput(view, event)) {
            hideSoftInput()
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
    return true
}


fun View.showSoftInput() {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideSoftInput() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.hideSoftInput(){
    val view = window.peekDecorView() ?: return
    view.hideSoftInput()
}

fun Fragment.hideSoftInput(){
    view?.hideSoftInput()
}
