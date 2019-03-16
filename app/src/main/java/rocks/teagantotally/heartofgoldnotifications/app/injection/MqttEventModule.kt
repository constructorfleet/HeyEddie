package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.managers.IntentMqttEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MqttEventConsumer
import javax.inject.Singleton

@ObsoleteCoroutinesApi
@Module
class MqttEventModule {
    @ExperimentalCoroutinesApi
    @Provides
    @Singleton
    fun provideMqttEventConsumer(
        context: Context
    ): MqttEventConsumer =
        IntentMqttEventConsumer(context)
}