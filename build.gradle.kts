plugins {
    // `java-library`
    id("net.minecraftforge.gitpatcher") version "0.10.+"
}

group = "net.tetratau.tokimak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

patches {
    submodule = "paperweight"
    target = file("plugin")
    patches = file("patches")
}
