package io.foundy.auth.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.auth.data.repository.FirebaseAuthRepository
import io.foundy.auth.domain.repository.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthRepositoryModule {

    @Binds
    @Singleton
    fun bindsAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository
    ): AuthRepository
}
