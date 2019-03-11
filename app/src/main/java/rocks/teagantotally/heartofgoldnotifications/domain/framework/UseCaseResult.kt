package rocks.teagantotally.heartofgoldnotifications.domain.framework

sealed class UseCaseResult<ValueType> {
    class Success<ValueType>(val value: ValueType) : UseCaseResult<ValueType>()
    class Failure<ValueType>(val throwable: Throwable) : UseCaseResult<ValueType>()
    class Unhandled<ValueType> : UseCaseResult<ValueType>()
}