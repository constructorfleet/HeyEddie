package rocks.teagantotally.heartofgoldnotifications.common.extensions

import kotlin.random.Random

fun Int.Companion.unique() =
    (Random(System.currentTimeMillis()).nextInt() / 1000L % Int.MAX_VALUE).toInt()