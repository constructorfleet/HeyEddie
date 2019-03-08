package rocks.teagantotally.heartofgoldnotifications.domain.usecases

abstract class UseCase<in ParamsT, out ResultT> {
    abstract suspend operator fun invoke(params: ParamsT): ResultT
}