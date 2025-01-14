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

import io.github.airflux.commons.types.AbstractRaise
import io.github.airflux.commons.types.RaiseException
import io.github.airflux.commons.types.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

public typealias Success<SuccessT> = ResultK.Success<SuccessT>
public typealias Failure<FailureT> = ResultK.Failure<FailureT>

public sealed interface ResultK<out SuccessT, out FailureT> {

    @Suppress("MemberNameEqualsClassName")
    public class Raise<FailureT> : AbstractRaise<FailureT>() {

        public operator fun <SuccessT> ResultK<SuccessT, FailureT>.component1(): SuccessT = bind()

        public fun <SuccessT> ResultK<SuccessT, FailureT>.bind(): SuccessT = if (isSuccess()) value else raise(this)

        public fun <SuccessT> ResultK<SuccessT, FailureT>.raise() {
            if (isFailure()) raise(this)
        }

        public override fun raise(cause: FailureT): Nothing {
            raise(Failure(cause))
        }

        private fun raise(failure: Failure<FailureT>): Nothing {
            throw RaiseException(failure, this)
        }
    }

    public data class Success<out SuccessT>(public val value: SuccessT) : ResultK<SuccessT, Nothing> {

        public companion object {

            public val asNull: Success<Nothing?> = Success(null)

            public val asTrue: Success<Boolean> = Success(true)

            public val asFalse: Success<Boolean> = Success(false)

            public val asUnit: Success<Unit> = Success(Unit)

            public val asEmptyList: Success<List<Nothing>> = Success(emptyList())

            public fun of(value: Boolean): Success<Boolean> = if (value) asTrue else asFalse
        }
    }

    public data class Failure<out FailureT>(public val cause: FailureT) : ResultK<Nothing, FailureT>

    public companion object {

        public fun <SuccessT> success(value: SuccessT): Success<SuccessT> = Success(value)

        public fun <FailureT> failure(cause: FailureT): Failure<FailureT> = Failure(cause)
    }
}

public fun <SuccessT> SuccessT.asSuccess(): Success<SuccessT> = ResultK.success(this)

public fun <FailureT> FailureT.asFailure(): Failure<FailureT> = ResultK.failure(this)

public fun <SuccessT> success(value: SuccessT): Success<SuccessT> = ResultK.success(value)

public fun <FailureT> failure(cause: FailureT): Failure<FailureT> = ResultK.failure(cause)

@OptIn(ExperimentalContracts::class)
public fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Success<SuccessT>)
        returns(false) implies (this@isSuccess is Failure<FailureT>)
    }
    return this is Success<SuccessT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.isSuccess(
    predicate: (SuccessT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Success<SuccessT> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.isFailure(): Boolean {
    contract {
        returns(false) implies (this@isFailure is Success<SuccessT>)
        returns(true) implies (this@isFailure is Failure<FailureT>)
    }
    return this is Failure<FailureT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.isFailure(
    predicate: (FailureT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Failure<FailureT> && predicate(cause)
}

@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, SuccessR, FailureT> ResultK<SuccessT, FailureT>.fold(
    onSuccess: (SuccessT) -> SuccessR,
    onFailure: (FailureT) -> SuccessR
): SuccessR {
    contract {
        callsInPlace(onFailure, AT_MOST_ONCE)
        callsInPlace(onSuccess, AT_MOST_ONCE)
    }

    return if (isSuccess()) onSuccess(value) else onFailure(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, SuccessR, FailureT> ResultK<SuccessT, FailureT>.map(
    transform: (SuccessT) -> SuccessR
): ResultK<SuccessR, FailureT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMap { value -> transform(value).asSuccess() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, SuccessR, FailureT> ResultK<SuccessT, FailureT>.flatMap(
    transform: (SuccessT) -> ResultK<SuccessR, FailureT>
): ResultK<SuccessR, FailureT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, SuccessR, FailureT> ResultK<SuccessT, FailureT>.andThen(
    block: (SuccessT) -> ResultK<SuccessR, FailureT>
): ResultK<SuccessR, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, FailureT, FailureR> ResultK<SuccessT, FailureT>.mapFailure(
    transform: (FailureT) -> FailureR
): ResultK<SuccessT, FailureR> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else transform(cause).asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, FailureT> ResultK<Boolean, FailureT>.flatMapBoolean(
    ifTrue: () -> ResultK<SuccessT, FailureT>,
    ifFalse: () -> ResultK<SuccessT, FailureT>
): ResultK<SuccessT, FailureT> {
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
public inline fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.onSuccess(
    block: (SuccessT) -> Unit
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isSuccess()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.onFailure(
    block: (FailureT) -> Unit
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isFailure()) block(it.cause) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.recover(
    block: (FailureT) -> SuccessT
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause).asSuccess()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.recoverWith(
    block: (FailureT) -> ResultK<SuccessT, FailureT>
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.getOrForward(
    block: (Failure<FailureT>) -> Nothing
): SuccessT {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else block(this)
}

@OptIn(ExperimentalContracts::class)
public fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.getOrNull(): SuccessT? {
    contract {
        returns(null) implies (this@getOrNull is Failure<FailureT>)
        returnsNotNull() implies (this@getOrNull is Success<SuccessT>)
    }

    return if (isSuccess()) value else null
}

public infix fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.getOrElse(default: SuccessT): SuccessT =
    if (isSuccess()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.getOrElse(
    default: (FailureT) -> SuccessT
): SuccessT {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else default(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.orElse(
    default: () -> ResultK<SuccessT, FailureT>
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.orThrow(
    exceptionBuilder: (FailureT) -> Throwable
): SuccessT {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else throw exceptionBuilder(cause)
}

@OptIn(ExperimentalContracts::class)
public fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.getFailureOrNull(): FailureT? {
    contract {
        returns(null) implies (this@getFailureOrNull is Success<SuccessT>)
        returnsNotNull() implies (this@getFailureOrNull is Failure<FailureT>)
    }

    return if (isFailure()) cause else null
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.forEach(block: (SuccessT) -> Unit) {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else Unit
}

public fun <T> ResultK<T, T>.merge(): T = fold(onSuccess = ::identity, onFailure = ::identity)

@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.apply(
    block: SuccessT.() -> ResultK<Unit, FailureT>
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (this.isSuccess()) {
        val result = block(this.value)
        if (result.isSuccess()) this else result
    } else
        this
}

public fun <SuccessT, FailureT> Iterable<ResultK<SuccessT, FailureT>>.sequence(): ResultK<List<SuccessT>, FailureT> =
    traverse(::identity)

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@OverloadResolutionByLambdaReturnType
public inline fun <SuccessT, SuccessR, FailureT> Iterable<SuccessT>.traverse(
    transform: (SuccessT) -> ResultK<SuccessR, FailureT>
): ResultK<List<SuccessR>, FailureT> {
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
public inline fun <SuccessT, SuccessR, FailureT, M : MutableList<SuccessR>> Iterable<SuccessT>.traverseTo(
    destination: M,
    transform: (SuccessT) -> ResultK<SuccessR, FailureT>
): ResultK<M, FailureT> {
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
public inline fun <SuccessT, FailureT, K, V, M : MutableMap<K, V>> Iterable<SuccessT>.traverseTo(
    destination: M,
    transform: (SuccessT) -> ResultK<Pair<K, V>, FailureT>
): ResultK<M, FailureT> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, { it.first }, { it.second }, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, SuccessR, FailureT, K, M : MutableMap<K, SuccessR>> Iterable<SuccessT>.traverseTo(
    destination: M,
    keySelector: (SuccessR) -> K,
    transform: (SuccessT) -> ResultK<SuccessR, FailureT>
): ResultK<M, FailureT> {
    contract {
        callsInPlace(keySelector, InvocationKind.UNKNOWN)
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, keySelector, ::identity, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, SuccessR, FailureT, K, V, M : MutableMap<K, V>> Iterable<SuccessT>.traverseTo(
    destination: M,
    keySelector: (SuccessR) -> K,
    valueTransform: (SuccessR) -> V,
    transform: (SuccessT) -> ResultK<SuccessR, FailureT>
): ResultK<M, FailureT> {
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
public inline fun <SuccessT : Any, FailureT : Any> ResultK<SuccessT?, FailureT>.filterNotNull(
    failureBuilder: () -> FailureT
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(failureBuilder, AT_MOST_ONCE)
    }
    return if (this.isFailure())
        this
    else if (this.value != null) {
        @Suppress("UNCHECKED_CAST")
        this as ResultK<SuccessT, FailureT>
    } else
        failureBuilder().asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, FailureT> ResultK<SuccessT, FailureT>.filterOrElse(
    predicate: (SuccessT) -> Boolean,
    default: () -> FailureT
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
        callsInPlace(default, AT_MOST_ONCE)
    }

    return if (isFailure() || predicate(this.value)) this else default().asFailure()
}
