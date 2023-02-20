package classes

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

open class Registration(val id: Long, val name: String) {
	private var _subjects = mutableStateListOf<Subject>()
	val subjects: SnapshotStateList<Subject>
		get() = _subjects
	
	fun addSubject(subject: Subject) {
		_subjects.add(subject)
	}
}