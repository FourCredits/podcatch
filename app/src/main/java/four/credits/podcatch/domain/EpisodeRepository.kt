package four.credits.podcatch.domain

import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    fun getEpisodeById(id: Long): Flow<Episode?>

    fun downloadEpisode(episode: Episode): Flow<DownloadProgress>

    suspend fun deleteDownload(episode: Episode)
}
