plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.example.miniapplib"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            // เปิด obfuscate เพื่อให้คนอื่นอ่าน code ยากขึ้น
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // ปิดไว้สำหรับ debug
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    publishing {
        singleVariant("release") {
            // ถ้าอยากให้ปล่อย sources.jar ด้วยก็ใช้บรรทัดนี้
            // withSourcesJar()
        }
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                // เอา Release variant ของ Android Library มาปล่อย
                from(components["release"])

                // ตรงนี้ไว้ตั้งค่าที่ JitPack จะใช้
                groupId = "com.github.sjitprogrammer"
                artifactId = "miniapplib"
                version = "1.0.0"
            }
        }
    }
}
