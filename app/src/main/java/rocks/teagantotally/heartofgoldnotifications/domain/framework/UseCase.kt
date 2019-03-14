package rocks.teagantotally.heartofgoldnotifications.domain.framework

interface UseCase<in ParameterType> {
    suspend operator fun invoke(parameter: ParameterType)
}