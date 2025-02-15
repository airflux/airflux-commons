import com.palantir.gradle.gitversion.VersionDetails

plugins {
    id("com.palantir.git-version")
}

val versionDetails: groovy.lang.Closure<VersionDetails> by extra
val gitDetails = versionDetails()

tasks.register("printGitCommitTag") {
    doLast {
        val hash = gitDetails.gitHash
        println(">> HASH $hash")

        val abbrev = hash.substring(0, 7)
        println(">> ABBREV $abbrev")

        val artifactTag = "${gitDetails.branchName}-$abbrev"
        println(artifactTag)
    }
}
