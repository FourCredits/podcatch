package four.credits.podcatch.presentation.screens.add_podcast

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.presentation.common.PodcastDisplay
import four.credits.podcatch.presentation.theme.LocalSpacing
import four.credits.podcatch.presentation.theme.PodcatchTheme

@Composable
fun AddPodcastScreen(
    viewModel: AddPodcastViewModel,
    onNavigateUp: () -> Unit
) {
    AddPodcastInner(
        url = viewModel.url,
        setUrl = { viewModel.url = it },
        result = viewModel.result,
        onSearch = { viewModel.searchUrl() },
        onClear = { viewModel.clearSearch() },
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
                        Icons.Filled.Clear,
                        "Clear input",
                        modifier = Modifier.clickable { onClear() }
                    )
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search for podcast",
                        modifier = Modifier.clickable { onSearch() }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        // TODO: add option to insert from clipboard
        when (result) {
            Result.Loading -> Text("Loading...")
            Result.Nothing -> Text("Nothing to display yet")
            is Result.Loaded -> Column {
                PodcastDisplay(result.podcast)
                Button(onClick = onAdd) {
                    Text("Add Podcast")
                    Icon(Icons.Filled.Add, null)
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
                "https://example.com/podcast"
            )),
            {},
            onClear = { text = "" },
            {},
        )
    }
}
