package rocks.teagantotally.heartofgoldnotifications.app

//import android.support.multidex.MultiDex
//import android.support.multidex.MultiDexApplication
import `in`.co.ophio.secure.core.KeyStoreKeyGenerator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.DaggerApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.DaggerClientComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ActivityJobManager
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.ClientConfigurationChangedEvent
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
        Timber.plant(Timber.DebugTree())
    }

    private fun setupGraph() {
        DaggerApplicationComponent.builder()
            .applicationModule(
                ApplicationModule(
                    this,
                    getKeyStoreKey(),
                    this.packageName
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