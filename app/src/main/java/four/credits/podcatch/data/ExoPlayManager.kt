package four.credits.podcatch.data

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import four.credits.podcatch.domain.PlayManager
import four.credits.podcatch.domain.PlayState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ExoPlayManager(private val player: ExoPlayer): PlayManager {
    private val currentlyPlaying: MutableStateFlow<PlayState> =
        MutableStateFlow(PlayState.NotStarted)

    override fun currentlyPlaying(): Flow<PlayState> = currentlyPlaying

    override suspend fun play(id: Long, uri: String) {
        currentlyPlaying.value.takeIf { it.playingId != id }?.run {
            player.setMediaItem(MediaItem.fromUri(uri))
        }
        player.play()
        currentlyPlaying.emit(PlayState.Playing(id))
    }

    override suspend fun pause() {
        currentlyPlaying.value.playingId?.let {
            currentlyPlaying.emit(PlayState.Paused(it))
            player.pause()
        }
    }
}
