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

package io.github.airflux.commons.types.resultk

import io.github.airflux.commons.types.fail.fold
import io.github.airflux.commons.types.fail.isError
import io.github.airflux.commons.types.fail.isException
import io.github.airflux.commons.types.fail.mapError
import io.github.airflux.commons.types.fail.mapException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, ErrorT, ExceptionT, ResultT> BiFailureResultK<ValueT, ErrorT, ExceptionT>.fold(
    onSuccess: (ValueT) -> ResultT,
    onError: (ErrorT) -> ResultT,
    onException: (ExceptionT) -> ResultT
): ResultT
    where ErrorT : Any,
          ExceptionT : Any {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onException, InvocationKind.AT_MOST_ONCE)
    }

    return fold(
        onSuccess = { value -> onSuccess(value) },
        onFailure = { failure ->
            failure.fold(
                onError = { value -> onError(value) },
                onException = { value -> onException(value) }
            )
        }
    )
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, ErrorT, ErrorR, ExceptionT> BiFailureResultK<ValueT, ErrorT, ExceptionT>.mapError(
    transform: (ErrorT) -> ErrorR
): BiFailureResultK<ValueT, ErrorR, ExceptionT>
    where ErrorT : Any,
          ErrorR : Any,
          ExceptionT : Any {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return this.mapFailure { failure ->
        failure.mapError { error -> transform(error) }
    }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, ErrorT, ExceptionT, ExceptionR> BiFailureResultK<ValueT, ErrorT, ExceptionT>.mapException(
    transform: (ExceptionT) -> ExceptionR
): BiFailureResultK<ValueT, ErrorT, ExceptionR>
    where ErrorT : Any,
          ExceptionT : Any,
          ExceptionR : Any {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return this.mapFailure { failure ->
        failure.mapException { error -> transform(error) }
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, ErrorT, ExceptionT> BiFailureResultK<ValueT, ErrorT, ExceptionT>.onError(
    block: (ErrorT) -> Unit
): BiFailureResultK<ValueT, ErrorT, ExceptionT>
    where ErrorT : Any,
          ExceptionT : Any {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isFailure() && it.cause.isError()) block(it.cause.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, ErrorT, ExceptionT> BiFailureResultK<ValueT, ErrorT, ExceptionT>.onException(
    block: (ExceptionT) -> Unit
): BiFailureResultK<ValueT, ErrorT, ExceptionT>
    where ErrorT : Any,
          ExceptionT : Any {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isFailure() && it.cause.isException()) block(it.cause.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, ErrorT, ExceptionT> BiFailureResultK<ValueT, ErrorT, ExceptionT>.recover(
    onError: (ErrorT) -> ValueT,
    onException: (ExceptionT) -> ValueT,
): BiFailureResultK<ValueT, ErrorT, ExceptionT>
    where ErrorT : Any,
          ExceptionT : Any {
    contract {
        callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onException, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess())
        this
    else if (this.cause.isError())
        onError(cause.value).asSuccess()
    else
        onException(cause.value).asSuccess()
}
