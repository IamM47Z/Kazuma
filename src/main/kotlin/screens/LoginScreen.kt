package screens

import interfaces.PlatformInterface

import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.text.input.PasswordVisualTransformation
import screens.navigation.NavController

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun LoginScreen(navController: NavController) {
	Column(modifier = Modifier.fillMaxSize().onKeyEvent {
		if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
			try {
				AppData.platform.value.login()
				if (AppData.platform.value.logged_in.value) navController.navigate(
						Screen.RegistrationsScreen.name)
			}
			catch (e: Exception) {
				AppData.last_exception.value = e
			}
			return@onKeyEvent true
		}
		false
	},
	       verticalArrangement = Arrangement.Center,
	       horizontalAlignment = Alignment.CenterHorizontally) {
		Text(text = "Login Form")
		Spacer(modifier = Modifier.height(32.dp))
		
		var expanded: Boolean by remember { mutableStateOf(false) }
		Box {
			val local_density = LocalDensity.current
			var size: Dp by remember { mutableStateOf(0.dp) }
			OutlinedTextField(value = AppData.platform.value.getName(),
			                  onValueChange = {},
			                  label = { Text(text = "Platform") },
			                  trailingIcon = {
				                  Icon(Icons.Filled.ArrowDropDown,
				                       contentDescription = null,
				                       modifier = Modifier.clickable { expanded = true })
			                  },
			                  readOnly = true,
			                  modifier = Modifier.onGloballyPositioned {
				                  size = with(local_density) {
					                  it.size.width.toDp()
				                  }
			                  })
			DropdownMenu(expanded = expanded,
			             onDismissRequest = { expanded = false },
			             modifier = Modifier.width(size)) {
				for (platform_package in AppData.platform_packages) {
					val name = platform_package.packageName.substringAfterLast(".")
						.replaceFirstChar { it.uppercaseChar() }
					DropdownMenuItem(onClick = {
						expanded = false
						
						if (name != AppData.platform.value.getName()) AppData.platform.value = platform_package.getConstructor()
							.newInstance() as PlatformInterface
					}) {
						Text(text = name)
					}
				}
			}
		}
		
		Spacer(modifier = Modifier.height(8.dp))
		OutlinedTextField(value = AppData.domain.value, onValueChange = {
			AppData.domain.value = it
		}, label = { Text("Domain") }, maxLines = 1)
		
		Spacer(modifier = Modifier.height(8.dp))
		OutlinedTextField(value = AppData.email.value,
		                  onValueChange = {
			                  AppData.email.value = it
		                  },
		                  label = { Text("E-Mail") },
		                  maxLines = 1,
		                  modifier = Modifier.focusRequester(AppData.focus_req))
		
		Spacer(modifier = Modifier.height(8.dp))
		OutlinedTextField(value = AppData.password.value,
		                  onValueChange = {
			                  AppData.password.value = it
		                  },
		                  label = { Text("Password") },
		                  visualTransformation = PasswordVisualTransformation(),
		                  maxLines = 1)
		
		Spacer(modifier = Modifier.height(32.dp))
		Button(onClick = {
			try {
				AppData.platform.value.login()
				if (AppData.platform.value.logged_in.value) navController.navigate(
						Screen.RegistrationsScreen.name)
			}
			catch (e: Exception) {
				AppData.last_exception.value = e
			}
		}) {
			Text("Login")
		}
	}
}