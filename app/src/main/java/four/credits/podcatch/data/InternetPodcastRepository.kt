package four.credits.podcatch.data

import android.os.Build
import androidx.annotation.RequiresApi
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.domain.PodcastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.time.Duration.Companion.milliseconds

class InternetPodcastRepository: PodcastRepository {
    // TODO: don't just store in memory
    private val podcasts = mutableListOf<Podcast>()

    override suspend fun getPodcast(url: String): Podcast =
        withContext(Dispatchers.IO) {
            URL(url).openStream().use { parsePodcast(it, url).first() }
        }

    override suspend fun addPodcast(podcast: Podcast) {
        podcasts.add(podcast)
    }

    override fun allPodcasts(): Flow<List<Podcast>> = flow {
        while(true) {
            emit(podcasts)
            delay(500.milliseconds)
        }
    }
}
