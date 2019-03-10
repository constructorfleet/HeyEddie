package rocks.teagantotally.heartofgoldnotifications.data.services.helpers

import android.app.Service
import android.os.Binder

class ServiceBinder<ServiceType : Service>(val service: ServiceType) : Binder()