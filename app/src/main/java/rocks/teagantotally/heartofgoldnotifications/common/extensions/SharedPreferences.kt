package rocks.teagantotally.heartofgoldnotifications.common.extensions

import android.annotation.SuppressLint
import android.content.SharedPreferences

@SuppressLint("ApplySharedPref")
fun SharedPreferences.synchronousSave(transaction: SharedPreferences.Editor.() -> Unit) {
    edit()
        .apply() { transaction() }
        .commit()
}

fun SharedPreferences.asynchronousSave(transaction: SharedPreferences.Editor.() -> Unit) {
    edit()
        .apply { transaction() }
        .apply()
}