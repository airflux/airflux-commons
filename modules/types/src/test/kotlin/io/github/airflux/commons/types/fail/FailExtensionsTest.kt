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

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.matcher.shouldBeError
import io.github.airflux.commons.types.fail.matcher.shouldBeException
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

@OptIn(AirfluxTypesExperimental::class)
@Suppress("LargeClass")
internal class FailExtensionsTest : FreeSpec() {

    init {

        "The extension functions of the `Fail` type" - {

            "the `isError` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return the true value" {
                        original.isError() shouldBe true
                    }

                    "then this function should do a smart cast of receiver type" {
                        if (original.isError())
                            original.value shouldBe ORIGINAL_VALUE
                        else
                            failure("The result is not an error.")
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return the false value" {
                        original.isError() shouldBe false
                    }
                }
            }

            "the `isError` function with predicate" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "when the predicate return the true value" - {
                        val predicate: (String) -> Boolean = { it == ORIGINAL_VALUE }

                        "then this function should return the true value" {
                            original.isError(predicate) shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (String) -> Boolean = { it != ORIGINAL_VALUE }

                        "then this function should return the true value" {
                            original.isError(predicate) shouldBe false
                        }
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))
                    val predicate: (String) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isError(predicate) shouldBe false
                    }
                }
            }

            "the `isException` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return the false value" {
                        original.isException() shouldBe false
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return the true value" {
                        original.isException() shouldBe true
                    }

                    "then this function should do a smart cast of receiver type" {
                        if (original.isException())
                            original.value shouldBe Errors.Empty
                        else
                            failure("The result is not an exception.")
                    }
                }
            }

            "the `isException` function with predicate" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))
                    val predicate: (Errors) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isException(predicate) shouldBe false
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "when the predicate return the true value" - {
                        val predicate: (Errors) -> Boolean = { it == Errors.Empty }

                        "then this function should return the true value" {
                            original.isException(predicate) shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (Errors) -> Boolean = { it != Errors.Empty }

                        "then this function should return the true value" {
                            original.isException(predicate) shouldBe false
                        }
                    }
                }
            }

            "the `fold` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.fold(onError = { it }, onException = { ALTERNATIVE_VALUE })
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return the alternative value" {
                        val result = original.fold(onError = { it }, onException = { ALTERNATIVE_VALUE })
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `mapError` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.mapError { it.toInt() }
                        result shouldBeError ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.mapError { it.toInt() }
                        result shouldBeSameInstanceAs original
                    }
                }
            }

            "the `map2` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, String> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.map2(
                            onError = { it.toInt() },
                            onException = { it.toInt() }
                        )
                        result shouldBeError ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, String> = createFail(Fail.exception(ALTERNATIVE_VALUE))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.map2(
                            onError = { it.toInt() },
                            onException = { it.toInt() }
                        )

                        result shouldBeException ALTERNATIVE_VALUE.toInt()
                    }
                }
            }

            "the `mapException` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, String> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.mapException { it.toInt() }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, String> = createFail(Fail.exception(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.mapException { it.toInt() }
                        result shouldBeException ORIGINAL_VALUE.toInt()
                    }
                }
            }

            "the `onError` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onError { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onError { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `onException` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onException { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onException { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getErrorOrNull` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getErrorOrNull()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return the null value" {
                        val result = original.getErrorOrNull()
                        result.shouldBeNull()
                    }
                }
            }

            "the `getExceptionOrNull` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, String> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return the null value" {
                        val result = original.getExceptionOrNull()
                        result.shouldBeNull()
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, String> = createFail(Fail.exception(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getExceptionOrNull()
                        result shouldBe ORIGINAL_VALUE
                    }
                }
            }

            "the `getErrorOrElse` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getErrorOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val result = original.getErrorOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getExceptionOrElse` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, String> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return the defaultValue value" {
                        val result = original.getExceptionOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, String> = createFail(Fail.exception(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getExceptionOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ORIGINAL_VALUE
                    }
                }
            }

            "the `getErrorOrElse` function with a predicate" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getErrorOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return a value from a handler" {
                        val result = original.getErrorOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getExceptionOrElse` function with a predicate" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, String> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a value from a handler" {
                        val result = original.getExceptionOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, String> = createFail(Fail.exception(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getExceptionOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ORIGINAL_VALUE
                    }
                }
            }

            "the `errorOrElse` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val elseResult: Fail<String, Errors> = createFail(Fail.error(ALTERNATIVE_VALUE))
                        val result = original.errorOrElse { elseResult }
                        result shouldBe original
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val elseResult: Fail<String, Errors> = createFail(Fail.error(ALTERNATIVE_VALUE))
                        val result = original.errorOrElse { elseResult }
                        result shouldBe elseResult
                    }
                }
            }

            "the `exceptionOrElse` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return the defaultValue value" {
                        val elseResult: Fail<String, Errors> = createFail(Fail.error(ALTERNATIVE_VALUE))
                        val result = original.exceptionOrElse { elseResult }
                        result shouldBe elseResult
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return a value" {
                        val elseResult: Fail<String, Errors> = createFail(Fail.error(ALTERNATIVE_VALUE))
                        val result = original.exceptionOrElse { elseResult }
                        result shouldBe original
                    }
                }
            }

            "the `errorOrThrow` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.errorOrThrow { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, Errors> = createFail(Fail.exception(Errors.Empty))

                    "then this function should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.errorOrThrow { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `exceptionOrThrow` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, String> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.exceptionOrThrow { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, String> = createFail(Fail.exception(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.exceptionOrThrow { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }
            }

            "the `toBiFailureResult` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, String> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return the `ResultK.Failure` type with value of the `Error` type" {
                        val result = original.toBiFailureResult()
                        result.shouldBeFailure()
                        result.cause shouldBeError ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, String> = createFail(Fail.exception(ORIGINAL_VALUE))

                    "then this function should return the `ResultK.Failure` type with value of the `Exception` type" {
                        val result = original.toBiFailureResult()
                        result.shouldBeFailure()
                        result.cause shouldBeException ORIGINAL_VALUE
                    }
                }
            }

            "the `merge` function" - {

                "when a variable has the `Error` type" - {
                    val original: Fail<String, String> = createFail(Fail.error(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.merge()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Exception` type" - {
                    val original: Fail<String, String> = createFail(Fail.exception(ALTERNATIVE_VALUE))

                    "then this function should return the alternative value" {
                        val result = original.merge()
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }
        }

        "The `asError` function should return the `Error` type with the passed value" {
            val result: Fail<String, Errors.Empty> = createFail(ORIGINAL_VALUE.asError())
            result shouldBeError ORIGINAL_VALUE
        }

        "The `asException` function should return the `Exception` type with the passed value" {
            val result: Fail<String, Errors.Empty> = createFail(Errors.Empty.asException())
            result shouldBeException Errors.Empty
        }
    }

    internal sealed interface Errors {
        data object Empty : Errors
        data object Blank : Errors
    }

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ALTERNATIVE_VALUE = "20"
    }

    private fun <ErrorT : Any, ExceptionT : Any> createFail(
        value: Fail<ErrorT, ExceptionT>
    ): Fail<ErrorT, ExceptionT> = value
}
