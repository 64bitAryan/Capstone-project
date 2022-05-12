package com.project.findme.di

import com.project.findme.mainactivity.chatFragment.repository.ChatRepository
import com.project.findme.mainactivity.chatFragment.repository.DefaultChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatRepository() = DefaultChatRepository() as ChatRepository
}