pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        val frcYear = "2022"
        val homeRoot = if (System.getProperty("os.name").contains("windows")) {
            val publicFolder = System.getenv("PUBLIC") ?: """C:\Users\Public"""
            File(publicFolder, "wpilib")
        } else {
            val userFolder = System.getProperty("user.home")
            File(userFolder, "wpilib")
        }
        val frcHome = File(homeRoot, frcYear)
        maven {
            name = "frcHome"
            url = frcHome.toURI()
        }
    }
}
