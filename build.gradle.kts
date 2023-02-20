import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	kotlin("jvm") version "1.8.0"
	id("org.jetbrains.compose") version "1.3.0"
}

group = "tk.m47z"
version = "1.0.0"

repositories {
	google()
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
	implementation(compose.desktop.currentOs)
	implementation("it.skrape:skrapeit:1.3.0-alpha.1")
	implementation("org.reflections:reflections:0.10.2")
}

compose.desktop {
	application {
		mainClass = "MainKt"
		jvmArgs.add("-Dapple.awt.application.appearance=system")
		
		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			
			packageName = "Kazuma"
			description = "Compose Automated Class Enroller"
			vendor = "M47Z"
			
			includeAllModules = true
			
			macOS {
				iconFile.set(project.file("src/main/resources/logo.icns"))
			}
			windows {
				iconFile.set(project.file("src/main/resources/logo.ico"))
			}
			linux {
				iconFile.set(project.file("src/main/resources/logo.png"))
			}
		}
	}
}