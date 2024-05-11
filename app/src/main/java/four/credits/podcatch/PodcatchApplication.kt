package four.credits.podcatch

import android.app.Application
import androidx.room.Room
import four.credits.podcatch.data.DbEpisodeRepository
import four.credits.podcatch.data.RealPodcastRepository
import four.credits.podcatch.data.persistence.PodcastDatabase
import four.credits.podcatch.domain.EpisodeRepository
import four.credits.podcatch.domain.PodcastRepository

class PodcatchApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            this,
            PodcastDatabase::class.java,
            "podcast-db"
        ).build()
    }

    val podcastRepository: PodcastRepository by lazy {
        RealPodcastRepository(database.podcastDao, database.episodeDao)
    }

    val episodeRepository: EpisodeRepository by lazy {
        DbEpisodeRepository(database.episodeDao)
    }
}
