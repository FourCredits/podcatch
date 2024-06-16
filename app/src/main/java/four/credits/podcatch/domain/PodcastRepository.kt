package four.credits.podcatch.domain

import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    suspend fun getNewPodcast(url: String): Podcast
    suspend fun addPodcast(podcast: Podcast)
    suspend fun deletePodcast(podcast: Podcast)
    fun allPodcasts(): Flow<List<Podcast>>
    fun getPodcastById(id: String): Flow<Podcast?>
}
