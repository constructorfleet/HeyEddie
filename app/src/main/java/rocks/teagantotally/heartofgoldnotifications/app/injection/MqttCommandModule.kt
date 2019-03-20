package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.managers.mqtt.IntentMqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.framework.commands.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StopClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish.PublishMessage
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.SubscribeTo
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.UnsubscribeFrom
import javax.inject.Singleton

@Module
class MqttCommandModule {
    @Provides
    @Singleton
    fun provideStartClientUseCase(
        commandExecutor: MqttCommandExecutor
    ): StartClient =
        StartClient(
            commandExecutor
        )

    @Provides
    @Singleton
    fun provideStopClientUseCase(
        commandExecutor: MqttCommandExecutor
    ): StopClient =
        StopClient(commandExecutor)

    @Provides
    @Singleton
    fun providePublishMessageUseCase(
        commandExecutor: MqttCommandExecutor
    ): PublishMessage =
        PublishMessage(commandExecutor)

    @Provides
    @Singleton
    fun provideSubscribeUseCase(
        commandExecutor: MqttCommandExecutor
    ): SubscribeTo =
        SubscribeTo(commandExecutor)

    @Provides
    @Singleton
    fun provideUnsubscribeUseCase(
        commandExecutor: MqttCommandExecutor
    ): UnsubscribeFrom =
        UnsubscribeFrom(commandExecutor)

    @ObsoleteCoroutinesApi
    @Provides
    @Singleton
    fun provideMqttCommandExecutor(
        context: Context
    ): MqttCommandExecutor =
        IntentMqttCommandExecutor(context)
}