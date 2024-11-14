import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

object Constants {
    const val INTELLIJ_VERSION = "2024.3"
    const val RUBY_PLUGIN_VERSION = "242.20224.419"
}

group = "io.moku"
version = "243.0.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        System.getenv("IDE_PATH")?.let {
            local(file(it))
        } ?: run {
            rubymine(Constants.INTELLIJ_VERSION)
        }
        plugin("org.jetbrains.plugins.ruby:${Constants.RUBY_PLUGIN_VERSION}")

//        jetbrainsRuntime()
        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild.set("243")
            untilBuild.set(provider { null })
        }

        changeNotes.set("""
            Fixed a deprecation warning
        """.trimIndent())
    }

    signing {
        val env = System.getenv()
        env["CERTIFICATE_CHAIN"]?.let {
            certificateChain.set(it)
        } ?: run {
            certificateChainFile.set(file("signing/chain.crt"))
        }
        env["PRIVATE_KEY"]?.let {
            privateKey.set(it)
        } ?: run {
            privateKeyFile.set(file("signing/private.pem"))
        }
        password.set(
            env["SIGNING_PASSWORD"] ?: file("signing/password.txt").readText()
        )
    }

    publishing {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    pluginVerification {
        ides {
            ide(IntelliJPlatformType.RubyMine, Constants.INTELLIJ_VERSION)
            ide(IntelliJPlatformType.IntellijIdeaUltimate, Constants.INTELLIJ_VERSION)
        }
    }

    autoReload.set(true)
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }

    clean<Delete> {
        delete(layout.buildDirectory)
    }
}
