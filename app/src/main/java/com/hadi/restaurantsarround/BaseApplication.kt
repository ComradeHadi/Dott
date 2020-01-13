package com.hadi.restaurantsarround

import android.app.Application
import com.hadi.restaurantsarround.di.components.ApiComponent
import com.hadi.restaurantsarround.di.ApiModule
import com.hadi.restaurantsarround.di.AppModule
import com.hadi.restaurantsarround.di.components.DaggerApiComponent

class BaseApplication : Application() {

    private lateinit var mApiComponent: ApiComponent



    override fun onCreate() {
        super.onCreate()

        mApiComponent = DaggerApiComponent.builder()
            .appModule(AppModule(this))
            .apiModule(ApiModule())
            .build()

    }

    //MARK: get api component
    fun getApiComponent(): ApiComponent {
        return mApiComponent
    }
}