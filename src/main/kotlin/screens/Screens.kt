package screens

import screens.navigation.composable
import screens.navigation.NavController
import screens.navigation.NavigationHost

import androidx.compose.runtime.Composable

/**
 * Screens
 */
enum class Screen {
	LoginScreen, RegistrationsScreen, SubjectsScreen, ClassesScreen
}

@Composable
fun CustomNavigationHost(navController: NavController) {
	NavigationHost(navController) {
		composable(Screen.LoginScreen.name) {
			LoginScreen(navController)
		}
		
		composable(Screen.RegistrationsScreen.name) {
			RegistrationsScreen(navController)
		}
		
		composable(Screen.SubjectsScreen.name) {
			SubjectsScreen(navController)
		}
		
		composable(Screen.ClassesScreen.name) {
			ClassesScreen(navController)
		}
		
	}.build()
}