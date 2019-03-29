package rocks.teagantotally.heartofgoldnotifications.app.injection

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.IO
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.UI
import javax.inject.Singleton

@Module
class CoroutineModule {
    @Provides
    @Singleton
    @UI
    fun provideUICoroutineScope(): CoroutineScope =
        MainScope()

    @Provides
    @Singleton
    @IO
    fun provideIOCoroutineScope(): CoroutineScope =
        CoroutineScope(Dispatchers.IO)
}