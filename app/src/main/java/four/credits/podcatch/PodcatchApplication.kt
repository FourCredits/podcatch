package four.credits.podcatch

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import four.credits.podcatch.data.DownloadManager
import four.credits.podcatch.data.ExoPlayManager
import four.credits.podcatch.data.RealEpisodeRepository
import four.credits.podcatch.data.RealPodcastRepository
import four.credits.podcatch.data.persistence.PodcastDatabase
import four.credits.podcatch.domain.EpisodeRepository
import four.credits.podcatch.domain.PodcastRepository

class PodcatchApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(
            this,
            PodcastDatabase::class.java,
            "podcast-db"
        ).build()
    }

    val podcastRepository: PodcastRepository by lazy {
        RealPodcastRepository(database.podcastDao, database.episodeDao)
    }

    val episodeRepository: EpisodeRepository by lazy {
        val downloadManager = DownloadManager(this)
        RealEpisodeRepository(downloadManager, database.episodeDao)
    }

    private val player by lazy { ExoPlayer.Builder(this).build() }
    val playManager by lazy { ExoPlayManager(player) }

    override fun onCreate() {
        super.onCreate()
        player.prepare()
    }

    override fun onTerminate() {
        super.onTerminate()
        player.release()
    }
}
