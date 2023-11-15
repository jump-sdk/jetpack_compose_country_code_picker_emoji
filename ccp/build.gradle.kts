plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("io.gitlab.arturbosch.detekt") version libs.versions.detekt.get()
    id("org.jetbrains.dokka")
    alias(libs.plugins.paparazzi)
}

kotlin {
    jvmToolchain(17)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.togitech.ccp"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    publishing {
        singleVariant("release") {
            withJavadocJar()
        }
    }
}

dependencies {
    api(libs.kotlinx.immutable)
    api(libs.libphonenumber)
    debugImplementation(libs.compose.tooling)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.compose.activity)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling.preview)

    detektPlugins("ru.kode:detekt-rules-compose:1.3.0")
    detektPlugins("io.nlopez.compose.rules:detekt:0.3.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:${libs.versions.detekt.get()}")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${libs.versions.detekt.get()}")
    detektPlugins("com.braisgabin.detekt:kotlin-compiler-wrapper:0.0.4")
    detektPlugins("com.github.hbmartin:hbmartin-detekt-rules:0.1.3")
}

detekt {
    config.setFrom(project.rootProject.file("detekt.yml"))
    allRules = true
}

afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class.java) {
                from(components.getByName("release"))
                groupId = "com.togisoft"
                artifactId = "jetpack_country_code_picker"
                // Update version in README when changing below
                version = "2.2.5"
            }
        }
    }
}
