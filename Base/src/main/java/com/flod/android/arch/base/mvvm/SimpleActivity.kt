package com.flod.android.arch.base.mvvm

import android.os.Bundle
import android.view.MotionEvent
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.flod.android.arch.base.utils.SoftInputUtil

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-10-28
 * UseDes:
 *
 */
abstract class SimpleActivity : AppCompatActivity() {

    @get:LayoutRes
    abstract val contentViewId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        if (initArg(intent.extras)) {
            setContentViewId(contentViewId)
            super.onCreate(savedInstanceState)
            initView(savedInstanceState)
            initData(savedInstanceState)
        } else {
            super.onCreate(savedInstanceState)
            finish()
        }

    }


    protected open fun setContentViewId(layoutResID: Int) {
        setContentView(layoutResID)
    }

    protected open fun initArg(bundle: Bundle?): Boolean {
        return true
    }

    protected abstract fun initView(savedInstanceState: Bundle?)


    protected open fun initData(savedInstanceState: Bundle?) {

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (touchOutsideHideSoftInput())
            SoftInputUtil.touchOutsideHideSoftInput(this, event)
        return super.dispatchTouchEvent(event)
    }

    protected open fun touchOutsideHideSoftInput(): Boolean {
        return false
    }


}