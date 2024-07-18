plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "io.moku"
version = "241.0.4"

repositories {
    mavenCentral()
}

dependencies {
    // Plugin signing for marketplace upload
    implementation("org.jetbrains:marketplace-zip-signer:0.1.24")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2024.1.4")
    type.set("IU") // Target IDE Platform

    // Require the targetIDE plugin or library. Use the stable version
    // compatible with intellij.version and intellij.type specified above:
    // Dependent on ruby plugin (RubyMine / IntelliJ ultimate with Ruby plugin)
    plugins.set(listOf("org.jetbrains.plugins.ruby:241.18034.62"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("241.*")

        changeNotes.set("""
            - Plugin settings are now project-dependant
        """.trimIndent())
    }

    signPlugin {
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

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    runIde {
        // Absolute path to the installed targetIDE to use as IDE Development
        // Instance (the "Contents" directory is macOS specific):
        // Es. /Users/$USER_NAME/Applications/RubyMine.app/Contents
        System.getenv("IDE_PATH")?.let {
            ideDir.set(file(it))
        }
    }

    clean<Delete> {
        delete(layout.buildDirectory)
    }
}
