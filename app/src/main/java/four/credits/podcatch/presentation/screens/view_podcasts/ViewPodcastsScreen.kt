package four.credits.podcatch.presentation.screens.view_podcasts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import four.credits.podcatch.R
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.presentation.theme.AppIcons
import four.credits.podcatch.presentation.theme.LocalSpacing
import four.credits.podcatch.presentation.theme.PodcatchTheme

@Composable
fun ViewPodcastsScreen(
    viewModel: ViewPodcastsViewModel,
    onAddPodcastPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val podcasts by viewModel.podcasts.collectAsStateWithLifecycle()
    ViewPodcastsScreenInner(
        podcasts,
        onAddPodcastPressed = onAddPodcastPressed,
        onDeletePodcast = viewModel::deletePodcast,
        modifier = modifier
    )
}

@Composable
private fun ViewPodcastsScreenInner(
    podcasts: List<Podcast>,
    onAddPodcastPressed: () -> Unit,
    onDeletePodcast: (Podcast) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        floatingActionButton = {
            AddPodcastFloatingActionButton(onAddPodcastPressed)
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(
                LocalSpacing.current.medium
            )
        ) {
            // TODO: is keying off title alright?
            items(podcasts, key = { it.title }) {
                PodcastDisplay(
                    podcast = it,
                    onDelete = { onDeletePodcast(it) }
                )
            }
        }
    }
}

@Composable
fun PodcastDisplay(podcast: Podcast, onDelete: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = podcast.title)
            HorizontalDivider()
            Text(
                text = podcast.description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onDelete) {
            Icon(AppIcons.Delete, stringResource(R.string.alt_delete_podcast))
        }
    }
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

@Preview
@Composable
private fun ViewPodcastsScreenPreview() {
    val podcasts = listOf(
        Podcast("Podcast 1", "A podcast about movies", link = ""),
        Podcast("Podcast 2", "A podcast about music", link = ""),
        Podcast("Podcast 3", "A podcast about coding", link = ""),
    )
    PodcatchTheme {
        ViewPodcastsScreenInner(podcasts, {}, {})
    }
}
