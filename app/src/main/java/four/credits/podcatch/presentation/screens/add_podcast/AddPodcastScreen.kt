package four.credits.podcatch.presentation.screens.add_podcast

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.getSystemService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import four.credits.podcatch.R
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.presentation.theme.AppIcons
import four.credits.podcatch.presentation.theme.LocalSpacing
import four.credits.podcatch.presentation.theme.PodcatchTheme

fun NavGraphBuilder.addPodcastScreen(
    onNavigateUp: () -> Unit
) = composable(AddPodcastRoute) {
    val viewModel =
        viewModel<AddPodcastViewModel>(
        factory = AddPodcastViewModel.Factory
    )
    val url by viewModel.url.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val context = LocalContext.current
    AddPodcastScreen(
        url,
        setUrl = viewModel::setSearch,
        result,
        onSearch = viewModel::searchUrl,
        onClear = viewModel::clearSearch,
        onAdd = {
            viewModel.addPodcast()
            onNavigateUp()
        },
        onPaste = {
            viewModel.setSearch(pasteFromClipboard(context).toString())
            viewModel.searchUrl()
        }
    )
}

fun NavController.navigateToAddPodcast() = navigate(AddPodcastRoute)

private const val AddPodcastRoute = "add_podcast"

@Composable
private fun AddPodcastScreen(
    url: String,
    setUrl: (String) -> Unit,
    result: Result,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onAdd: () -> Unit,
    onPaste: () -> Unit,
) {
    Column {
        OutlinedTextField(
            value = url,
            onValueChange = setUrl,
            label = { Text(stringResource(R.string.enter_the_url_to_use)) },
            singleLine = true,
            trailingIcon = { SearchButtons(onPaste, onClear, onSearch) },
            modifier = Modifier.fillMaxWidth(),
        )
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

@Composable
private fun SearchButtons(
    onPaste: () -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
) {
    Icon(
        painterResource(id = R.drawable.paste),
        stringResource(R.string.paste_from_clipboard),
        modifier = Modifier.clickable { onPaste() }
    )
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

private fun pasteFromClipboard(context: Context): CharSequence? {
    val clipboard = context.getSystemService<ClipboardManager>() ?: return null
    val hasPrimaryClip = clipboard.hasPrimaryClip()
    val isPlainText = clipboard
        .primaryClipDescription
        ?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
    if (!hasPrimaryClip || isPlainText != true) return null
    val item = clipboard.primaryClip?.getItemAt(0)
    return item?.text ?: item?.uri?.resolve(context)
}

fun Uri.resolve(context: Context) = context
    .contentResolver
    .openInputStream(this)
    ?.use { it.bufferedReader().readText() }

@Preview(showBackground = true)
@Composable
private fun AddPodcastPreview() {
    var text by remember { mutableStateOf("https://www.example.com") }
    PodcatchTheme {
        AddPodcastScreen(
            text,
            { text = it },
            Result.Loaded(
                Podcast(
                    "My Podcast",
                    "A podcast where I talk about me",
                    "https://example.com/podcast",
                    // TODO: you should possibly be able to see a details screen for
                    //  a podcast you haven't necessarily saved
                    listOf(),
                )
            ),
            {},
            onClear = { text = "" },
            {},
            {},
        )
    }
}
