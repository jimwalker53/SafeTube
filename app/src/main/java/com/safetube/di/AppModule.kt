package com.safetube.di

import android.content.Context
import com.safetube.data.local.preferences.AppPreferences
import com.safetube.data.local.preferences.SecurePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSecurePreferences(
        @ApplicationContext context: Context
    ): SecurePreferences {
        return SecurePreferences(context)
    }

    @Provides
    @Singleton
    fun provideAppPreferences(
        @ApplicationContext context: Context
    ): AppPreferences {
        return AppPreferences(context)
    }
}
