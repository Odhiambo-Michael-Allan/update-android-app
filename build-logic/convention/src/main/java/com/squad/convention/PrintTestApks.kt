package com.squad.convention

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.build.api.variant.HasAndroidTest
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * configurePrintApksTask Function
 * Purpose:
 * This function configures a custom Gradle task to print the location of the APK generated for
 * Android test variants.
 *
 * Parameters:
 *
 * extension: The AndroidComponentsExtension, which provides access to the Android variants and
 * other components during the build process.
 *
 * Logic:
 *
 * It iterates over the Android variants using extension.onVariants.
 * For variants that have an associated Android test variant (HasAndroidTest), it retrieves the APK
 * artifact, the Java sources, and the Kotlin sources related to the Android test.
 * It then checks if both the APK and the test sources are available.
 * If both are available, it registers a custom task (PrintApkLocationTask) with a name based on the
 * variant ("${variant.name}PrintTestApk"). This task will print the location of the test APK if
 * there are relevant source files.
 *
 * PrintApkLocationTask Class
 *
 * Purpose:
 *
 * This is a custom Gradle task that prints the file path of the generated APK for the Android test
 * variant.
 *
 * Properties:
 *
 * apkFolder: The directory where the APK is located.
 * sources: The source directories (Java and/or Kotlin) for the Android test.
 * builtArtifactsLoader: A loader to retrieve the built artifacts (APK files).
 * variantName: The name of the variant.
 * Task Action (taskAction):
 *
 * It checks if there are any source files in the specified directories that aren't in a generated
 * build directory.
 * If there are source files, it loads the APK using builtArtifactsLoader.
 * If exactly one APK is found, it prints the file path of that APK.
 *
 * Summary
 *
 * This code dynamically registers a Gradle task for each Android test variant in the project.
 * The task checks if there are test source files and, if so, prints the location of the
 * generated test APK. This can be useful for debugging or for custom build processes
 * where knowing the APK location is important.
 */
internal fun Project.configurePrintApksTask( extension: AndroidComponentsExtension<*, *, *> ) {
    extension.onVariants { variant ->
        if ( variant is HasAndroidTest ) {
            val loader = variant.artifacts.getBuiltArtifactsLoader()
            val artifact = variant.androidTest?.artifacts?.get( SingleArtifact.APK )
            val javaSources = variant.androidTest?.sources?.java?.all
            val kotlinSources = variant.androidTest?.sources?.kotlin?.all

            val testSources = if ( javaSources != null && kotlinSources != null ) {
                javaSources.zip( kotlinSources ) { javaDirs, kotlinDirs ->
                    javaDirs + kotlinDirs
                }
            } else javaSources ?: kotlinSources

            if ( artifact != null && testSources != null ) {
                tasks.register(
                    "${variant.name}PrintTestApk",
                    PrintApkLocationTask::class.java,
                ) {
                    apkFolder = artifact
                    builtArtifactsLoader = loader
                    variantName = variant.name
                    sources = testSources
                }
            }
        }
    }
}

@DisableCachingByDefault( because = "Prints output" )
internal abstract class PrintApkLocationTask : DefaultTask() {

    @get:PathSensitive( PathSensitivity.RELATIVE )
    @get:InputDirectory
    abstract val apkFolder: DirectoryProperty

    @get:PathSensitive( PathSensitivity.RELATIVE )
    @get:InputFiles
    abstract val sources: ListProperty<Directory>

    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    @get:Input
    abstract val variantName: Property<String>

    @TaskAction
    fun taskAction() {
        val hasFiles = sources.orNull?.any { directory ->
            directory.asFileTree.files.any {
                it.isFile && "build${File.separator}generated" !in it.parentFile.path
            }
        } ?: throw RuntimeException( "Cannot check androidTest sources" )

        // Don't print APK location if there are no androidTest source files
        if (!hasFiles) return

        val builtArtifacts = builtArtifactsLoader.get().load( apkFolder.get() )
            ?: throw RuntimeException( "Cannot load APKs" )
        if (builtArtifacts.elements.size != 1)
            throw RuntimeException( "Expected one APK !" )
        val apk = File( builtArtifacts.elements.single().outputFile ).toPath()
        println( apk )
    }
}