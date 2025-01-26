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

package io.github.airflux.commons.types.fail

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

public fun <ErrorT : Any> ErrorT.asError(): Fail.Error<ErrorT> = Fail.error(this)

public fun <ExceptionT : Any> ExceptionT.asException(): Fail.Exception<ExceptionT> = Fail.exception(this)

public fun <ErrorT : Any> error(value: ErrorT): Fail.Error<ErrorT> = Fail.error(value)

public fun <ExceptionT : Any> exception(value: ExceptionT): Fail.Exception<ExceptionT> = Fail.exception(value)

@OptIn(ExperimentalContracts::class)
public fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.isError(): Boolean {
    contract {
        returns(true) implies (this@isError is Fail.Error<ErrorT>)
        returns(false) implies (this@isError is Fail.Exception<ExceptionT>)
    }
    return this is Fail.Error<ErrorT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.isError(
    predicate: (ErrorT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Fail.Error<ErrorT> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.isException(): Boolean {
    contract {
        returns(false) implies (this@isException is Fail.Error<ErrorT>)
        returns(true) implies (this@isException is Fail.Exception<ExceptionT>)
    }
    return this is Fail.Exception<ExceptionT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.isException(
    predicate: (ExceptionT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Fail.Exception<ExceptionT> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT : Any, ExceptionT : Any, FailureT> Fail<ErrorT, ExceptionT>.fold(
    onError: (ErrorT) -> FailureT,
    onException: (ExceptionT) -> FailureT
): FailureT {
    contract {
        callsInPlace(onError, AT_MOST_ONCE)
        callsInPlace(onException, AT_MOST_ONCE)
    }
    return if (isError()) onError(value) else onException(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ErrorR : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.mapError(
    transform: (ErrorT) -> ErrorR
): Fail<ErrorR, ExceptionT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMapError { value -> transform(value).asError() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ErrorR : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.flatMapError(
    transform: (ErrorT) -> Fail<ErrorR, ExceptionT>
): Fail<ErrorR, ExceptionT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isError()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ExceptionT : Any, ExceptionR : Any> Fail<ErrorT, ExceptionT>.mapException(
    transform: (ExceptionT) -> ExceptionR
): Fail<ErrorT, ExceptionR> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMapException { value -> transform(value).asException() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ExceptionT : Any, ExceptionR : Any> Fail<ErrorT, ExceptionT>.flatMapException(
    transform: (ExceptionT) -> Fail<ErrorT, ExceptionR>
): Fail<ErrorT, ExceptionR> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isException()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.onError(
    block: (ErrorT) -> Unit
): Fail<ErrorT, ExceptionT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isError()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.onException(
    block: (ExceptionT) -> Unit
): Fail<ErrorT, ExceptionT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isException()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.getErrorOrNull(): ErrorT? {
    contract {
        returnsNotNull() implies (this@getErrorOrNull is Fail.Error<ErrorT>)
        returns(null) implies (this@getErrorOrNull is Fail.Exception<ExceptionT>)
    }
    return if (isError()) value else null
}

@OptIn(ExperimentalContracts::class)
public fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.getExceptionOrNull(): ExceptionT? {
    contract {
        returns(null) implies (this@getExceptionOrNull is Fail.Error<ErrorT>)
        returnsNotNull() implies (this@getExceptionOrNull is Fail.Exception<ExceptionT>)
    }
    return if (isException()) value else null
}

public infix fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.getErrorOrElse(default: ErrorT): ErrorT =
    if (isError()) value else default

public infix fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.getExceptionOrElse(
    default: ExceptionT
): ExceptionT =
    if (isException()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.getErrorOrElse(
    default: (ExceptionT) -> ErrorT
): ErrorT {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isError()) value else default(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.getExceptionOrElse(
    default: (ErrorT) -> ExceptionT
): ExceptionT {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isException()) value else default(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.errorOrElse(
    default: () -> Fail<ErrorT, ExceptionT>
): Fail<ErrorT, ExceptionT> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isError()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.exceptionOrElse(
    default: () -> Fail<ErrorT, ExceptionT>
): Fail<ErrorT, ExceptionT> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isException()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.errorOrThrow(
    exceptionBuilder: (ExceptionT) -> Throwable
): ErrorT {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isError()) value else throw exceptionBuilder(value)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT : Any, ExceptionT : Any> Fail<ErrorT, ExceptionT>.exceptionOrThrow(
    exceptionBuilder: (ErrorT) -> Throwable
): ExceptionT {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isException()) value else throw exceptionBuilder(value)
}
