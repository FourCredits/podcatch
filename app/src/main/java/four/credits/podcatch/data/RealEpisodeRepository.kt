package four.credits.podcatch.data

import four.credits.podcatch.data.persistence.episodes.EpisodeDao
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealEpisodeRepository(
    private val episodeDao: EpisodeDao
) : EpisodeRepository {
    override fun getEpisodeById(id: Long): Flow<Episode?> =
        episodeDao.getEpisodeById(id).map { it?.toDomainModel() }
}
