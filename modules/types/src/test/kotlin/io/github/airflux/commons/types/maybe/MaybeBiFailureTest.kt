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

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.maybe.matcher.shouldBeError
import io.github.airflux.commons.types.maybe.matcher.shouldBeException
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.kotest.core.spec.style.FreeSpec

@OptIn(AirfluxTypesExperimental::class)
internal class MaybeBiFailureTest : FreeSpec() {

    init {

        "The extension functions of the `MaybeBiFailure` type" - {

            "the `maybeError` function with a value" - {

                "when a value returns a nullable type" - {
                    val result: Maybe<Fail<Errors.First, Exceptions.First>> = maybeError(null)

                    "then this function should return the the `None` type" {
                        result.shouldBeNone()
                    }
                }

                "when a value returns a non-nullable type" - {
                    val result: Maybe<Fail<Errors.First, Exceptions.First>> = maybeError(Errors.First)

                    "then this function should return the the `Some` type with the value" {
                        result shouldBeError Errors.First
                    }
                }
            }

            "the `maybeException` function with a value" - {

                "when a value returns a nullable type" - {
                    val result: Maybe<Fail<Errors.First, Exceptions.First>> = maybeException(null)

                    "then this function should return the the `None` type" {
                        result.shouldBeNone()
                    }
                }

                "when a value returns a non-nullable type" - {
                    val result: Maybe<Fail<Errors.First, Exceptions.First>> = maybeException(Exceptions.First)

                    "then this function should return the the `Some` type with the value" {
                        result shouldBeException Exceptions.First
                    }
                }
            }
        }
    }

    internal sealed interface Errors {
        data object First : Errors
    }

    internal sealed interface Exceptions {
        data object First : Exceptions
    }
}
