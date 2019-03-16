package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.managers.mqtt.IntentMqttEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MqttEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish.ProcessMessagePublished
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish.RecordMessagePublished
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.receive.Notify
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.receive.ProcessMessageReceived
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.receive.RecordMessageReceived
import javax.inject.Singleton

@ObsoleteCoroutinesApi
@Module
class MqttEventModule {
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
    fun provideRecordMessageReceivedUseCase(
        messageHistoryManager: MessageHistoryManager
    ): RecordMessageReceived =
        RecordMessageReceived(
            messageHistoryManager
        )

    @Provides
    @Singleton
    fun provideProcessReceivedUseCase(
        notify: Notify,
        recordMessageReceived: RecordMessageReceived
    ): ProcessMessageReceived =
        ProcessMessageReceived(
            notify,
            recordMessageReceived
        )

    @Provides
    @Singleton
    fun provideRecordMessagePublishedUseCase(
        messageHistoryManager: MessageHistoryManager
    ): RecordMessagePublished =
        RecordMessagePublished(
            messageHistoryManager
        )

    @Provides
    @Singleton
    fun provideProcessPublishedUseCase(
        recordMessagePublished: RecordMessagePublished
    ): ProcessMessagePublished =
        ProcessMessagePublished(
            recordMessagePublished
        )

    @ExperimentalCoroutinesApi
    @Provides
    @Singleton
    fun provideMqttEventConsumer(
        context: Context
    ): MqttEventConsumer =
        IntentMqttEventConsumer(context)
}