package four.credits.podcatch.data.persistence.episodes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import four.credits.podcatch.data.persistence.podcasts.Podcast
import four.credits.podcatch.domain.Episode as DomainEpisode

@Entity(
    foreignKeys = [ForeignKey(
        entity = Podcast::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("podcastId"),
        onDelete = ForeignKey.CASCADE
    )],
)
data class Episode(
    val title: String,
    val description: String,
    val link: String,
    @ColumnInfo(index = true) val podcastId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {
    fun toDomainModel(): DomainEpisode =
        DomainEpisode(title, description, link, id)
}

fun DomainEpisode.toDatabaseModel() = Episode(title, description, link, id)
