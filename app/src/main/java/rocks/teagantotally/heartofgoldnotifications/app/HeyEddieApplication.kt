package rocks.teagantotally.heartofgoldnotifications.app

//import android.support.multidex.MultiDex
//import android.support.multidex.MultiDexApplication
import `in`.co.ophio.secure.core.KeyStoreKeyGenerator
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.DaggerApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ActivityJobManager
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class HeyEddieApplication : Application() {
    companion object {
        @ObsoleteCoroutinesApi
        lateinit var applicationComponent: ApplicationComponent
        private const val PREF_FILE_NAME = "rocks.teagantotally.heartofgoldnotifications.app.pref"
    }

//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(base)
//        MultiDex.install(this);
//    }
//

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        setupGraph()
        startForegroundService(Intent(this, MqttService::class.java))
    }

    private fun setupGraph() {
        DaggerApplicationComponent.builder()
            .applicationModule(
                ApplicationModule(
                    this,
                    getKeyStoreKey(),
                    PREF_FILE_NAME
                )
            )
            .build()
            .also { applicationComponent = it }

        registerActivityLifecycleCallbacks(ActivityJobManager)
    }

    private fun getKeyStoreKey() =
        KeyStoreKeyGenerator.get(
            this,
            this.packageName
        )
            .loadOrGenerateKeys()
}