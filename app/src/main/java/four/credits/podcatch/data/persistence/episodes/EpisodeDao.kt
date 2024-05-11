package four.credits.podcatch.data.persistence.episodes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Upsert
    suspend fun upsertEpisode(episode: List<Episode>)

    @Upsert
    suspend fun upsertEpisode(episode: Episode)

    @Delete
    suspend fun deleteEpisode(episode: Episode)

    @Query("SELECT * FROM episode WHERE id = :id")
    fun getEpisodeById(id: Long): Flow<Episode?>

    @Query("SELECT * FROM episode WHERE podcastId = :podcastId")
    fun getEpisodesByPodcastId(podcastId: Long): Flow<List<Episode>>
}
