package rocks.teagantotally.heartofgoldnotifications.presentation.base

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.MenuItem
import rocks.teagantotally.heartofgoldnotifications.R

sealed class ConnectionViewState(
    @StringRes val displayText: Int,
    @DrawableRes val navigationIcon: Int,
    @StringRes val navigationText: Int,
    val canNavigate: Boolean,
    val connected: Boolean
) {

    object Unconfigured : ConnectionViewState(
        R.string.status_unconfigured,
        R.drawable.ic_sync_problem,
        R.string.connection_checking,
        true,
        false
    )

    object Disconnected : ConnectionViewState(
        R.string.status_disconnected,
        R.drawable.ic_sync,
        R.string.connection_establish,
        true,
        false
    )

    object Disconnecting : ConnectionViewState(
        R.string.status_disconnected,
        R.drawable.ic_sync,
        R.string.connection_establish,
        false,
        false
    )

    object Connected : ConnectionViewState(
        R.string.status_connected,
        R.drawable.ic_unsync,
        R.string.connection_disconnect,
        true,
        true
    )

    object Connecting : ConnectionViewState(
        R.string.status_connecting,
        R.drawable.ic_unsync,
        R.string.connection_disconnect,
        false,
        false
    )

    object Checking : ConnectionViewState(
        R.string.status_checking,
        R.drawable.ic_hourglass,
        R.string.connection_checking,
        false,
        false
    )
}