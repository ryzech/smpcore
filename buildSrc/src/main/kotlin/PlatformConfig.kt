import org.gradle.api.Project
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import kotlin.collections.set

fun Project.applyPlatformAndCoreConfiguration(javaRelease: Int = 17) {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    ext["internalVersion"] = "$version+${rootProject.ext["gitCommitHash"]}"
}