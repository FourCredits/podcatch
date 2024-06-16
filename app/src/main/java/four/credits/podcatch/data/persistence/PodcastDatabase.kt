package four.credits.podcatch.data.persistence

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import four.credits.podcatch.data.persistence.episodes.Episode
import four.credits.podcatch.data.persistence.episodes.EpisodeDao
import four.credits.podcatch.data.persistence.podcasts.Podcast
import four.credits.podcatch.data.persistence.podcasts.PodcastDao

@Database(
    entities = [Podcast::class, Episode::class],
    version = 6,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(
            from = 4,
            to = 5,
            spec = PodcastDatabase.Migration4To5::class
        ),
        AutoMigration(
            from = 5,
            to = 6,
            spec = PodcastDatabase.Migration5To6::class
        ),
    ]
)
abstract class PodcastDatabase : RoomDatabase() {
    abstract val podcastDao: PodcastDao
    abstract val episodeDao: EpisodeDao

    @DeleteColumn(tableName = "Episode", columnName = "downloaded")
    class Migration4To5 : AutoMigrationSpec

    @DeleteColumn(tableName = "Podcast", columnName = "id")
    class Migration5To6 : AutoMigrationSpec
}
