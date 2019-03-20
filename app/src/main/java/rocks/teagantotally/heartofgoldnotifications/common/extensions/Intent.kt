package rocks.teagantotally.heartofgoldnotifications.common.extensions

import android.content.Context
import android.content.Intent
import com.github.ajalt.timberkt.Timber
import kotlin.reflect.KClass

const val EXTRA_INVOKER = "invoker"

fun <InvokerType : Any> Intent.putInvoker(invokerClass: KClass<InvokerType>) =
    putExtra(EXTRA_INVOKER, invokerClass.qualifiedName)

fun <InvokerType : Any> Intent.broadCast(invokerClass: KClass<InvokerType>, context: Context) =
    context.sendBroadcast(putInvoker(invokerClass))

fun <ProcessorType : Any> Intent.log(processor: KClass<ProcessorType>) =
    Timber.d { "Processing  $action by ${processor.qualifiedName} from ${getStringExtra(EXTRA_INVOKER)}" }