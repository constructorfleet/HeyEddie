package rocks.teagantotally.heartofgoldnotifications.presentation.base

import android.support.v4.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : Fragment(), Navigable, CoroutineScope {
    abstract var coroutineScope: CoroutineScope

    override val coroutineContext: CoroutineContext by lazy { coroutineScope.coroutineContext }
}