package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.content.Context
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.data.services.NotificationService
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.main.injection.MainActivityComponent
import javax.inject.Singleton

@ObsoleteCoroutinesApi
@UseExperimental(ExperimentalCoroutinesApi::class)
@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        MqttCommandModule::class,
        MqttEventModule::class
    ]
)
interface ApplicationComponent {
    fun inject(service: NotificationService)
    fun inject(service: MqttService)

    fun clientComponentBuilder(): ClientComponent.Builder

    fun mainActivityComponentBuilder(): MainActivityComponent.Builder

    fun provideApplicationContext(): Context
}