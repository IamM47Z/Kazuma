package platforms.nonio

import AppData
import androidx.compose.runtime.MutableState
import classes.Exceptions
import interfaces.LoginInterface

import it.skrape.fetcher.*
import it.skrape.core.document

import java.net.URL
import javax.imageio.ImageIO
import java.util.regex.Pattern
import java.net.HttpURLConnection
import java.io.ByteArrayOutputStream

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

open class LoginManager : LoginInterface {
	private var apache_token: String = ""
	private var jsession_id: String = ""
	
	protected var nonio_path: String = ""
	protected var session_headers: Map<String, String> = mapOf()
	
	override val logged_in: MutableState<Boolean> = mutableStateOf(false)
	override val captcha: MutableState<BitmapPainter?> = mutableStateOf(null)
	
	// regex patterns
	//
	private val email_pattern: Pattern = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}@[a-zA-Z0-9][a-zA-Z0-9-]{0,64}(.[a-zA-Z0-9][a-zA-Z0-9-]{0,25})+")
	private val password_pattern: Pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}\$")
	
	override fun login(domain: String,
	                   email: String,
	                   password: String,
	                   captcha: String) {
		if (logged_in.value) throw Exceptions.AlreadyLoggedInException()
		
		if (!email_pattern.matcher(email)
				.matches()
		) throw Exceptions.InvalidEmailException()
		if (!password_pattern.matcher(password)
				.matches()
		) throw Exceptions.InvalidPasswordException()
		
		nonio_path = "$domain/nonio"
		
		if (jsession_id == "") {
			skrape(HttpFetcher) {
				request {
					url = "$nonio_path/security/login.do"
					method = Method.GET
					timeout = 1000 * 60
					
					userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
				}
				
				response {
					if (responseStatus.message != "OK") throw Exceptions.InvalidStatusException(
							responseStatus.code,
							responseStatus.message)
					
					jsession_id = cookies.find { it.name == "JSESSIONID" }!!.value
				}
			}
		}
		
		skrape(HttpFetcher) {
			request {
				url = "$nonio_path/security/login.do?method=submeter"
				method = Method.POST
				timeout = 1000 * 60
				
				userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
				headers = mapOf("Cookie" to "JSESSIONID=$jsession_id")
				
				body {
					form {
						"tipoCaptcha" to "text"
						"username" to email
						"password" to password
						
						if (captcha.isNotEmpty()) "captcha" to captcha
						if (apache_token.isNotEmpty()) "org.apache.struts.taglib.html.TOKEN" to apache_token
					}
				}
			}
			
			response {
				if (responseStatus.code == 302) {
					logged_in.value = true
					this@LoginManager.captcha.value = null
					return@response
				}
				
				if (responseStatus.message != "OK") throw Exceptions.InvalidStatusException(
						responseStatus.code,
						responseStatus.message)
				
				try {
					document.findFirst("div#div_erros_preenchimento_formulario")
				}
				catch (_: Exception) {                      // no errors
					logged_in.value = true
				}
				
				try {
					document.findFirst("div.captchaTable")
					
					if (captcha.isNotEmpty()) throw Exception(document.findFirst(
							"div#div_erros_preenchimento_formulario").text)
				}
				catch (_: Exception) {                    // no more captcha errors
					this@LoginManager.captcha.value = null
					
					if (!logged_in.value)                         // no captcha but has error
						throw Exception(document.findFirst("div#div_erros_preenchimento_formulario").text)
					
					// successfully logged in
					return@response
				}
				
				// fix possible captcha without error message (unlikely)
				if (logged_in.value) logged_in.value = false
				
				// get apache token for captcha
				if (apache_token.isEmpty()) {
					try {
						apache_token = document.findFirst("input[name=org.apache.struts.taglib.html.TOKEN]").attributes["value"]!!
					}
					catch (_: Exception) {
					}
				}
				
				// get captcha image
				val connection = URL("$nonio_path/simpleCaptchaImg").openConnection() as HttpURLConnection
				connection.setRequestProperty("Cookie",
				                              "JSESSIONID=$jsession_id")
				connection.connect()
				
				val inputStream = connection.inputStream
				val bufferedImage = ImageIO.read(inputStream)
				
				val stream = ByteArrayOutputStream()
				ImageIO.write(bufferedImage, "png", stream)
				val byteArray = stream.toByteArray()
				
				this@LoginManager.captcha.value = BitmapPainter(loadImageBitmap(
						byteArray.inputStream()))
			}
		}
		
		if (logged_in.value) {
			session_headers = mapOf("Cookie" to "JSESSIONID=$jsession_id" + if (apache_token.isNotEmpty()) ";org.apache.struts.taglib.html.TOKEN=$apache_token" else "")
			AppData.platform.value.parse()
		}
	}
	
	override fun logout() {
		if (!logged_in.value) throw Exceptions.NotLoggedInException()
		
		skrape(HttpFetcher) {
			request {
				url = "$nonio_path/security/logout.do"
				method = Method.GET
				timeout = 1000 * 60
				
				userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
				headers = session_headers
				followRedirects = false
			}
			
			response {
				if (responseStatus.code != 302) throw Exceptions.InvalidStatusException(
						responseStatus.code,
						responseStatus.message)
				
				jsession_id = ""
				apache_token = ""
				logged_in.value = false
				session_headers = mapOf()
				
				AppData.email.value = ""
				AppData.password.value = ""
			}
		}
	}
}