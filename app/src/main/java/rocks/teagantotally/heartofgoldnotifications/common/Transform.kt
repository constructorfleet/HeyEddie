package rocks.teagantotally.heartofgoldnotifications.common

interface Transform<OutputType : Any> {
    fun transform(): OutputType
}