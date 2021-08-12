package com.project.findme.credentialactivity.repository

import com.project.findme.data.entity.Credential

interface CredentialRepository {

    suspend fun postCredentials(
        credential: Credential
    )
}