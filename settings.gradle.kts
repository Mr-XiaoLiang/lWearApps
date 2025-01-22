pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "LWearApps"
include(":ttt")
include(":basic")
include(":woodenFish")
include(":flappyBird")
include(":barometer")
include(":solar2dBase")
include(":aar:corona")
include(":petSprite")
include(":godotBase")
include(":soundMeter")
include(":fileBrowser")
include(":wifiP2p")
include(":myWear")
include(":wearBasic")
include(":swiftp")
include(":qrView")
include(":fileSender")
include(":qrScan")
include(":renderScript")
include(":ftp4j")
