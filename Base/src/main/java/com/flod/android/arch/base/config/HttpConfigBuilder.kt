package com.flod.android.arch.base.config

import android.app.Application

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-01-08
 * UseDes:
 */
interface HttpConfigBuilder {
    fun applyConfig(context: Application, settings: HttpSettings)
}
