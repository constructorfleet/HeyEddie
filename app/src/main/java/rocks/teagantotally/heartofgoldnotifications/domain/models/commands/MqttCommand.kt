package rocks.teagantotally.heartofgoldnotifications.domain.models.commands

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelConverter
import org.parceler.Parcels
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

@Parcel(converter = MqttCommandConverter::class)
sealed class MqttCommand : Command, Parcelable {
    @Parcel
    @Parcelize
    object Connect : MqttCommand()

    @Parcel
    @Parcelize
    object Disconnect : MqttCommand()

    @Parcel
    @Parcelize
    class Subscribe @ParcelConstructor constructor(val topic: String, val maxQoS: Int) : MqttCommand(), Parcelable

    @Parcel
    @Parcelize
    class Unsubscribe @ParcelConstructor constructor(val topic: String) : MqttCommand(), Parcelable

    @Parcel
    @Parcelize
    class Publish @ParcelConstructor constructor(val message: Message) : MqttCommand(), Parcelable
}

class MqttCommandConverter : ParcelConverter<MqttCommand> {
    override fun fromParcel(parcel: android.os.Parcel?): MqttCommand =
        Parcels.unwrap(parcel?.readParcelable(MqttCommand::class.java.classLoader))

    override fun toParcel(input: MqttCommand?, parcel: android.os.Parcel?) {
        safeLet(parcel, input) { out, obj ->
            when (obj) {
                MqttCommand.Connect -> out.writeParcelable(Parcels.wrap(obj), 0)
                MqttCommand.Disconnect -> out.writeParcelable(Parcels.wrap(obj), 0)
                is MqttCommand.Subscribe -> out.writeParcelable(Parcels.wrap(obj), 0)
                is MqttCommand.Unsubscribe -> out.writeParcelable(Parcels.wrap(obj), 0)
                is MqttCommand.Publish -> out.writeParcelable(Parcels.wrap(obj), 0)
            }
        }
    }
}