package com.flod.android.arch.base.utils

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-11-13
 * UseDes:
 *
 */
infix fun Any?.ifNull(block: () -> Unit) {
    if (this == null) block()
}

