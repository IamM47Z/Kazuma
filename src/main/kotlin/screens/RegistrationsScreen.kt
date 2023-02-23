package screens

import screens.navigation.NavController

import androidx.compose.material.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
fun RegistrationsScreen(navController: NavController) {
	val focus_manager = LocalFocusManager.current
	
	Column(modifier = Modifier.fillMaxSize(),
	       verticalArrangement = Arrangement.Center,
	       horizontalAlignment = Alignment.CenterHorizontally) {
		Text(text = "Welcome ${AppData.platform.value.username.value}!",
		     fontSize = 32.sp)
		
		Spacer(modifier = Modifier.height(32.dp))
		LazyHorizontalGrid(rows = GridCells.Fixed(2),
		                   verticalArrangement = Arrangement.Center,
		                   horizontalArrangement = Arrangement.Center,
		                   state = rememberLazyGridState(),
		                   modifier = Modifier.onKeyEvent {
			                   if (it.key == Key.Tab) {
				                   if (it.type == KeyEventType.KeyUp) focus_manager.moveFocus(
						                   FocusDirection.Down)
				                   return@onKeyEvent true
			                   }
			                   false
		                   }.height(300.dp).width(740.dp)) {
			AppData.platform.value.registrations.value.forEach {
				item {
					Card(modifier = Modifier.padding(8.dp).clickable { }.run {
						if (it.id == AppData.platform.value.registrations.value.first().id) this.focusRequester(
								AppData.focus_req)
						else this
					}, onClick = {
						try {
							AppData.platform.value.setRegistration(it)
							navController.navigate(Screen.SubjectsScreen.name)
						}
						catch (e: Exception) {
							AppData.last_exception.value = e
						}
					}) {
						Column(modifier = Modifier.padding(8.dp)) {
							Text(text = "ID: ${it.id}")
							Text(text = "Name: ${it.name}")
						}
					}
				}
			}
		}
		
		Spacer(modifier = Modifier.height(32.dp))
		Row {
			Button(onClick = {
				try {
					AppData.platform.value.logout()
					if (!AppData.platform.value.logged_in.value) navController.navigateBack()
				}
				catch (e: Exception) {
					AppData.last_exception.value = e
				}
			}, modifier = Modifier.onKeyEvent {
				if (it.key == Key.Tab) {
					if (it.type == KeyEventType.KeyUp) focus_manager.moveFocus(
							FocusDirection.Next)
					return@onKeyEvent true
				}
				false
			}.run {
				if (AppData.platform.value.registrations.value.isEmpty()) this.focusRequester(
						AppData.focus_req)
				else this
			}) {
				Text("Logout")
			}
		}
	}
}