package four.credits.podcatch.presentation.screens.add_podcast

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import four.credits.podcatch.R
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.presentation.theme.AppIcons
import four.credits.podcatch.presentation.theme.LocalSpacing
import four.credits.podcatch.presentation.theme.PodcatchTheme

@Composable
fun AddPodcastScreen(
    viewModel: AddPodcastViewModel,
    onNavigateUp: () -> Unit
) {
    val url by viewModel.url.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    AddPodcastInner(
        url,
        setUrl = viewModel::setSearch,
        result,
        onSearch = viewModel::searchUrl,
        onClear = viewModel::clearSearch,
        onAdd = {
            viewModel.addPodcast()
            onNavigateUp()
        }
    )
}

@Composable
private fun AddPodcastInner(
    url: String,
    setUrl: (String) -> Unit,
    result: Result,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onAdd: () -> Unit,
) {
    Column {
        OutlinedTextField(
            value = url,
            onValueChange = setUrl,
            label = { Text("Enter the url to use") },
            singleLine = true,
            trailingIcon = {
                // TODO: what's the recommended spacing?
                Row(horizontalArrangement = Arrangement.spacedBy(
                    LocalSpacing.current.small
                )) {
                    Icon(
                        AppIcons.Clear,
                        stringResource(R.string.alt_clear_input),
                        modifier = Modifier.clickable { onClear() }
                    )
                    Icon(
                        imageVector = AppIcons.Search,
                        stringResource(R.string.alt_search_for_podcast),
                        modifier = Modifier.clickable { onSearch() }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        // TODO: add option to insert from clipboard
        when (result) {
            Result.Loading -> Text(stringResource(R.string.loading))
            Result.Nothing -> Text(
                stringResource(R.string.nothing_to_display_yet)
            )
            is Result.Loaded -> Column {
                Card {
                    Text(text = result.podcast.title)
                    HorizontalDivider()
                    Text(text = result.podcast.description)
                }
                Button(onClick = onAdd) {
                    Text(stringResource(R.string.add_podcast))
                    Icon(AppIcons.Add, null)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPodcastPreview() {
    var text by remember { mutableStateOf("https://www.example.com") }
    PodcatchTheme {
        AddPodcastInner(
            text,
            { text = it },
            Result.Loaded(Podcast(
                "My Podcast",
                "A podcast where I talk about me",
                "https://example.com/podcast",
                // TODO: you should possibly be able to see a details screen for
                //  a podcast you haven't necessarily saved
                listOf(),
            )),
            {},
            onClear = { text = "" },
            {},
        )
    }
}
