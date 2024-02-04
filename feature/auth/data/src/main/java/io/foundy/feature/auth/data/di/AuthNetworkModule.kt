package io.foundy.feature.auth.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.NewDefaultRetrofit
import io.foundy.feature.auth.data.api.AuthApi
import io.foundy.feature.auth.data.source.AuthRemoteDataSource
import io.foundy.feature.auth.data.source.RetrofitAuthDataSource
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthNetworkModule {

    @Provides
    @Singleton
    fun provideAuthApiService(@NewDefaultRetrofit retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(api: AuthApi): AuthRemoteDataSource {
        return RetrofitAuthDataSource(api)
    }
}
