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

package io.github.airflux.commons.types.maybe

import io.github.airflux.commons.types.fail.fold
import io.github.airflux.commons.types.fail.isError
import io.github.airflux.commons.types.fail.isException
import io.github.airflux.commons.types.fail.map2
import io.github.airflux.commons.types.fail.mapError
import io.github.airflux.commons.types.fail.mapException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT, ExceptionT, ValueR> MaybeBiFailure<ErrorT, ExceptionT>.fold(
    onNone: () -> ValueR,
    onError: (ErrorT) -> ValueR,
    onException: (ExceptionT) -> ValueR
): ValueR
    where ErrorT : Any,
          ExceptionT : Any,
          ValueR : Any {
    contract {
        callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onException, InvocationKind.AT_MOST_ONCE)
    }

    return this.fold(
        onNone = onNone,
        onSome = { failure ->
            failure.fold(
                onError = { value -> onError(value) },
                onException = { value -> onException(value) }
            )
        }
    )
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT, ErrorR, ExceptionT, ExceptionR> MaybeBiFailure<ErrorT, ExceptionT>.map(
    onError: (ErrorT) -> ErrorR,
    onException: (ExceptionT) -> ExceptionR
): MaybeBiFailure<ErrorR, ExceptionR>
    where ErrorT : Any,
          ErrorR : Any,
          ExceptionT : Any,
          ExceptionR : Any {
    contract {
        callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onException, InvocationKind.AT_MOST_ONCE)
    }
    return this.map {
        it.map2(
            onError = { value -> onError(value) },
            onException = { value -> onException(value) }
        )
    }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT, ErrorR, ExceptionT> MaybeBiFailure<ErrorT, ExceptionT>.mapError(
    transform: (ErrorT) -> ErrorR
): MaybeBiFailure<ErrorR, ExceptionT>
    where ErrorT : Any,
          ErrorR : Any,
          ExceptionT : Any {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return this.map { failure -> failure.mapError { error -> transform(error) } }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ErrorT, ExceptionT, ExceptionR> MaybeBiFailure<ErrorT, ExceptionT>.mapException(
    transform: (ExceptionT) -> ExceptionR
): MaybeBiFailure<ErrorT, ExceptionR>
    where ErrorT : Any,
          ExceptionT : Any,
          ExceptionR : Any {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return this.map { failure -> failure.mapException { error -> transform(error) } }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT, ExceptionT> MaybeBiFailure<ErrorT, ExceptionT>.onError(
    block: (ErrorT) -> Unit
): MaybeBiFailure<ErrorT, ExceptionT>
    where ErrorT : Any,
          ExceptionT : Any {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isSome() && it.value.isError()) block(it.value.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT, ExceptionT> MaybeBiFailure<ErrorT, ExceptionT>.onException(
    block: (ExceptionT) -> Unit
): MaybeBiFailure<ErrorT, ExceptionT>
    where ErrorT : Any,
          ExceptionT : Any {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isSome() && it.value.isException()) block(it.value.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT, ExceptionT, ValueR> MaybeBiFailure<ErrorT, ExceptionT>.recover(
    onError: (ErrorT) -> ValueR,
    onException: (ExceptionT) -> ValueR,
): Maybe<ValueR>
    where ErrorT : Any,
          ExceptionT : Any,
          ValueR : Any {
    contract {
        callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onException, InvocationKind.AT_MOST_ONCE)
    }
    return if (this.isSome()) {
        val fail = this.value
        if (fail.isError())
            onError(fail.value).asSome()
        else
            onException(fail.value).asSome()
    } else
        this
}
