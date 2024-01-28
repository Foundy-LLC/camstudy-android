package io.foundy.search.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.DefaultRetrofit
import io.foundy.search.data.api.SearchApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SearchNetworkModule {

    @Provides
    @Singleton
    fun provideSearchApiService(@DefaultRetrofit retrofit: Retrofit): SearchApi {
        return retrofit.create(SearchApi::class.java)
    }
}
