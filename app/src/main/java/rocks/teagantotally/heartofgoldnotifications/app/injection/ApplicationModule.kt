package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.local.SharedPreferenceConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.managers.IntentCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.data.managers.SystemNotifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.CommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.*
import javax.inject.Singleton

@Module
class ApplicationModule(
    private val applicationContext: Context
) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context =
        applicationContext

    @Provides
    @Singleton
    fun provideSharedPreferences(
        context: Context
    ): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

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
        commandExecutor: CommandExecutor
    ): StartClientUseCase =
        StartClientUseCase(
            commandExecutor
        )

    @Provides
    @Singleton
    fun provideStopClientUseCase(
        commandExecutor: CommandExecutor
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
    fun provideCommandExecutor(
        context: Context
    ): CommandExecutor =
        IntentCommandExecutor(context)

//    @Provides
//    @Singleton
//    @Suppress("UNCHECKED_CAST")
//    fun provideEventProcessor(
//        notifyUseCase: NotifyUseCase,
//        finishNotifyUseCase: FinishNotifyUseCase,
//        startClientUseCase: StartClientUseCase,
//        stopClientUseCase: StopClientUseCase,
//        updatePersistentNotificationUseCase: UpdatePersistentNotificationUseCase
//    ): ProcessMessage =
//        ProcessMessage(
//            notifyUseCase as EventP<Event, Event>,
//            finishNotifyUseCase as EventProcessingUseCase<Event, Event>,
//            startClientUseCase as EventProcessingUseCase<Event, Event>,
//            stopClientUseCase as EventProcessingUseCase<Event, Event>,
//            updatePersistentNotificationUseCase as EventProcessingUseCase<Event, Event>
//        )

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