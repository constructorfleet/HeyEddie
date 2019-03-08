package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.preference.PreferenceManager
import kotlinx.coroutines.channels.Channel
import org.eclipse.paho.android.service.MqttAndroidClient
import rocks.teagantotally.heartofgoldnotifications.data.common.BrokerUriBuilder
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.local.TestConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.clients.MqttClient
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.MessageEvent
import javax.inject.Inject

class MqttService : IntentService(NAME) {
    companion object {
        const val NAME = "MqttService"
        private const val INTENT_BASE = "rocks.teagantotally.heartofgoldnotifications.data.services.mqtt"
        const val CONNECT = "$INTENT_BASE.connect"
        const val DISCONNECT = "$INTENT_BASE.disconnect"
    }

    override fun onHandleIntent(intent: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}