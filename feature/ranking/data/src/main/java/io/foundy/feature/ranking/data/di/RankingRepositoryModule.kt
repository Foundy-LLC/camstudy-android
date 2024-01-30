package io.foundy.feature.ranking.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.feature.ranking.data.repository.NetworkRankingRepository
import io.foundy.feature.ranking.data.repository.RankingRepository
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
