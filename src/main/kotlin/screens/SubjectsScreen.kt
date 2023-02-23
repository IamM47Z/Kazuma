package screens

import androidx.compose.material.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import screens.navigation.NavController
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
fun SubjectsScreen(navController: NavController) {
	val focus_manager = LocalFocusManager.current
	
	Column(modifier = Modifier.fillMaxSize(),
	       verticalArrangement = Arrangement.Center,
	       horizontalAlignment = Alignment.CenterHorizontally) {
		Text(text = "Welcome ${AppData.platform.value.username.value}!",
		     fontSize = 32.sp)
		
		Spacer(modifier = Modifier.height(16.dp))
		Text(text = "Registration: ${AppData.platform.value.cur_registration.value?.name}",
		     fontSize = 16.sp)
		
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
			AppData.platform.value.cur_registration.value?.subjects?.forEach {
				item {
					Card(modifier = Modifier.padding(8.dp).clickable { }.run {
						if (it.id == AppData.platform.value.cur_registration.value!!.subjects.first().id) this.focusRequester(
								AppData.focus_req)
						else this
					}, onClick = {
						try {
							AppData.platform.value.cur_registration.value!!.setSubject(
									it)
							navController.navigate(Screen.ClassesScreen.name)
						}
						catch (e: Exception) {
							AppData.last_exception.value = e
						}
					}) {
						Column(modifier = Modifier.padding(8.dp)) {
							Text(text = "ID: ${it.id}")
							Text(text = "Name: ${it.name}")
							Text(text = "Code: ${it.code}")
							Text(text = "Semester: ${it.semester}")
							if (it.enroll_start != null) Text(text = "Enroll Start: ${it.enroll_start}")
							if (it.enroll_end != null) Text(text = "Enroll End: ${it.enroll_end}")
						}
					}
				}
			}
		}
		
		Spacer(modifier = Modifier.height(32.dp))
		Row {
			Button(onClick = {
				try {
					navController.navigateBack()
					
					AppData.platform.value.setRegistration(null)
					AppData.platform.value.parse()
				}
				catch (e: Exception) {
					AppData.last_exception.value = e
				}
			}) {
				Text("Back")
			}
		}
	}
}