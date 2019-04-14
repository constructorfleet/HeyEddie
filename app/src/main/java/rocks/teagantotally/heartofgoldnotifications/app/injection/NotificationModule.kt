package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.data.managers.SystemNotifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.FinishNotifyUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.receive.Notify
import javax.inject.Singleton

@Module
class NotificationModule {
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
        notificationManager: NotificationManager,
        connectionConfigManager: ConnectionConfigManager,
        alarmManager: AlarmManager
    ): Notifier =
        SystemNotifier(
            context,
            notificationManager,
            connectionConfigManager,
            alarmManager
        )

    @Provides
    @Singleton
    fun provideNotifyUseCase(
        notifier: Notifier,
        gson: Gson
    ): Notify =
        Notify(
            gson,
            notifier
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
}