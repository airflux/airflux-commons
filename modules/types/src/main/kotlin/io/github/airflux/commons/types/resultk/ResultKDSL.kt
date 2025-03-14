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

import io.github.airflux.commons.types.withRaise
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Suppress("FunctionNaming")
@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailureT : Any> result(
    block: ResultK.Raise<FailureT>.() -> ValueT
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return resultWith { block().asSuccess() }
}

@Suppress("FunctionNaming")
@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailureT : Any> resultWith(
    block: ResultK.Raise<FailureT>.() -> ResultK<ValueT, FailureT>
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return withRaise(ResultK.Raise<FailureT>(), { it.asFailure() }, block)
}
