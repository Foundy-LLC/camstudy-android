package io.foundy.feature.user.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.feature.user.data.repository.NetworkUserRepository
import io.foundy.feature.user.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsUserRepository(
        networkUserRepository: NetworkUserRepository
    ): UserRepository
}
