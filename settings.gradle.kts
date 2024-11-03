pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
}
rootProject.name = "hive"
include("hive-web")
include("schema-manager")
include("hive-utils")
