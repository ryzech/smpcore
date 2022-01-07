buildscript {
    repositories {
        mavenCentral()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
    }
    dependencies {
        classpath("net.fabricmc:fabric-loom:0.10.65")
    }
}
plugins {
    jacoco
}

subprojects {
    if (buildscript.sourceFile?.extension?.toLowerCase() == "kts"
            && parent != rootProject) {
        generateSequence(parent) { project -> project.parent.takeIf { it != rootProject } }
                .forEach { evaluationDependsOn(it.path) }
    }
}

logger.lifecycle("""
*******************************************
 You are building SmpCore!
 If you encounter trouble:
 1) Read COMPILING.md if you haven't yet
 2) Try running 'build' in a separate Gradle run
 3) Use gradlew and not gradle
 4) If you still need help, ask on GitHub(Or GitLab!)! https://github.com/ryzech/SmpCore
 Output files will be in [subproject]/build/libs :D
*******************************************
""")