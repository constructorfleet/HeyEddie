package rocks.teagantotally.heartofgoldnotifications.presentation

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.HeartOfGoldNotificationsApplication
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientEventType
import rocks.teagantotally.heartofgoldnotifications.domain.models.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.presentation.config.ConfigFragment
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

object CompatMainScoped : CoroutineScope {
    override var coroutineContext: CoroutineContext = Dispatchers.Main
}

class MainActivity : AppCompatActivity(),
    CoroutineScope by CompatMainScoped {
    @Inject
    lateinit var client: Client

    @Inject
    lateinit var configProvider: ConnectionConfigProvider

    val eventChannel = Channel<ClientEvent>()

    var messageChannel = Channel<MessageEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        HeartOfGoldNotificationsApplication.applicationComponent
            .clientComponentBuilder()
            .clientModule(ClientModule(this, this, eventChannel, messageChannel))
            .build()
            .inject(this)

        navigation_drawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_settings ->
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, ConfigFragment())
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .commit()
                        .run { it.isChecked = true }
                        .run { true }

                else -> false
            }.also { drawer_container.closeDrawers() }
        }

        client.connect(configProvider.getConnectionConfiguration())
    }

    override fun onResume() {
        super.onResume()
        launch {
            while (!eventChannel.isClosedForReceive) {
                eventChannel.receiveOrNull()
                    ?.let {
                        when (it.type) {
                            is ClientEventType.Connection ->
                                client.subscribe("/test", 0)
                        }
                    }
            }
        }

        launch {
            while (!messageChannel.isClosedForReceive) {
                messageChannel.receiveOrNull()
                    ?.let {
                        when (it) {
                            is MessageEvent.Received.Success -> Toast.makeText(
                                this@MainActivity,
                                String(it.message.payload),
                                Toast.LENGTH_LONG
                            ).show()
                            is MessageEvent.Received.Failure -> Timber.d("{${it.throwable}")
                            else -> return@let
                        }
                    }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home ->
                drawer_container.openDrawer(GravityCompat.START)
                    .run { true }
            else -> super.onOptionsItemSelected(item)
        }
}