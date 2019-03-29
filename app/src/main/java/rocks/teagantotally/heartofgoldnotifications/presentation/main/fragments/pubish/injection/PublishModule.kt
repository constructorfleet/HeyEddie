package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.pubish.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.pubish.PublishContract
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.pubish.PublishPresenter

@Module
class PublishModule(
    private val view: PublishContract.View
) {
    @Provides
    @FragmentScope
    fun providePresenter(): PublishContract.Presenter =
        PublishPresenter(
            view,
            view
        )
}