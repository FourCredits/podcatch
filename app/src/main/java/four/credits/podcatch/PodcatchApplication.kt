package four.credits.podcatch

import android.app.Application
import four.credits.podcatch.data.InternetPodcastRepository
import four.credits.podcatch.domain.PodcastRepository

class PodcatchApplication : Application() {
    val podcastRepository: PodcastRepository = InternetPodcastRepository()
}
