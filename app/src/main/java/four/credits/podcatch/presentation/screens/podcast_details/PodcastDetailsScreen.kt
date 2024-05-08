package four.credits.podcatch.presentation.screens.podcast_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import four.credits.podcatch.R
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.presentation.theme.AppIcons
import four.credits.podcatch.presentation.theme.PodcatchTheme

@Composable
fun PodcastDetailsScreen(
    viewModel: PodcastDetailsViewModel,
    onNavigateUp: () -> Unit,
) {
    val podcast by viewModel.podcast.collectAsStateWithLifecycle()
    PodcastDetailsScreenInternal(podcast, onDelete = {
        viewModel.delete()
        onNavigateUp()
    })
}

@Composable
private fun PodcastDetailsScreenInternal(
    podcast: Podcast,
    onDelete: () -> Unit,
) {
    Column {
        // TODO: don't hardcode font sizes
        Text(podcast.title, fontSize = 24.sp)
        HorizontalDivider()
        Text(podcast.description)
        Box(modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete) {
            Icon(AppIcons.Delete, stringResource(R.string.alt_delete_podcast))
        }
    }
}

@Preview
@Composable
private fun PodcastDetailsScreenPreview() {
    PodcatchTheme {
        PodcastDetailsScreenInternal(
            Podcast(
                title = "My very important podcast",
                description = "A podcast about android development",
                link = "This shouldn't be visible",
                // TODO: implement displaying podcasts
                listOf(),
            ),
            {},
        )
    }
}
