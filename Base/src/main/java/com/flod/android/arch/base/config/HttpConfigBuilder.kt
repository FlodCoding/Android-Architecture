package com.flod.android.arch.base.config

import android.app.Application

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2020-08-06
 * UseDes:
 */
interface HttpConfigBuilder {
    fun applyConfig(context: Application, settings: HttpSettings)
}
