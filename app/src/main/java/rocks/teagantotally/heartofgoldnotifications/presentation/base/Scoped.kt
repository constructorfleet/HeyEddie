package rocks.teagantotally.heartofgoldnotifications.presentation.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface Scoped : CoroutineScope {
    var job: Job
}