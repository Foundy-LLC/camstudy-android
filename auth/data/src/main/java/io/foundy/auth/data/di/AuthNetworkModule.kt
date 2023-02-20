package io.foundy.auth.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.auth.data.api.AuthApi
import io.foundy.auth.data.source.AuthRemoteDataSource
import io.foundy.auth.data.source.RetrofitAuthDataSource
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthNetworkModule {

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(api: AuthApi): AuthRemoteDataSource {
        return RetrofitAuthDataSource(api)
    }
}
