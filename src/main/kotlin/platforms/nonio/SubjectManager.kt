package platforms.nonio

import classes.Class
import classes.Subject
import classes.Exceptions

import interfaces.SubjectInterface

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

import it.skrape.core.document
import it.skrape.fetcher.skrape
import it.skrape.fetcher.Method
import it.skrape.fetcher.response
import it.skrape.fetcher.HttpFetcher

import androidx.compose.runtime.mutableStateOf

import org.apache.commons.httpclient.HttpStatus

class SubjectManager(subject: Subject,
                     private val session_headers: Map<String, String>,
                     private val nonio_path: String) : SubjectInterface,
                                                       Subject(subject.id,
                                                               subject.code,
                                                               subject.name,
                                                               subject.semester,
                                                               subject.enroll_start,
                                                               subject.enroll_end) {
	private var classes_process: MutableMap<Class.Type, Long?> = mutableMapOf(
			Class.Type.THEORIC to classes.value.find { it.type == Class.Type.THEORIC && it.enrolled.value }?.id,
			Class.Type.PRACTICAL to classes.value.find { it.type == Class.Type.PRACTICAL && it.enrolled.value }?.id,
			Class.Type.THEORIC_PRACTICAL to classes.value.find { it.type == Class.Type.THEORIC_PRACTICAL && it.enrolled.value }?.id)
	
	
	override fun parse() {
		skrape(HttpFetcher) {
			request {
				url = "$nonio_path/inscturmas/inscrever.do?args=${id}"
				method = Method.GET
				timeout = 1000 * 60
				
				userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
				headers = session_headers
				followRedirects = false
			}
			
			response {
				if (responseStatus.code != 200) throw Exceptions.InvalidStatusException(
						responseStatus.code,
						responseStatus.message)
				
				val class_types = document.findAll("table.displaytable")
				
				for (classes in class_types) for (c in classes.findAll("tbody>tr")) {
					val tds = c.findAll("td")
					
					addClass(Class(id = tds[4].findFirst("input")
						.attribute("value")
						.toLong(),
					               num = tds[0].text.run {
						               val pos_first_digit_begin_index = this.indexOfFirst { it.isDigit() }
						               val pos_first_digit_begin = this.substring(
								               pos_first_digit_begin_index)
						
						               val pos_first_digit_end_index = pos_first_digit_begin.indexOfFirst { !it.isDigit() }
						               if (pos_first_digit_end_index == -1) return@run pos_first_digit_begin.toInt()
						
						               pos_first_digit_begin.substring(0,
						                                               pos_first_digit_end_index)
							               .toInt()
					               },
					               type = tds[4].findFirst("input")
						               .attribute("alt")
						               .run {
							               when (this) {
								               "T" -> Class.Type.THEORIC
								               "P" -> Class.Type.PRACTICAL
								               "TP" -> Class.Type.THEORIC_PRACTICAL
								               else -> throw Exceptions.InvalidClassTypeException(
										               this)
							               }
						               },
					               vacancies = tds[3].text.toInt(),
					               num_lessons = tds[2].text.toInt(),
					               enrolled = mutableStateOf(tds.run {
						               try {
							               this[5].findFirst("input")
								               .attribute("checked") == "checked"
						               }
						               catch (e: Exception) {
							               false
						               }
					               }),
					               teacher_name = tds[1].text,
					               _can_enroll = try {
						               tds[5].findFirst("input")
							               .attribute("disabled") != "disabled"
					               }
					               catch (e: Exception) {
						               false
					               }).apply {
						if (this.enrolled.value) AppData.platform.value.cur_registration.value!!.cur_subject.value!!.markProcessClass(
								this.type,
								this.id)
					})
				}
			}
		}
	}
	
	override fun markProcessClass(type: Class.Type, id: Long?) {
		if (classes_process[type] != null) classes.value.find { it.id == classes_process[type] }?.enrolled?.value = false
		classes_process[type] = id
	}
	
	override fun process() {
		if (classes_process.isEmpty()) return
		
		val client = HttpClient.newBuilder()
			.followRedirects(HttpClient.Redirect.ALWAYS)
			.build()
		val request_builder = HttpRequest.newBuilder()
			.uri(URI.create("$nonio_path/inscturmas/inscrever.do?method=submeter"))
		
		session_headers.forEach { request_builder.header(it.key, it.value) }
		
		request_builder.header("User-Agent",
		                       "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
		request_builder.header("Content-Type",
		                       "application/x-www-form-urlencoded")
		request_builder.header("Referer",
		                       "$nonio_path/inscturmas/inscrever.do?args=${id}")
		
		val form_data = classes_process.filter { it.value != null }
			.map { "inscrever=${it.value}" }
			.joinToString("&")
		
		request_builder.POST(HttpRequest.BodyPublishers.ofString(form_data))
		
		val request = request_builder.build()
		
		client.send(request, HttpResponse.BodyHandlers.ofString()).apply {
			if (this.statusCode() != 200) throw Exceptions.InvalidStatusException(
					this.statusCode(),
					HttpStatus.getStatusText(this.statusCode()))
			
			classes_process.clear()
		}
	}
}