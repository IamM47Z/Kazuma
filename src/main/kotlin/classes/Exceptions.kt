package classes

class Exceptions {
	class InvalidEmailException : RuntimeException("Invalid email")
	class InvalidPasswordException : RuntimeException("Invalid password")
	class InvalidStatusException(code: Int, message: String) : RuntimeException(
			"Invalid status code: $code ($message)")
	
	class AlreadyLoggedInException : RuntimeException("Already logged in")
	class NotLoggedInException : RuntimeException("Not logged in")
	class NoRegistrationsException : RuntimeException("No registrations")
	class InvalidClassTypeException(str: String) : RuntimeException("Invalid class type: $str")
}