/*
 * Copyright 2024 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("TooManyFunctions")

package io.github.airflux.commons.types.resultk

import io.github.airflux.commons.types.RaiseException
import io.github.airflux.commons.types.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

public typealias Success<ValueT> = ResultK.Success<ValueT>
public typealias Failure<FailT> = ResultK.Failure<FailT>

public sealed interface ResultK<out ValueT, out FailT> {

    @Suppress("MemberNameEqualsClassName")
    public class Raise<in FailT> : io.github.airflux.commons.types.Raise<FailT> {

        public operator fun <ValueT> ResultK<ValueT, FailT>.component1(): ValueT = bind()

        public fun <ValueT> ResultK<ValueT, FailT>.bind(): ValueT = if (isSuccess()) value else raise(this)

        public fun <ValueT> ResultK<ValueT, FailT>.raise() {
            if (isFailure()) raise(this)
        }

        public override fun raise(cause: FailT): Nothing {
            raise(Failure(cause))
        }

        private fun raise(failure: Failure<FailT>): Nothing {
            throw RaiseException(failure, this)
        }
    }

    public data class Success<out ValueT>(public val value: ValueT) : ResultK<ValueT, Nothing> {

        public companion object {

            public val asNull: Success<Nothing?> = Success(null)

            public val asTrue: Success<Boolean> = Success(true)

            public val asFalse: Success<Boolean> = Success(false)

            public val asUnit: Success<Unit> = Success(Unit)

            public val asEmptyList: Success<List<Nothing>> = Success(emptyList())

            public fun of(value: Boolean): Success<Boolean> = if (value) asTrue else asFalse
        }
    }

    public data class Failure<out FailT>(public val cause: FailT) : ResultK<Nothing, FailT>

    public companion object {

        public fun <ValueT> success(value: ValueT): Success<ValueT> = Success(value)

        public fun <FailT> failure(cause: FailT): Failure<FailT> = Failure(cause)
    }
}

public fun <ValueT> ValueT.asSuccess(): Success<ValueT> = ResultK.success(this)

public fun <FailT> FailT.asFailure(): Failure<FailT> = ResultK.failure(this)

public fun <ValueT> success(value: ValueT): Success<ValueT> = ResultK.success(value)

public fun <FailT> failure(cause: FailT): Failure<FailT> = ResultK.failure(cause)

@OptIn(ExperimentalContracts::class)
public fun <ValueT, FailT> ResultK<ValueT, FailT>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Success<ValueT>)
        returns(false) implies (this@isSuccess is Failure<FailT>)
    }
    return this is Success<ValueT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailT> ResultK<ValueT, FailT>.isSuccess(
    predicate: (ValueT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Success<ValueT> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT, FailT> ResultK<ValueT, FailT>.isFailure(): Boolean {
    contract {
        returns(false) implies (this@isFailure is Success<ValueT>)
        returns(true) implies (this@isFailure is Failure<FailT>)
    }
    return this is Failure<FailT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailT> ResultK<ValueT, FailT>.isFailure(
    predicate: (FailT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Failure<FailT> && predicate(cause)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, FailT> ResultK<ValueT, FailT>.fold(
    onSuccess: (ValueT) -> SuccessR,
    onFailure: (FailT) -> SuccessR
): SuccessR {
    contract {
        callsInPlace(onFailure, AT_MOST_ONCE)
        callsInPlace(onSuccess, AT_MOST_ONCE)
    }

    return if (isSuccess()) onSuccess(value) else onFailure(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, SuccessR, FailT> ResultK<ValueT, FailT>.map(
    transform: (ValueT) -> SuccessR
): ResultK<SuccessR, FailT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMap { value -> transform(value).asSuccess() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, SuccessR, FailT> ResultK<ValueT, FailT>.flatMap(
    transform: (ValueT) -> ResultK<SuccessR, FailT>
): ResultK<SuccessR, FailT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, SuccessR, FailT> ResultK<ValueT, FailT>.andThen(
    block: (ValueT) -> ResultK<SuccessR, FailT>
): ResultK<SuccessR, FailT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailT, FailureR> ResultK<ValueT, FailT>.mapFailure(
    transform: (FailT) -> FailureR
): ResultK<ValueT, FailureR> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else transform(cause).asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailT> ResultK<Boolean, FailT>.flatMapBoolean(
    ifTrue: () -> ResultK<ValueT, FailT>,
    ifFalse: () -> ResultK<ValueT, FailT>
): ResultK<ValueT, FailT> {
    contract {
        callsInPlace(ifTrue, AT_MOST_ONCE)
        callsInPlace(ifFalse, AT_MOST_ONCE)
    }
    return if (this.isFailure())
        this
    else if (this.value)
        ifTrue()
    else
        ifFalse()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailT> ResultK<ValueT, FailT>.onSuccess(
    block: (ValueT) -> Unit
): ResultK<ValueT, FailT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isSuccess()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailT> ResultK<ValueT, FailT>.onFailure(
    block: (FailT) -> Unit
): ResultK<ValueT, FailT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isFailure()) block(it.cause) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailT> ResultK<ValueT, FailT>.recover(
    block: (FailT) -> ValueT
): ResultK<ValueT, FailT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause).asSuccess()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailT> ResultK<ValueT, FailT>.recoverWith(
    block: (FailT) -> ResultK<ValueT, FailT>
): ResultK<ValueT, FailT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailT> ResultK<ValueT, FailT>.getOrForward(
    block: (Failure<FailT>) -> Nothing
): ValueT {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else block(this)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT, FailT> ResultK<ValueT, FailT>.getOrNull(): ValueT? {
    contract {
        returns(null) implies (this@getOrNull is Failure<FailT>)
        returnsNotNull() implies (this@getOrNull is Success<ValueT>)
    }

    return if (isSuccess()) value else null
}

public infix fun <ValueT, FailT> ResultK<ValueT, FailT>.getOrElse(default: ValueT): ValueT =
    if (isSuccess()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailT> ResultK<ValueT, FailT>.getOrElse(
    default: (FailT) -> ValueT
): ValueT {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else default(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailT> ResultK<ValueT, FailT>.orElse(
    default: () -> ResultK<ValueT, FailT>
): ResultK<ValueT, FailT> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailT> ResultK<ValueT, FailT>.orThrow(
    exceptionBuilder: (FailT) -> Throwable
): ValueT {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else throw exceptionBuilder(cause)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT, FailT> ResultK<ValueT, FailT>.getFailureOrNull(): FailT? {
    contract {
        returns(null) implies (this@getFailureOrNull is Success<ValueT>)
        returnsNotNull() implies (this@getFailureOrNull is Failure<FailT>)
    }

    return if (isFailure()) cause else null
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailT> ResultK<ValueT, FailT>.forEach(block: (ValueT) -> Unit) {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else Unit
}

public fun <T> ResultK<T, T>.merge(): T = fold(onSuccess = ::identity, onFailure = ::identity)

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailT> ResultK<ValueT, FailT>.apply(
    block: ValueT.() -> ResultK<Unit, FailT>
): ResultK<ValueT, FailT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (this.isSuccess()) {
        val result = block(this.value)
        if (result.isSuccess()) this else result
    } else
        this
}

public fun <ValueT, FailT> Iterable<ResultK<ValueT, FailT>>.sequence(): ResultK<List<ValueT>, FailT> =
    traverse(::identity)

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@OverloadResolutionByLambdaReturnType
public inline fun <ValueT, SuccessR, FailT> Iterable<ValueT>.traverse(
    transform: (ValueT) -> ResultK<SuccessR, FailT>
): ResultK<List<SuccessR>, FailT> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    val items = buildList {
        val iter = this@traverse.iterator()
        while (iter.hasNext()) {
            val item = transform(iter.next())
            if (item.isFailure()) return item
            add(item.value)
        }
    }
    return if (items.isNotEmpty()) items.asSuccess() else Success.asEmptyList
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, FailT, M : MutableList<SuccessR>> Iterable<ValueT>.traverseTo(
    destination: M,
    transform: (ValueT) -> ResultK<SuccessR, FailT>
): ResultK<M, FailT> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }

    for (item in this@traverseTo) {
        val result = transform(item)
        if (result.isFailure()) return result
        destination.add(result.value)
    }
    return destination.asSuccess()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailT, K, V, M : MutableMap<K, V>> Iterable<ValueT>.traverseTo(
    destination: M,
    transform: (ValueT) -> ResultK<Pair<K, V>, FailT>
): ResultK<M, FailT> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, { it.first }, { it.second }, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, FailT, K, M : MutableMap<K, SuccessR>> Iterable<ValueT>.traverseTo(
    destination: M,
    keySelector: (SuccessR) -> K,
    transform: (ValueT) -> ResultK<SuccessR, FailT>
): ResultK<M, FailT> {
    contract {
        callsInPlace(keySelector, InvocationKind.UNKNOWN)
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, keySelector, ::identity, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, FailT, K, V, M : MutableMap<K, V>> Iterable<ValueT>.traverseTo(
    destination: M,
    keySelector: (SuccessR) -> K,
    valueTransform: (SuccessR) -> V,
    transform: (ValueT) -> ResultK<SuccessR, FailT>
): ResultK<M, FailT> {
    contract {
        callsInPlace(keySelector, InvocationKind.UNKNOWN)
        callsInPlace(valueTransform, InvocationKind.UNKNOWN)
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }

    for (item in this@traverseTo) {
        val result = transform(item)
        if (result.isFailure()) return result
        val key = keySelector(result.value)
        val value = valueTransform(result.value)
        destination[key] = value
    }
    return destination.asSuccess()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT : Any, FailT : Any> ResultK<ValueT?, FailT>.filterNotNull(
    failureBuilder: () -> FailT
): ResultK<ValueT, FailT> {
    contract {
        callsInPlace(failureBuilder, AT_MOST_ONCE)
    }
    return if (this.isFailure())
        this
    else if (this.value != null) {
        @Suppress("UNCHECKED_CAST")
        this as ResultK<ValueT, FailT>
    } else
        failureBuilder().asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailT> ResultK<ValueT, FailT>.filterOrElse(
    predicate: (ValueT) -> Boolean,
    default: () -> FailT
): ResultK<ValueT, FailT> {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
        callsInPlace(default, AT_MOST_ONCE)
    }

    return if (isFailure() || predicate(this.value)) this else default().asFailure()
}
