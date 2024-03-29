plugins {
  java
}

group = "container-tdd-java"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("jakarta.inject:jakarta.inject-api:2.0.1.MR")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
