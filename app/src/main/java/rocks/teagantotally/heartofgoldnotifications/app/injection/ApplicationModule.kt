package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.local.SharedPreferenceConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.processors.notifications.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.processors.notifications.SystemNotifier
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
    fun provideConnectionConfigProvider(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): ConnectionConfigProvider =
        SharedPreferenceConnectionConfigProvider(
            sharedPreferences,
            gson
        )
}