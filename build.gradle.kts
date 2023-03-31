plugins {
    id("com.android.application") version libs.versions.android.gradle.plugin apply false
    id("com.android.library") version libs.versions.android.gradle.plugin apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin apply false
    id("org.jetbrains.dokka") version libs.versions.dokka
    id("org.sonarqube") version "3.5.0.2730"
    alias(libs.plugins.gradleVersions)
}

sonarqube {
    properties {
        property("sonar.projectKey", "jump-sdk_jetpack_compose_country_code_picker_emoji")
        property("sonar.organization", "jump-sdk")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.kotlin.source.version", "1.8.10")
        property("sonar.pullrequest.github.summary_comment", "true")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
