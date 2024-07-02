import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij.platform") version "2.0.0-beta8"
}

object Constants {
    const val INTELLIJ_VERSION = "242.19533.56"
    const val RUBY_PLUGIN_VERSION = "242.19533.56"
}

group = "io.moku"
version = "242.0.3"

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
            // replace intellijIdeaUltimate(Constants.intellijVersion) with rubymine(Constants.intellijVersion) for when it will work
            intellijIdeaUltimate(Constants.INTELLIJ_VERSION)
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
            sinceBuild.set("242")
        }
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

    verifyPlugin {
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
