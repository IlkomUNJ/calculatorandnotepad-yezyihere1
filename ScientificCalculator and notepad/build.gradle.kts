// Sintaks BENAR
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false // Pastikan ini juga menggunakan alias
    alias(libs.plugins.ksp) apply false
}
