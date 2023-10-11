pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/") {
            content {
                includeGroup("app.cash.paparazzi")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") {
            content {
                includeGroup("com.github.hbmartin")
            }
        }
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/") {
            content {
                includeGroup("app.cash.paparazzi")
            }
        }
    }
}
rootProject.name = "CountryCodePicker"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(":app")
include(":ccp")
