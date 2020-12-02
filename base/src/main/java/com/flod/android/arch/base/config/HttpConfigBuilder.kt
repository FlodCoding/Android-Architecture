package com.flod.android.arch.base.config

import android.app.Application


interface HttpConfigBuilder {
    fun applyConfig(context: Application, settings: HttpSettings)
}
