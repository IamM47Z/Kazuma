package screens.dialogs

import screens.Screen
import screens.navigation.NavController

import androidx.compose.ui.unit.dp
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.painter.Painter

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
internal fun showCaptcha(navController: NavController) {
	val captcha: Painter = AppData.platform.value.captcha.value!!
	
	var captcha_input: String by mutableStateOf("")
	AlertDialog(onDismissRequest = { }, confirmButton = {
		OutlinedTextField(value = captcha_input,
		                  onValueChange = { captcha_input = it },
		                  label = { Text("Captcha") },
		                  modifier = Modifier.width(300.dp)
			                  .focusRequester(AppData.focus_req),
		                  maxLines = 1)
		Button(onClick = {
			try {
				AppData.platform.value.login(captcha_input)
				if (AppData.platform.value.logged_in.value) navController.navigate(
						Screen.RegistrationsScreen.name)
			}
			catch (e: Exception) {
				AppData.last_exception.value = e
			}
		}, modifier = Modifier.padding(1.dp)) { Text("OK") }
	}, title = { Text("Captcha") }, text = {
		Column(verticalArrangement = Arrangement.Center,
		       horizontalAlignment = Alignment.CenterHorizontally) {
			Text("Please enter the captcha below:")
			Spacer(modifier = Modifier.height(32.dp))
			Image(painter = captcha,
			      contentDescription = "Captcha",
			      modifier = Modifier.width(300.dp).height(100.dp))
		}
	}, modifier = Modifier.onKeyEvent {
		if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
			try {
				AppData.platform.value.login(captcha_input)
				if (AppData.platform.value.logged_in.value) navController.navigate(
						Screen.RegistrationsScreen.name)
			}
			catch (e: Exception) {
				AppData.last_exception.value = e
			}
			return@onKeyEvent true
		}
		false
	})
}