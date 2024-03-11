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
}

tasks.withType<Test> {
  useJUnitPlatform()
}
