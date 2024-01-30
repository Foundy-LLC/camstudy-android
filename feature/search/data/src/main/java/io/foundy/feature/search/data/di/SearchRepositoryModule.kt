package io.foundy.feature.search.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.feature.search.data.repository.NetworkSearchRepository
import io.foundy.feature.search.data.repository.SearchRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsSearchRepository(
        searchRepository: NetworkSearchRepository
    ): SearchRepository
}
