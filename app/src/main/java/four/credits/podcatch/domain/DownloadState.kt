package four.credits.podcatch.domain

sealed interface DownloadState {
    data object NotDownloaded
    data class Downloading(val progress: DownloadProgress)
    data object Downloaded
}

data class DownloadProgress(
    val downloadedBytes: Long,
    val totalBytes: Long
) {
    fun amountDownloaded(): Float =
        downloadedBytes.toFloat() / totalBytes.toFloat()
}
