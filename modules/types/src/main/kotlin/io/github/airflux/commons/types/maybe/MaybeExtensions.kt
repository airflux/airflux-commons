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

package io.github.airflux.commons.types.maybe

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.resultk.ResultK
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public fun <ValueT : Any> Maybe<ValueT>.isSome(): Boolean {
    contract {
        returns(true) implies (this@isSome is Some<ValueT>)
        returns(false) implies (this@isSome is None)
    }
    return this is Some<ValueT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT : Any> Maybe<ValueT>.isSome(predicate: (ValueT) -> Boolean): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Some<ValueT> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT : Any> Maybe<ValueT>.isNone(): Boolean {
    contract {
        returns(false) implies (this@isNone is Some<ValueT>)
        returns(true) implies (this@isNone is None)
    }
    return this is None
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT : Any, ResultT> Maybe<ValueT>.fold(
    onSome: (ValueT) -> ResultT,
    onNone: () -> ResultT
): ResultT {
    contract {
        callsInPlace(onNone, AT_MOST_ONCE)
        callsInPlace(onSome, AT_MOST_ONCE)
    }

    return if (isSome()) onSome(value) else onNone()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT : Any, ResultT : Any> Maybe<ValueT>.map(
    transform: (ValueT) -> ResultT
): Maybe<ResultT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMap { value -> transform(value).asSome() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT : Any, ResultT : Any> Maybe<ValueT>.flatMap(
    transform: (ValueT) -> Maybe<ResultT>
): Maybe<ResultT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSome()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT : Any, ResultT : Any> Maybe<ValueT>.andThen(
    block: (ValueT) -> Maybe<ResultT>
): Maybe<ResultT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSome()) block(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT : Any> Maybe<ValueT>.onSome(
    block: (ValueT) -> Unit
): Maybe<ValueT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isSome()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT : Any> Maybe<ValueT>.onNone(
    block: () -> Unit
): Maybe<ValueT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isNone()) block() }
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT : Any> Maybe<ValueT>.getOrNull(): ValueT? {
    contract {
        returns(null) implies (this@getOrNull is None)
        returnsNotNull() implies (this@getOrNull is Some<ValueT>)
    }

    return if (isSome()) value else null
}

public infix fun <ValueT : Any> Maybe<ValueT>.getOrElse(default: ValueT): ValueT =
    if (isSome()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT : Any> Maybe<ValueT>.getOrElse(
    default: () -> ValueT
): ValueT {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSome()) value else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT : Any> Maybe<ValueT>.orElse(
    default: () -> Maybe<ValueT>
): Maybe<ValueT> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSome()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT : Any> Maybe<ValueT>.orThrow(
    exceptionBuilder: () -> Throwable
): ValueT {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isSome()) value else throw exceptionBuilder()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT : Any> Maybe<ValueT>.forEach(block: (ValueT) -> Unit) {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSome()) block(value) else Unit
}

public inline fun <ValueT : Any> Maybe<ValueT>.toError(onNone: () -> ValueT): Fail<ValueT, Nothing> =
    if (isNone())
        Fail.error(onNone())
    else
        Fail.error(this.value)

public inline fun <ValueT : Any> Maybe<ValueT>.toException(onNone: () -> ValueT): Fail<Nothing, ValueT> =
    if (isNone())
        Fail.exception(onNone())
    else
        Fail.exception(this.value)

public inline fun <ValueT : Any> Maybe<ValueT>.toFailure(onNone: () -> ValueT): ResultK<Nothing, ValueT> =
    if (isNone())
        ResultK.failure(onNone())
    else
        ResultK.failure(this.value)
