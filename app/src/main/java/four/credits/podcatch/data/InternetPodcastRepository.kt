package four.credits.podcatch.data

import four.credits.podcatch.data.persistence.episodes.EpisodeDao
import four.credits.podcatch.data.persistence.episodes.toDatabaseModel
import four.credits.podcatch.data.persistence.podcasts.PodcastDao
import four.credits.podcatch.data.persistence.podcasts.toDatabaseModel
import four.credits.podcatch.data.persistence.podcasts.toDomainModel
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.domain.PodcastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.net.URL

class InternetPodcastRepository(
    private val podcastDao: PodcastDao,
    private val episodeDao: EpisodeDao,
) : PodcastRepository {
    override suspend fun getPodcast(url: String): Podcast =
        withContext(Dispatchers.IO) {
            URL(url).openStream().use { parsePodcast(it, url).first() }
        }

    override suspend fun addPodcast(podcast: Podcast) {
        val podcastId = podcastDao.insertPodcast(podcast.toDatabaseModel())
        episodeDao.upsertEpisode(podcast.episodes.map { episode ->
            episode.toDatabaseModel().copy(podcastId = podcastId)
        })
    }

    override suspend fun deletePodcast(podcast: Podcast) =
        podcastDao.deletePodcast(podcast.toDatabaseModel())

    override fun allPodcasts(): Flow<List<Podcast>> = podcastDao
        .getPodcastsOrderedByTitle()
        .map { podcasts -> podcasts.map { it.toDomainModel() } }

    // TODO: make a better way of querying this
    override fun getPodcastById(id: Long): Flow<Podcast?> =
        combine(
            podcastDao.getPodcastById(id),
            episodeDao.getEpisodesByPodcastId(id)
        ) { podcast, episodes ->
            podcast
                ?.toDomainModel()
                ?.copy(episodes = episodes.map { it.toDomainModel() })
        }
}
