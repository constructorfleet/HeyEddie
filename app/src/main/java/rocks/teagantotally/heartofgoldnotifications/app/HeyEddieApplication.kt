package rocks.teagantotally.heartofgoldnotifications.app

import `in`.co.ophio.secure.core.KeyStoreKeyGenerator
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.provider.Settings
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.DaggerApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ActivityJobManager
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService

class HeyEddieApplication : Application() {
    companion object {
        lateinit var applicationComponent: ApplicationComponent
        private const val PREF_FILE_NAME = "rocks.teagantotally.heartofgoldnotifications.app.pref"
    }

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        setupGraph()
        startForegroundService(Intent(this, MqttService::class.java))
    }

    @SuppressLint("HardwareIds")
    private fun getKeyStoreSeed() =
        Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

    private fun setupGraph() {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(
                this,
                getKeyStoreKey(),
                PREF_FILE_NAME
            ))
            .build()
            .let { applicationComponent = it }

        registerActivityLifecycleCallbacks(ActivityJobManager)
    }

    private fun getKeyStoreKey() =
        KeyStoreKeyGenerator.get(
            this,
            this.packageName
        )
            .loadOrGenerateKeys()
}