package four.credits.podcatch.data

import android.content.Context
import android.os.Environment
import four.credits.podcatch.data.persistence.episodes.EpisodeDao
import four.credits.podcatch.data.persistence.episodes.toDatabaseModel
import four.credits.podcatch.domain.DownloadProgress
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.EpisodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

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
}
