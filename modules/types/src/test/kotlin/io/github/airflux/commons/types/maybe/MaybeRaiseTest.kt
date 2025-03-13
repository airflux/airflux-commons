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
import io.github.airflux.commons.types.RaiseException
import io.github.airflux.commons.types.ensure
import io.github.airflux.commons.types.ensureNotNull
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.result
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class MaybeRaiseTest : FreeSpec() {

    init {

        "The Maybe#Raise type" - {

            "the extension functions for the `ResultK` type" - {

                "the `component1` function" - {

                    "when the result is successful" - {
                        val raise = Maybe.Raise<Error>()
                        val result: ResultK<Int, Error> = FIRST_VALUE.asSuccess()

                        "then should return the value" {
                            val result = with(raise) {
                                val (bindValue: Int) = result
                                bindValue
                            }

                            result shouldBe FIRST_VALUE
                        }
                    }

                    "when the result is failure" - {
                        val raise = Maybe.Raise<Error>()
                        val result: ResultK<Int, Error> = Error.First.asFailure()

                        "then should raise an exception" {
                            val exception = shouldThrow<RaiseException> {
                                with(raise) {
                                    val (bindValue) = result
                                    bindValue
                                }
                            }

                            exception.raise shouldBe raise
                            exception.failure shouldBe Error.First
                        }
                    }
                }

                "the `bind` function" - {

                    "when the result is successful" - {
                        val raise = Maybe.Raise<Error>()
                        val result: ResultK<Int, Error> = FIRST_VALUE.asSuccess()

                        "then should return the value" {
                            val result = with(raise) {
                                result.bind()
                            }

                            result shouldBe FIRST_VALUE
                        }
                    }

                    "when the result is failure" - {
                        val raise = Maybe.Raise<Error>()
                        val result: ResultK<Int, Error> = Error.First.asFailure()

                        "then should raise an exception" {
                            val exception = shouldThrow<RaiseException> {
                                with(raise) {
                                    result.bind()
                                }
                            }

                            exception.raise shouldBe raise
                            exception.failure shouldBe Error.First
                        }
                    }
                }

                "the `raise` function" - {

                    "when the result is successful" - {
                        val raise = Maybe.Raise<Error>()
                        val result: ResultK<Int, Error> = FIRST_VALUE.asSuccess()

                        "then should not raise an exception" {
                            shouldNotThrow<RaiseException> {
                                with(raise) {
                                    result.raise()
                                }
                            }
                        }
                    }

                    "when the result is failure" - {
                        val raise = Maybe.Raise<Error>()
                        val result: ResultK<Int, Error> = Error.First.asFailure()

                        "then should raise an exception" {
                            val exception = shouldThrow<RaiseException> {
                                with(raise) {
                                    result.raise()
                                }
                            }

                            exception.raise shouldBe raise
                            exception.failure shouldBe Error.First
                        }
                    }
                }

                "the `ensure` function" - {

                    "when condition is true" - {
                        val condition = true

                        "then should return a successful value" {
                            val result: ResultK<Unit, Error> = result {
                                ensure(condition) { Error.First }
                            }

                            result shouldBeSuccess Unit
                        }
                    }

                    "when condition is false" - {
                        val condition = false

                        "then should return a failure value" {
                            val result: ResultK<Unit, Error> = result {
                                ensure(condition) { Error.First }
                            }

                            result shouldBeFailure Error.First
                        }
                    }
                }

                "the `ensureNotNull` function" - {

                    "when value is not null" - {
                        val value: Int? = createValue(FIRST_VALUE)

                        "then should return a successful value" {
                            val result: ResultK<Int, Error> = result {
                                ensureNotNull(value) { Error.First }
                            }

                            result shouldBeSuccess value
                        }
                    }

                    "when value is null" - {
                        val value: Int? = createValue(null)

                        "then should return a failure value" {
                            val result: ResultK<Int, Error> = result {
                                ensureNotNull(value) { Error.First }
                            }

                            result shouldBeFailure Error.First
                        }
                    }
                }
            }

            "the extension functions for the `Maybe` type" - {

                "the `raise` function" - {

                    "when the result contains a value" - {
                        val raise = Maybe.Raise<Error>()
                        val result: Maybe<Error> = Error.First.asSome()

                        "then should raise an exception" {
                            val exception = shouldThrow<RaiseException> {
                                with(raise) {
                                    result.raise()
                                }
                            }

                            exception.raise shouldBe raise
                            exception.failure shouldBe Error.First
                        }
                    }

                    "when the result does not contain a value" - {
                        val raise = Maybe.Raise<Error>()
                        val result: Maybe<Error> = Maybe.None

                        "then should not raise an exception" {
                            shouldNotThrow<RaiseException> {
                                with(raise) {
                                    result.raise()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    internal sealed class Error {
        data object First : Error()
    }

    private companion object {
        private const val FIRST_VALUE = 1
    }

    private fun createValue(value: Int?): Int? = value
}
