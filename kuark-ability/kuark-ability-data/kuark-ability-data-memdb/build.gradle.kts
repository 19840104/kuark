
dependencies {
    api(project(":kuark-context"))
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.apache.commons:commons-pool2")

    testImplementation(project(":kuark-test:kuark-test-common"))
}