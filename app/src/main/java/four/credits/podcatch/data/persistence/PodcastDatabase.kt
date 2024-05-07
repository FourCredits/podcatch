package four.credits.podcatch.data.persistence

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import four.credits.podcatch.data.persistence.episodes.Episode
import four.credits.podcatch.data.persistence.episodes.EpisodeDao
import four.credits.podcatch.data.persistence.podcasts.Podcast
import four.credits.podcatch.data.persistence.podcasts.PodcastDao

@Database(
    entities = [Podcast::class, Episode::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ]
)
abstract class PodcastDatabase : RoomDatabase() {
    // TODO: rename podcast dao
    abstract val podcastDao: PodcastDao
    abstract val episodeDao: EpisodeDao
}
