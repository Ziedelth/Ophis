package fr.ziedelth.ophis.services

private const val DOCKER_VERSION_COMMAND = "docker -v"
private const val DOCKER_COMPOSE_VERSION_COMMAND = "docker compose version"

class DockerService {
    private val runtime = Runtime.getRuntime()
    private var dockerVersion: String? = null
    private var dockerComposeVersion: String? = null

    fun isInstalled(): Boolean {
        return try {
            val dockerVersion = runtime.exec(DOCKER_VERSION_COMMAND)
            val dockerComposeVersion = runtime.exec(DOCKER_COMPOSE_VERSION_COMMAND)

            val isInstalled = dockerVersion.waitFor() == 0 && dockerComposeVersion.waitFor() == 0

            if (isInstalled) {
                this.dockerVersion = dockerVersion.inputStream.bufferedReader().readLine()
                this.dockerComposeVersion = dockerComposeVersion.inputStream.bufferedReader().readLine()
            }

            isInstalled
        } catch (e: Exception) {
            false
        }
    }

    fun isRunning(): Boolean {
        val dockerPs = runtime.exec("docker ps")
        return dockerPs.waitFor() == 0
    }

    fun getVersion(): Pair<String, String> {
        val dockerVersionNumber = this.dockerVersion?.split(" ")?.get(2)
        val dockerComposeVersionNumber = this.dockerComposeVersion?.split(" ")?.get(3)?.substring(1)

        if (dockerVersionNumber == null || dockerComposeVersionNumber == null) {
            throw Exception("Docker or Docker Compose is not installed")
        }

        return dockerVersionNumber to dockerComposeVersionNumber
    }
}