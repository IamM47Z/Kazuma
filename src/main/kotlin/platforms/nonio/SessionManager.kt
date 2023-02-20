package platforms.nonio


import interfaces.SessionInterface

open class SessionManager : SessionInterface, LoginManager() {
	override fun login(captcha: String) {
		return login(AppData.domain.value,
		             AppData.email.value,
		             AppData.password.value,
		             captcha)
	}
}