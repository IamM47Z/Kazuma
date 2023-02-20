package platforms.nonio

import classes.Class
import classes.Subject
import classes.Exceptions

import interfaces.SubjectInterface

import it.skrape.core.document
import it.skrape.fetcher.Method
import it.skrape.fetcher.skrape
import it.skrape.fetcher.response
import it.skrape.fetcher.HttpFetcher

import androidx.compose.runtime.mutableStateOf

class SubjectManager(subject: Subject,
                     private val session_headers: Map<String, String>,
                     private val nonio_path: String) : SubjectInterface,
                                                       Subject(subject.id,
                                                               subject.code,
                                                               subject.name,
                                                               subject.semester,
                                                               subject.enroll_start,
                                                               subject.enroll_end) {
	private var classes_process: MutableList<Long> = mutableListOf()
	
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
					               num = tds[0].text.filter { it.isDigit() }
						               .toInt(),
					               type = when (tds[0].text.filter { !it.isDigit() }) {
						               "T" -> Class.Type.THEORIC
						               "P" -> Class.Type.PRACTICAL
						               "TP" -> Class.Type.THEORIC_PRACTICAL
						               else -> throw Exceptions.InvalidClassTypeException(
								               tds[0].text.filter { !it.isDigit() })
					               },
					               vacancies = tds[3].text.toInt(),
					               num_lessons = tds[2].text.toInt(),
					               enrolled = mutableStateOf(if (tds.size > 5) tds[5].findFirst(
							               "input")
						               .attribute("checked") == "checked"
					                                         else false),
					               teacher_name = tds[1].text))
				}
			}
		}
	}
	
	override fun markProcessClass(id: Long, mark: Boolean) {
		if (mark) classes_process += id
		else classes_process -= id
	}
	
	override fun process() {
		if (classes_process.isEmpty()) return
		
		skrape(HttpFetcher) {
			request {
				url = "$nonio_path/inscturmas/inscrever.do?method=submeter"
				method = Method.POST
				timeout = 1000 * 60
				
				userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
				headers = session_headers
				followRedirects = false
				
				body {
					form {
						"inscrever" to classes_process
					}
				}
			}
			
			response {
				if (responseStatus.code != 200) throw Exceptions.InvalidStatusException(
						responseStatus.code,
						responseStatus.message)
				
				classes_process.clear()
			}
		}
	}
}