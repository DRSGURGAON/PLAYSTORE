import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "${Company.ID_PREFIX}.directchat"
    compileSdk = Company.COMPILE_SDK

    defaultConfig {
        // PERMANENT once published on Play. Do not change.
        applicationId = "${Company.ID_PREFIX}.directchat"
        minSdk = Company.MIN_SDK
        targetSdk = Company.TARGET_SDK
        // Raise versionCode by 1 for every Play upload.
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        val ks = Company.uploadKeystoreFile
        if (ks != null) {
            create("upload") {
                storeFile = file(ks)
                storePassword = Company.uploadKeystorePassword
                keyAlias = Company.uploadKeyAlias
                keyPassword = Company.uploadKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            // Without the upload key the release is debug-signed: installable for testing,
            // but Play Console will reject it.
            signingConfig = signingConfigs.findByName("upload") ?: signingConfigs.getByName("debug")
        }
    }

    testOptions {
        // Robolectric: run the real Activity, layouts and resources on the JVM (no emulator needed).
        unitTests.isIncludeAndroidResources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

dependencies {
    implementation(project(":core"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.16")
}
