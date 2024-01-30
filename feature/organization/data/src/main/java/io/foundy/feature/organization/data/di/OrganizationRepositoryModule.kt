package io.foundy.feature.organization.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.feature.organization.data.repository.NetworkOrganizationRepository
import io.foundy.feature.organization.data.repository.OrganizationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrganizationRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsOrganizationRepository(
        networkOrganizationRepository: NetworkOrganizationRepository
    ): OrganizationRepository
}
