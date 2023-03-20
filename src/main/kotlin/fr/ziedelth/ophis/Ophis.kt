package fr.ziedelth.ophis

import fr.ziedelth.ophis.services.DockerService

class Ophis {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val dockerService = DockerService()
            val isInstalled = dockerService.isInstalled()

            if (!isInstalled) {
                println("Docker is not installed or partially installed")
                println("Please install Docker and Docker Compose before using Ophis")
                println("  - Docker: https://docs.docker.com/engine/install/")
                println("  - Docker Compose: https://docs.docker.com/compose/install/")
                return
            }

            val isRunning = dockerService.isRunning()
            println("Docker is ${if (isRunning) "running" else "not running"}")
            println("Docker version: ${dockerService.getVersion().first}")
            println("Docker Compose version: ${dockerService.getVersion().second}")
        }
    }
}