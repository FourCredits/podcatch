package four.credits.podcatch.data.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import four.credits.podcatch.domain.Podcast as DomainPodcast

@Entity
data class Podcast(
    // TODO: guarantee the link is unique?
    // TODO: provide some way of preventing duplicates
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val link: String,
)

fun Podcast.toDomainModel() =
    DomainPodcast(title = title, description = description, link = link)

fun DomainPodcast.toDatabaseModel() =
    Podcast(title = title, description = description, link = link)
