pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

include(":app", ":shared")
rootProject.name = "KaMPKit-ballast"

enableFeaturePreview("VERSION_CATALOGS")
