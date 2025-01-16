import com.palantir.gradle.gitversion.VersionDetails

plugins {
    id("com.palantir.git-version")
}

val versionDetails: groovy.lang.Closure<VersionDetails> by extra
val gitDetails = versionDetails()

tasks.register("printGitCommitTag") {
    doLast {
        val abbrev = gitDetails.gitHash.substring(0, 7)
        val artifactTag = "${gitDetails.branchName}-$abbrev"
        println(artifactTag)
    }
}
