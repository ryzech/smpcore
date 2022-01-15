import org.ajoberstar.grgit.Grgit

buildscript {
    repositories {
        mavenCentral()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
    }
    dependencies {
        classpath("net.fabricmc:fabric-loom:0.11.1")
    }
}
plugins {
    jacoco
}

if (!project.hasProperty("gitCommitHash")) {
    apply(plugin = "org.ajoberstar.grgit")
    ext["gitCommitHash"] = try {
        extensions.getByName<Grgit>("grgit").head()?.abbreviatedId
    } catch (e: Exception) {
        logger.warn("Error getting commit hash", e)

        "no.git.id"
    }
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