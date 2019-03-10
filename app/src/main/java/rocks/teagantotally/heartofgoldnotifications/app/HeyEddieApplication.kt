package rocks.teagantotally.heartofgoldnotifications.app

import android.app.Application
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.DaggerApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.SubComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ActivityJobManager
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientComponent
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import timber.log.Timber

class HeyEddieApplication : Application() {
    companion object {
        lateinit var applicationComponent: ApplicationComponent
        var clientComponent: SubComponent<ClientComponent> = SubComponent.NotInitialized()

        fun setClient(module: ClientModule): ClientComponent =
            applicationComponent
                .clientComponentBuilder()
                .clientModule(module)
                .build()
                .also { clientComponent = SubComponent.Initialized(it) }

        fun getClient(): SubComponent<ClientComponent> =
            clientComponent
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        setupGraph()
    }

    private fun setupGraph() {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
            .let { applicationComponent = it }

        registerActivityLifecycleCallbacks(ActivityJobManager)
    }
}