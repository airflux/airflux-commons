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

@file:Suppress("TooManyFunctions")

package io.github.airflux.commons.types.either

public typealias Left<LeftT> = Either.Left<LeftT>
public typealias Right<RightT> = Either.Right<RightT>

public sealed interface Either<out LeftT, out RightT> {

    public data class Left<out LeftT>(public val value: LeftT) : Either<LeftT, Nothing> {

        public companion object {

            public val asNull: Left<Nothing?> = Left(null)

            public val asTrue: Left<Boolean> = Left(true)

            public val asFalse: Left<Boolean> = Left(false)

            public val asUnit: Left<Unit> = Left(Unit)

            public val asEmptyList: Left<List<Nothing>> = Left(emptyList())

            public fun of(value: Boolean): Left<Boolean> = if (value) asTrue else asFalse
        }
    }

    public data class Right<out RightT>(public val value: RightT) : Either<Nothing, RightT> {

        public companion object {

            public val asNull: Right<Nothing?> = Right(null)

            public val asTrue: Right<Boolean> = Right(true)

            public val asFalse: Right<Boolean> = Right(false)

            public val asUnit: Right<Unit> = Right(Unit)

            public val asEmptyList: Right<List<Nothing>> = Right(emptyList())

            public fun of(value: Boolean): Right<Boolean> = if (value) asTrue else asFalse
        }
    }

    public companion object {

        public fun <LeftT> left(value: LeftT): Left<LeftT> = Left(value)

        public fun <RightT> right(cause: RightT): Right<RightT> = Right(cause)
    }
}
