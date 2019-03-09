package rocks.teagantotally.heartofgoldnotifications.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.models.ConnectionConfiguration

class SharedPreferenceConnectionConfigProvider(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : ConnectionConfigProvider {

    companion object {
        private const val KEY_CONFIG = "ConnectionConfiguration"
    }

    override fun hasConnectionConfiguration(): Boolean =
        sharedPreferences.contains(KEY_CONFIG)

    override fun getConnectionConfiguration(): ConnectionConfiguration =
        sharedPreferences.getString(KEY_CONFIG, null)
            .let {
                gson.fromJson(it, ConnectionConfiguration::class.java)
            }

    override fun setConnectionConfiguration(connectionConfiguration: ConnectionConfiguration) {
        sharedPreferences
            .edit()
            .apply {
                putString(KEY_CONFIG, gson.toJson(connectionConfiguration))
            }
            .apply()
    }
}