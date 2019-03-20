package rocks.teagantotally.heartofgoldnotifications.presentation.base

sealed class ConnectionViewState {
    object Unconfigured : ConnectionViewState()
    object Disconnected : ConnectionViewState()
    object Connected : ConnectionViewState()
}