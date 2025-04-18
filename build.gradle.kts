// Top-level build file where you can add configuration options common to all sub-projects/modules. (ROOT FILE!)
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id ("com.android.library") version "8.7.3" apply false

    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
    id ("com.google.dagger.hilt.android") version "2.44" apply false
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1"
    id("com.google.gms.google-services") version "4.3.15" apply false

}