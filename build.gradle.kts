plugins {
    java
    id("io.spring.dependency-management") version "1.1.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencyManagement {
    dependencies {
        dependencySet("com.github.seratch:1.8.0") {
            entry("notion-sdk-jvm-core")
            entry("notion-sdk-jvm-httpclient")
            entry("notion-sdk-jvm-slf4j2")
        }
    }
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${property("springBootVersion")}")
    }
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.3")
    }
}

dependencies {
    implementation("com.github.seratch:notion-sdk-jvm-core")
    implementation("com.github.seratch:notion-sdk-jvm-httpclient")
    implementation("com.github.seratch:notion-sdk-jvm-slf4j2")
    implementation("org.springframework:spring-beans")
    implementation("org.springframework.batch:spring-batch-infrastructure")
    testImplementation("com.h2database:h2")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.0.1")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.boot:spring-boot-starter-batch")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
}

tasks.withType<Test> {
    testLogging.showStandardStreams = true
    useJUnitPlatform()
}
