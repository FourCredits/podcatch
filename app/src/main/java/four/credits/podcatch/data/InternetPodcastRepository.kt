package four.credits.podcatch.data

import four.credits.podcatch.data.persistence.podcasts.PodcastDao
import four.credits.podcatch.data.persistence.podcasts.toDatabaseModel
import four.credits.podcatch.data.persistence.podcasts.toDomainModel
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.domain.PodcastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.net.URL

class InternetPodcastRepository(
    private val podcastDao: PodcastDao,
) : PodcastRepository {
    override suspend fun getPodcast(url: String): Podcast =
        withContext(Dispatchers.IO) {
            URL(url).openStream().use { parsePodcast(it, url).first() }
        }

    override suspend fun addPodcast(podcast: Podcast) =
        podcastDao.upsertPodcast(podcast.toDatabaseModel())

    override suspend fun deletePodcast(podcast: Podcast) =
        podcastDao.deletePodcast(podcast.toDatabaseModel())

    override fun allPodcasts(): Flow<List<Podcast>> = podcastDao
        .getPodcastsOrderedByTitle()
        .map { podcasts -> podcasts.map { it.toDomainModel() } }

    override fun getPodcastById(id: Long): Flow<Podcast?> = podcastDao
        .getPodcastById(id)
        .map { podcast -> podcast?.toDomainModel() }
}
