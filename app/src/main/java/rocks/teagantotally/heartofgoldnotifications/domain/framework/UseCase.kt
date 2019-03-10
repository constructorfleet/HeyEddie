package rocks.teagantotally.heartofgoldnotifications.domain.framework

interface UseCase<in ParameterType, ResultType> {
    suspend operator fun invoke(parameter: ParameterType): UseCaseResult<ResultType>
}