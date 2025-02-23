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

package io.github.airflux.commons.types.fail

public typealias Error<ErrorT> = Fail.Error<ErrorT>
public typealias Exception<ExceptionT> = Fail.Exception<ExceptionT>

public sealed interface Fail<out ErrorT : Any, out ExceptionT : Any> {

    /**
     * Represents a domain (business) error.
     */
    public data class Error<out ErrorT : Any>(public val value: ErrorT) : Fail<ErrorT, Nothing>

    /**
     * Represents a technical error.
     */
    public data class Exception<out ExceptionT : Any>(public val value: ExceptionT) : Fail<Nothing, ExceptionT>

    public companion object {

        @JvmStatic
        public fun <ErrorT : Any> error(value: ErrorT): Error<ErrorT> = Error(value)

        @JvmStatic
        public fun <ExceptionT : Any> exception(value: ExceptionT): Exception<ExceptionT> = Exception(value)
    }
}
