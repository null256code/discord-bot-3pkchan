import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging

group = "nl2co"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    id("org.springframework.boot") version Version.springBoot
    id("io.spring.dependency-management") version "1.1.0"
    id("org.graalvm.buildtools.native") version "0.9.19"
    kotlin("jvm") version Version.kotlin
    kotlin("plugin.spring") version Version.kotlin

    id("org.flywaydb.flyway") version Version.flyway
    id("nu.studer.jooq") version "8.1"
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
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    // -------------------------------------------------------------------
    implementation("com.discord4j:discord4j-core:3.2.3")
    implementation("com.github.kittinunf.fuel:fuel:${Version.fuel}")
    implementation("com.github.kittinunf.fuel:fuel-gson:${Version.fuel}")
    implementation("com.google.code.gson:gson:2.10")

    implementation("org.postgresql:postgresql:${Version.postgres}")
    // implementation("org.flywaydb:flyway-core:${Version.flyway")

    jooqGenerator("org.postgresql:postgresql:${Version.postgres}")
    // https://github.com/etiennestuder/gradle-jooq-plugin/issues/209
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
}
tasks {
    build {
        mustRunAfter(clean)
    }

    create("stage") {
        dependsOn(build, clean)
    }

    bootJar {
        archiveFileName.set("bot3pkchan.jar")
    }

    bootBuildImage {
        setProperty("builder", "heroku/builder:22")
        environment.put("BP_NATIVE_IMAGE", "true")
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
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
            // ビルド時にjOOQの自動生成を行わない。local以外ではjOOQの生成ができる設定にしていないため
            generateSchemaSourceOnCompilation.set(false)

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
