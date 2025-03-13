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

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.isSome
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.isSuccess

internal interface DefaultRaise<in FailureT : Any> : Raise<FailureT> {

    operator fun <ValueT> ResultK<ValueT, FailureT>.component1(): ValueT = bind()

    fun <ValueT> ResultK<ValueT, FailureT>.bind(): ValueT = if (isSuccess()) value else raise(cause)

    fun <ValueT> ResultK<ValueT, FailureT>.raise() {
        if (isFailure()) raise(cause)
    }

    fun Maybe<FailureT>.raise() {
        if (isSome()) raise(value)
    }
}
