plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.recipia.aos"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.recipia.aos"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // BuildConfig 필드 추가
        buildConfigField("String", "MEMBER_SERVER_URL", "\"${property("MemberServerUrl")}\"")
        buildConfigField("String", "RECIPE_SERVER_URL", "\"${property("RecipeServerUrl")}\"")
        buildConfigField("String", "CHAT_SERVER_URL", "\"${property("ChatServerUrl")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.1.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Coil for Compose
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")

    // navigation
    val nav_version = "2.7.6"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.compose.foundation:foundation:1.6.0")

    // http 요청
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.12.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // icon, lottie
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("androidx.compose.material:material:1.6.0")
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // image Cropper
    implementation("com.github.CanHub:Android-Image-Cropper:4.0.0")

    // 이미지 드래그 앤 드롭
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

}