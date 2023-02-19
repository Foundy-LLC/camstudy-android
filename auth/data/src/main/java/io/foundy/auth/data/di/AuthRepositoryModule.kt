package io.foundy.auth.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.auth.data.repository.FirebaseAuthRepository

@Module
@InstallIn(SingletonComponent::class)
interface AuthRepositoryModule {

    @Binds
    fun bindsAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository
    ): AuthRepository
}
