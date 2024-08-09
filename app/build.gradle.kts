plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/Users/Colin/Project/Android/Android-Java/app/app.jks")
            storePassword = "ludapeng31"
            keyAlias = "colinapp"
            keyPassword = "ludapeng31"
        }
    }
    namespace = libs.versions.app.id.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.app.id.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":OkHttp"))
    implementation(project(":Utils"))
    implementation(project(":Widgets"))
    implementation(project(":Base"))
    implementation(project(":Map"))
//    implementation("com.colin.library.android:Utils:0.2.3")
//    implementation("com.colin.library.android:Base:0.2.3")
//    implementation("com.colin.library.android:OkHttp:0.2.3")
//    implementation("com.colin.library.android:Widgets:0.2.3")
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.okhttp)
    implementation(libs.gson)
//    implementation(libs.map.gaode.location)
//    implementation(libs.map.gaode.search)
    implementation(libs.map.gaode.navi)
//    implementation(libs.android.java)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}