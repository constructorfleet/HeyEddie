package rocks.teagantotally.heartofgoldnotifications.domain.framework

interface UseCase {
    suspend operator fun invoke()
}

interface UseCaseWithParameter<in ParameterType> {
    suspend operator fun invoke(parameter: ParameterType)
}