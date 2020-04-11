plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  compileSdkVersion(29)

  defaultConfig {
    applicationId = "com.thuytrinh.leakdemos"
    minSdkVersion(27)
    targetSdkVersion(29)
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  viewBinding { isEnabled = true }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.71")
  implementation("androidx.appcompat:appcompat:1.1.0")
  implementation("androidx.core:core-ktx:1.2.0")
  implementation("androidx.constraintlayout:constraintlayout:1.1.3")
  implementation("com.squareup.leakcanary:leakcanary-android:2.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
  implementation("androidx.fragment:fragment-ktx:1.2.4")
}
