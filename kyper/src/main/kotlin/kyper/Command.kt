package kyper

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas

internal sealed class Command {

    public abstract val name: String

    internal abstract val help: String?

    internal abstract val parameters: List<KParameter>

    public abstract fun call(args: Array<String>)

    internal class Function(
        private val func: KFunction<*>,
        override val name: String = func.name,
        private val receiver: Any? = null,
    ) : Command() {

        override val help: String?
            get() = func.findAnnotation<Help>()?.help

        override val parameters: List<KParameter> =
            if (receiver != null) func.parameters.drop(1) else func.parameters

        override fun call(args: Array<String>) {
            // TODO: support positional arguments
            val paramToValue = parameters.zip(args)
                .associate { (kPar, arg) -> kPar to kPar.type.convert(arg) }
            if (receiver == null) {
                func.callBy(paramToValue)
            } else {
                func.callBy(mapOf(func.parameters.first() to receiver) + paramToValue)
            }
        }
    }

    internal class Lambda(
        override val name: String,
        override val help: String? = null,
        reflect: KFunction<Unit>,
        private val wrapper: (Array<String>) -> Unit,
    ) : Command() {
        @ExperimentalReflectionOnLambdas
        override val parameters: List<KParameter> = reflect.parameters
        override fun call(args: Array<String>): Unit = wrapper(args)
    }
}
