package rocks.teagantotally.heartofgoldnotifications.presentation.injection

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.MainDispatcher
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.ActivityScope
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivityContract
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivityPresenter
import kotlin.coroutines.CoroutineContext

@Module
class MainActivityModule(
    private val view: MainActivityContract.View,
    private val coroutineScope: CoroutineScope
) {
    @Provides
    @ActivityScope
    fun provideView(): MainActivityContract.View =
        view

    @Provides
    @ActivityScope
    fun provideCoroutineContext(): CoroutineContext =
        Dispatchers.Main + Job()

    @Provides
    @ActivityScope
    fun provideCoroutineScope(): CoroutineScope =
        coroutineScope

    @Provides
    @ActivityScope
    fun providePresenter(
        view: MainActivityContract.View
    ): MainActivityContract.Presenter =
        MainActivityPresenter(view)
}