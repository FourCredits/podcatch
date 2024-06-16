package four.credits.podcatch.presentation.screens.view_podcasts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import four.credits.podcatch.R
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.presentation.theme.AppIcons
import four.credits.podcatch.presentation.theme.LocalSpacing
import four.credits.podcatch.presentation.theme.PodcatchTheme

fun NavGraphBuilder.viewPodcastsScreen(
    onAddPressed: () -> Unit,
    onPodcastPressed: (Podcast) -> Unit,
) = composable(ViewPodcastsRoute) {
    val podcasts by viewModel<ViewPodcastsViewModel>(
        factory = ViewPodcastsViewModel.Factory
    ).podcasts.collectAsStateWithLifecycle()
    ViewPodcastsScreen(
        podcasts,
        onAddPodcastPressed = onAddPressed,
        onPodcastPressed = onPodcastPressed,
    )
}

const val ViewPodcastsRoute = "view_podcasts"

@Composable
private fun ViewPodcastsScreen(
    podcasts: List<Podcast>,
    onAddPodcastPressed: () -> Unit,
    onPodcastPressed: (Podcast) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            AddPodcastFloatingActionButton(onAddPodcastPressed)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(
                LocalSpacing.current.medium
            )
        ) {
            items(podcasts, key = { it.link }) {
                PodcastDisplay(podcast = it, onPressed = onPodcastPressed)
            }
        }
    }
}

@Composable
fun PodcastDisplay(podcast: Podcast, onPressed: (Podcast) -> Unit) =
    Card(modifier = Modifier.clickable { onPressed(podcast) }) {
        Text(text = podcast.title)
        HorizontalDivider()
        Text(
            text = podcast.description,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }

@Composable
private fun AddPodcastFloatingActionButton(onAddPodcastPressed: () -> Unit) {
    FloatingActionButton(
        onClick = onAddPodcastPressed,
        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
    ) {
        Icon(AppIcons.Add, stringResource(R.string.alt_add_new_podcast))
    }
}

@Preview(showBackground = true)
@Composable
private fun ViewPodcastsScreenPreview() {
    val podcasts = listOf(
        Podcast(
            "Podcast 1",
            "A podcast about movies",
            link = "1",
            episodes = listOf(),
        ),
        Podcast(
            "Podcast 2",
            "A podcast about music",
            link = "2",
            episodes = listOf(),
        ),
        Podcast(
            "Podcast 3",
            "A podcast about coding",
            link = "3",
            episodes = listOf(),
        ),
    )
    PodcatchTheme {
        ViewPodcastsScreen(podcasts, {}, {})
    }
}
