import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.maven.MavenPom


public fun Project.isReleaseBuild(): Boolean =
    !this.version.toString().endsWith("SNAPSHOT", true)

object Configuration {

    object JVM {
        val targetVersion: String
            get() = System.getenv("JAVA_TARGET_VERSION") ?: "1.8"

        val compatibility: JavaVersion
            get() = JavaVersion.toVersion(targetVersion)
    }

    object Artifact {
        val jdk: String
            get() = "-jdk" + JVM.compatibility.majorVersion
    }

    object Publishing {

        val mavenCentralMetadata: MavenPom.() -> Unit = {
            name.set("Airflux")
            description.set("The commons library.")
            url.set("https://airflux.github.io/airflux/")
            licenses {
                license {
                    name.set("Apache License Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }

            developers {
                developer {
                    id.set("smblt")
                    name.set("Maxim Sambulat")
                    email.set("airflux.github.io@gmail.com")
                    organization.set("airflux")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/airflux/airflux-commons.git")
                developerConnection.set("scm:git:ssh://github.com:airflux/airflux-commons.git")
                url.set("https://github.com/airflux/airflux-commons/tree/main")
            }
        }

        fun RepositoryHandler.mavenSonatypeRepository(project: Project) {
            maven {
                name = "mvn-repository"
                url = if (project.isReleaseBuild())
                    project.uri(System.getenv("REPOSITORY_RELEASES_URL") ?: DEFAULT_REPOSITORY_URL)
                else
                    project.uri(System.getenv("REPOSITORY_SNAPSHOTS_URL") ?: DEFAULT_REPOSITORY_URL)

                credentials {
                    username = System.getenv("REPOSITORY_USERNAME")
                    password = System.getenv("REPOSITORY_PASSWORD")
                }
            }
        }

        private const val DEFAULT_REPOSITORY_URL = ""
    }
}
