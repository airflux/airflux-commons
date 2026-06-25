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
import io.github.airflux.commons.types.maybe.matcher.shouldBeSome
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

@OptIn(AirfluxTypesExperimental::class)
internal class BiFailureMaybeExtensionsTest : FreeSpec() {

    init {

        "The extension functions of the `BiFailureMaybe` type" - {

            "the `fold` function" - {

                "when a variable has the `None` type" - {
                    val original: BiFailureMaybe<Errors, Exceptions> = createNone()

                    "then this function should return a result of invoking the onNone block" {
                        val result = original.fold(
                            onNone = { ORIGINAL_VALUE },
                            onError = { FIRST_ALTERNATIVE_VALUE },
                            onException = { SECOND_ALTERNATIVE_VALUE }
                        )
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Some` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> = createError(Errors.First)

                        "then this function should return a result of invoking the onError block" {
                            val result = original.fold(
                                onNone = { ORIGINAL_VALUE },
                                onError = { FIRST_ALTERNATIVE_VALUE },
                                onException = { SECOND_ALTERNATIVE_VALUE }
                            )
                            result shouldBe FIRST_ALTERNATIVE_VALUE
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> = createException(Exceptions.First)

                        "then this function should return a result of invoking the onException block" {
                            val result = original.fold(
                                onNone = { ORIGINAL_VALUE },
                                onError = { FIRST_ALTERNATIVE_VALUE },
                                onException = { SECOND_ALTERNATIVE_VALUE }
                            )
                            result shouldBe SECOND_ALTERNATIVE_VALUE
                        }
                    }
                }
            }

            "the `mapError` function" - {

                "when a variable has the `None` type" - {
                    val original: BiFailureMaybe<Errors, Exceptions> = createNone()

                    "then this function should return an original" {
                        val result = original.mapError { Errors.Second }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Some` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> = createError(Errors.First)

                        "then this function should return a result of applying the transform function to an error" {
                            val result = original.mapError { Errors.Second }
                            result shouldBeError Errors.Second
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> =
                            createException(Exceptions.First)

                        "then this function should return an original" {
                            val result = original.mapError { Errors.Second }
                            result shouldBeException Exceptions.First
                        }
                    }
                }
            }

            "the `mapException` function" - {

                "when a variable has the `None` type" - {
                    val original: BiFailureMaybe<Errors, Exceptions> = createNone()

                    "then this function should return an original" {
                        val result = original.mapException { Exceptions.Second }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Some` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> =
                            createError(Errors.First)

                        "then this function should return an original" {
                            val result = original.mapException { Exceptions.Second }
                            result shouldBeError Errors.First
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> =
                            createException(Exceptions.First)

                        "then this function should return a result of applying the transform function to an error" {
                            val result = original.mapException { Exceptions.Second }
                            result shouldBeException Exceptions.Second
                        }
                    }
                }
            }

            "the `onError` function" - {

                "when a variable has the `None` type" - {
                    val original: BiFailureMaybe<Errors, Exceptions> = createNone()

                    "then this function should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onError { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Some` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> =
                            createError(Errors.First)

                        "then a code block should execute" {
                            shouldThrow<IllegalStateException> {
                                original.onError { throw IllegalStateException() }
                            }
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> =
                            createException(Exceptions.First)

                        "then this function should not anything do" {
                            shouldNotThrow<IllegalStateException> {
                                original.onError { throw IllegalStateException() }
                            }
                        }
                    }
                }
            }

            "the `onException` function" - {

                "when a variable has the `None` type" - {
                    val original: BiFailureMaybe<Errors, Exceptions> = createNone()

                    "then this function should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onException { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Some` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> =
                            createError(Errors.First)

                        "then this function should not anything do" {
                            shouldNotThrow<IllegalStateException> {
                                original.onException { throw IllegalStateException() }
                            }
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> =
                            createException(Exceptions.First)

                        "then a code block should execute" {
                            shouldThrow<IllegalStateException> {
                                original.onException { throw IllegalStateException() }
                            }
                        }
                    }
                }
            }

            "the `recover` function" - {

                "when a variable has the `None` type" - {
                    val original: BiFailureMaybe<Errors, Exceptions> = createNone()

                    "then this function should return an original value" {
                        val result = original.recover(
                            onError = { FIRST_ALTERNATIVE_VALUE },
                            onException = { SECOND_ALTERNATIVE_VALUE }
                        )
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Some` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> =
                            createError(Errors.First)

                        "then this function should return the result of invoking the onError block" {
                            val result = original.recover(
                                onError = { FIRST_ALTERNATIVE_VALUE },
                                onException = { SECOND_ALTERNATIVE_VALUE }
                            )
                            result shouldBeSome FIRST_ALTERNATIVE_VALUE
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureMaybe<Errors, Exceptions> =
                            createException(Exceptions.First)

                        "then this function should return the result of invoking the onException block" {
                            val result = original.recover(
                                onError = { FIRST_ALTERNATIVE_VALUE },
                                onException = { SECOND_ALTERNATIVE_VALUE }
                            )
                            result shouldBeSome SECOND_ALTERNATIVE_VALUE
                        }
                    }
                }
            }
        }
    }

    internal sealed interface Errors {
        data object First : Errors
        data object Second : Errors
    }

    internal sealed interface Exceptions {
        data object First : Exceptions
        data object Second : Exceptions
    }

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val FIRST_ALTERNATIVE_VALUE = "20"
        private const val SECOND_ALTERNATIVE_VALUE = "30"
    }

    private fun createNone(): BiFailureMaybe<Nothing, Nothing> = Maybe.none()

    private fun <ErrorT : Any> createError(value: ErrorT): BiFailureMaybe<ErrorT, Nothing> =
        Maybe.some(Fail.error(value))

    private fun <ExceptionT : Any> createException(value: ExceptionT): BiFailureMaybe<Nothing, ExceptionT> =
        Maybe.some(Fail.exception(value))
}
