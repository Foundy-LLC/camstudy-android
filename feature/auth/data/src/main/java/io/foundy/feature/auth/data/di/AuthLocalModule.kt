package io.foundy.feature.auth.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.feature.auth.data.source.AuthLocalDataSource
import io.foundy.feature.auth.data.source.DataStoreAuthDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthLocalModule {

    @Binds
    abstract fun bindsAuthLocalDataSource(
        dataStoreAuthDataSource: DataStoreAuthDataSource
    ): AuthLocalDataSource
}
