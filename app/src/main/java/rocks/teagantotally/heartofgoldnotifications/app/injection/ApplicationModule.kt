package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.local.SharedPreferenceConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.local.TestConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientComponent
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.presentation.CompatMainScoped
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
    fun provideGson(): Gson =
        GsonBuilder().create()

    @Provides
    @Singleton
    fun provideConnectionConfigProvider(
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): ConnectionConfigProvider =
        TestConnectionConfigProvider()
//        SharedPreferenceConnectionConfigProvider(
//            sharedPreferences,
//            gson
//        )
}