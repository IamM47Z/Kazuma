import interfaces.PlatformInterface

import platforms.nonio.Platform

import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester

import org.reflections.Reflections

object AppData {
	val platform_packages = Reflections("platforms").getSubTypesOf(
			PlatformInterface::class.java)
	
	val focus_req = FocusRequester()
	
	val platform: MutableState<PlatformInterface> = mutableStateOf(Platform())
	
	val email = mutableStateOf("")
	val password = mutableStateOf("")
	val domain = mutableStateOf("")
	
	val last_exception: MutableState<Exception?> = mutableStateOf(null)
}