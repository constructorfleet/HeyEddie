package rocks.teagantotally.heartofgoldnotifications.common.extensions

fun Int.Companion.unique() =
    (System.currentTimeMillis() / 1000L % Int.MAX_VALUE).toInt()