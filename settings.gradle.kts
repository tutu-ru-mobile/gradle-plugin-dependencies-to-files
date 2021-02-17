
rootProject.name = "ru.tutu.dependencies2files"
includeBuild("include_build") {
    dependencySubstitution {
        substitute(module("ru.tutu:plugin-dependencies-to-disk:SNAPSHOT")).with(project(":plugin-dependencies-to-disk"))
    }
}
pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "ru.tutu.dependencies2files" -> useModule("ru.tutu:plugin-dependencies-to-disk:SNAPSHOT")
            }
        }
    }
}

include("sample-kts")
include("sample-groovy")
