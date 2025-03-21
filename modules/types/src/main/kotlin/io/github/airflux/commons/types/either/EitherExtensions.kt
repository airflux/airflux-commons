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

public fun <LeftT> LeftT.asLeft(): Either.Left<LeftT> = left(this)

public fun <RightT> RightT.asRight(): Either.Right<RightT> = right(this)

public fun <LeftT> left(value: LeftT): Either.Left<LeftT> = Either.left(value)

public fun <RightT> right(cause: RightT): Either.Right<RightT> = Either.right(cause)

@OptIn(ExperimentalContracts::class)
public fun <LeftT, RightT> Either<LeftT, RightT>.isLeft(): Boolean {
    contract {
        returns(true) implies (this@isLeft is Either.Left<LeftT>)
        returns(false) implies (this@isLeft is Either.Right<RightT>)
    }
    return this is Either.Left<LeftT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <LeftT, RightT> Either<LeftT, RightT>.isLeft(predicate: (LeftT) -> Boolean): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Either.Left<LeftT> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <LeftT, RightT> Either<LeftT, RightT>.isRight(): Boolean {
    contract {
        returns(false) implies (this@isRight is Either.Left<LeftT>)
        returns(true) implies (this@isRight is Either.Right<RightT>)
    }
    return this is Either.Right<RightT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <LeftT, RightT> Either<LeftT, RightT>.isRight(predicate: (RightT) -> Boolean): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Either.Right<RightT> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public inline fun <LeftT, LeftR, RightT> Either<LeftT, RightT>.fold(
    onLeft: (LeftT) -> LeftR,
    onRight: (RightT) -> LeftR
): LeftR {
    contract {
        callsInPlace(onRight, AT_MOST_ONCE)
        callsInPlace(onLeft, AT_MOST_ONCE)
    }

    return if (isLeft()) onLeft(value) else onRight(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, LeftR, RightT> Either<LeftT, RightT>.mapLeft(
    transform: (LeftT) -> LeftR
): Either<LeftR, RightT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMapLeft { value -> transform(value).asLeft() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, LeftR, RightT> Either<LeftT, RightT>.flatMapLeft(
    transform: (LeftT) -> Either<LeftR, RightT>
): Either<LeftR, RightT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isLeft()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, RightT, RightR> Either<LeftT, RightT>.mapRight(
    transform: (RightT) -> RightR
): Either<LeftT, RightR> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMapRight { value -> transform(value).asRight() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, RightT, RightR> Either<LeftT, RightT>.flatMapRight(
    transform: (RightT) -> Either<LeftT, RightR>
): Either<LeftT, RightR> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isRight()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline fun <LeftT, RightT> Either<LeftT, RightT>.onLeft(block: (LeftT) -> Unit): Either<LeftT, RightT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isLeft()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <LeftT, RightT> Either<LeftT, RightT>.onRight(block: (RightT) -> Unit): Either<LeftT, RightT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isRight()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public fun <LeftT, RightT> Either<LeftT, RightT>.getLeftOrNull(): LeftT? {
    contract {
        returnsNotNull() implies (this@getLeftOrNull is Either.Left<LeftT>)
        returns(null) implies (this@getLeftOrNull is Either.Right<RightT>)
    }

    return if (isLeft()) value else null
}

@OptIn(ExperimentalContracts::class)
public fun <LeftT, RightT> Either<LeftT, RightT>.getRightOrNull(): RightT? {
    contract {
        returns(null) implies (this@getRightOrNull is Either.Left<LeftT>)
        returnsNotNull() implies (this@getRightOrNull is Either.Right<RightT>)
    }

    return if (isRight()) value else null
}

public infix fun <LeftT, RightT> Either<LeftT, RightT>.getLeftOrElse(default: LeftT): LeftT =
    if (isLeft()) value else default

public infix fun <LeftT, RightT> Either<LeftT, RightT>.getRightOrElse(default: RightT): RightT =
    if (isRight()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, RightT> Either<LeftT, RightT>.getLeftOrElse(default: (RightT) -> LeftT): LeftT {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isLeft()) value else default(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, RightT> Either<LeftT, RightT>.getRightOrElse(default: (LeftT) -> RightT): RightT {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isRight()) value else default(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, RightT> Either<LeftT, RightT>.leftOrElse(
    default: () -> Either<LeftT, RightT>
): Either<LeftT, RightT> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isLeft()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, RightT> Either<LeftT, RightT>.rightOrElse(
    default: () -> Either<LeftT, RightT>
): Either<LeftT, RightT> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isRight()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, RightT> Either<LeftT, RightT>.leftOrThrow(builder: (RightT) -> Throwable): LeftT {
    contract {
        callsInPlace(builder, AT_MOST_ONCE)
    }
    return if (isLeft()) value else throw builder(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <LeftT, RightT> Either<LeftT, RightT>.rightOrThrow(builder: (LeftT) -> Throwable): RightT {
    contract {
        callsInPlace(builder, AT_MOST_ONCE)
    }
    return if (isRight()) value else throw builder(value)
}

public fun <T> Either<T, T>.merge(): T = fold(onLeft = ::identity, onRight = ::identity)
