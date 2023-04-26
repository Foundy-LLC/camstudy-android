package io.foundy.core.data.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultRetrofit

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class RankingRetrofit
