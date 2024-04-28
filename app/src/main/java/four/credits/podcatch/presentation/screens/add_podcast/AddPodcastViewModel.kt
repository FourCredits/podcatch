package four.credits.podcatch.presentation.screens.add_podcast

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

class AddPodcastViewModel(
    private val podcastRepository: PodcastRepository
): ViewModel() {
    var url by mutableStateOf("")
    var result: Result by mutableStateOf(Result.Nothing)
        private set

    fun searchUrl() {
        result = Result.Loading
        viewModelScope.launch {
            result = Result.Loaded(podcastRepository.getPodcast(url))
        }
    }

    fun clearSearch() {
        url = ""
    }

    fun addPodcast() {
        val r = result
        if (r is Result.Loaded) {
            val podcast = r.podcast
            viewModelScope.launch {
                podcastRepository.addPodcast(podcast)
            }
        }
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as PodcatchApplication
                val repository = application.podcastRepository
                AddPodcastViewModel(repository)
            }
        }
    }
}

sealed interface Result {
    data object Nothing : Result
    data object Loading : Result
    data class Loaded(val podcast: Podcast) : Result
}
