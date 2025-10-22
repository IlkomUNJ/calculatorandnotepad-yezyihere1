// File: C:/Users/rajan/Downloads/dongo/scientific-calculator-yezyihere1-main/ScientificCalculator/settings.gradle.kts

pluginManagement {
    repositories {        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://plugins.gradle.org/m2/") // <-- Add this line
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ScientificCalculator"
include(":app")
