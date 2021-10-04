plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("org.springframework.boot") version "2.5.5"
}

group = "dev.ksyim.example.r2dbc.sharding"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(platform("com.linecorp.armeria:armeria-bom:1.11.0"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.5.5"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.5.1"))

    implementation("com.linecorp.armeria:armeria-kotlin")
    implementation("com.linecorp.armeria:armeria-spring-boot2-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

    implementation("org.mariadb:r2dbc-mariadb:1.0.1")
}
