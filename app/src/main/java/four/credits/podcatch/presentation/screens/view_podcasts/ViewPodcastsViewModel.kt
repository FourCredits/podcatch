package four.credits.podcatch.presentation.screens.view_podcasts

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import four.credits.podcatch.domain.Podcast

class ViewPodcastsViewModel : ViewModel() {
    var podcasts = mutableStateOf(listOf<Podcast>())
        private set

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ViewPodcastsViewModel()
            }
        }
    }
}
