package io.foundy.room.ui.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.foundy.room.ui.media.MediaManager
import io.foundy.room.ui.media.MediaManagerImpl

@InstallIn(ViewModelComponent::class)
@Module
abstract class MediaManagerModule {

    @Binds
    @ViewModelScoped
    abstract fun bindsMediaManager(mediaManagerImpl: MediaManagerImpl): MediaManager
}
