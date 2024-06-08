package four.credits.podcatch.presentation

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(PodcastDownloadService.downloadCache.getOrCreate(this))
            .setUpstreamDataSourceFactory(null)
            .setCacheWriteDataSinkFactory(null) // disable writing
        val dataSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(cacheDataSourceFactory)
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(dataSourceFactory)
            .build()
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        mediaSession?.player!!.takeIf { it.shouldStop }?.let { stopSelf() }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? = mediaSession
}

private val Player.shouldStop get() =
    !playWhenReady || mediaItemCount == 0 || playbackState == Player.STATE_ENDED
