package rocks.teagantotally.heartofgoldnotifications.presentation.history.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClientUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.history.HistoryContract
import rocks.teagantotally.heartofgoldnotifications.presentation.history.HistoryPresenter

@Module
class HistoryModule(
    private val view: HistoryContract.View
) {
    @Provides
    @FragmentScope
    fun provideView(): HistoryContract.View = view

    @Provides
    @FragmentScope
    fun providePresenter(
//        startClientUseCase: StartClientUseCase
    ): HistoryContract.Presenter =
        HistoryPresenter(
            view
        )
}