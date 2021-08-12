package com.project.findme.di

import com.project.findme.credentialactivity.repository.CredentialRepository
import com.project.findme.credentialactivity.repository.DefaultCredentialRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CredentialModule {

    @Provides
    @Singleton
    fun provideCredentialRepository() = DefaultCredentialRepository() as CredentialRepository

}