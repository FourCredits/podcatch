package four.credits.podcatch.data

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import four.credits.podcatch.domain.DownloadManager
import four.credits.podcatch.domain.DownloadProgress
import four.credits.podcatch.domain.DownloadState
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.presentation.PodcastDownloadService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
class Media3DownloadManager(val context: Context) : DownloadManager {
    override fun downloadStatus(episode: Episode): Flow<DownloadState> = flow {
        while (true) {
            delay(50.milliseconds)
            emit(determineState(getDownload(context, episode.link)))
        }
    }

    private fun determineState(download: Download?): DownloadState =
        when (download?.state) {
            Download.STATE_COMPLETED -> DownloadState.Downloaded
            Download.STATE_DOWNLOADING -> DownloadState.InProgress(
                DownloadProgress(download.percentDownloaded)
            )
            Download.STATE_RESTARTING, Download.STATE_QUEUED ->
                DownloadState.InProgress(DownloadProgress())
            Download.STATE_FAILED,
            Download.STATE_REMOVING,
            Download.STATE_STOPPED -> (DownloadState.NotDownloaded)
            else -> DownloadState.NotDownloaded
        }

    // using the link as the content id
    override suspend fun download(episode: Episode) {
        val uri = Uri.parse(episode.link)
        DownloadService.sendAddDownload(
            context,
            PodcastDownloadService::class.java,
            DownloadRequest.Builder(episode.link, uri).build(),
            false
        )
    }

    override suspend fun deleteDownload(episode: Episode) =
        DownloadService.sendRemoveDownload(
            context,
            PodcastDownloadService::class.java,
            episode.link,
            false
        )

    override suspend fun cancelDownload(episode: Episode) =
        deleteDownload(episode)
}
