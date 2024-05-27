package four.credits.podcatch.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import four.credits.podcatch.presentation.screens.add_podcast.addPodcastScreen
import four.credits.podcatch.presentation.screens.add_podcast.navigateToAddPodcast
import four.credits.podcatch.presentation.screens.episode_details.episodeDetailsScreen
import four.credits.podcatch.presentation.screens.episode_details.navigateToEpisode
import four.credits.podcatch.presentation.screens.podcast_details.navigateToPodcast
import four.credits.podcatch.presentation.screens.podcast_details.podcastDetailsScreen
import four.credits.podcatch.presentation.screens.view_podcasts.ViewPodcastsRoute
import four.credits.podcatch.presentation.screens.view_podcasts.viewPodcastsScreen
import four.credits.podcatch.presentation.theme.PodcatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handlePermissions()
        setContent { Root() }
    }

    private fun handlePermissions() {
        // TODO: handle permissions better
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }
}

@Composable
private fun Root() = PodcatchTheme {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        NavRoot()
    }
}

@Composable
private fun NavRoot() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = ViewPodcastsRoute) {
        viewPodcastsScreen(
            onAddPressed = navController::navigateToAddPodcast,
            onPodcastPressed = navController::navigateToPodcast
        )
        addPodcastScreen(onNavigateUp = navController::popBackStack)
        podcastDetailsScreen(
            onNavigateUp = navController::popBackStack,
            onEpisodeClick = navController::navigateToEpisode
        )
        episodeDetailsScreen()
    }
}
