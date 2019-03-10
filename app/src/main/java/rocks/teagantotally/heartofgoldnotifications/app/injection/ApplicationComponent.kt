package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.content.Context
import dagger.Component
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.data.services.NotificationService
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.injection.MainActivityComponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ApplicationModule::class]
)
interface ApplicationComponent {
    fun inject(service: NotificationService)
    fun inject(receiver: MqttService.PublishReceiver)

    fun clientComponentBuilder(): ClientComponent.Builder

    fun mainActivityComponentBuilder(): MainActivityComponent.Builder

    fun provideApplicationContext(): Context
}