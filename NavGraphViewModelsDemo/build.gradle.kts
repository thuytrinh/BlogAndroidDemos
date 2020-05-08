plugins {
  id("com.android.application")
  id("kotlin-android")
}

android {
  compileSdkVersion(29)

  defaultConfig {
    minSdkVersion("27")
    targetSdkVersion("29")
    applicationId = "thuytrinh.navgraphviewmodelsdemo"
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
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.71")
  implementation("androidx.activity:activity-ktx:1.2.0-alpha02")
  implementation("androidx.core:core-ktx:1.2.0")
  implementation("androidx.appcompat:appcompat:1.1.0")
  implementation("com.google.android.material:material:1.1.0")
  implementation("androidx.fragment:fragment-ktx:1.2.4")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
  implementation("androidx.navigation:navigation-fragment-ktx:2.3.0-alpha06")
  implementation("androidx.navigation:navigation-ui-ktx:2.3.0-alpha06")
}
