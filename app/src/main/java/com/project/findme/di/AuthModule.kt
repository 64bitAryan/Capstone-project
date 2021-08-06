package com.project.findme.di

import android.widget.TextView
import androidx.lifecycle.SavedStateHandle
import com.project.findme.authactivity.repositories.AuthRepository
import com.project.findme.authactivity.repositories.DefaultAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthRepository() = DefaultAuthRepository() as AuthRepository
}