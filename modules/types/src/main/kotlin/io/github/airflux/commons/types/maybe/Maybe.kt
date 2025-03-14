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

import io.github.airflux.commons.types.DefaultRaise
import io.github.airflux.commons.types.doRaise
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public typealias Some<ValueT> = Maybe.Some<ValueT>
public typealias None = Maybe.None

public sealed interface Maybe<out ValueT : Any> {

    public class Raise<in FailureT : Any> : DefaultRaise<FailureT> {

        public override fun raise(error: FailureT): Nothing {
            doRaise(error)
        }
    }

    public data class Some<out ValueT : Any>(public val value: ValueT) : Maybe<ValueT>

    public data object None : Maybe<Nothing>

    public companion object {

        @JvmStatic
        public fun none(): None = None

        @JvmStatic
        public fun <ValueT> some(value: ValueT): Maybe<ValueT & Any> = if (value == null) None else Some(value)

        @OptIn(ExperimentalContracts::class)
        @JvmStatic
        public inline fun <ErrorT : Any> catch(
            catch: (Throwable) -> ErrorT,
            block: () -> Unit
        ): Maybe<ErrorT> {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
                callsInPlace(catch, InvocationKind.AT_MOST_ONCE)
            }
            return io.github.airflux.commons.types.catch(
                catch = { some(catch(it)) },
                block = {
                    block()
                    none()
                }
            )
        }

        @OptIn(ExperimentalContracts::class)
        @JvmStatic
        public inline fun <ErrorT : Any> catchWith(
            catch: (Throwable) -> ErrorT,
            block: () -> Maybe<ErrorT>
        ): Maybe<ErrorT> {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
                callsInPlace(catch, InvocationKind.AT_MOST_ONCE)
            }
            return io.github.airflux.commons.types.catch(
                catch = { some(catch(it)) },
                block = { block() }
            )
        }
    }
}

public fun <ValueT> ValueT.asSome(): Maybe<ValueT & Any> = some(this)

public fun <ValueT> some(value: ValueT): Maybe<ValueT & Any> = Maybe.some(value)

public fun none(): None = Maybe.none()
