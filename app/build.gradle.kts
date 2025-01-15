plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.lapostreria"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lapostreria"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.android.gif.drawable)
    /*implementation ("com.android.support:design:28.0.0")*/
    //Material
    implementation ("androidx.compose.material3:material3:1.2.1")
    implementation ("androidx.compose.material:material:1.6.4")
    implementation ("androidx.compose.material:material-icons-extended:1.6.4")
    //Imagen Circular para Perfil
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    //CountryCodePicker Seleccion de LADA de Paises.
    implementation ("com.hbb20:ccp:2.5.0")
    //Slider de Imagenes
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    //TextoJustificado
    implementation ("com.codesgood:justifiedtextview:1.1.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.play.services.auth)
    

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}