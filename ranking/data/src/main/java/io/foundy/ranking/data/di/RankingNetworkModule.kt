package io.foundy.ranking.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.RankingRetrofit
import io.foundy.ranking.data.api.RankingApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RankingNetworkModule {

    @Provides
    @Singleton
    fun providesRankingApi(@RankingRetrofit retrofit: Retrofit): RankingApi {
        return retrofit.create(RankingApi::class.java)
    }
}
