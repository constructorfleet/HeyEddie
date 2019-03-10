package rocks.teagantotally.heartofgoldnotifications.common.extensions

inline fun Boolean.ifTrue(block: () -> Unit): Unit = if (this) block() else Unit

//if true execute block and nullable value
inline fun <R> Boolean.ifTrueMaybe(block: () -> R): R? = if (this) block() else null

//if true execute block, return receiver
inline fun Boolean.ifTrueAlso(block: () -> Unit): Boolean = this.ifTrueMaybe { this.also { block() } } ?: this

//if false execute block
inline fun Boolean.ifFalse(block: () -> Unit): Unit = if (!this) block() else Unit

//if false, execute block return value, else return null
inline fun <R> Boolean.ifFalseMaybe(block: () -> R): R? = if (!this) block() else null

//if false execute block, return receiver, else return receiver without executing block
inline fun Boolean.ifFalseAlso(block: () -> Unit): Boolean = this.ifFalseMaybe { this.also { block() } } ?: this

/**
 * Universal Conditional Execution Extensions
 */

//if predicate is true, execute block, no return
inline fun <T> T.ifTrue(
    predicate: (T) -> Boolean,
    block: (T) -> Unit
) = predicate(this).ifTrue { block(this) }

//if expression is true, execute block, no return
inline fun <T> T.ifTrue(
    expression: Boolean,
    block: (T) -> Unit
) = expression.ifTrue { block(this) }

//if predicate is true, execute block and return a value, else return null
inline fun <T, R> T.ifMaybe(
    predicate: (T) -> Boolean,
    block: (T) -> R
): R? = predicate(this).ifTrueMaybe { block(this) }

//if expression is true, execute block and return a value, else return null
inline fun <T, R> T.ifMaybe(
    expression: Boolean,
    block: (T) -> R
): R? = expression.ifTrueMaybe { block(this) }

//if predicate is true, execute block, always return receiver
inline fun <T> T.ifAlso(
    predicate: (T) -> Boolean,
    block: (T) -> Unit
): T = predicate(this).ifTrueMaybe { this.also { block(it) } } ?: this

//if expression is true, execute block, always return receiver
inline fun <T> T.ifAlso(
    expression: Boolean,
    block: (T) -> Unit
): T = expression.ifTrueMaybe { this.also { block(it) } } ?: this

//if predicate is true, execute block, always return receiver, facilitate "apply" idioms
inline fun <T> T.ifApply(
    predicate: (T) -> Boolean,
    block: T.() -> Unit
): T = predicate(this).ifTrueMaybe { this.also { block() } } ?: this

//if expression is true, execute block, always return receiver, facilitate "apply" idioms
inline fun <T> T.ifApply(
    expression: Boolean,
    block: T.() -> Unit
): T = expression.ifTrueMaybe { this.also { block() } } ?: this