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

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public typealias Some<ValueT> = Maybe.Some<ValueT>
public typealias None = Maybe.None

public sealed interface Maybe<out ValueT : Any> {

    public data class Some<out ValueT : Any>(public val value: ValueT) : Maybe<ValueT>

    public data object None : Maybe<Nothing>

    public companion object {

        public fun none(): None = None

        public fun <ValueT> some(value: ValueT): Maybe<ValueT & Any> = if (value == null) None else Some(value)

        @OptIn(ExperimentalContracts::class)
        public inline fun <ErrorT : Any> catch(
            catch: (Throwable) -> ErrorT,
            block: () -> Unit
        ): Maybe<ErrorT> {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
                callsInPlace(catch, InvocationKind.AT_MOST_ONCE)
            }
            return try {
                block()
                none()
            } catch (expected: Throwable) {
                some(catch(expected))
            }
        }

        @OptIn(ExperimentalContracts::class)
        public inline fun <ErrorT : Any> catchWith(
            catch: (Throwable) -> ErrorT,
            block: () -> Maybe<ErrorT>
        ): Maybe<ErrorT> {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
                callsInPlace(catch, InvocationKind.AT_MOST_ONCE)
            }
            return try {
                block()
            } catch (expected: Throwable) {
                some(catch(expected))
            }
        }
    }
}

public fun <ValueT> ValueT.asSome(): Maybe<ValueT & Any> = some(this)

public fun <ValueT> some(value: ValueT): Maybe<ValueT & Any> = Maybe.some(value)

public fun none(): None = Maybe.none()
