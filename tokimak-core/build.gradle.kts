import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `config-kotlin`
    id("com.github.johnrengelman.shadow")
    id("com.gradle.plugin-publish")
}

group = "net.tetratau.tokimak.core"
version = "0.1.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://mvn.tetratau.net/releases")
}

gradlePlugin {
    website.set("https://github.com/TetraTau/tokimak")
    vcsUrl.set("https://github.com/TetraTau/tokimak")
    plugins.create("tokimak-core") {
        id = "net.tetratau.tokimak.core"
        displayName = "tokimak-core"
        description = "A Gradle plugin for automatic building of Toki"
        implementationClass = "net.tetratau.tokimak.core.TokimakCorePlugin"
        tags.set(listOf("toki", "minecraft"))
    }
}

val shadowJar by tasks.existing(ShadowJar::class) {
    archiveClassifier.set(null as String?)

    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "OSGI-INF/**", "*.profile", "module-info.class", "ant_tasks/**")

    mergeServiceFiles()
}

publishing {
    repositories {
        maven("https://mvn.tetratau.net/releases") {
            credentials(PasswordCredentials::class)
            name = "tetratau"
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        withType(MavenPublication::class).configureEach {
            pom {
                pomConfig()
            }
        }
    }
}

fun MavenPom.pomConfig() {
    val repoPath = "TetraTau/tokimak"
    val repoUrl = "https://github.com/$repoPath"

    name.set("tokimak-core")
    description.set("A Gradle plugin for automatic building of Toki")
    url.set(repoUrl)
    inceptionYear.set("2023")

    licenses {
        license {
            name.set("Apache-2.0")
            url.set("$repoUrl/blob/master/LICENSE")
        }
    }

    issueManagement {
        system.set("GitHub")
        url.set("$repoUrl/issues")
    }

    developers {
        developer {
            id.set("Denery")
            name.set("Daniel Dorofeev")
            email.set("dorofeevij@gmail.com")
            url.set("https://github.com/maestro-denery")
        }
    }

    scm {
        url.set(repoUrl)
        connection.set("scm:git:$repoUrl.git")
        developerConnection.set("scm:git:git@github.com:$repoPath.git")
    }
}


dependencies {
    implementation("net.tetratau.toki:toki-installer:0.1.1-SNAPSHOT")
    implementation(libs.httpclient)
    implementation(libs.kotson)

    // ASM for inspection
    implementation(libs.bundles.asm)

    implementation(libs.bundles.hypo)
    implementation(libs.slf4j.jdk14) // slf4j impl for hypo
    implementation(libs.bundles.cadix)

    implementation(libs.lorenzTiny)

    implementation(libs.jbsdiff)

    implementation("net.minecraftforge:DiffPatch:2.0.7:all") {
        isTransitive = false
    }
}

