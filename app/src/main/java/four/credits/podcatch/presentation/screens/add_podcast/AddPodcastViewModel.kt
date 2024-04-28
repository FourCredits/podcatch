package four.credits.podcatch.presentation.screens.add_podcast

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
    val url = mutableStateOf("")
    val result: MutableState<Result> = mutableStateOf(Result.Nothing)

    fun submitUrl() {
        result.value = Result.Loading
        viewModelScope.launch {
            result.value =
                Result.Loaded(podcastRepository.getPodcast(url.value))
        }
    }

    fun clearContent() {
        result.value = Result.Nothing
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
    data class Loaded(val result: Podcast) : Result
}
