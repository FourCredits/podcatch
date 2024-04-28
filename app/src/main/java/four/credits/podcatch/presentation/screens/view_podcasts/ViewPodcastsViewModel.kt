package four.credits.podcatch.presentation.screens.view_podcasts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import four.credits.podcatch.PodcatchApplication
import four.credits.podcatch.domain.Podcast
import four.credits.podcatch.domain.PodcastRepository
import kotlinx.coroutines.launch

class ViewPodcastsViewModel(
    private val podcastRepository: PodcastRepository
) : ViewModel() {
    var podcasts by mutableStateOf(listOf<Podcast>())
        private set

    init {
        viewModelScope.launch {
            podcastRepository.allPodcasts().collect {
                podcasts = it
            }
        }
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PodcatchApplication
                val repository = application.podcastRepository
                ViewPodcastsViewModel(repository)
            }
        }
    }
}
