package rocks.teagantotally.heartofgoldnotifications.domain.framework

import kotlin.reflect.KClass

abstract class ProcessingUseCase<ParameterType : ParameterBaseType, ParameterBaseType : Any>(
    private val parameterType: KClass<ParameterType>
) : UseCase<ParameterBaseType> {
    @Suppress("UNCHECKED_CAST")
    override suspend fun invoke(parameter: ParameterBaseType) {
        if (parameterType.isInstance(parameter)) {
            handle(parameter as ParameterType)
        }
    }

    protected abstract suspend fun handle(parameter: ParameterType)
}