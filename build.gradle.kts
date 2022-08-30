import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val GITLAB_TOKEN: String by project

plugins {
    kotlin("jvm") version "1.6.20"
    id("com.github.johnrengelman.shadow") version("7.1.2")
    id("maven-publish")
    id("java-library")
    id("org.ajoberstar.grgit") version "5.0.0"
}

group = "cz.craftmania.craftlibs"
val VERSION = getVersionNumber()
print("Version: " + getVersionNumber())

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
    maven {
        url = uri("https://gitlab.com/api/v4/groups/craftmania/-/packages/maven")
        name = "Gitlab"
        credentials(HttpHeaderCredentials::class) {
            name = "Private-Token"
            value = GITLAB_TOKEN
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}

dependencies {
    // Basic
    // https://repo.papermc.io/service/rest/repository/browse/maven-public/io/papermc/paper/paper-api/
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-20220703.182221-166")

    // Required
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("io.sentry:sentry:1.7.30")
    implementation("io.sentry:sentry-log4j2:1.7.30")
    implementation("org.jetbrains:annotations:16.0.2")
    implementation("org.slf4j:slf4j-nop:1.7.30")
    compileOnly("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<JavaCompile>{
    options.encoding = "UTF-8"
}

tasks.withType<ShadowJar>{
    archiveBaseName.set("craftlibs")
    archiveClassifier.set("")
    archiveVersion.set(VERSION)

    dependencies {
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
    }
}

tasks.withType<ProcessResources>{
    filesMatching("plugin.yml") {
        expand("version" to VERSION)
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            url = uri("https://gitlab.com/api/v4/projects/37514561/packages/maven")
            credentials(HttpHeaderCredentials::class){
                name = "Private-Token"
                value = GITLAB_TOKEN
            }
            authentication{
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
    publications {
        create<MavenPublication>("maven"){
            version = VERSION
            artifact(tasks["jar"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }
}

fun getVersionNumber(): String {
    val mainBranch = "main" // main | master dle projektu
    val gitVersionName = grgit.describe{ tags }
    val mainVersion = gitVersionName.split("-")[0]
    val minorVersion = gitVersionName.split("-")[1]
    if (!grgit.branch.current.fullName.contains(mainBranch) && !grgit.branch.current.fullName.contains("HEAD")) {
        return mainVersion + "." + minorVersion + "-" + grgit.branch.current.name.replace("/", "-") // 1.2.X-feature-mix
    } else {
        return "$mainVersion.$minorVersion" // 1.1.X
    }
}
