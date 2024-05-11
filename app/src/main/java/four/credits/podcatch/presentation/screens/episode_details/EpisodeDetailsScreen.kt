package four.credits.podcatch.presentation.screens.episode_details

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import four.credits.podcatch.R
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.presentation.theme.PodcatchTheme

// TODO: add download logic here
fun NavGraphBuilder.episodeDetailsScreen() = composable(
    "$EpisodeDetailsRoute/{$IdArg}",
    arguments = listOf(navArgument(IdArg) { type = NavType.LongType })
) {
    val viewModel = viewModel<EpisodeDetailsViewModel>(
        factory = EpisodeDetailsViewModel.Factory,
    )
    val episode by viewModel.episode.collectAsStateWithLifecycle()
    val context = LocalContext.current
    EpisodeDetailsScreen(episode, onDownload = {
        Toast.makeText(
            context,
            "download not implemented yet",
            Toast.LENGTH_SHORT
        ).show()
    })
}

fun NavController.navigateToEpisode(id: Long) = navigate("$EpisodeDetailsRoute/$id")

private const val EpisodeDetailsRoute = "episode_details"
internal const val IdArg = "episodeId"

// TODO: display podcast name
@Composable
private fun EpisodeDetailsScreen(
    episode: Episode,
    onDownload: () -> Unit,
) {
    Column {
        // TODO: don't hardcode font sizes
        Text(text = episode.title, fontSize = 24.sp)
        HorizontalDivider()
        Text(text = episode.description)
        Box(modifier = Modifier.weight(1f))
        HorizontalDivider()
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            IconButton(onClick = onDownload) {
                Icon(
                    painterResource(R.drawable.download),
                    stringResource(R.string.download_episode)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EpisodeDetailsScreenPreview() {
    PodcatchTheme {
        EpisodeDetailsScreen(
            episode = Episode(
                "My example episode",
                "A description for the episode",
                "shouldn't be shown",
            ),
            onDownload = {},
        )
    }
}
