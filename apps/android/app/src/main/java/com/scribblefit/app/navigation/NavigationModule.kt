package com.scribblefit.app.navigation

import androidx.navigation3.runtime.NavBackStack
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    @Singleton
    abstract fun bindNavigator(impl: NavigatorImpl): Navigator
}
