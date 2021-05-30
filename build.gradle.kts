import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
    jacoco
}

group = "com.jobsalrt"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.javacrumbs.shedlock:shedlock-spring:4.12.0")
    implementation("net.javacrumbs.shedlock:shedlock-provider-mongo-reactivestreams:4.12.0")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.security:spring-security-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("bouncycastle:bcprov-jdk16:136")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.3.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.3.2")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:2.2.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Jacoco configuration

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.07".toBigDecimal()
            }
        }

        rule {
            limit {
                counter = "BRANCH"
                minimum = "0".toBigDecimal()
            }
        }
    }
}
