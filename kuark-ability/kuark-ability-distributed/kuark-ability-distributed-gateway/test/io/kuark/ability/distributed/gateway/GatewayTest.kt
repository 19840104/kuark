package io.kuark.ability.distributed.gateway

import io.kuark.base.lang.SystemKit
import io.kuark.base.net.http.HttpClientKit
import io.kuark.test.server.eureka.EurekaServer
import io.kuark.test.service.service1.Service1Node1Application
import io.kuark.test.service.service1.Service1Node2Application
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal open class GatewayTest {

    @BeforeAll
    fun setUp() {
        SystemKit.setEnvVars(
            mapOf(
                "eureka.client.service-url.defaultZone" to "http://localhost:8001/eureka/",
                "spring.cloud.gateway.enabled" to "false"
            )
        )

        EurekaServer.main(arrayOf())
        Service1Node1Application.main(arrayOf())
        Service1Node2Application.main(arrayOf())
        GatewayServerApplication.main(arrayOf())
    }

    @AfterAll
    fun tearDown() {
        Service1Node1Application.exit()
        Service1Node2Application.exit()
        GatewayServerApplication.exit()
        EurekaServer.exit()
    }

    @Test
    fun getTablesByType() {
        val url = "http://localhost:9201/service1/list"
        val httpClientBuilder = HttpClient.newBuilder()
        val httpRequestBuilder = HttpRequest.newBuilder().uri(URI.create(url))
        val response =
            HttpClientKit.request(httpClientBuilder, httpRequestBuilder, HttpResponse.BodyHandlers.ofString())
        val body = response.body()
        println(body)
    }

}