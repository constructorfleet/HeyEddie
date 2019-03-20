package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.os.Handler
import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.IO
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.UI
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.InstanceScope

@InstanceScope
@Subcomponent(
    modules = [ThreadModule::class]
)
interface ThreadComponent {
    @IO
    fun getIOHandler(): Handler

    @UI
    fun getUIHandler(): Handler

    @Background
    fun getBackgroundHandler(): Handler

    @Subcomponent.Builder
    interface Builder {
        fun module(module: ThreadModule): Builder

        fun build(): ThreadComponent
    }
}