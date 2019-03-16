package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.managers.IntentMqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.framework.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClientUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StopClientUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.SubscribeTo
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UnsubscribeFrom
import javax.inject.Singleton

@Module
class MqttCommandModule {
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