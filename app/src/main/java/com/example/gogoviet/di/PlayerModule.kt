package com.example.gogoviet.di
import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.android.scopes.ViewScoped

@Module
@InstallIn(ViewModelComponent::class)
object PlayerModule {
    @Provides
    @ViewModelScoped
    fun provideVideoPlayer(app: Application): ExoPlayer {
        return ExoPlayer.Builder(app)
            .build()
    }
}