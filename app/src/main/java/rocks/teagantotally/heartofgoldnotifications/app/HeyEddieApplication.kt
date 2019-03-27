package rocks.teagantotally.heartofgoldnotifications.app

//import android.support.multidex.MultiDex
//import android.support.multidex.MultiDexApplication
import `in`.co.ophio.secure.core.KeyStoreKeyGenerator
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import kotlinx.coroutines.*
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.DaggerApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.DaggerClientComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ActivityJobManager
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class HeyEddieApplication : MultiDexApplication() {
    companion object {
        @ObsoleteCoroutinesApi
        lateinit var applicationComponent: ApplicationComponent
        lateinit var clientComponent: ClientComponent

        fun setClientComponent(module: ClientModule) {
            DaggerClientComponent.builder()
                .applicationComponent(applicationComponent)
                .clientModule(module)
                .build()
                .let { clientComponent = it }
        }

        private const val PREF_FILE_NAME = "rocks.teagantotally.heartofgoldnotifications.app.pref"

        private val clientChangeCoroutineScope: CoroutineScope =
            object : CoroutineScope {
                override val coroutineContext: CoroutineContext =
                    Job().plus(Dispatchers.IO)
            }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

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