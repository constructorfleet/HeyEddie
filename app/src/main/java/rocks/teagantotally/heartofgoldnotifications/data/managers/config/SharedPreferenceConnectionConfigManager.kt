package rocks.teagantotally.heartofgoldnotifications.data.managers.config

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientModule
import rocks.teagantotally.heartofgoldnotifications.common.extensions.asynchronousSave
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.post.PostConnectionConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.pre.PreConnectionConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientConfigurationChangedEvent

class SharedPreferenceConnectionConfigManager(
    private val preSave: PreConnectionConfigurationChangedUseCase,
    private val sharedPreferences: SharedPreferences,
    private val postSave: PostConnectionConfigurationChangedUseCase,
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

    override suspend fun setConnectionConfiguration(
        connectionConfiguration: ConnectionConfiguration
    ) {
        getConnectionConfiguration()
            .let {
                ClientConfigurationChangedEvent(
                    it,
                    connectionConfiguration
                )
            }.let { event ->
                runBlocking {
                    preSave(event)
                }.run {
                    sharedPreferences
                        .asynchronousSave {
                            putString(KEY_CONFIG, gson.toJson(connectionConfiguration))
                        }
                }.run {
                    setupClientComponent(connectionConfiguration)
                }.run {
                    runBlocking {
                        postSave(event)
                    }
                }
            }
    }

    private fun setupClientComponent(connectionConfiguration: ConnectionConfiguration) {
        HeyEddieApplication.setClientComponent(ClientModule(connectionConfiguration))
    }
}