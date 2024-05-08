package four.credits.podcatch.data.persistence.podcasts

import androidx.room.Entity
import androidx.room.PrimaryKey
import four.credits.podcatch.domain.Podcast as DomainPodcast
import four.credits.podcatch.domain.Episode as DomainEpisode

@Entity
data class Podcast(
    // TODO: guarantee the link is unique?
    // TODO: provide some way of preventing duplicates
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val link: String,
)

fun Podcast.toDomainModel(
    episodes: List<DomainEpisode> = listOf()
) = DomainPodcast(title, description, link, episodes, id)

fun DomainPodcast.toDatabaseModel(): Podcast =
    Podcast(id, title, description, link)
