package rocks.teagantotally.heartofgoldnotifications.app

import android.app.Application
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.DaggerApplicationComponent
import timber.log.Timber

class HeartOfGoldNotificationsApplication : Application() {
    companion object {
        lateinit var applicationComponent: ApplicationComponent
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
    }
}