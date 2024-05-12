package four.credits.podcatch.data

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLConnection

// function based off the stack overflow answer here:
// https://stackoverflow.com/a/50900037
suspend fun downloadFile(
    from: String,
    to: String,
    onProgressReport: ((Long, Long) -> Unit)? = null
): Long = withContext(Dispatchers.IO) {
    val url = URL(from)
    val connection = url.openConnection()
    connection.connect()
    val length = connection.length
    url.openStream().use { input ->
        FileOutputStream(File(to)).use { output ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead = input.read(buffer)
            var bytesCopied = 0L
            while (bytesRead >= 0) {
                output.write(buffer, 0, bytesRead)
                bytesCopied += bytesRead
                onProgressReport?.invoke(bytesCopied, length)
                bytesRead = input.read(buffer)
            }
            bytesCopied
        }
    }
}

private val URLConnection.length get() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) contentLengthLong
    else contentLength.toLong()
