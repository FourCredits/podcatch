package four.credits.podcatch.presentation

import android.content.Context
import android.os.Environment
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.workmanager.WorkManagerScheduler
import four.credits.podcatch.R
import java.io.File
import java.util.concurrent.Executor

@OptIn(UnstableApi::class)
class PodcastDownloadService : DownloadService(
    foregroundNotificationId,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    downloadNotificationChannelId,
    androidx.media3.exoplayer.R.string.exo_download_notification_channel_name,
    0
) {
    override fun getDownloadManager() =
        PodcastDownloadService.downloadManager.getOrCreate(this)

    override fun getScheduler() = WorkManagerScheduler(this, downloadNotificationChannelId)

    override fun getForegroundNotification(
        downloads: List<Download>,
        notMetRequirements: Int,
    ) = notificationHelper.getOrCreate(this).buildProgressNotification(
        this,
        R.drawable.download,
        null, // TODO: make this not null
        downloadingMessage,
        downloads,
        notMetRequirements
    )

    // TODO: make this whole thing a lot better
    companion object {
        val downloadManager = ContextInitialised { context ->
            DownloadManager(
                context,
                databaseProvider.getOrCreate(context),
                downloadCache.getOrCreate(context),
                DefaultHttpDataSource.Factory(),
                Executor(Runnable::run)
            )
        }

        val databaseProvider = ContextInitialised(::StandaloneDatabaseProvider)

        val downloadCache = ContextInitialised { context ->
            SimpleCache(
                downloadDirectory.getOrCreate(context),
                NoOpCacheEvictor(),
                databaseProvider.getOrCreate(context)
            )
        }

        val downloadDirectory = ContextInitialised { context ->
            context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS)
            ?: File(context.filesDir, "downloads")
        }

        val notificationHelper = ContextInitialised { context ->
            DownloadNotificationHelper(
                context,
                downloadNotificationChannelId
            )
        }
    }
}

class ContextInitialised<T>(private val create: (Context) -> T) {
    private var value: T? = null

    fun getOrCreate(context: Context) =
        value ?: create(context).also { value = it }
}

// TODO: move elsewhere
// TODO: are these good values?
private const val foregroundNotificationId = 2
private const val downloadNotificationChannelId = "download_channel"

// TODO: should these be a string resource thing?
private const val workName = "podcast_downloading"
private const val downloadingMessage = "Podcatch is downloading..."
