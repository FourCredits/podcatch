package four.credits.podcatch.data

import four.credits.podcatch.data.persistence.episodes.EpisodeDao
import four.credits.podcatch.data.persistence.episodes.toDatabaseModel
import four.credits.podcatch.domain.DownloadProgress
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

class RealEpisodeRepository(
    private val downloadManager: DownloadManager,
    private val episodeDao: EpisodeDao
) : EpisodeRepository {
    override fun getEpisodeById(id: Long): Flow<Episode?> =
        episodeDao.getEpisodeById(id).map { it?.toDomainModel() }

    override fun downloadEpisode(episode: Episode): Flow<DownloadProgress> =
        downloadManager.downloadEpisode(episode).onCompletion {
            episodeDao.upsertEpisode(
                episode.copy(downloaded = true).toDatabaseModel()
            )
        }

    override suspend fun deleteDownload(episode: Episode) {
        downloadManager.deleteDownload(episode)
        episodeDao.upsertEpisode(
            episode.copy(downloaded = false).toDatabaseModel()
        )
    }

    override fun getEpisodeUri(episode: Episode): String? =
        downloadManager.getDownload(episode)
}
