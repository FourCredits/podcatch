package four.credits.podcatch

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import four.credits.podcatch.data.DownloadManager
import four.credits.podcatch.data.ExoPlayManager
import four.credits.podcatch.data.RealEpisodeRepository
import four.credits.podcatch.data.RealPodcastRepository
import four.credits.podcatch.data.persistence.PodcastDatabase
import four.credits.podcatch.domain.EpisodeRepository
import four.credits.podcatch.domain.PodcastRepository
import four.credits.podcatch.presentation.playerChannelId
import four.credits.podcatch.presentation.playerNotificationDescription

class PodcatchApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(this, PodcastDatabase::class.java, "podcast-db")
            .build()
    }

    val podcastRepository: PodcastRepository by lazy {
        RealPodcastRepository(database.podcastDao, database.episodeDao)
    }

    val episodeRepository: EpisodeRepository by lazy {
        val downloadManager = DownloadManager(this)
        RealEpisodeRepository(downloadManager, database.episodeDao)
    }

    val player by lazy { ExoPlayer.Builder(this).build() }
    val playManager by lazy { ExoPlayManager(player) }

    override fun onCreate() {
        super.onCreate()
        player.prepare()
        setUpNotifications()
    }

    private fun setUpNotifications() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        getSystemService<NotificationManager>()!!.createNotificationChannel(
            NotificationChannel(
                playerChannelId,
                playerNotificationDescription,
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    override fun onTerminate() {
        super.onTerminate()
        player.release()
    }
}
