package four.credits.podcatch.data.persistence.podcasts

import androidx.room.Entity
import androidx.room.PrimaryKey
import four.credits.podcatch.domain.Podcast as DomainPodcast
import four.credits.podcatch.domain.Episode as DomainEpisode

@Entity
data class Podcast(
    val title: String,
    val description: String,
    @PrimaryKey val link: String,
) {
    fun toDomainModel(episodes: List<DomainEpisode> = listOf()) =
        DomainPodcast(title, description, link, episodes)
}

fun DomainPodcast.toDatabaseModel() = Podcast(title, description, link)
