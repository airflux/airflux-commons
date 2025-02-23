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

import io.github.airflux.commons.types.doRaise
import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.isSome
import io.github.airflux.commons.types.resultk.ResultK.Success.Companion.asEmptyList
import io.github.airflux.commons.types.resultk.ResultK.Success.Companion.asNull
import io.github.airflux.commons.types.resultk.ResultK.Success.Companion.asUnit
import io.github.airflux.commons.types.resultk.ResultK.Success.Companion.of
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public typealias Success<ValueT> = ResultK.Success<ValueT>
public typealias Failure<FailureT> = ResultK.Failure<FailureT>

public sealed interface ResultK<out ValueT, out FailureT : Any> {

    @Suppress("MemberNameEqualsClassName")
    public class Raise<in FailureT : Any> : io.github.airflux.commons.types.Raise<FailureT> {

        public operator fun <ValueT> ResultK<ValueT, FailureT>.component1(): ValueT = bind()

        public fun <ValueT> ResultK<ValueT, FailureT>.bind(): ValueT = if (isSuccess()) value else raise(cause)

        public fun <ValueT> ResultK<ValueT, FailureT>.raise() {
            if (isFailure()) raise(cause)
        }

        public fun Maybe<FailureT>.raise() {
            if (isSome()) raise(value)
        }

        public override fun raise(error: FailureT): Nothing {
            doRaise(error)
        }
    }

    public data class Success<out ValueT>(public val value: ValueT) : ResultK<ValueT, Nothing> {

        public companion object {

            public val asNull: Success<Nothing?> = Success(null)

            public val asTrue: Success<Boolean> = Success(true)

            public val asFalse: Success<Boolean> = Success(false)

            public val asUnit: Success<Unit> = Success(Unit)

            public val asEmptyList: Success<List<Nothing>> = Success(emptyList())

            @JvmStatic
            public fun of(value: Boolean): Success<Boolean> = if (value) asTrue else asFalse
        }
    }

    public data class Failure<out FailureT : Any>(public val cause: FailureT) : ResultK<Nothing, FailureT>

    public companion object {

        /**
         * Creates a new instance of the [Success] based on the given [value].
         *
         * The strategy is as follows:
         * 1. If the value is null, it returns the value of the [asNull] property.
         * 2. If the type of value is Unit, it returns the value of the [asUnit] property.
         * 3. If the value is a boolean, it returns the value of the [ResultK.Success.asTrue] or
         * [ResultK.Success.asFalse] property depending on the value.
         * 4. If the value is a list, it returns [asEmptyList] if the list is empty, or creates a new [Success] with
         * the list.
         * 5. Otherwise, it creates a new [Success] with the given value.
         *
         * @param value the value to be wrapped in a [Success].
         * @return a [Success] instance wrapping the given value.
         */
        @JvmStatic
        public fun <ValueT> success(value: ValueT): Success<ValueT> {
            val result = when {
                value == null -> asNull
                value is Unit -> asUnit
                value is Boolean -> of(value)
                value is List<*> && value.isEmpty() -> asEmptyList
                else -> Success(value)
            }

            @Suppress("UNCHECKED_CAST")
            return result as Success<ValueT>
        }

        @JvmStatic
        public fun <FailureT : Any> failure(cause: FailureT): Failure<FailureT> = Failure(cause)

        @OptIn(ExperimentalContracts::class)
        @JvmStatic
        public inline fun <SuccessT, FailureT : Any> catch(
            catch: (Throwable) -> FailureT,
            block: () -> SuccessT
        ): ResultK<SuccessT, FailureT> {
            contract {
                callsInPlace(catch, InvocationKind.AT_MOST_ONCE)
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
            }
            return catchWith(catch = catch, block = { block().asSuccess() })
        }

        @OptIn(ExperimentalContracts::class)
        @JvmStatic
        public inline fun <SuccessT, FailureT : Any> catchWith(
            catch: (Throwable) -> FailureT,
            block: () -> ResultK<SuccessT, FailureT>
        ): ResultK<SuccessT, FailureT> {
            contract {
                callsInPlace(catch, InvocationKind.AT_MOST_ONCE)
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
            }
            return io.github.airflux.commons.types.catch(
                catch = { failure(catch(it)) },
                block = { block() }
            )
        }
    }
}
