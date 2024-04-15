plugins {
    `maven-publish`
    signing
}

configure<SigningExtension> {
    isRequired = isReleaseBuild()
    val signingKey: String? = System.getenv("GPG_PRIVATE_KEY")
    val signingKeyPassphrase: String? = System.getenv("GPG_PRIVATE_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
}
