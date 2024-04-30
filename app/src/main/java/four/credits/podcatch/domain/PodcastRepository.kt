package four.credits.podcatch.domain

import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    suspend fun getPodcast(url: String): Podcast
    suspend fun addPodcast(podcast: Podcast)
    suspend fun deletePodcast(podcast: Podcast)
    fun allPodcasts(): Flow<List<Podcast>>
}
