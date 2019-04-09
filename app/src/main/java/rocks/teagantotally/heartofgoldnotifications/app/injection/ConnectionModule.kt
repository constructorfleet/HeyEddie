package rocks.teagantotally.heartofgoldnotifications.app.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.connected.MqttConnectedProcessor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.connected.SetConnectedPersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.connected.SubscribeOnConnectUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.disconnected.MqttDisconnectedProcessor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.disconnected.SetDisconnectedPersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.SubscribeTo
import javax.inject.Singleton

@Module
class ConnectionModule {
    // region Connected
    @Provides
    @Singleton
    fun provideSetConnectedPersistentNotificationUseCase(
        updatePersistentNotificationUseCase: UpdatePersistentNotificationUseCase
    ): SetConnectedPersistentNotificationUseCase =
        SetConnectedPersistentNotificationUseCase(
            updatePersistentNotificationUseCase
        )

    @Provides
    @Singleton
    fun provideSubscribeOnConnectUseCase(
        subscriptionManager: SubscriptionManager
    ): SubscribeOnConnectUseCase =
        SubscribeOnConnectUseCase(
            subscriptionManager
        )

    @Provides
    @Singleton
    fun provideMqttConnectedProcessor(
        updatePersistentNotificationUseCase: SetConnectedPersistentNotificationUseCase,
        subscribe: SubscribeOnConnectUseCase
    ): MqttConnectedProcessor =
        MqttConnectedProcessor(
            updatePersistentNotificationUseCase,
            subscribe
        )
    // endregion

    // region Disconnected
    @Provides
    @Singleton
    fun provideSetDisconnectedPersistentNotificationUseCase(
        updatePersistentNotificationUseCase: UpdatePersistentNotificationUseCase
    ): SetDisconnectedPersistentNotificationUseCase =
        SetDisconnectedPersistentNotificationUseCase(
            updatePersistentNotificationUseCase
        )

    @Provides
    @Singleton
    fun provideMqttDisconnectedProcessor(
        setDisconnectedPersistentNotificationUseCase: SetDisconnectedPersistentNotificationUseCase
    ): MqttDisconnectedProcessor =
        MqttDisconnectedProcessor(
            setDisconnectedPersistentNotificationUseCase
        )

    // endregion
}