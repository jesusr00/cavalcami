package com.smartestidea.cavalcami.application

import android.app.Application
import com.parse.Parse
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CavalCamiApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("h9bsxUDMThEMfz5Cf21b9bpLNZ1bmeKJOYVcqeDv")
                .clientKey("6bj1StgJzFsuVqa3ziSwRppwv5ae6LOiHvTH3mjl")
                .server("https://parseapi.back4app.com")
                .build()
        )
    }
}