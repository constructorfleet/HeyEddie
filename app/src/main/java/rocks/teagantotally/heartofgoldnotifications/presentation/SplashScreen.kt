package rocks.teagantotally.heartofgoldnotifications.presentation

import android.content.Intent
import android.os.Bundle
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity

class SplashScreen : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}