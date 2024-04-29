package four.credits.podcatch.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val verySmall: Dp = 2.dp,
    val small: Dp = 4.dp,
    val medium: Dp = 8.dp,
    val large: Dp = 16.dp,
    val veryLarge: Dp = 32.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

@Preview
@Composable
fun SpacingPreview() = Column {
    val spacingValues = listOf(
        LocalSpacing.current.verySmall,
        LocalSpacing.current.small,
        LocalSpacing.current.medium,
        LocalSpacing.current.large,
        LocalSpacing.current.veryLarge,
    )
    spacingValues.forEach { spacing ->
        Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
            Text("box 1", modifier = Modifier.background(Color.Red))
            Text("box 2", modifier = Modifier.background(Color.Blue))
        }
    }
}
