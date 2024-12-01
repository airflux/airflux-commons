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

public abstract class BasicRaise<in E> {

    public abstract fun raise(cause: E): Nothing

    @OptIn(ExperimentalContracts::class)
    public inline fun ensure(condition: Boolean, raise: () -> E) {
        contract {
            callsInPlace(raise, AT_MOST_ONCE)
            returns() implies condition
        }
        return if (condition) Unit else raise(raise())
    }

    @OptIn(ExperimentalContracts::class)
    public inline fun <T : Any> ensureNotNull(value: T?, raise: () -> E): T {
        contract {
            callsInPlace(raise, AT_MOST_ONCE)
            returns() implies (value != null)
        }
        return value ?: raise(raise())
    }
}

internal class RaiseException(val failure: Any, val raise: BasicRaise<*>) : CancellationException()

@PublishedApi
internal fun <T> CancellationException.failureOrRethrow(raise: BasicRaise<*>): T =
    if (this is RaiseException && this.raise === raise)
        @Suppress("UNCHECKED_CAST")
        failure as T
    else
        throw this
