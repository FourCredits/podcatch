package four.credits.podcatch.data.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {
    @Upsert
    suspend fun upsertPodcast(podcast: Podcast)

    @Delete
    suspend fun deletePodcast(podcast: Podcast)

    @Query("SELECT * FROM podcast ORDER BY title ASC")
    fun getPodcastsOrderedByTitle(): Flow<List<Podcast>>
}
