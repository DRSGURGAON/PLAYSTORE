pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "playstore"

// Shared code for every app.
include(":core")

// One line per app. A new app = new folder under apps/ + one include here.
include(":apps:direct-chat")
