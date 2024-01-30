package io.foundy.feature.organization.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.core.data.di.DefaultRetrofit
import io.foundy.feature.organization.data.api.OrganizationApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OrganizationNetworkModel {

    @Provides
    @Singleton
    fun providesOrganizationApi(@DefaultRetrofit retrofit: Retrofit): OrganizationApi {
        return retrofit.create(OrganizationApi::class.java)
    }
}
