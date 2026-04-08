plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.13.1"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(26)
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

sourceSets {
    main {
        java.srcDirs("tmc-plugin-intellij/src/main/java")
        resources.srcDirs("tmc-plugin-intellij/resources")
    }
    test {
        java.srcDirs("tmc-plugin-intellij/src/test/java")
    }
}

dependencies {
    intellijPlatform {
        intellijIdea(providers.gradleProperty("platformVersion"))
        bundledPlugin("com.intellij.java")
    }

    // tmc-core and all transitive dependencies are vendored under libs/
    // because maven.testmycode.net is no longer reachable.
    implementation(fileTree("libs") { include("*.jar") })

    // Apache Ant is required by fi.helsinki.cs.tmc.langs.util.ProjectType
    // (BuildException referenced from <clinit>) but is not vendored under libs/.
    implementation("org.apache.ant:ant:1.10.14")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.14.0")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = provider { null }
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.release = 21
        options.encoding = "UTF-8"
    }
}
