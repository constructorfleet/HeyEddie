package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.local.SharedPreferenceConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.managers.SystemNotifier
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
        notifyUseeCase: NotifyUseCase
    ) : ProcessMessage =
        ProcessMessage(
            notifyUseeCase as ProcessingUseCase<*, MessageEvent.Received>
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
        channelManager: ChannelManager
    ): StartClientUseCase =
        StartClientUseCase(
            channelManager.connectionCommandChannel,
            channelManager.failureEventChannel
        )

    @Provides
    @Singleton
    fun provideStopClientUseCase(
        channelManager: ChannelManager
    ): StopClientUseCase =
        StopClientUseCase(channelManager.connectionCommandChannel)

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