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

import io.github.airflux.commons.types.BasicRaise
import io.github.airflux.commons.types.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

public inline fun <T> doTry(block: () -> T): Result<T, Throwable> = try {
    block().success()
} catch (expected: Throwable) {
    expected.failure()
}

@JvmName("asSuccess")
public fun <T> T.success(): Result.Success<T> = success(this)

@JvmName("asFailure")
public fun <E> E.failure(): Result.Failure<E> = failure(this)

public fun <T> success(value: T): Result.Success<T> = Result.Success(value)

public fun <E> failure(cause: E): Result.Failure<E> = Result.Failure(cause)

public sealed class Result<out T, out E> {

    public data class Success<out T>(public val value: T) : Result<T, Nothing>()

    public data class Failure<out E>(public val cause: E) : Result<Nothing, E>()

    public interface Raise<E> : BasicRaise {
        public fun <T> Result<T, E>.bind(): T
        public operator fun <T> Result<T, E>.component1(): T = bind()
        public fun raise(cause: E): Nothing
        public fun <T> Result<T, E>.raise()
    }

    public companion object {

        public val asNull: Success<Nothing?> = Success(null)

        public val asTrue: Success<Boolean> = Success(true)

        public val asFalse: Success<Boolean> = Success(false)

        public val asUnit: Success<Unit> = Success(Unit)

        public val asEmptyList: Success<List<Nothing>> = Success(emptyList())

        public fun of(value: Boolean): Success<Boolean> = if (value) asTrue else asFalse
    }
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Result.Success<T>)
        returns(false) implies (this@isSuccess is Result.Failure<E>)
    }
    return this is Result.Success<T>
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.isSuccess(predicate: (T) -> Boolean): Boolean {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }
    return this is Result.Success<T> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.isFailure(): Boolean {
    contract {
        returns(false) implies (this@isFailure is Result.Success<T>)
        returns(true) implies (this@isFailure is Result.Failure<E>)
    }
    return this is Result.Failure<E>
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.isFailure(predicate: (E) -> Boolean): Boolean {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }
    return this is Result.Failure<E> && predicate(cause)
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R, E> Result<T, E>.fold(onSuccess: (T) -> R, onFailure: (E) -> R): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess()) onSuccess(value) else onFailure(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R, E> Result<T, E>.map(transform: (T) -> R): Result<R, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return flatMap { value -> transform(value).success() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R, E> Result<T, E>.flatMap(transform: (T) -> Result<R, E>): Result<R, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R, E> Result<T, E>.andThen(block: (T) -> Result<R, E>): Result<R, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E, R> Result<T, E>.mapFailure(transform: (E) -> R): Result<T, R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else transform(cause).failure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.onSuccess(block: (T) -> Unit): Result<T, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isSuccess()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.onFailure(block: (E) -> Unit): Result<T, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isFailure()) block(it.cause) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.recover(block: (E) -> T): Result<T, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause).success()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.recoverWith(block: (E) -> Result<T, E>): Result<T, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.getOrForward(block: (Result.Failure<E>) -> Nothing): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else block(this)
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.getOrNull(): T? {
    contract {
        returns(null) implies (this@getOrNull is Result.Failure<E>)
        returnsNotNull() implies (this@getOrNull is Result.Success<T>)
    }

    return if (isSuccess()) value else null
}

public infix fun <T, E> Result<T, E>.getOrElse(default: T): T = if (isSuccess()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.getOrElse(default: (E) -> T): T {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else default(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.orElse(default: () -> Result<T, E>): Result<T, E> {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.orThrow(exceptionBuilder: (E) -> Throwable): T {
    contract {
        callsInPlace(exceptionBuilder, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else throw exceptionBuilder(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.forEach(block: (T) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
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
    return if (items.isNotEmpty()) items.success() else Result.asEmptyList
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
            if (item.isSuccess()) add(item.value) else return item
        }
    }
    return if (items.isNotEmpty()) items.success() else Result.asEmptyList
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T?, E>.failureIfNullValue(failureBuilder: () -> E): Result<T, E> {
    contract {
        callsInPlace(failureBuilder, InvocationKind.AT_MOST_ONCE)
    }
    return if (this.isFailure())
        this
    else if (this.value != null) {
        @Suppress("UNCHECKED_CAST")
        this as Result<T, E>
    } else
        failureBuilder().failure()
}
