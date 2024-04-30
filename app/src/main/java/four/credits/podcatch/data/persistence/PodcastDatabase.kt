package four.credits.podcatch.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Podcast::class], version = 1)
abstract class PodcastDatabase : RoomDatabase() {
    abstract val dao: PodcastDao
}
