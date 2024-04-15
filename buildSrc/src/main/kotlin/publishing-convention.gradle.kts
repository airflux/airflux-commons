plugins {
    `maven-publish`
    signing
}

configure<SigningExtension> {
    val signingKey: String? = System.getenv("GPG_PRIVATE_KEY")
    val signingKeyPassphrase: String? = System.getenv("GPG_PRIVATE_PASSWORD")
    setRequired {
        val required = !signingKey.isNullOrBlank()
        println("Task is required: " + required)
    }
    useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
}
