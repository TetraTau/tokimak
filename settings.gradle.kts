pluginManagement {
    repositories {
        gradlePluginPortal() 
	    maven("https://maven.minecraftforge.net/releases/")
    }
}

rootProject.name = "tokimak"

include(":plugin")
include(":plugin:paperweight-core", ":plugin:paperweight-lib", ":plugin:paperweight-patcher", ":plugin:paperweight-userdev")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
