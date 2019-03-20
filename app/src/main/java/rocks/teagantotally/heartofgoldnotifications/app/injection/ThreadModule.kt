package rocks.teagantotally.heartofgoldnotifications.app.injection

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.os.Process.THREAD_PRIORITY_LESS_FAVORABLE
import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.IO
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.UI
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.InstanceScope

@Module
class ThreadModule {
    companion object {
        private const val THREAD_IO_NAME = "io_thread"
        private const val THREAD_BACKGROUND_NAME = "ui_thread"
    }

    @Provides
    @InstanceScope
    @IO
    fun provideIOHanderThread(): HandlerThread =
        HandlerThread(THREAD_IO_NAME, THREAD_PRIORITY_LESS_FAVORABLE)

    @Provides
    @InstanceScope
    @IO
    fun provideIOHandler(
        @IO handlerThread: HandlerThread
    ): Handler =
        handlerThread.start()
            .let { Handler(handlerThread.looper) }

    @Provides
    @InstanceScope
    @Background
    fun provideBackgroundHanderThread(): HandlerThread =
        HandlerThread(THREAD_BACKGROUND_NAME, THREAD_PRIORITY_BACKGROUND)

    @Provides
    @InstanceScope
    @Background
    fun provideBackgroundHandler(
        @Background handlerThread: HandlerThread
    ): Handler =
        handlerThread.start()
            .let { Handler(handlerThread.looper) }

    @Provides
    @InstanceScope
    @UI
    fun provideUIHandler(): Handler =
        Handler(Looper.getMainLooper())
}