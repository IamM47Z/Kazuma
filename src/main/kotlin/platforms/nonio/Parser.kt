package platforms.nonio

import classes.Exceptions
import classes.Registration
import interfaces.ParserInterface

import java.util.*

import androidx.compose.runtime.*

import it.skrape.core.document
import it.skrape.fetcher.Method
import it.skrape.fetcher.skrape
import it.skrape.fetcher.response
import it.skrape.fetcher.HttpFetcher

open class Parser : ParserInterface, SessionManager() {
	override var username = mutableStateOf("")
	override var parse_date: MutableState<Date?> = mutableStateOf(null)
	override var registrations: MutableState<MutableList<Registration>> = mutableStateOf(
			mutableListOf())
	override var cur_registration: MutableState<RegistrationManager?> = mutableStateOf(
			null)
	
	override fun parse() {
		registrations.value.clear()
		parse_date.value = null
		
		skrape(HttpFetcher) {
			request {
				url = "$nonio_path/inscturmas/init.do"
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
				
				val names = document.findFirst("span.hidden-tablet").text.split(
						" |").first().split(" ")
				username.value = "${names.first()} ${names.last()}"
				
				val regs = document.findFirst("table.displaytable")
					.findAll("tbody>tr")
				
				for (reg in regs) {
					val tds = reg.findAll("td")
					registrations.value.add(Registration(id = tds[1].eachHref.first()
						.split("=")
						.last()
						.toLong(), name = tds[0].text))
				}
				
				if (registrations.value.isEmpty()) throw Exceptions.NoRegistrationsException()
				if (registrations.value.size == 1) setRegistration(registrations.value.first())
			}
		}
		parse_date.value = Date()
	}
	
	override fun setRegistration(registration: Registration?) {
		if (registration == null) cur_registration.value = null
		else cur_registration.value = RegistrationManager(registration,
		                                                  session_headers,
		                                                  nonio_path)
		
		cur_registration.value?.parse()
	}
}