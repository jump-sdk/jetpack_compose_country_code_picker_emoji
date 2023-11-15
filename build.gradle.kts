plugins {
    id("com.android.application") version libs.versions.android.gradle.plugin apply false
    id("com.android.library") version libs.versions.android.gradle.plugin apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin apply false
    id("org.jetbrains.dokka") version libs.versions.dokka
    id("org.sonarqube") version "4.4.1.3373"
    alias(libs.plugins.gradleVersions)
}

sonarqube {
    properties {
        property("sonar.projectKey", "jump-sdk_jetpack_compose_country_code_picker_emoji")
        property("sonar.organization", "jump-sdk")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.kotlin.source.version", libs.versions.kotlin.get())
        property("sonar.pullrequest.github.summary_comment", "true")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        allWarningsAsErrors = true
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    checkForGradleUpdate = true
    rejectVersionIf {
        listOf("2.0.0-Beta1", "2.7.0-beta01", "-alpha", "-dev-").any { word ->
            candidate.version.contains(word)
        }
    }
}
