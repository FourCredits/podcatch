package four.credits.podcatch.data

import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.domain.PodcastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.net.URL

class InternetPodcastRepository: PodcastRepository {
    override suspend fun getPodcast(url: String): Podcast =
        withContext(Dispatchers.IO) {
            URL(url).openStream().use { parsePodcast(it).first() }
        }
}
