package interfaces

import classes.Subject

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList

interface RegistrationInterface {
	val cur_subject: MutableState<SubjectInterface?>
	val subjects: SnapshotStateList<Subject>
	val id: Long
	val name: String
	
	fun parse()
	
	fun setSubject(subject: Subject?)
}