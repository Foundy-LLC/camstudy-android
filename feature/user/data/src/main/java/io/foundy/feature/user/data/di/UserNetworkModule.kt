package io.foundy.feature.user.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.DefaultRetrofit
import io.foundy.feature.user.data.api.UserApi
import io.foundy.feature.user.data.source.RetrofitUserDataSource
import io.foundy.feature.user.data.source.UserRemoteDataSource
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserNetworkModule {

    @Provides
    @Singleton
    fun provideUserApiService(@DefaultRetrofit retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun providesUserRemoteDataSource(api: UserApi): UserRemoteDataSource {
        return RetrofitUserDataSource(api = api)
    }
}
