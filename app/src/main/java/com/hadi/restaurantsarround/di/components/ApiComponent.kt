package com.hadi.restaurantsarround.di.components

import com.hadi.restaurantsarround.ui.MainActivity
import com.hadi.restaurantsarround.di.ApiModule
import com.hadi.restaurantsarround.di.AppModule
import com.hadi.restaurantsarround.ui.DetailActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (ApiModule::class)])
interface ApiComponent {
    fun inject(mainActivity: MainActivity)
    fun injectToDetail(detailActivity: DetailActivity)
}