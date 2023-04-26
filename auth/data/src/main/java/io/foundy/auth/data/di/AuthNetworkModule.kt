package io.foundy.auth.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.auth.data.api.AuthApi
import io.foundy.auth.data.source.AuthRemoteDataSource
import io.foundy.auth.data.source.RetrofitAuthDataSource
import io.foundy.core.data.di.DefaultRetrofit
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthNetworkModule {

    @Provides
    @Singleton
    fun provideAuthApiService(@DefaultRetrofit retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(api: AuthApi): AuthRemoteDataSource {
        return RetrofitAuthDataSource(api)
    }
}
