import java.util.Properties
import java.util.Base64

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { input ->
        localProperties.load(input)
    }
}

android {
    namespace = "com.g57.issuehub"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.g57.issuehub"
        minSdk = 26
        targetSdk = 36
        versionCode = 10
        versionName = "0.1 Beta"

        val sbUrl = localProperties.getProperty("SUPABASE_URL", System.getenv("SUPABASE_URL") ?: "")
        val sbKey = localProperties.getProperty("SUPABASE_PUBLISHABLE_KEY", System.getenv("SUPABASE_PUBLISHABLE_KEY") ?: "")
        buildConfigField("String", "SUPABASE_URL", "\"$sbUrl\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"$sbKey\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

kotlin {
    jvmToolchain(17)
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.10.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation(platform("io.github.jan-tennert.supabase:bom:3.5.0"))
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")
    implementation("io.ktor:ktor-client-android:3.0.3")
}


// Decode the uploaded launcher image into an Android drawable during the build.
val generatedG57IconRes = layout.buildDirectory.dir("generated/g57Icon/res")
val decodeG57Icon by tasks.registering {
    val encodedIcon = layout.projectDirectory.file("src/main/icon/g57_icon.b64")
    val outputIcon = generatedG57IconRes.map { it.file("drawable/g57_uploaded_logo.jpg") }
    inputs.file(encodedIcon)
    outputs.file(outputIcon)
    doLast {
        val target = outputIcon.get().asFile
        target.parentFile.mkdirs()
        target.writeBytes(Base64.getDecoder().decode(encodedIcon.asFile.readText().trim()))
    }
}

android.sourceSets.getByName("main").res.srcDir(generatedG57IconRes)
tasks.configureEach {
    if (name.startsWith("merge") && name.endsWith("Resources")) {
        dependsOn(decodeG57Icon)
    }
}
