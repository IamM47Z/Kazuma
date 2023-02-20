package platforms.nonio

import interfaces.PlatformInterface

class Platform : PlatformInterface, Parser() {
	override fun getName(): String {
		return "Nonio"
	}
}