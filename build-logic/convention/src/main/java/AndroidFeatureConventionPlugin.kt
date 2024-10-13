import com.squad.update.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply( target: Project ) {
        with( target ) {
            pluginManager.apply {
                apply( "update.android.library" )
                apply( "update.hilt" )
//                apply( "org.jetbrains.kotlin.plugin.serialization" )
            }

            dependencies {
                add( "implementation", project( ":core:ui" ) )
                add( "implementation", project( ":core:designsystem" ) )

                add( "implementation", libs.findLibrary( "androidx.hilt.navigation.compose" ).get() )
                add( "implementation", libs.findLibrary( "androidx.navigation.compose" ).get() )

//                add( "implementation", libs.findLibrary( "kotlinx.serialization.json" ).get() )
            }
        }
    }
}