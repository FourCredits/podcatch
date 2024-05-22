package four.credits.podcatch.domain

data class DownloadProgress(
    val downloadedBytes: Long,
    val totalBytes: Long
) {
    fun amountDownloaded(): Float =
        downloadedBytes.toFloat() / totalBytes.toFloat()

    fun isComplete() = downloadedBytes == totalBytes
}

