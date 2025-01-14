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

package io.github.airflux.commons.types.either

import io.github.airflux.commons.types.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

public typealias Left<L> = Either.Left<L>
public typealias Right<L> = Either.Right<L>

public sealed interface Either<out L, out R> {

    public data class Left<out L>(public val value: L) : Either<L, Nothing> {

        public companion object {

            public val asNull: Left<Nothing?> = Left(null)

            public val asTrue: Left<Boolean> = Left(true)

            public val asFalse: Left<Boolean> = Left(false)

            public val asUnit: Left<Unit> = Left(Unit)

            public val asEmptyList: Left<List<Nothing>> = Left(emptyList())

            public fun of(value: Boolean): Left<Boolean> = if (value) asTrue else asFalse
        }
    }

    public data class Right<out R>(public val value: R) : Either<Nothing, R> {

        public companion object {

            public val asNull: Right<Nothing?> = Right(null)

            public val asTrue: Right<Boolean> = Right(true)

            public val asFalse: Right<Boolean> = Right(false)

            public val asUnit: Right<Unit> = Right(Unit)

            public val asEmptyList: Right<List<Nothing>> = Right(emptyList())

            public fun of(value: Boolean): Right<Boolean> = if (value) asTrue else asFalse
        }
    }

    public companion object {

        public fun <L> left(value: L): Left<L> = Left(value)

        public fun <R> right(cause: R): Right<R> = Right(cause)
    }
}

public fun <L> L.asLeft(): Left<L> = Either.left(this)

public fun <R> R.asRight(): Right<R> = Either.right(this)

public fun <L> left(value: L): Left<L> = Either.left(value)

public fun <R> right(cause: R): Right<R> = Either.right(cause)

@OptIn(ExperimentalContracts::class)
public fun <L, R> Either<L, R>.isLeft(): Boolean {
    contract {
        returns(true) implies (this@isLeft is Left<L>)
        returns(false) implies (this@isLeft is Right<R>)
    }
    return this is Left<L>
}

@OptIn(ExperimentalContracts::class)
public inline fun <L, R> Either<L, R>.isLeft(predicate: (L) -> Boolean): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Left<L> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <L, R> Either<L, R>.isRight(): Boolean {
    contract {
        returns(false) implies (this@isRight is Left<L>)
        returns(true) implies (this@isRight is Right<R>)
    }
    return this is Right<R>
}

@OptIn(ExperimentalContracts::class)
public inline fun <L, R> Either<L, R>.isRight(predicate: (R) -> Boolean): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Right<R> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public inline fun <L, V, R> Either<L, R>.fold(onLeft: (L) -> V, onRight: (R) -> V): V {
    contract {
        callsInPlace(onRight, AT_MOST_ONCE)
        callsInPlace(onLeft, AT_MOST_ONCE)
    }

    return if (isLeft()) onLeft(value) else onRight(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, V, R> Either<L, R>.mapLeft(transform: (L) -> V): Either<V, R> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMapLeft { value -> transform(value).asLeft() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, V, R> Either<L, R>.flatMapLeft(transform: (L) -> Either<V, R>): Either<V, R> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isLeft()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, R, V> Either<L, R>.mapRight(transform: (R) -> V): Either<L, V> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMapRight { value -> transform(value).asRight() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, R, V> Either<L, R>.flatMapRight(transform: (R) -> Either<L, V>): Either<L, V> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isRight()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline fun <L, R> Either<L, R>.onLeft(block: (L) -> Unit): Either<L, R> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isLeft()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <L, R> Either<L, R>.onRight(block: (R) -> Unit): Either<L, R> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isRight()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public fun <L, R> Either<L, R>.getLeftOrNull(): L? {
    contract {
        returnsNotNull() implies (this@getLeftOrNull is Left<L>)
        returns(null) implies (this@getLeftOrNull is Right<R>)
    }

    return if (isLeft()) value else null
}

@OptIn(ExperimentalContracts::class)
public fun <L, R> Either<L, R>.getRightOrNull(): R? {
    contract {
        returns(null) implies (this@getRightOrNull is Left<L>)
        returnsNotNull() implies (this@getRightOrNull is Right<R>)
    }

    return if (isRight()) value else null
}

public infix fun <L, R> Either<L, R>.getLeftOrElse(default: L): L = if (isLeft()) value else default

public infix fun <L, R> Either<L, R>.getRightOrElse(default: R): R = if (isRight()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, R> Either<L, R>.getLeftOrElse(default: (R) -> L): L {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isLeft()) value else default(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, R> Either<L, R>.getRightOrElse(default: (L) -> R): R {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isRight()) value else default(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, R> Either<L, R>.leftOrElse(default: () -> Either<L, R>): Either<L, R> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isLeft()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, R> Either<L, R>.rightOrElse(default: () -> Either<L, R>): Either<L, R> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isRight()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, R> Either<L, R>.leftOrThrow(exceptionBuilder: (R) -> Throwable): L {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isLeft()) value else throw exceptionBuilder(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <L, R> Either<L, R>.rightOrThrow(exceptionBuilder: (L) -> Throwable): R {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isRight()) value else throw exceptionBuilder(value)
}

public fun <T> Either<T, T>.merge(): T = fold(onLeft = ::identity, onRight = ::identity)
