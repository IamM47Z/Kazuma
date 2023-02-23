package screens

import screens.navigation.NavController

import androidx.compose.material.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.*
import androidx.compose.ui.graphics.Color
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
@OptIn(ExperimentalComposeUiApi::class)
fun ClassesScreen(navController: NavController) {
	val focus_manager = LocalFocusManager.current
	
	Column(modifier = Modifier.fillMaxSize(),
	       verticalArrangement = Arrangement.Center,
	       horizontalAlignment = Alignment.CenterHorizontally) {
		Text(text = "Welcome ${AppData.platform.value.username.value}!",
		     fontSize = 32.sp)
		
		
		Spacer(modifier = Modifier.height(16.dp))
		Text(text = "Subject: ${AppData.platform.value.cur_registration.value?.cur_subject?.value?.name}",
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
		                   }.height(330.dp).width(740.dp)) {
			AppData.platform.value.cur_registration.value!!.cur_subject.value?.classes?.value?.forEach {
				item {
					Card(modifier = Modifier.padding(8.dp)
						.clickable(enabled = it.can_enroll) {
							try {
								it.enrolled.value = !it.enrolled.value
								AppData.platform.value.cur_registration.value!!.cur_subject.value!!.markProcessClass(
										it.type,
										it.run { if (it.enrolled.value) it.id else null })
							}
							catch (e: Exception) {
								AppData.last_exception.value = e
							}
						}
						.run {
							if (it.id == AppData.platform.value.cur_registration.value!!.subjects.first().id) this.focusRequester(
									AppData.focus_req)
							else this
						},
					     backgroundColor = if (it.enrolled.value) Color(0,
					                                                    255,
					                                                    0,
					                                                    64)
					     else Color(255, 0, 0, 64)) {
						Column(modifier = Modifier.padding(8.dp)) {
							Text(text = "ID: ${it.id}")
							Text(text = "Name: ${it.type} ${it.num}")
							Text(text = "Teacher Name: ${it.teacher_name}")
							Text(text = "Num Lessons: ${it.num_lessons}")
							Text(text = "Num Vacancies: ${it.vacancies}")
							Text(text = "Is Enrolled: ${it.enrolled.value}")
							Text(text = "Can Enroll: ${it.can_enroll}")
						}
					}
				}
			}
		}
		
		Spacer(modifier = Modifier.height(32.dp))
		Row {
			Button(onClick = {
				try {
					AppData.platform.value.cur_registration.value!!.cur_subject.value!!.process()
					AppData.platform.value.cur_registration.value!!.setSubject(
							null)
					navController.navigateBack()
				}
				catch (e: Exception) {
					AppData.last_exception.value = e
				}
			}) {
				Text("Apply")
			}
			Spacer(modifier = Modifier.width(400.dp))
			Button(onClick = {
				try {
					AppData.platform.value.cur_registration.value!!.setSubject(
							null)
					navController.navigateBack()
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