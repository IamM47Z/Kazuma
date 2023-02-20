package interfaces

import classes.Registration

import platforms.nonio.RegistrationManager

import java.util.*

import androidx.compose.runtime.MutableState

interface ParserInterface : SessionInterface {
	val username: MutableState<String>
	val parse_date: MutableState<Date?>
	val cur_registration: MutableState<RegistrationManager?>
	val registrations: MutableState<MutableList<Registration>>
	
	fun parse()
	
	fun setRegistration(registration: Registration?)
}