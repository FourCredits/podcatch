package four.credits.podcatch.data

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import four.credits.podcatch.presentation.PodcastDownloadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(UnstableApi::class)
suspend fun getDownload(context: Context, id: String): Download? =
    withContext(Dispatchers.IO) {
        PodcastDownloadService.downloadManager
            .getOrCreate(context)
            .downloadIndex
            .getDownload(id)
    }
