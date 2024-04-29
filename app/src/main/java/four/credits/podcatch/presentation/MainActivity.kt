package four.credits.podcatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import four.credits.podcatch.presentation.screens.add_podcast.AddPodcastScreen
import four.credits.podcatch.presentation.screens.add_podcast.AddPodcastViewModel
import four.credits.podcatch.presentation.screens.view_podcasts.ViewPodcastsScreen
import four.credits.podcatch.presentation.screens.view_podcasts.ViewPodcastsViewModel
import four.credits.podcatch.presentation.theme.PodcatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Root() }
    }
}


@Composable
private fun Root() {
    PodcatchTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            NavRoot()
        }
    }
}

private const val ViewPodcastsRoute = "view_podcasts"
private const val AddPodcastRoute = "add_podcast"

@Composable
private fun NavRoot() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = ViewPodcastsRoute) {
        composable(ViewPodcastsRoute) {
            ViewPodcastsScreen(
                viewModel<ViewPodcastsViewModel>(
                    factory = ViewPodcastsViewModel.Factory
                ),
                onAddPodcastPressed = {
                    navController.navigate(AddPodcastRoute)
                }
            )
        }
        composable(AddPodcastRoute) {
            AddPodcastScreen(
                viewModel<AddPodcastViewModel>(
                    factory = AddPodcastViewModel.Factory
                ),
                onNavigateUp = { navController.popBackStack() }
            )
        }
    }
}
