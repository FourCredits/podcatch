package four.credits.podcatch.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import four.credits.podcatch.presentation.theme.PodcatchTheme

@Composable
fun Home(viewModel: HomeViewModel) {
     HomeInner(
         url = viewModel.url.value,
         setUrl = { viewModel.url.value = it },
         result = viewModel.result.value,
         onSubmit = { viewModel.submitUrl() },
         onClear = { viewModel.clearContent() },
     )
}

@Composable
private fun HomeInner(
    url: String,
    setUrl: (String) -> Unit,
    result: Result,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
) {
    Column {
        TextField(
            value = url,
            onValueChange = setUrl,
            label = { Text("Enter the url to use") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onClear) {
                Text("Clear")
            }
            Button(onClick = onSubmit) {
                Text("Submit")
            }
        }
        Text(
            text = when (result) {
                is Result.Loaded -> result.result.take(10_000)
                Result.Loading -> "Loading..."
                Result.Nothing -> "Nothing to load yet"
            },
            modifier = Modifier.verticalScroll(rememberScrollState()),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    PodcatchTheme {
        HomeInner(
            "https://www.example.com",
            {},
            Result.Loaded("<some xml>"),
            {},
            {}
        )
    }
}
