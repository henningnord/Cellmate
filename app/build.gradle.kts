import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.testImplementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version ("2.0.0-1.0.21")
    id("com.google.dagger.hilt.android") version("2.56.1")
    kotlin("plugin.serialization") version "1.9.23"
}

android {
    namespace = "no.uio.ifi.in2000.cellmate"
    compileSdk = 35

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.cellmate"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}
hilt {
    enableAggregatingTask = false
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Ktor
    implementation("io.ktor:ktor-client-core:2.3.8")
    implementation("io.ktor:ktor-client-cio:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-gson:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")

    // Dotenv
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")

    //gson
    implementation("com.google.code.gson:gson:2.8.9")

    // Mapbox
    implementation("com.mapbox.search:autofill:2.8.0-rc.1")
    implementation("com.mapbox.search:discover:2.8.0-rc.1")
    implementation("com.mapbox.search:place-autocomplete:2.8.0-rc.1")
    implementation("com.mapbox.search:offline:2.8.0-rc.1")
    implementation("com.mapbox.search:mapbox-search-android:2.8.0-rc.1")
    implementation("com.mapbox.search:mapbox-search-android-ui:2.8.0-rc.1")
    implementation("com.mapbox.maps:android:11.2.0")
    implementation("com.mapbox.search:mapbox-search-android:1.0.0-beta.39")

    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation(libs.vico.compose)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.halilibo.compose-richtext:richtext-commonmark:0.17.0")

    //Room
    val room_version = "2.7.0"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-rxjava2:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")
    implementation("androidx.room:room-paging:$room_version")


    // splash screen
    implementation(libs.androidx.core.splashscreen)

    //Hilt
    implementation("com.google.dagger:hilt-android:2.56.1")
    ksp("com.google.dagger:hilt-compiler:2.56.1")
    // Coroutines testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    //Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation(kotlin("test"))
}