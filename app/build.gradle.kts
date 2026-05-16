
plugins {
    id("com.android.application")
}

android {
    namespace = "com.start.craftbox"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.start.craftbox"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
        aidl = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.zip4j)
    implementation(libs.gson)
    implementation(libs.dexlib2)
    implementation(libs.apksig)
    implementation(libs.eventbus)
    implementation(libs.preference)
    implementation(libs.activity)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.editor.bom))
    implementation(libs.editor)
    implementation(libs.language.textmate)
    implementation(libs.language.treesitter)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.api)
    implementation(libs.provider)
    implementation(libs.lifecycle.extensions)
    implementation(libs.core)
    implementation(libs.image)
    implementation(libs.ext.tables)
    implementation(libs.ext.strikethrough)
    implementation(libs.ext.tasklist)
    implementation(libs.syntax.highlight)
    implementation(libs.okhttp)
    implementation(libs.glide)
}

configurations.all {
    exclude(group = "org.jetbrains", module = "annotations-java5")
}