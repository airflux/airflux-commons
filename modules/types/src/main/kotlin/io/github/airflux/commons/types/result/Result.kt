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

package io.github.airflux.commons.types.result

import io.github.airflux.commons.types.AbstractRaise
import io.github.airflux.commons.types.RaiseException
import io.github.airflux.commons.types.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

public sealed interface Result<out T, out E> {

    @Suppress("MemberNameEqualsClassName")
    public class Raise<E> : AbstractRaise<E>() {

        public operator fun <T> Result<T, E>.component1(): T = bind()

        public fun <T> Result<T, E>.bind(): T = if (isSuccess()) value else raise(this)

        public fun <T> Result<T, E>.raise() {
            if (isFailure()) raise(this)
        }

        public override fun raise(cause: E): Nothing {
            raise(Failure(cause))
        }

        private fun raise(failure: Failure<E>): Nothing {
            throw RaiseException(failure, this)
        }
    }

    public companion object
}

public data class Success<out T>(public val value: T) : Result<T, Nothing> {

    public companion object {

        public val asNull: Success<Nothing?> = Success(null)

        public val asTrue: Success<Boolean> = Success(true)

        public val asFalse: Success<Boolean> = Success(false)

        public val asUnit: Success<Unit> = Success(Unit)

        public val asEmptyList: Success<List<Nothing>> = Success(emptyList())

        public fun of(value: Boolean): Success<Boolean> = if (value) asTrue else asFalse
    }
}

public data class Failure<out E>(public val cause: E) : Result<Nothing, E>

public fun <T> T.asSuccess(): Success<T> = success(this)

public fun <E> E.asFailure(): Failure<E> = failure(this)

public fun <T> success(value: T): Success<T> = Success(value)

public fun <E> failure(cause: E): Failure<E> = Failure(cause)

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Success<T>)
        returns(false) implies (this@isSuccess is Failure<E>)
    }
    return this is Success<T>
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.isSuccess(predicate: (T) -> Boolean): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Success<T> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.isFailure(): Boolean {
    contract {
        returns(false) implies (this@isFailure is Success<T>)
        returns(true) implies (this@isFailure is Failure<E>)
    }
    return this is Failure<E>
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.isFailure(predicate: (E) -> Boolean): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Failure<E> && predicate(cause)
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R, E> Result<T, E>.fold(onSuccess: (T) -> R, onFailure: (E) -> R): R {
    contract {
        callsInPlace(onFailure, AT_MOST_ONCE)
        callsInPlace(onSuccess, AT_MOST_ONCE)
    }

    return if (isSuccess()) onSuccess(value) else onFailure(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R, E> Result<T, E>.map(transform: (T) -> R): Result<R, E> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMap { value -> transform(value).asSuccess() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R, E> Result<T, E>.flatMap(transform: (T) -> Result<R, E>): Result<R, E> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R, E> Result<T, E>.andThen(block: (T) -> Result<R, E>): Result<R, E> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E, R> Result<T, E>.mapFailure(transform: (E) -> R): Result<T, R> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else transform(cause).asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.onSuccess(block: (T) -> Unit): Result<T, E> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isSuccess()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.onFailure(block: (E) -> Unit): Result<T, E> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isFailure()) block(it.cause) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.recover(block: (E) -> T): Result<T, E> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause).asSuccess()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.recoverWith(block: (E) -> Result<T, E>): Result<T, E> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.getOrForward(block: (Failure<E>) -> Nothing): T {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else block(this)
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.getOrNull(): T? {
    contract {
        returns(null) implies (this@getOrNull is Failure<E>)
        returnsNotNull() implies (this@getOrNull is Success<T>)
    }

    return if (isSuccess()) value else null
}

public infix fun <T, E> Result<T, E>.getOrElse(default: T): T = if (isSuccess()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.getOrElse(default: (E) -> T): T {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else default(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.orElse(default: () -> Result<T, E>): Result<T, E> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.orThrow(exceptionBuilder: (E) -> Throwable): T {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else throw exceptionBuilder(cause)
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.getFailureOrNull(): E? {
    contract {
        returns(null) implies (this@getFailureOrNull is Success<T>)
        returnsNotNull() implies (this@getFailureOrNull is Failure<E>)
    }

    return if (isFailure()) cause else null
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.forEach(block: (T) -> Unit) {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else Unit
}

public fun <T> Result<T, T>.merge(): T = fold(onSuccess = ::identity, onFailure = ::identity)

public fun <T, E> Iterable<Result<T, E>>.sequence(): Result<List<T>, E> {
    val items = buildList {
        val iter = this@sequence.iterator()
        while (iter.hasNext()) {
            val item = iter.next()
            if (item.isSuccess()) add(item.value) else return item
        }
    }
    return if (items.isNotEmpty()) items.asSuccess() else Success.asEmptyList
}

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@OverloadResolutionByLambdaReturnType
public inline fun <T, R, E> Iterable<T>.traverse(transform: (T) -> Result<R, E>): Result<List<R>, E> {
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
public inline fun <T, R, E, M : MutableList<R>> Iterable<T>.traverseTo(
    destination: M,
    transform: (T) -> Result<R, E>
): Result<M, E> {
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
public inline fun <T, E, K, V, M : MutableMap<K, V>> Iterable<T>.traverseTo(
    destination: M,
    transform: (T) -> Result<Pair<K, V>, E>
): Result<M, E> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, { it.first }, { it.second }, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R, E, K, M : MutableMap<K, R>> Iterable<T>.traverseTo(
    destination: M,
    keySelector: (R) -> K,
    transform: (T) -> Result<R, E>
): Result<M, E> {
    contract {
        callsInPlace(keySelector, InvocationKind.UNKNOWN)
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, keySelector, ::identity, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R, E, K, V, M : MutableMap<K, V>> Iterable<T>.traverseTo(
    destination: M,
    keySelector: (R) -> K,
    valueTransform: (R) -> V,
    transform: (T) -> Result<R, E>
): Result<M, E> {
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
public inline fun <T : Any, E : Any> Result<T?, E>.filterNotNull(failureBuilder: () -> E): Result<T, E> {
    contract {
        callsInPlace(failureBuilder, AT_MOST_ONCE)
    }
    return if (this.isFailure())
        this
    else if (this.value != null) {
        @Suppress("UNCHECKED_CAST")
        this as Result<T, E>
    } else
        failureBuilder().asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.filterOrElse(predicate: (T) -> Boolean, default: () -> E): Result<T, E> {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
        callsInPlace(default, AT_MOST_ONCE)
    }

    return if (isFailure() || predicate(this.value)) this else default().asFailure()
}
