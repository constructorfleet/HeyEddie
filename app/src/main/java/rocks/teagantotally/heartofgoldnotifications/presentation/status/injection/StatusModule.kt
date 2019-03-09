package rocks.teagantotally.heartofgoldnotifications.presentation.status.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.presentation.status.StatusContract
import rocks.teagantotally.heartofgoldnotifications.presentation.status.StatusPresenter

@Module
class StatusModule(
    private val view: StatusContract.View
) {
    @Provides
    @FragmentScope
    fun provideView(): StatusContract.View = view

    @Provides
    @FragmentScope
    fun providePresenter(
        channelManager: ChannelManager
    ): StatusContract.Presenter =
        StatusPresenter(
            view,
            channelManager
        )
}