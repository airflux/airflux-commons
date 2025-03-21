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

package io.github.airflux.commons.types

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException

public interface Raise<in ErrorT : Any> {
    public fun raise(error: ErrorT): Nothing
}

@OptIn(ExperimentalContracts::class)
public inline fun <ErrorT : Any, RaiseT : Raise<ErrorT>> RaiseT.ensure(condition: Boolean, error: () -> ErrorT) {
    contract {
        callsInPlace(error, AT_MOST_ONCE)
    }
    if (!condition) this.raise(error())
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT : Any, ErrorT : Any, RaiseT : Raise<ErrorT>> RaiseT.ensureNotNull(
    value: ValueT?,
    error: () -> ErrorT
): ValueT {
    contract {
        callsInPlace(error, AT_MOST_ONCE)
        returns() implies (value != null)
    }
    return value ?: this.raise(error())
}

@Suppress("FunctionNaming")
@OptIn(ExperimentalContracts::class)
public inline fun <ResultT, ErrorT : Any, RaiseT : Raise<ErrorT>> withRaise(
    raise: RaiseT,
    wrap: (ErrorT) -> ResultT,
    block: RaiseT.() -> ResultT
): ResultT {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return try {
        block(raise)
    } catch (expected: Exception) {
        wrap(expected.failureOrRethrow<ErrorT>(raise))
    }
}

public fun <ErrorT : Any, RaiseT : Raise<ErrorT>> RaiseT.doRaise(error: ErrorT): Nothing =
    throw RaiseException(error, this)

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT> catch(catch: (Throwable) -> ValueT, block: () -> ValueT): ValueT {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
        callsInPlace(catch, AT_MOST_ONCE)
    }
    return try {
        block()
    } catch (expected: Throwable) {
        if (expected.isFatal())
            throw expected
        else
            catch(expected)
    }
}

internal class RaiseException(val failure: Any, val raise: Raise<*>) : CancellationException()

@PublishedApi
internal fun <ErrorT : Any> Exception.failureOrRethrow(raise: Raise<*>): ErrorT =
    if (this is RaiseException && this.raise === raise)
        @Suppress("UNCHECKED_CAST")
        failure as ErrorT
    else
        throw this
