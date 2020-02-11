@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.flod.android.arch.base.utils

import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleObserver
import java.util.*

/**
 * SimpleDes:fragment调度帮助类
 * Creator: Flood
 * Date: 2019-04-15
 * UseDes:
 * 用来调度Fragment
 *
 *
 * 必要时请调用(当屏幕旋转，或资源回收时)
 * [onSaveInstanceState]
 * [onRestoreInstanceState]
 * 这两个方法，用来恢复当前选择的fragment
 */
@MainThread
class FragmentsHelper(var fragmentManager: FragmentManager,
                      @param:IdRes private val containerId: Int = 0,
                      private val onItemChangeListener: OnItemChangeListener? = null) : LifecycleObserver {

    companion object {
        private const val Key_NavHelper_SavedPosition = "Key_NavHelper_SavedPosition"
    }

    var items: MutableList<Item> = ArrayList()
        private set

    private var backStackCount: Int = 0
    private var oldItemIndex = -1
    var curItemIndex = -1
        private set

    init {

        fragmentManager.addOnBackStackChangedListener {
            //addToBackStack，popBackStack都会触发
            if (backStackCount > fragmentManager.backStackEntryCount) {
                //popBackStack
                curItemIndex = oldItemIndex
            }
            backStackCount = fragmentManager.backStackEntryCount
        }

    }

    /**
     * 传递参数给Fragment，需要Fragment实现[OnSetNewArgumentsListener]
     *
     * @param index fragment索引
     * @param arg      Bundle
     * @return 是否传递成功
     */
    fun setArguments(index: Int, arg: Any): Boolean {
        if (index >= 0 && index < items.size) {
            val item = items[index]
            if (item.fragment is OnSetNewArgumentsListener) {
                (item.fragment as OnSetNewArgumentsListener).onNewArguments(arg)
                return true
            }

        }
        return false
    }


    fun selectItem(index: Int, newItemArgs: Bundle? = null): Boolean {
        if (index >= 0 && index < items.size) {
            val newItem = items[index]
            var oldItem: Item? = null
            if (curItemIndex >= 0 && curItemIndex < items.size) {
                oldItem = items[curItemIndex]
            }
            doItemChanged(newItem, oldItem, newItemArgs)
            oldItemIndex = curItemIndex
            curItemIndex = index
            return true

        }
        return false
    }

    private fun doItemChanged(newItem: Item?, oldItem: Item?, newItemArgs: Bundle?) {
        val ft = fragmentManager.beginTransaction()
        if (newItem != null) {
            val retainFragment = fragmentManager.findFragmentByTag(newItem.tag)
            if (newItemArgs != null) {
                newItem.args = newItemArgs
            }
            if (retainFragment == null) {
                //添加到FragmentTransaction

                newItem.args?.apply {
                    newItem.fragment?.arguments = this
                }

                ft.add(containerId, newItem.fragment!!, newItem.tag)
                if (newItem.addToBackStack) {
                    ft.addToBackStack(newItem.tag)
                }

            } else {
                //如果有就直接显示，不再重新初始化了
                newItem.fragment = retainFragment
                newItemArgs?.apply {
                    newItem.fragment?.arguments = this
                    if (newItem.fragment is OnSetNewArgumentsListener) {
                        (newItem.fragment as OnSetNewArgumentsListener).onNewArguments(newItemArgs)
                    }
                }


            }

            //设置过场动画，然鹅只有用出入栈的方式才能显示完全
            ft.setCustomAnimations(newItem.customAnimations[0], newItem.customAnimations[1],
                    newItem.customAnimations[2], newItem.customAnimations[3])

            newItem.fragment?.apply {
                ft.show(this)
            }

        }

        if (oldItem?.fragment != null
                && oldItem !== newItem) {
            oldItem.fragment?.apply {
                ft.hide(this)
            }
        }

        ft.commit()
        onItemChangeListener?.onItemChanged(newItem, oldItem)
    }

    /**
     * 当Activity旋转或被回收时会调用
     *
     * @see androidx.appcompat.app.AppCompatActivity.onSaveInstanceState
     */
    fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(Key_NavHelper_SavedPosition, curItemIndex)

    }

    /**
     * 当Activity恢复时取出的position
     *
     * @return 被保存的Position
     * @see androidx.appcompat.app.AppCompatActivity.onRestoreInstanceState
     */
    fun onRestoreInstanceState(savedInstanceState: Bundle?): Int {
        if (savedInstanceState != null) {
            val savedPosition = savedInstanceState.getInt(Key_NavHelper_SavedPosition, -1)
            if (savedPosition >= 0) {
                selectItem(savedPosition)
                return savedPosition
            }
        }
        return -1
    }

    /**
     * 添加后并实例化Fragment
     */
    fun add(item: Item): FragmentsHelper {
        items.add(item)

        if (item.fragment == null) {
            item.fragment = fragmentManager.fragmentFactory.instantiate(item.clx.classLoader!!, item.clx.name)
        }

        return this
    }


    interface OnItemChangeListener {
        fun onItemChanged(newItem: Item?, oldItem: Item?)
    }

    class Item(internal val clx: Class<*>,
               var fragment: Fragment? = null,
               val tag: String = clx.simpleName,
               var args: Bundle? = null,
               var addToBackStack: Boolean = false) {


        internal var customAnimations = arrayOf(0, 0, 0, 0)

        fun setCustomAnimations(@AnimatorRes @AnimRes enter: Int,
                                @AnimatorRes @AnimRes exit: Int,
                                @AnimatorRes @AnimRes popEnter: Int,
                                @AnimatorRes @AnimRes popExit: Int): Item {
            customAnimations = arrayOf(enter, exit, popEnter, popExit)
            return this
        }
    }

    /**
     * 配合[setArguments]使用，需要该fragment继承该方法
     */
    interface OnSetNewArgumentsListener {

        fun onNewArguments(data: Any)

    }


}
