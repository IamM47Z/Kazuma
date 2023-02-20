package classes

import androidx.compose.runtime.MutableState

class Class(val id: Long,
            val num: Int,
            val type: Type,
            val vacancies: Int,
            val num_lessons: Int,
            val enrolled: MutableState<Boolean>,
            val teacher_name: String) {
	enum class Type {
		THEORIC, PRACTICAL, THEORIC_PRACTICAL
	}
	
	fun isFull(): Boolean {
		return vacancies == 0
	}
}