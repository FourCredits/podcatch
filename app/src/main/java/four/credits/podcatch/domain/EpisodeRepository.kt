package four.credits.podcatch.domain

import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    fun getEpisodeById(id: Long): Flow<Episode?>

    suspend fun downloadEpisode(
        episode: Episode,
        onProgressUpdate: ((Long, Long) -> Unit)? = null,
    )

    suspend fun deleteDownload(episode: Episode)
}
