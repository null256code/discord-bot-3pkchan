import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging

group = "nl2co"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

plugins {
    id("org.springframework.boot") version Version.springBoot
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version Version.kotlin
    kotlin("plugin.spring") version Version.kotlin

    id("org.flywaydb.flyway") version Version.flyway
    id("nu.studer.jooq") version "7.1.1"

    id("com.palantir.docker") version Version.dockerPlugin
    // id("com.palantir.docker-run") version Version.dockerPlugin

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
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    // -------------------------------------------------------------------
    implementation("com.discord4j:discord4j-core:3.2.3")

    implementation("org.postgresql:postgresql:${Version.postgres}")
    // implementation("org.flywaydb:flyway-core:${Version.flyway")

    jooqGenerator("org.postgresql:postgresql:${Version.postgres}")
    // https://github.com/etiennestuder/gradle-jooq-plugin/issues/209
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
}

tasks {
    bootJar {
        archiveFileName.set("bot3pkchan.jar")
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
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

// https://github.com/etiennestuder/gradle-jooq-plugin#gradle-kotlin-dsl-4
jooq {
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // default (can be omitted)

    configurations {
        create("main") {  // name of the jOOQ configuration
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/postgres"
                    user = "postgres"
                    password = "postgres"
//                    properties.add(Property().apply {
//                        key = "ssl"
//                        value = "true"
//                    })
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        forcedTypes.addAll(listOf(
                            ForcedType().apply {
                                name = "varchar"
                                includeExpression = ".*"
                                includeTypes = "JSONB?"
                            },
                            ForcedType().apply {
                                name = "varchar"
                                includeExpression = ".*"
                                includeTypes = "INET"
                            }
                        ))
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "nu.studer.jooq.spkchan"
                        directory = "build/generated-src/jooq/main"  // default (can be omitted)
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

docker {
    setDockerfile(file("../Dockerfile"))
    // name = "${rootProject.name}:${version}"
    name = rootProject.name
    tasks.bootJar.get().let {
        dependsOn(it)
        files(it.archiveFile.get())
        buildArgs(mapOf("JAR_FILE" to it.archiveFileName.get()))
    }
}

//dockerRun {
//    name = rootProject.name
//    image = rootProject.name
//}