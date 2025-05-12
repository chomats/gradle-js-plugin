plugins {
    id("java-library")
    id("application")
    id("groovy")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

defaultTasks("clean", "build")

version = "3.0.0"
group = "com.eriwen"

repositories {
    mavenCentral()
}

tasks.register("createClasspathManifest") {
    val outputDir = sourceSets["test"].output.resourcesDir

    inputs.files(sourceSets["main"].runtimeClasspath)
    outputs.dir(outputDir)

    doLast {
        outputDir?.mkdirs()
        file("$outputDir/plugin-classpath.txt").writeText(
            sourceSets["main"].runtimeClasspath.joinToString("\n")
        )
    }
}

dependencies {
    implementation(gradleApi())
    implementation("com.google.javascript:closure-compiler:v20160208") {
        exclude(module = "junit")
    }
    implementation("io.jdev.html2js:html2js:0.1") {
        exclude(module = "groovy-all")
    }
    testImplementation(gradleTestKit())
    testImplementation(files(tasks.named("createClasspathManifest")))
    testImplementation("org.spockframework:spock-core:2.3-groovy-3.0")  {
        exclude(module = "junit")
        exclude(module = "groovy-all")
    }
    testImplementation("commons-lang:commons-lang:2.6")
}

tasks.named<GroovyCompile>("compileGroovy") {
    options.compilerArgs = listOf("-Xlint:deprecation", "-Xlint:unchecked")
}

tasks.register<Jar>("sourceJar") {
    description = "An archive of the source code for Maven Central"
    archiveClassifier = "sources"
    from(sourceSets["main"].groovy)
}

tasks.register<Jar>("groovydocJar") {
    description = "An archive of the GroovyDocs for Maven Central"
    archiveClassifier = "javadoc"
    from(tasks.named("groovydoc"))
}

artifacts {
    add("archives", tasks["groovydocJar"])
    add("archives", tasks["sourceJar"])
}

publishing {
    publications {
        create<MavenPublication>("jsPlugin") {
            from(components["java"])
            artifact(tasks["groovydocJar"])
            artifact(tasks["sourceJar"])
            pom {
                name = "Gradle JS Plugin"
                description = "A Gradle plugin for working with JS."
                url = "https://github.com/eriwen/gradle-js-plugin"
                packaging = "jar"
                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "eriwen"
                        name = "Eric Wendelin"
                        email = "me@eriwen.com"
                    }
                }
                scm {
                    connection = "scm:https://eriwen@github.com/eriwen/gradle-js-plugin"
                    developerConnection = "scm:git@github.com:eriwen/gradle-js-plugin.git"
                    url = "https://github.com/eriwen/gradle-js-plugin"
                }
            }
        }
    }
}