package screens.dialogs

import screens.navigation.NavController

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.material.AlertDialog
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi

@Composable
@OptIn(ExperimentalMaterialApi::class)
internal fun showException(navController: NavController) {
	AlertDialog(onDismissRequest = { }, confirmButton = {
		Button(onClick = { AppData.last_exception.value = null },
		       modifier = Modifier.padding(1.dp)
			       .focusRequester(AppData.focus_req)) {
			Text("OK")
		}
	}, title = {
		AppData.last_exception.value?.message?.let {
			Text(it)
		}
	}, text = {
		Text("${AppData.last_exception.value?.javaClass?.canonicalName}: ${AppData.last_exception.value?.message}")
	}, modifier = Modifier.width(300.dp))
}