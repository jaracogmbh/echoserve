package de.jaraco

import de.jaraco.config.ConfigurationService
import de.jaraco.exception.NoConfigDirectoryGivenException
import de.jaraco.model.StubConfiguration

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import java.util.logging.Logger

fun main(args: Array<String>) {
    val logger: Logger = Logger.getLogger("main")
    logger.info("Test Parameters: ${args.contentToString()}")

    if (args.isEmpty()) {
        throw IllegalArgumentException("No mode given")
    } else {
        val mode = args[0]
        val configurationService = ConfigurationService.getInstance()

        if (mode == "docker") {
            if (args.size == 2) {
                val configFilename = args[1]
                configurationService.configureCustomWireMock(mode = mode, filename = configFilename)
            } else {
                throw NoConfigDirectoryGivenException("No config directory given!")
            }
        } else if (mode == "local") {
            configurationService.configureCustomWireMock(mode = mode, filename = null)
        } else {
            throw IllegalArgumentException("Mode $mode not supported")
        }

        /**
         * To make it easier to configure the port for the /configure endpoint, we could add a parameter to the
         * configuration file. This way, we could make the port configurable and not hardcoded.
         */
        // handling /configure endpoint
        embeddedServer(Netty, port = 19991) { //todo? set the port in some config?
            install(ContentNegotiation) {
                json()
            }
            routing {
                post("/configure") {
                    val stubConfig = call.receive<StubConfiguration>()
                    logger.info("Received configuration: $stubConfig")
                    configurationService.configureStubFromPayload(stubConfig)
                    call.respond(HttpStatusCode.OK, "Stub configured successfully.")
                }

                post("/reset") {
                    logger.info("Resetting WireMock server")
                    configurationService.resetCustomWireMock()
                    call.respond(HttpStatusCode.OK, "WireMock server reset successfully.")
                }
            }
        }.start(wait = true)
    }
}
