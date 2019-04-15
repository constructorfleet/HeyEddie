package rocks.teagantotally.heartofgoldnotifications.app.injection

import `in`.co.ophio.secure.core.ObscuredPreferencesBuilder
import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.UI
import rocks.teagantotally.heartofgoldnotifications.data.managers.config.SharedPreferenceConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.data.managers.history.SharedPreferenceMessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.data.managers.subscription.SharedPreferenceSubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.post.PostClientConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.post.PostConnectionConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.pre.PreConnectionChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.pre.PreConnectionConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.ClientConfigurationSavedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.MqttEventProcessor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.connected.MqttConnectedProcessor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.disconnected.MqttDisconnectedProcessor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.receive.Notify
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.receive.ProcessMessageReceived
import javax.inject.Singleton

@Module
class ApplicationModule(
    private val applicationContext: Context,
    private val secureKey: String,
    private val prefFileName: String
) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context =
        applicationContext

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(
        context: Context
    ): SharedPreferences =
        ObscuredPreferencesBuilder()
            .setApplication(context as Application)
            .obfuscateValue(true)
            .obfuscateKey(true)
            .setSharePrefFileName(prefFileName)
            .setSecret(secureKey)
            .createSharedPrefs()

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().create()

    @Provides
    @Singleton
    fun provideProcessMessageReceived(
        notify: Notify
    ): ProcessMessageReceived =
        ProcessMessageReceived(
            notify
        )

    @Provides
    @Singleton
    fun provideConnectionConfigurationChangedUseCase(): ClientConfigurationSavedUseCase =
        ClientConfigurationSavedUseCase(
            BroadcastChannel(100)
        )

    @Provides
    fun providePreConnectionConfigurationChangedUseCase(
        @UI coroutineScope: CoroutineScope
    ): PreConnectionConfigurationChangedUseCase =
        PreConnectionChangedUseCase(
            coroutineScope
        )

    @Provides
    fun providePostConnectionConfigurationChangedUseCase(
        clientConfigurationSavedUseCase: ClientConfigurationSavedUseCase,
        @UI coroutineScope: CoroutineScope
    ): PostConnectionConfigurationChangedUseCase =
        PostClientConfigurationChangedUseCase(
            clientConfigurationSavedUseCase,
            coroutineScope
        )

    @Provides
    @Singleton
    fun provideConnectionConfigManager(
        sharedPreferences: SharedPreferences,
        gson: Gson,
        preSave: PreConnectionConfigurationChangedUseCase,
        postSave: PostConnectionConfigurationChangedUseCase
    ): ConnectionConfigManager =
        SharedPreferenceConnectionConfigManager(
            preSave,
            sharedPreferences,
            postSave,
            gson
        )

    @Provides
    @Singleton
    fun provideSubscriptionManager(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): SubscriptionManager =
        SharedPreferenceSubscriptionManager(
            sharedPreferences,
            gson
        )

    @Provides
    @Singleton
    fun provideMessageHistoryManager(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): MessageHistoryManager =
        SharedPreferenceMessageHistoryManager(
            sharedPreferences,
            gson
        )

    @Provides
    @Singleton
    fun provideMqttEventProcessor(
        updatePersistentNotificationUseCase: UpdatePersistentNotificationUseCase,
        onConnected: MqttConnectedProcessor,
        onDisconnected: MqttDisconnectedProcessor,
        processMessage: ProcessMessageReceived
    ): MqttEventProcessor =
        MqttEventProcessor(
            updatePersistentNotificationUseCase,
            onDisconnected,
            onConnected,
            processMessage
        )

    @Provides
    @Singleton
    fun provideAlarmManager(
        context: Context
    ): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}