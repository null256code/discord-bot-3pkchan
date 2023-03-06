import com.diffplug.gradle.spotless.SpotlessExtension
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
    id("com.diffplug.spotless") version "6.15.0"
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
    implementation("com.google.code.gson:gson:2.10")

    implementation("org.postgresql:postgresql:${Version.postgres}")
    // implementation("org.flywaydb:flyway-core:${Version.flyway")

    implementation("com.github.scribejava:scribejava-core:8.3.3")

    jooqGenerator("org.postgresql:postgresql:${Version.postgres}")
    // https://github.com/etiennestuder/gradle-jooq-plugin/issues/209
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
}

tasks {
    build {
        mustRunAfter(clean)
    }

    create("stage") {
        dependsOn(clean, flywayMigrate, build)
    }

    bootJar {
        archiveFileName.set("bot3pkchan.jar")
    }

    bootBuildImage {
        setProperty("builder", "heroku/builder:22")
        environment["BP_NATIVE_IMAGE"] = "true"
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
    cleanDisabled = false
}

// https://github.com/etiennestuder/gradle-jooq-plugin#gradle-kotlin-dsl-4
jooq {
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // default (can be omitted)

    configurations {
        create("main") {  // name of the jOOQ configuration
            generateSchemaSourceOnCompilation.set(true)

            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = Environments.DataSource.url
                    user = Environments.DataSource.username
                    password = Environments.DataSource.password
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
                        excludes = "flyway_schema_history"
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

spotless {
    // ratchetFrom = "origin/main"
    format("misc") {
        target("*.gradle", "*.md", ".gitignore")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
        lineEndings = com.diffplug.spotless.LineEnding.UNIX
    }
    kotlin {
        lineEndings = com.diffplug.spotless.LineEnding.UNIX
        ktlint("0.48.2")
            .setUseExperimental(true)
        // .setEditorConfigPath("$projectDir/config/.editorconfig")
    }
}

// https://github.com/diffplug/spotless/issues/752
// https://devcenter.heroku.com/articles/buildpack-api#stacks
if (System.getenv().containsKey("STACK")) {
    afterEvaluate {
        project.extensions.findByType(SpotlessExtension::class.java)?.ratchetFrom(null)
    }
}