package rocks.teagantotally.heartofgoldnotifications.app.injection

import `in`.co.ophio.secure.core.ObscuredPreferencesBuilder
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.local.SharedPreferenceConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.managers.IntentMqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.data.managers.SystemNotifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.framework.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.*
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
    fun provideChannelManager(): ChannelManager =
        ChannelManager()

    @Provides
    @Singleton
    fun provideNotificationManager(
        context: Context
    ): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideNotifier(
        context: Context,
        notificationManager: NotificationManager
    ): Notifier =
        SystemNotifier(
            context,
            notificationManager
        )

    @Provides
    @Singleton
    fun provideMessageProcessor(
        notifyUseCase: NotifyUseCase
    ): ProcessMessage =
        ProcessMessage(
            notifyUseCase as ProcessingUseCase<*, MessageEvent.Received>
        )

    @Provides
    @Singleton
    fun providePersistentNotificationUpdater(
        notifier: Notifier
    ): UpdatePersistentNotificationUseCase =
        UpdatePersistentNotificationUseCase(notifier)

    @Provides
    @Singleton
    fun provideFinishNotifyUseCase(
        notifier: Notifier
    ): FinishNotifyUseCase =
        FinishNotifyUseCase(notifier)

    @Provides
    @Singleton
    fun provideStartClientUseCase(
        commandExecutor: MqttCommandExecutor
    ): StartClientUseCase =
        StartClientUseCase(
            commandExecutor
        )

    @Provides
    @Singleton
    fun provideStopClientUseCase(
        commandExecutor: MqttCommandExecutor
    ): StopClientUseCase =
        StopClientUseCase(commandExecutor)

    @Provides
    @Singleton
    fun provideNotifyUseCase(
        notifier: Notifier,
        gson: Gson
    ): NotifyUseCase =
        NotifyUseCase(
            gson,
            notifier
        )

    @ObsoleteCoroutinesApi
    @Provides
    @Singleton
    fun provideMqttCommandExecutor(
        context: Context
    ): MqttCommandExecutor =
        IntentMqttCommandExecutor(context)

    @Provides
    @Singleton
    fun provideConnectionConfigProvider(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): ConnectionConfigProvider =
        SharedPreferenceConnectionConfigProvider(
            sharedPreferences,
            gson
        )
}