dependencies {
    api(project(":kuark-context"))
    api("org.springframework.cloud:spring-cloud-starter-gateway")
    api("org.springframework.cloud:spring-cloud-starter-netflix-hystrix")
    api("org.springframework.boot:spring-boot-starter-data-redis-reactive") // 限流用
    api("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client") // 动态路由用

    testImplementation("com.sun.jersey.contribs:jersey-apache-client4:1.19.1")
    testImplementation(project(":kuark-test:kuark-test-service"))
    testImplementation(project(":kuark-test:kuark-test-server:kuark-test-server-eureka"))
}