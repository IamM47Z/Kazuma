import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val dark_colors = darkColors(primary = Color(0xFF2c9fff),
                                     onPrimary = Color(0xFF003258),
                                     secondary = Color(0xFFbbc7db),
                                     onSecondary = Color(0xFF253140),
                                     error = Color(0xFFffb4ab),
                                     onError = Color(0xFF690005),
                                     background = Color(0xFF1a1c1e),
                                     onBackground = Color(0xFFe2e2e6),
                                     surface = Color(0xFF1a1c1e))

private val light_colors = lightColors(primary = Color(0xFF0061a4),
                                       onPrimary = Color(0xFFFFFFFF),
                                       secondary = Color(0xFF535f70),
                                       onSecondary = Color(0xFFffffff),
                                       error = Color(0xFFba1a1a),
                                       onError = Color(0xFFffffff),
                                       background = Color(0xFFfdfcff),
                                       onBackground = Color(0xFF1a1c1e),
                                       surface = Color(0xFFfdfcff))

@Composable
fun Theme(dark_theme: Boolean = isSystemInDarkTheme(),
          content: @Composable (PaddingValues) -> Unit) {
	MaterialTheme(colors = if (dark_theme) dark_colors else light_colors) {
		Scaffold(content = content)
	}
}

@Preview
@Composable
private fun ThemePreview() {
	Theme(dark_theme = false) {
		Column(modifier = Modifier.fillMaxSize(),
		       verticalArrangement = Arrangement.Center,
		       horizontalAlignment = Alignment.CenterHorizontally) {
			Text("Hello World")
		}
	}
}