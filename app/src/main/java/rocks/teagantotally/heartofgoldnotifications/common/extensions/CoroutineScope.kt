package rocks.teagantotally.heartofgoldnotifications.common.extensions

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

private val CoroutineScope.job: Job?
    get() = try {
        coroutineContext[Job]
    } catch (e: NullPointerException) {
        Timber.d(e) { "Context has not job"}
        null
    }

fun CoroutineScope.childScope(dispatcher: CoroutineDispatcher): CoroutineScope =
    object : CoroutineScope {
        override val coroutineContext: CoroutineContext = Job(job) + dispatcher
    }

