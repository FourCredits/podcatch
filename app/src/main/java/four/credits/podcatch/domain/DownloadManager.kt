package four.credits.podcatch.domain

import kotlinx.coroutines.flow.Flow

interface DownloadManager {
    fun downloadStatus(episode: Episode): Flow<DownloadState>
    suspend fun download(episode: Episode)
    suspend fun deleteDownload(episode: Episode)
    suspend fun cancelDownload(episode: Episode)
}

sealed interface DownloadState {
    data object NotDownloaded : DownloadState
    data object Downloaded : DownloadState
    data class InProgress(val progress: DownloadProgress) : DownloadState
}

data class DownloadProgress(val percentage: Float = 0f) {
    fun asDecimal() = percentage / 100
}
