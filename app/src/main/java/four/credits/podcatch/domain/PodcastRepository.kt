package four.credits.podcatch.domain

interface PodcastRepository {
    suspend fun getPodcast(url: String): String
}
