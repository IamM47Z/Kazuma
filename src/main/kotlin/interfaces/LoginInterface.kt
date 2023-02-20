package interfaces

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.painter.BitmapPainter

interface LoginInterface {
	val logged_in: MutableState<Boolean>
	val captcha: MutableState<BitmapPainter?>
	
	fun login(domain: String,
	          email: String,
	          password: String,
	          captcha: String = "")
	
	fun logout()
}