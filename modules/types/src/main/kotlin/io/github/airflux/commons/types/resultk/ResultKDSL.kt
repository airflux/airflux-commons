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

import io.github.airflux.commons.types.failureOrRethrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Suppress("FunctionNaming")
@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, FailureT> result(
    block: ResultK.Raise<FailureT>.() -> SuccessT
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return resultWith { block().asSuccess() }
}

@Suppress("FunctionNaming")
@OptIn(ExperimentalContracts::class)
public inline fun <SuccessT, FailureT> resultWith(
    block: ResultK.Raise<FailureT>.() -> ResultK<SuccessT, FailureT>
): ResultK<SuccessT, FailureT> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val raise = ResultK.Raise<FailureT>()
    return try {
        block(raise)
    } catch (expected: Exception) {
        expected.failureOrRethrow(raise)
    }
}
