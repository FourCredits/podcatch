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
class PodcastDownloadService : DownloadService(foregroundNotificationId) {
    override fun getDownloadManager() = getDownloadManager(this)
    override fun getScheduler() = WorkManagerScheduler(this, workName)

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int,
    ) = getNotificationHelper(this).buildProgressNotification(
        this,
        R.drawable.download,
        null, // TODO: make this not null
        downloadingMessage,
        downloads,
        notMetRequirements
    )

    companion object {
        private var manager: DownloadManager? = null
        private var file: File? = null
        private var notificationHelper: DownloadNotificationHelper? = null

        private fun getDownloadManager(context: Context) =
            manager ?: createDownloadManager(context).also { manager = it }

        private fun createDownloadManager(context: Context): DownloadManager =
            StandaloneDatabaseProvider(context).let { databaseProvider ->
                DownloadManager(
                    context,
                    databaseProvider,
                    createCache(context, databaseProvider),
                    DefaultHttpDataSource.Factory(),
                    Executor(Runnable::run)
                )
            }

        private fun createCache(
            context: Context,
            databaseProvider: StandaloneDatabaseProvider,
        ) = SimpleCache(getFile(context), NoOpCacheEvictor(), databaseProvider)

        private fun getFile(context: Context): File =
            file ?: createFile(context).also { file = it }

        private fun createFile(context: Context) =
            context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS)
            ?: File(context.filesDir, "downloads")

        private fun getNotificationHelper(context: Context) =
            notificationHelper ?: DownloadNotificationHelper(
                context,
                downloadNotificationChannelId
            ).also { notificationHelper = it }
    }
}

// TODO: move elsewhere
// TODO: are these good values?
private const val foregroundNotificationId = 2
private const val downloadNotificationChannelId = "download_channel"

// TODO: should these be a string resource thing?
private const val workName = "podcast_downloading"
private const val downloadingMessage = "Podcatch is downloading..."
