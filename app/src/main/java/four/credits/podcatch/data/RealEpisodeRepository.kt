package four.credits.podcatch.data

import android.content.Context
import android.os.Environment
import four.credits.podcatch.data.persistence.episodes.EpisodeDao
import four.credits.podcatch.data.persistence.episodes.toDatabaseModel
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class RealEpisodeRepository(
    private val context: Context,
    private val episodeDao: EpisodeDao
) : EpisodeRepository {
    override fun getEpisodeById(id: Long): Flow<Episode?> =
        episodeDao.getEpisodeById(id).map { it?.toDomainModel() }

    override suspend fun downloadEpisode(
        episode: Episode,
        onProgressUpdate: ((Long, Long) -> Unit)?,
    ) {
        // TODO: extract getting episode file path to a common function
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS),
            "episodes${File.separatorChar}${episode.id}.mp3"
        )
        file.parentFile?.mkdirs()
        downloadFile(
            from = episode.link,
            to = file.absolutePath,
            onProgressUpdate
        )
        episodeDao.upsertEpisode(
            episode.copy(downloaded = true).toDatabaseModel()
        )
    }
}
