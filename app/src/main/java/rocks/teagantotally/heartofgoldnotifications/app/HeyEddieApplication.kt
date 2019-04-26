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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.DaggerApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientModule
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.DaggerClientComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ActivityJobManager
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.kotqtt.domain.models.commands.MqttCommand
import rocks.teagantotally.kotqtt.domain.models.events.MqttEvent

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class HeyEddieApplication : MultiDexApplication() {
    companion object {
        @ObsoleteCoroutinesApi
        lateinit var applicationComponent: ApplicationComponent
        var clientComponent: ClientComponent? = null

        val eventChannel: BroadcastChannel<MqttEvent> = BroadcastChannel(100)
        val commandChannel: ReceiveChannel<MqttCommand> = Channel()

        fun setClientComponent(config: ConnectionConfiguration) {
            DaggerClientComponent.builder()
                .applicationComponent(applicationComponent)
                .clientModule(
                    ClientModule(
                        config,
                        eventChannel,
                        commandChannel
                    )
                )
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