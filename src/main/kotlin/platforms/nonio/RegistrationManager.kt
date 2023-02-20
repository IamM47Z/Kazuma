package platforms.nonio

import classes.Subject
import classes.Exceptions
import classes.Registration

import interfaces.SubjectInterface
import interfaces.RegistrationInterface

import it.skrape.core.document
import it.skrape.fetcher.skrape
import it.skrape.fetcher.Method
import it.skrape.fetcher.response
import it.skrape.fetcher.HttpFetcher

import java.text.SimpleDateFormat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class RegistrationManager(registration: Registration,
                          private val session_headers: Map<String, String>,
                          private val nonio_path: String) :
	RegistrationInterface,
	Registration(registration.id, registration.name) {
	private val date_format = SimpleDateFormat("dd-MM-yyyy HH:mm")
	
	override val cur_subject: MutableState<SubjectInterface?> = mutableStateOf(
			null)
	
	override fun parse() {
		skrape(HttpFetcher) {
			request {
				url = "$nonio_path/inscturmas/listaInscricoes.do?args=${id}"
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
				
				val subs = document.findFirst("table.displaytable")
					.findAll("tbody>tr")
				
				for (sub in subs) {
					val tds = sub.findAll("td")
					addSubject(Subject(id = tds[6].eachHref.first()
						.split("=")
						.last()
						.toLong(),
					                   code = tds[0].text.toLong(),
					                   name = tds[1].text,
					                   semester = tds[2].text.first()
						                   .toString()
						                   .toInt(),
					                   enroll_start = if (tds[4].text.isNotBlank()) date_format.parse(
							                   tds[4].text)
					                   else null,
					                   enroll_end = if (tds[5].text.isNotBlank()) date_format.parse(
							                   tds[5].text)
					                   else null))
				}
			}
		}
	}
	
	override fun setSubject(subject: Subject?) {
		
		if (subject == null) cur_subject.value = null
		else {
			cur_subject.value = SubjectManager(subject,
			                                   session_headers,
			                                   nonio_path)
			
			cur_subject.value!!.parse()
		}
	}
}