package io.foundy.welcome.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.DefaultRetrofit
import io.foundy.welcome.data.api.WelcomeApi
import io.foundy.welcome.data.repository.NetworkWelcomeRepository
import io.foundy.welcome.data.repository.WelcomeRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WelcomeNetworkModule {

    @Provides
    @Singleton
    fun provideUserApiService(@DefaultRetrofit retrofit: Retrofit): WelcomeApi {
        return retrofit.create(WelcomeApi::class.java)
    }

    @Provides
    @Singleton
    fun providesUserRepository(api: WelcomeApi): WelcomeRepository {
        return NetworkWelcomeRepository(api = api)
    }
}
