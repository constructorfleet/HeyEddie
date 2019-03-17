package rocks.teagantotally.heartofgoldnotifications.presentation.common.annotations

import android.support.annotation.StringRes

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ActionBarTitle(
    @StringRes val value: Int
)