package interfaces

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.painter.BitmapPainter

interface SessionInterface {
	val logged_in: MutableState<Boolean>
	val captcha: MutableState<BitmapPainter?>
	
	fun login(captcha: String = "")
	
	fun logout()
}