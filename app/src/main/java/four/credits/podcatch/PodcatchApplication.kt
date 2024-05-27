package four.credits.podcatch

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.os.Build
import androidx.core.content.getSystemService
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.room.Room
import com.google.common.util.concurrent.MoreExecutors
import four.credits.podcatch.data.DownloadManager
import four.credits.podcatch.data.Media3PlayManager
import four.credits.podcatch.data.RealEpisodeRepository
import four.credits.podcatch.data.RealPodcastRepository
import four.credits.podcatch.data.persistence.PodcastDatabase
import four.credits.podcatch.domain.EpisodeRepository
import four.credits.podcatch.domain.PodcastRepository
import four.credits.podcatch.presentation.PlayerService
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

    private lateinit var player: Player
    lateinit var playManager: Media3PlayManager

    private fun createController() {
        val context = applicationContext
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlayerService::class.java)
        )
        val controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                player = controllerFuture.get()
                playManager = Media3PlayManager(player)
                player.prepare()
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onCreate() {
        super.onCreate()
        createController()
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
