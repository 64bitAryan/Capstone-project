package com.project.findme.di

import com.project.findme.mainactivity.repository.DefaultMainRepository
import com.project.findme.mainactivity.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideMainRepository() = DefaultMainRepository() as MainRepository
}