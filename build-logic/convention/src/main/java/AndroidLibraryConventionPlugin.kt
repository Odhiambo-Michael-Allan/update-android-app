import com.android.build.api.dsl.LibraryExtension
import com.squad.update.convention.configureFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply( target: Project ) {
        with( target ) {
            extensions.configure<LibraryExtension> {
                configureFlavors( this )
            }
        }
    }
}