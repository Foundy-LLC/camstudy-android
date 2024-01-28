package io.foundy.ranking.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.ranking.data.repository.NetworkRankingRepository
import io.foundy.ranking.data.repository.RankingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RankingRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsRankingRepository(
        networkRankingRepository: NetworkRankingRepository
    ): RankingRepository
}
