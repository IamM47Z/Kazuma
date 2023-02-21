package interfaces

import classes.Class

import java.util.*

import androidx.compose.runtime.MutableState

interface SubjectInterface {
	val id: Long
	val code: Long
	val name: String
	val semester: Int
	val enroll_start: Date?
	val enroll_end: Date?
	
	val classes: MutableState<MutableList<Class>>
	
	fun parse()
	fun markProcessClass(type: Class.Type, id: Long?)
	fun process()
}