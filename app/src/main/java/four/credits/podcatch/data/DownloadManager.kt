package four.credits.podcatch.data

import android.content.Context
import android.os.Build
import android.os.Environment
import four.credits.podcatch.domain.DownloadProgress
import four.credits.podcatch.domain.Episode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLConnection

class DownloadManager(private val context: Context) {
    private val inFlightDownloads =
        mutableMapOf<Long, StateFlow<DownloadProgress>>()

    fun downloadProgress(id: Long): StateFlow<DownloadProgress>? =
        inFlightDownloads[id]

    fun getDownload(episode: Episode): String? =
        episode.takeIf { it.downloaded }?.fileLocation()?.absolutePath

    suspend fun deleteDownload(episode: Episode) = withContext(Dispatchers.IO) {
        episode.fileLocation().delete()
    }

    private fun Episode.fileLocation(): File = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS),
        "episodes${File.separatorChar}$id.mp3"
    )

    fun downloadEpisode(episode: Episode): Flow<DownloadProgress> =
        episode.fileLocation().let { file ->
            if (episode.downloaded || file.exists()) emptyFlow()
            else flow {
                file.parentFile?.mkdirs()
                // terminate the state flow
                addDownload(episode, file)
                    .takeWhile { it.downloadedBytes < it.totalBytes }
                    .onCompletion { emit(DownloadProgress(1, 1)) }
                    .collect(this)
            }
        }

    private suspend fun addDownload(
        episode: Episode,
        file: File,
    ) = inFlightDownloads.getOrPut(episode.id) {
        downloadFile(from = episode.link, to = file.absolutePath)
            .onCompletion { inFlightDownloads.remove(episode.id) }
            .stateIn(CoroutineScope(Dispatchers.IO))
    }
}

// function based off the stack overflow answer here:
// https://stackoverflow.com/a/50900037 and also InputStream.copyTo
private suspend fun downloadFile(from: String, to: String) = flow {
    val url = URL(from)
    val length = url.openConnection().apply { connect() }.length
    url.openStream().use { input ->
        FileOutputStream(File(to)).use { output ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead = input.read(buffer)
            var bytesCopied = 0L
            while (bytesRead >= 0) {
                output.write(buffer, 0, bytesRead)
                bytesCopied += bytesRead
                emit(DownloadProgress(bytesCopied, length))
                bytesRead = input.read(buffer)
            }
        }
    }
}.flowOn(Dispatchers.IO)

private val URLConnection.length
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) contentLengthLong
        else contentLength.toLong()
