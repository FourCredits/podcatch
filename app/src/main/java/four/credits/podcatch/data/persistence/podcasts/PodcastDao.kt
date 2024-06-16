package four.credits.podcatch.data.persistence.podcasts

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import four.credits.podcatch.data.persistence.episodes.Episode
import kotlinx.coroutines.flow.Flow
import four.credits.podcatch.domain.Podcast as DomainPodcast

@Dao
interface PodcastDao {
    @Upsert
    suspend fun upsertPodcast(podcast: Podcast)

    @Delete
    suspend fun deletePodcast(podcast: Podcast)

    @Query("SELECT * FROM podcast ORDER BY title ASC")
    fun getPodcastsOrderedByTitle(): Flow<List<Podcast>>

    @Transaction
    @Query("SELECT * FROM podcast WHERE podcast.link = :podcastId")
    fun getPodcastWithEpisodes(podcastId: String): Flow<PodcastAndEpisodes?>
}

data class PodcastAndEpisodes(
    @Embedded val podcast: Podcast,
    @Relation(parentColumn = "link", entityColumn = "podcastId")
    val episodes: List<Episode> = emptyList()
) {
    fun toDomainModel() = DomainPodcast(
        podcast.title,
        podcast.description,
        podcast.link,
        episodes.map { it.toDomainModel() },
    )
}
