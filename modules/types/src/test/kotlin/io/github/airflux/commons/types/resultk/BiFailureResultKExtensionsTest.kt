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

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.resultk.matcher.shouldBeError
import io.github.airflux.commons.types.resultk.matcher.shouldBeException
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

@OptIn(AirfluxTypesExperimental::class)
internal class BiFailureResultKExtensionsTest : FreeSpec() {

    init {

        "The extension functions of the `ResultK` type" - {

            "the `fold` function" - {

                "when a variable has the `Success` type" - {
                    val original: BiFailureResultK<String, Errors, Exceptions> =
                        createSuccessResult(ORIGINAL_VALUE)

                    "then this function should return a value" {
                        val result = original.fold(
                            onSuccess = { it },
                            onError = { FIRST_ALTERNATIVE_VALUE },
                            onException = { SECOND_ALTERNATIVE_VALUE }
                        )
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createErrorResult(Errors.First)

                        "then this function should return the first alternative value" {
                            val result = original.fold(
                                onSuccess = { it },
                                onError = { FIRST_ALTERNATIVE_VALUE },
                                onException = { SECOND_ALTERNATIVE_VALUE }
                            )
                            result shouldBe FIRST_ALTERNATIVE_VALUE
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createExceptionResult(Exceptions.First)

                        "then this function should return the second alternative value" {
                            val result = original.fold(
                                onSuccess = { it },
                                onError = { FIRST_ALTERNATIVE_VALUE },
                                onException = { SECOND_ALTERNATIVE_VALUE }
                            )
                            result shouldBe SECOND_ALTERNATIVE_VALUE
                        }
                    }
                }
            }

            "the `mapError` function" - {

                "when a variable has the `Success` type" - {
                    val original: BiFailureResultK<String, Errors, Exceptions> =
                        createSuccessResult(ORIGINAL_VALUE)

                    "then this function should return an original" {
                        val result = original.mapError { Errors.Second }
                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createErrorResult(Errors.First)

                        "then this function should return a result of applying the transform function to an error" {
                            val result = original.mapError { Errors.Second }
                            result shouldBeError Errors.Second
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createExceptionResult(Exceptions.First)

                        "then this function should return an original" {
                            val result = original.mapError { Errors.Second }
                            result shouldBeException Exceptions.First
                        }
                    }
                }
            }

            "the `mapException` function" - {

                "when a variable has the `Success` type" - {
                    val original: BiFailureResultK<String, Errors, Exceptions> =
                        createSuccessResult(ORIGINAL_VALUE)

                    "then this function should return an original" {
                        val result = original.mapException { Exceptions.Second }
                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createErrorResult(Errors.First)

                        "then this function should return an original" {
                            val result = original.mapException { Exceptions.Second }
                            result shouldBeError Errors.First
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createExceptionResult(Exceptions.First)

                        "then this function should return a result of applying the transform function to an error" {
                            val result = original.mapException { Exceptions.Second }
                            result shouldBeException Exceptions.Second
                        }
                    }
                }
            }

            "the `onError` function" - {

                "when a variable has the `Success` type" - {
                    val original: BiFailureResultK<String, Errors, Exceptions> = createSuccessResult(ORIGINAL_VALUE)

                    "then this function should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onError { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createErrorResult(Errors.First)

                        "then a code block should execute" {
                            shouldThrow<IllegalStateException> {
                                original.onError { throw IllegalStateException() }
                            }
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createExceptionResult(Exceptions.First)

                        "then this function should not anything do" {
                            shouldNotThrow<IllegalStateException> {
                                original.onError { throw IllegalStateException() }
                            }
                        }
                    }
                }
            }

            "the `onException` function" - {

                "when a variable has the `Success` type" - {
                    val original: BiFailureResultK<String, Errors, Exceptions> = createSuccessResult(ORIGINAL_VALUE)

                    "then this function should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onException { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createErrorResult(Errors.First)

                        "then this function should not anything do" {
                            shouldNotThrow<IllegalStateException> {
                                original.onException { throw IllegalStateException() }
                            }
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createExceptionResult(Exceptions.First)

                        "then a code block should execute" {
                            shouldThrow<IllegalStateException> {
                                original.onException { throw IllegalStateException() }
                            }
                        }
                    }
                }
            }

            "the `recover` function" - {

                "when a variable has the `Success` type" - {
                    val original: BiFailureResultK<String, Errors, Exceptions> = createSuccessResult(ORIGINAL_VALUE)

                    "then this function should return an original value" {
                        val result = original.recover(
                            onError = { FIRST_ALTERNATIVE_VALUE },
                            onException = { SECOND_ALTERNATIVE_VALUE }
                        )
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Failure` type" - {

                    "when a failure is the `Error` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createErrorResult(Errors.First)

                        "then this function should return the result of invoking the onError block" {
                            val result = original.recover(
                                onError = { FIRST_ALTERNATIVE_VALUE },
                                onException = { SECOND_ALTERNATIVE_VALUE }
                            )
                            result shouldBeSuccess FIRST_ALTERNATIVE_VALUE
                        }
                    }

                    "when a failure is the `Exception` type" - {
                        val original: BiFailureResultK<String, Errors, Exceptions> =
                            createExceptionResult(Exceptions.First)

                        "then this function should return the result of invoking the onException block" {
                            val result = original.recover(
                                onError = { FIRST_ALTERNATIVE_VALUE },
                                onException = { SECOND_ALTERNATIVE_VALUE }
                            )
                            result shouldBeSuccess SECOND_ALTERNATIVE_VALUE
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

    private fun <ValueT> createSuccessResult(value: ValueT): BiFailureResultK<ValueT, Nothing, Nothing> =
        ResultK.success(value)

    private fun <ErrorT : Any> createErrorResult(value: ErrorT): BiFailureResultK<Nothing, ErrorT, Nothing> =
        ResultK.failure(Fail.error(value))

    private fun <ExceptionT : Any> createExceptionResult(
        value: ExceptionT
    ): BiFailureResultK<Nothing, Nothing, ExceptionT> =
        ResultK.failure(Fail.exception(value))
}
