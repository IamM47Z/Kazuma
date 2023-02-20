package classes

import java.util.Date

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

open class Subject(val id: Long,
                   val code: Long,
                   val name: String,
                   val semester: Int,
                   val enroll_start: Date?,
                   val enroll_end: Date?) {
	val classes: MutableState<MutableList<Class>> = mutableStateOf(mutableListOf())
	
	fun addClass(c: Class) {
		classes.value.add(c)
	}
}