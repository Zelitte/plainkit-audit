plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "app.plainkit.audit"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "app.plainkit.audit"
        minSdk = 26
        targetSdk = 37
        versionCode = 9
        versionName = "1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Testovacia verzia sa nainštaluje VEDĽA ostrej (iný názov balíka),
            // takže neprepíše appku z obchodu ani jej dáta.
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            // R8: zmenší a premenuje kód. Play od 10/2026 upozorňuje, že
            // „znejasňovanie" pod 25 % môže znížiť viditeľnosť (termín feb 2027).
            // Tvrdenie „bez INTERNET" sa tým nemení — manifest R8 neupravuje.
            // Zdroják ostáva verejný. Debug build sa neoptimalizuje.
            optimization {
                enable = true
            }
        }
        // Test R8 na telefóne VEDĽA ostrej appky: rovnaká optimalizácia ako release,
        // ale vlastný názov balíka a ladiaci podpis. Ostrú verziu z Play nemožno
        // prepísať vlastným release buildom — Play ju podpisuje svojím kľúčom.
        create("r8test") {
            initWith(getByName("release"))
            applicationIdSuffix = ".r8"
            versionNameSuffix = "-r8"
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.material.icons.core)
}