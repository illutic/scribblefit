package com.scribblefit.app.navigation

import com.scribblefit.core.navigation.Navigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NavigationModule {
    @Binds
    @Singleton
    fun bindsNavigator(impl: NavigatorImpl): Navigator
}
