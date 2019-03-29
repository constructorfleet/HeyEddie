package rocks.teagantotally.heartofgoldnotifications.common.extensions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

private val CoroutineScope.job: Job?
    get() = coroutineContext[Job]

fun CoroutineScope.childScope(dispatcher: CoroutineDispatcher): CoroutineScope =
    object : CoroutineScope {
        override val coroutineContext: CoroutineContext = Job(job) + dispatcher
    }

