package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.managers.device.DeviceConnectivityListener
import rocks.teagantotally.heartofgoldnotifications.data.managers.device.IntentDeviceConnectivityConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.device.DeviceConnectivityEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.device.DeviceConnectivityAvailable
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.device.DeviceConnectivityUnavailable
import javax.inject.Singleton

@Module
class DeviceEventModule {
    @Provides
    @Singleton
    fun provideDeviceConnectivityAvailableUseCase(
        deviceConnectivityEventConsumer: DeviceConnectivityEventConsumer
    ): DeviceConnectivityAvailable =
        DeviceConnectivityAvailable(
            deviceConnectivityEventConsumer
        )

    @Provides
    @Singleton
    fun provideDeviceConnectivityUnavailableUseCase(
        deviceConnectivityEventConsumer: DeviceConnectivityEventConsumer
    ): DeviceConnectivityUnavailable =
        DeviceConnectivityUnavailable(
            deviceConnectivityEventConsumer
        )

    @ObsoleteCoroutinesApi
    @UseExperimental(ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideDeviceEventConsumerUseCase(
        context: Context,
        connectionConfigManager: ConnectionConfigManager
    ): DeviceConnectivityEventConsumer =
        IntentDeviceConnectivityConsumer(
            context,
            connectionConfigManager
        )

    @Provides
    @Singleton
    fun provideNetworkRequest(): NetworkRequest =
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .build()

    @Provides
    @Singleton
    fun provideNetworkConnectivityCallback(
        deviceConnectivityEventConsumer: DeviceConnectivityEventConsumer
    ): ConnectivityManager.NetworkCallback =
        DeviceConnectivityListener(deviceConnectivityEventConsumer)

    @Provides
    @Singleton
    fun provideConnectivityManager(
        context: Context
    ): ConnectivityManager =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
}