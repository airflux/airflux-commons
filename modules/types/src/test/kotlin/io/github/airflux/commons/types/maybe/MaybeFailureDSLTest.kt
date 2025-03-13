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
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldBeSome
import io.kotest.core.spec.style.FreeSpec

@OptIn(AirfluxTypesExperimental::class)
internal class MaybeFailureDSLTest : FreeSpec() {

    init {

        "The DSL" - {

            "the function `maybeFailure`" - {

                "when every level of the DSL returns a None type" - {

                    "then should return a None type" {
                        val result: Maybe<Error> = maybeFailure {
                            function(Maybe.none()).raise()
                            function(Maybe.none()).raise()

                            maybeFailure {
                                function(Maybe.none()).raise()
                                function(Maybe.none())
                            }.raise()

                            function(Maybe.none()).raise()
                            function(Maybe.none())
                        }

                        result.shouldBeNone()
                    }
                }

                "when a failure occurs at the top-level DSL before the nested block DSL" - {
                    val result: Maybe<Error> = maybeFailure {
                        function(Error.First.asSome()).raise()
                        function(Error.Second.asSome()).raise()

                        maybeFailure {
                            function(Error.Third.asSome()).raise()
                            function(Error.Fourth.asSome())
                        }.raise()

                        function(Error.Fifth.asSome()).raise()
                        function(Error.Sixth.asSome())
                    }

                    "then should return the first returned failure" {
                        result shouldBeSome Error.First
                    }
                }

                "when a failure occurs at the nested block DSL" - {
                    val result: Maybe<Error> = maybeFailure {
                        function(Maybe.none()).raise()

                        maybeFailure {
                            function(Error.First.asSome()).raise()
                            function(Error.Second.asSome())
                        }.raise()

                        function(Error.Third.asSome())
                    }

                    "then should return the first returned failure" {
                        result shouldBeSome Error.First
                    }
                }

                "when a failure occurs at the top-level DSL after the nested block DSL" - {
                    val result: Maybe<Error> = maybeFailure {
                        function(Maybe.none()).raise()
                        function(Maybe.none()).raise()

                        maybeFailure {
                            function(Maybe.none()).raise()
                            function(Maybe.none())
                        }.raise()

                        function(Error.First.asSome()).raise()
                        function(Error.Second.asSome())
                    }

                    "then should return the first returned failure" {
                        result shouldBeSome Error.First
                    }
                }
            }
        }
    }

    internal fun function(result: Maybe<Error>): Maybe<Error> = result

    internal sealed class Error {
        data object First : Error()
        data object Second : Error()
        data object Third : Error()
        data object Fourth : Error()
        data object Fifth : Error()
        data object Sixth : Error()
    }
}
