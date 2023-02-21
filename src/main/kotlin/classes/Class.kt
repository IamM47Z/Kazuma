package classes

import androidx.compose.runtime.MutableState

class Class(val id: Long,
            val num: Int,
            val type: Type,
            val vacancies: Int,
            val num_lessons: Int,
            val enrolled: MutableState<Boolean>,
            val teacher_name: String,
            private val _can_enroll: Boolean) {
	val can_enroll: Boolean
		get() = _can_enroll && vacancies != 0
	
	enum class Type {
		THEORIC, PRACTICAL, THEORIC_PRACTICAL
	}
}