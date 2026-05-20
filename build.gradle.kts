allprojects {
    // IntelliJ/Gradle sync can incorrectly request this Kotlin DSL model task
    // from Java-only projects or subprojects. Register a no-op task so sync
    // can proceed without treating the module as a standalone Kotlin DSL build.
    if (tasks.findByName("prepareKotlinBuildScriptModel") == null) {
        tasks.register("prepareKotlinBuildScriptModel")
    }
}
