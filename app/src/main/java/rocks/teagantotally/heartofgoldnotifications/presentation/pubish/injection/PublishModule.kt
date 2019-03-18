package rocks.teagantotally.heartofgoldnotifications.presentation.pubish.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish.PublishMessage
import rocks.teagantotally.heartofgoldnotifications.presentation.pubish.PublishContract
import rocks.teagantotally.heartofgoldnotifications.presentation.pubish.PublishPresenter

@Module
class PublishModule(
    private val view: PublishContract.View
) {
    @Provides
    @FragmentScope
    fun providePresenter(
        publishMessage: PublishMessage
    ): PublishContract.Presenter =
        PublishPresenter(
            view,
            publishMessage
        )
}