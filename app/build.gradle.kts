group = "nl2co"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

plugins {
    id("org.springframework.boot") version Version.springBoot
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version Version.kotlin
    kotlin("plugin.spring") version Version.kotlin

    id("org.flywaydb.flyway") version Version.flyway
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Initializer
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // -------------------------------------------------------------------
    implementation("com.discord4j:discord4j-core:3.1.3")

    implementation("org.postgresql:postgresql:42.2.23")
    // implementation("org.flywaydb:flyway-core:${Version.flyway")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

flyway {
    driver = "org.postgresql.Driver"
    url = "jdbc:postgresql://localhost:5432/postgres"
    user = "postgres"
    password = "postgres"
    encoding = "UTF-8"
    schemas = arrayOf("public")
//    placeholders = mapOf(
//        "keyABC" to "valueXYZ",
//        "otherplaceholder" to "value123"
//    )
}