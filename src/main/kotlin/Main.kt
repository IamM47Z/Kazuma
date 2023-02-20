import screens.Screen

import screens.dialogs.*
import screens.CustomNavigationHost
import screens.navigation.rememberNavController

import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.application
import androidx.compose.runtime.LaunchedEffect

@Composable
@ExperimentalMaterialApi
fun App() {
	val navController by rememberNavController(Screen.LoginScreen.name)
	
	Theme {
		Scaffold {
			if (AppData.platform.value.captcha.value != null) {
				showCaptcha(navController)
				
				LaunchedEffect(Unit) {
					AppData.focus_req.requestFocus()
				}
			}
			else if (AppData.last_exception.value != null) {
				showException(navController)
				
				LaunchedEffect(Unit) {
					AppData.focus_req.requestFocus()
				}
			}
			else {
				CustomNavigationHost(navController)
				
				LaunchedEffect(Unit) {
					AppData.focus_req.requestFocus()
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
fun main() = application {
	Window(title = "Kazuma - Automated Class Enroller",
	       onCloseRequest = ::exitApplication) {
		App()
	}
}