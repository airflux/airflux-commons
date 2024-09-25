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
package io.github.airflux.commons.types.result

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class ResultTest : FreeSpec() {

    init {

        "The `Result` type functions" - {

            "the `isSuccess` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return the true value" {
                        original.isSuccess() shouldBe true
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the false value" {
                        original.isSuccess() shouldBe false
                    }
                }
            }

            "the `isSuccess` function with predicate" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "when the predicate return the true value" - {
                        val predicate: (String) -> Boolean = { it == ORIGINAL_VALUE }

                        "then this function should return the true value" {
                            original.isSuccess(predicate) shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (String) -> Boolean = { it != ORIGINAL_VALUE }

                        "then this function should return the true value" {
                            original.isSuccess(predicate) shouldBe false
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))
                    val predicate: (String) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isSuccess(predicate) shouldBe false
                    }
                }
            }

            "the `isFailure` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return the false value" {
                        original.isFailure() shouldBe false
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the true value" {
                        original.isFailure() shouldBe true
                    }
                }
            }

            "the `isFailure` function with predicate" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))
                    val predicate: (Errors) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isFailure(predicate) shouldBe false
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "when the predicate return the true value" - {
                        val predicate: (Errors) -> Boolean = { it == Errors.Empty }

                        "then this function should return the true value" {
                            original.isFailure(predicate) shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (Errors) -> Boolean = { it != Errors.Empty }

                        "then this function should return the true value" {
                            original.isFailure(predicate) shouldBe false
                        }
                    }
                }
            }

            "the `fold` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.fold(onFailure = { ALTERNATIVE_VALUE }, onSuccess = { it })
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the null value" {
                        val result = original.fold(onFailure = { ALTERNATIVE_VALUE }, onSuccess = { it })
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `map` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.map { it.toInt() }
                        result shouldBeSuccess ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.map { it.toInt() }
                        result shouldBe original
                    }
                }
            }

            "the `flatMap` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.flatMap { result -> Success(result.toInt()) }
                        result shouldBeSuccess ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.flatMap { success ->
                            Success(success.toInt())
                        }

                        result shouldBe original
                    }
                }
            }

            "the `andThen` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a result of invoke the [block]" {
                        val result = original.andThen { result -> Success(result.toInt()) }
                        result shouldBeSuccess ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return an original do not invoke the [block]" {
                        val result = original.andThen { success ->
                            Success(success.toInt())
                        }

                        result shouldBe original
                    }
                }
            }

            "the `mapFailure` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return an original do not apply the transform function to a failure" {
                        val result = original.mapFailure { Errors.Blank }
                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return a result of applying the transform function to a failure" {
                        val result = original.mapFailure { Errors.Blank }
                        result shouldBeFailure Errors.Blank
                    }
                }
            }

            "the `onSuccess` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onSuccess { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onSuccess { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `onFailure` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onFailure { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onFailure { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getOrForward` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrForward { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.getOrForward { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getFailureOrNull` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return the null value" {
                        val result = original.getFailureOrNull()
                        result.shouldBeNull()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return a failure" {
                        val result = original.getFailureOrNull()
                        result shouldBe Errors.Empty
                    }
                }
            }

            "the `recover` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return an original value" {
                        val result = original.recover { ALTERNATIVE_VALUE }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the result of invoking the recovery function" {
                        val result = original.recover { ALTERNATIVE_VALUE }
                        result shouldBeSuccess ALTERNATIVE_VALUE
                    }
                }
            }

            "the `recoverWith` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return an original value" {
                        val result = original.recoverWith { Success(ALTERNATIVE_VALUE) }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the result of invoking the recovery function" {
                        val result = original.recoverWith { Success(ALTERNATIVE_VALUE) }
                        result shouldBeSuccess ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrNull` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrNull()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the null value" {
                        val result = original.getOrNull()
                        result.shouldBeNull()
                    }
                }
            }

            "the `getOrElse` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrElse` function with a predicate" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return a value from a handler" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `orElse` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val elseResult: Result<String, Errors> = createResult(Success(ALTERNATIVE_VALUE))
                        val result = original.orElse { elseResult }
                        result shouldBe original
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val elseResult: Result<String, Errors> = createResult(Success(ALTERNATIVE_VALUE))
                        val result = original.orElse { elseResult }
                        result shouldBe elseResult
                    }
                }
            }

            "the `orThrow` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.orThrow { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.orThrow { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `forEach` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then should not thrown exception" {
                        shouldNotThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `merge` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, String> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.merge()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, String> = createResult(Failure(ALTERNATIVE_VALUE))

                    "then this function should return the alternative value" {
                        val result = original.merge()
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `sequence` function" - {

                "when a collection is empty" - {
                    val original: List<Result<String, Errors>> = listOf()

                    "then this function should return the value of the asEmptyList property" {
                        val result = original.sequence()
                        result.shouldBeSuccess()
                        result shouldBeSameInstanceAs Success.asEmptyList
                    }
                }

                "when a collection has items only the `Success` type" - {
                    val original: List<Result<String, Errors>> = listOf(
                        createResult(Success(ORIGINAL_VALUE)),
                        createResult(Success(ALTERNATIVE_VALUE))
                    )

                    "then this function should return a list with all values" {
                        val result = original.sequence()
                        val success = result.shouldBeSuccess()
                        success.value shouldContainExactly listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    }
                }

                "when a collection has a item of the `Failure` type" - {
                    val original: List<Result<String, Errors>> = listOf(
                        createResult(Success(ORIGINAL_VALUE)),
                        createResult(Failure(Errors.Empty))
                    )

                    "then this function should return the failure value" {
                        val result = original.sequence()
                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `traverse` function" - {

                "when a collection is empty" - {
                    val original: List<String> = listOf()
                    val transform: (String) -> Result<Int, Errors> = { Success(it.toInt()) }

                    "then this function should return the value of the asEmptyList property" {
                        val result: Result<List<Int>, Errors> = original.traverse(transform)
                        result.shouldBeSuccess()
                        result shouldBeSameInstanceAs Success.asEmptyList
                    }
                }

                "when a transform function returns items only the `Success` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> Result<Int, Errors> = { Success(it.toInt()) }

                    "then this function should return a list with all transformed values" {
                        val result: Result<List<Int>, Errors> = original.traverse(transform)
                        val success = result.shouldBeSuccess()
                        success.value shouldContainExactly listOf(ORIGINAL_VALUE.toInt(), ALTERNATIVE_VALUE.toInt())
                    }
                }

                "when a transform function returns any item of the `Failure` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> Result<Int, Errors> = {
                        val res = it.toInt()
                        if (res > 10) Failure(Errors.Empty) else Success(res)
                    }

                    "then this function should return the failure value" {
                        val result: Result<List<Int>, Errors> = original.traverse(transform)
                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `filterNotNull` function" - {

                "when a variable has the `Success` type" - {

                    "when a value is not null" - {
                        val original: Result<String?, Errors> = createResult(Success(ORIGINAL_VALUE))

                        "then this function should return the `Success` type with the value" {
                            val result = original.filterNotNull { Errors.Blank }
                            result shouldBeSuccess ORIGINAL_VALUE
                        }
                    }

                    "when a value is null" - {
                        val original: Result<String?, Errors> = createResult(Success.asNull)

                        "then this function should return the `Failure` type with the failure" {
                            val result = original.filterNotNull { Errors.Blank }
                            result shouldBeFailure Errors.Blank
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return an original value" {
                        val result = original.filterNotNull { Errors.Blank }
                        result.shouldBeSameInstanceAs(original)
                    }
                }
            }

            "the `filterOrElse` function" - {

                "when a variable has the `Success` type" - {
                    val original: Result<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "when predicate return the true value" - {
                        val predicate: (String) -> Boolean = { true }

                        "then this function should return the `Success` type with the value" {
                            val result = original.filterOrElse(predicate) { Errors.Blank }
                            result shouldBeSuccess ORIGINAL_VALUE
                        }
                    }

                    "when predicate return the false value" - {
                        val predicate: (String) -> Boolean = { false }

                        "then this function should return the `Success` type with the value" {
                            val result = original.filterOrElse(predicate) { Errors.Blank }
                            result shouldBeFailure Errors.Blank
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Result<String, Errors> = createResult(Failure(Errors.Empty))

                    "when predicate return the true value" - {
                        val predicate: (String) -> Boolean = { true }

                        "then this function should return original value" {
                            val result = original.filterOrElse(predicate) { Errors.Blank }
                            result.shouldBeSameInstanceAs(original)
                        }
                    }

                    "when predicate return the false value" - {
                        val predicate: (String) -> Boolean = { false }

                        "then this function should return original value" {
                            val result = original.filterOrElse(predicate) { Errors.Blank }
                            result.shouldBeSameInstanceAs(original)
                        }
                    }
                }
            }
        }

        "The `asSuccess` function should return the `Success` type with the passed value" {
            val result: Result<String, Errors.Empty> = createResult(ORIGINAL_VALUE.asSuccess())
            result shouldBeSuccess ORIGINAL_VALUE
        }

        "The `asFailure` function should return the `Failure` type with the passed value" {
            val result: Result<String, Errors.Empty> = createResult(Errors.Empty.asFailure())
            result shouldBeFailure Errors.Empty
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

    private fun <T, E> createResult(value: Result<T, E>): Result<T, E> = value
}
