package io.foundy.feature.auth.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.feature.auth.data.repository.FirebaseAuthRepository
import io.foundy.feature.auth.domain.repository.AuthRepository
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
