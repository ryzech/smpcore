plugins {
    `java-library`
    publishing
    id("com.github.johnrengelman.shadow") version "7.1.2"
}
applyPlatformAndCoreConfiguration()

repositories {
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {

    implementation("net.dv8tion:JDA:5.0.0-alpha.9")
    implementation("net.kyori:adventure-text-minimessage:4.10.1")
    implementation("com.zaxxer:HikariCP:5.0.0")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("net.essentialsx:EssentialsX:2.19.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("net.kyori:adventure-platform-bukkit:4.1.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("org.geysermc.floodgate:api:2.0-SNAPSHOT")
    shadow("net.dv8tion:JDA:5.0.0-alpha.9")
}

tasks.named<Copy>("processResources") {
    val internalVersion = project.ext["internalVersion"]
    inputs.property("internalVersion", internalVersion)
    filesMatching("plugin.yml") {
        expand("internalVersion" to internalVersion)
    }
}


    group = "com.ryzech.smpcore"
    description = "SmpCore"
    java.sourceCompatibility = JavaVersion.VERSION_17

tasks {
    shadowJar {
        archiveBaseName.set("smpcore-bukkit-" + project.ext["internalVersion"]!!)
        archiveClassifier.set("")
    }
}