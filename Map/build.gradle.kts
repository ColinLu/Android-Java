plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = libs.versions.publish.group.map.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    compileOnly(project(":Utils"))
    compileOnly(project(":AAR"))
    compileOnly(libs.androidx.core.ktx)
    compileOnly(libs.androidx.appcompat)
    compileOnly(libs.material)
//    compileOnly(libs.map.gaode.location)
//    compileOnly(libs.map.gaode.search)
//    compileOnly(libs.map.gaode.navi)
//    compileOnly(libs.map.gaode.map3d)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = libs.versions.publish.group.id.get()
                artifactId = libs.versions.publish.artifact.map.get()
                version = libs.versions.publish.version.map.get()
                from(components["release"])
            }
        }
    }
}