package four.credits.podcatch.presentation.screens.view_podcasts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.presentation.common.PodcastDisplay

@Composable
fun ViewPodcastsScreen(
    viewModel: ViewPodcastsViewModel,
    onAddPodcastPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    ViewPodcastsScreenInner(
        podcasts = viewModel.podcasts.value,
        onAddPodcastPressed = onAddPodcastPressed,
        modifier = modifier
    )
}

@Composable
private fun ViewPodcastsScreenInner(
    podcasts: List<Podcast>,
    onAddPodcastPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        floatingActionButton = {
            AddPodcastFloatingActionButton(onAddPodcastPressed)
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier.padding(padding),
            // TODO: standardise padding
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TODO: is keying off title alright?
            items(podcasts, key = { it.title }) { PodcastDisplay(it) }
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
        // TODO: don't hard code the button type, or alt text
        Icon(Icons.Filled.Add, "Add new podcast")
    }
}

@Preview
@Composable
private fun ViewPodcastsScreenPreview() {
    val podcasts = listOf(
        Podcast("Podcast 1", "A podcast about movies"),
        Podcast("Podcast 2", "A podcast about music"),
        Podcast("Podcast 3", "A podcast about coding"),
    )
    ViewPodcastsScreenInner(podcasts, {})
}
