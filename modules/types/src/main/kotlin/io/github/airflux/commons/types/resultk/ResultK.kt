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
import io.github.airflux.commons.types.resultk.ResultK.Success.Companion.asEmptyList
import io.github.airflux.commons.types.resultk.ResultK.Success.Companion.asNull
import io.github.airflux.commons.types.resultk.ResultK.Success.Companion.asUnit
import io.github.airflux.commons.types.resultk.ResultK.Success.Companion.of

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

            public fun of(value: Boolean): Success<Boolean> = if (value) asTrue else asFalse
        }
    }

    public data class Failure<out FailureT : Any>(public val cause: FailureT) : ResultK<Nothing, FailureT>

    public companion object {

        @Suppress("UNCHECKED_CAST")
        public fun <ValueT> success(value: ValueT): Success<ValueT> =
            when (value) {
                null -> asNull
                is Unit -> asUnit
                is Boolean -> of(value)
                is List<*> -> if (value.isEmpty()) asEmptyList else Success(value)
                else -> Success(value)
            } as Success<ValueT>

        public fun <FailureT : Any> failure(cause: FailureT): Failure<FailureT> = Failure(cause)
    }
}
