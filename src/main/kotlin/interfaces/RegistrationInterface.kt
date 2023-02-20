package interfaces

import classes.Subject

import androidx.compose.runtime.MutableState

interface RegistrationInterface {
	val cur_subject: MutableState<SubjectInterface?>
	val id: Long
	val name: String
	
	fun parse()
	
	fun setSubject(subject: Subject?)
}