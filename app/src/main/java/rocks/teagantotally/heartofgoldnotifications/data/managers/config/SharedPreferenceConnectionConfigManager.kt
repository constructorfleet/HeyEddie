package rocks.teagantotally.heartofgoldnotifications.data.managers.config

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.ClientConfigurationChangedEvent
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.ClientConfigurationChangedUseCase

class SharedPreferenceConnectionConfigManager(
    private val configurationChanged: ClientConfigurationChangedUseCase,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : ConnectionConfigManager {

    companion object {
        private const val KEY_CONFIG = "ConnectionConfiguration"
    }

    init {
        getConnectionConfiguration()
            ?.let { setupClientComponent(it) }
    }

    override fun hasConnectionConfiguration(): Boolean =
        try {
            getConnectionConfiguration()
                .let { true }
        } catch (_: Throwable) {
            false
        }

    override fun getConnectionConfiguration(): ConnectionConfiguration? =
        sharedPreferences.getString(KEY_CONFIG, null)
            ?.let {
                try {
                    gson.fromJson(it, ConnectionConfiguration::class.java)
                } catch (_: Throwable) {
                    null
                }
            }

    override fun setConnectionConfiguration(
        connectionConfiguration: ConnectionConfiguration
    ) {
        sharedPreferences
            .edit()
            .apply {
                putString(KEY_CONFIG, gson.toJson(connectionConfiguration))
            }
            .apply()

        setupClientComponent(connectionConfiguration)
        runBlocking {
            configurationChanged.send(ClientConfigurationChangedEvent(connectionConfiguration))
        }
    }

    private fun setupClientComponent(connectionConfiguration: ConnectionConfiguration) {
        HeyEddieApplication.setClientComponent(ClientModule(connectionConfiguration))
    }
}