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
import rocks.teagantotally.heartofgoldnotifications.data.managers.SystemNotifier
import rocks.teagantotally.heartofgoldnotifications.data.managers.config.SharedPreferenceConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.data.managers.history.SharedPreferenceMessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.FinishNotifyUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
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
//
//    @Provides
//    @Singleton
//    fun provideChannelManager(): ChannelManager =
//        ChannelManager()

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
    fun provideConnectionConfigManager(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): ConnectionConfigManager =
        SharedPreferenceConnectionConfigManager(
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
}