package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.history.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.history.HistoryContract
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.history.HistoryPresenter

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
        messageHistoryManager: MessageHistoryManager
    ): HistoryContract.Presenter =
        HistoryPresenter(
            view,
            messageHistoryManager,
            view
        )
}