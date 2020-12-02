package com.flod.android.arch.base.view

import android.os.Bundle
import android.view.MotionEvent
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.flod.android.arch.base.utils.touchOutsideHideSoftInput


abstract class SimpleActivity : AppCompatActivity() {

    @get:LayoutRes
    abstract val contentViewId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (initArg(intent.extras)) {
            setContentView()
            initView(savedInstanceState)
            initData(savedInstanceState)
        } else {
            finish()
        }

    }


    protected open fun setContentView() {
        setContentView(contentViewId)
    }


    protected open fun initArg(bundle: Bundle?): Boolean {
        return true
    }

    protected abstract fun initView(savedInstanceState: Bundle?)


    protected open fun initData(savedInstanceState: Bundle?) {
    }


    protected open val touchOutsideHideSoftInput: Boolean = false
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (touchOutsideHideSoftInput)
            touchOutsideHideSoftInput(event)
        return super.dispatchTouchEvent(event)
    }

}