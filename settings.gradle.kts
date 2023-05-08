pluginManagement {
    repositories {
        gradlePluginPortal() 
	    maven("https://maven.minecraftforge.net/releases/")
    }
}

rootProject.name = "tokimak"

include(":plugin")
include(":plugin:bootstrap")
include(":tokimak-core")
include(":paperweight-plugin")
include(":paperweight-plugin:paperweight-lib")
include(":paperweight-plugin:paperweight-userdev")
include(":compile-extensions")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
