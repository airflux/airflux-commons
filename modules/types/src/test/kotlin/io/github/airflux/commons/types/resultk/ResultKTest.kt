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

import io.github.airflux.commons.types.resultk.ResultK.Failure
import io.github.airflux.commons.types.resultk.ResultK.Success
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

@Suppress("LargeClass")
internal class ResultKTest : FreeSpec() {

    init {

        "The `ResultK` type functions" - {

            "the `isSuccess` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return the true value" {
                        original.isSuccess() shouldBe true
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the false value" {
                        original.isSuccess() shouldBe false
                    }
                }
            }

            "the `isSuccess` function with predicate" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

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
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))
                    val predicate: (String) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isSuccess(predicate) shouldBe false
                    }
                }
            }

            "the `isFailure` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return the false value" {
                        original.isFailure() shouldBe false
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the true value" {
                        original.isFailure() shouldBe true
                    }
                }
            }

            "the `isFailure` function with predicate" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))
                    val predicate: (Errors) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isFailure(predicate) shouldBe false
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

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
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.fold(onFailure = { ALTERNATIVE_VALUE }, onSuccess = { it })
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the null value" {
                        val result = original.fold(onFailure = { ALTERNATIVE_VALUE }, onSuccess = { it })
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `map` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.map { it.toInt() }
                        result shouldBeSuccess ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.map { it.toInt() }
                        result shouldBe original
                    }
                }
            }

            "the `flatMap` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.flatMap { result -> Success(result.toInt()) }
                        result shouldBeSuccess ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

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
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a result of invoke the [block]" {
                        val result = original.andThen { result -> Success(result.toInt()) }
                        result shouldBeSuccess ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

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
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return an original do not apply the transform function to a failure" {
                        val result = original.mapFailure { Errors.Blank }
                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return a result of applying the transform function to a failure" {
                        val result = original.mapFailure { Errors.Blank }
                        result shouldBeFailure Errors.Blank
                    }
                }
            }

            "the `flatMapBoolean` function" - {

                "when a variable has the `Success` type" - {

                    "when a value equals true" - {
                        val original: ResultK<Boolean, Errors> = createResult(Success.of(true))

                        "then this function should return a result of calling the ifTrue lambda" {
                            val result = original.flatMapBoolean(
                                ifTrue = { Success(ORIGINAL_VALUE) },
                                ifFalse = { Success(ALTERNATIVE_VALUE) }
                            )
                            result shouldBeSuccess ORIGINAL_VALUE
                        }
                    }

                    "when a value equals false" - {
                        val original: ResultK<Boolean, Errors> = createResult(Success.of(false))

                        "then this function should return a result of calling the ifFalse lambda" {
                            val result = original.flatMapBoolean(
                                ifTrue = { Success(ORIGINAL_VALUE) },
                                ifFalse = { Success(ALTERNATIVE_VALUE) }
                            )
                            result shouldBeSuccess ALTERNATIVE_VALUE
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<Boolean, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return an original failure" {
                        val result = original.flatMapBoolean(
                            ifTrue = { Success(ORIGINAL_VALUE) },
                            ifFalse = { Success(ALTERNATIVE_VALUE) }
                        )
                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `onSuccess` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onSuccess { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onSuccess { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `onFailure` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onFailure { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onFailure { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getOrForward` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrForward { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.getOrForward { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getFailureOrNull` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return the null value" {
                        val result = original.getFailureOrNull()
                        result.shouldBeNull()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return a failure" {
                        val result = original.getFailureOrNull()
                        result shouldBe Errors.Empty
                    }
                }
            }

            "the `recover` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return an original value" {
                        val result = original.recover { ALTERNATIVE_VALUE }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the result of invoking the recovery function" {
                        val result = original.recover { ALTERNATIVE_VALUE }
                        result shouldBeSuccess ALTERNATIVE_VALUE
                    }
                }
            }

            "the `recoverWith` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return an original value" {
                        val result = original.recoverWith { Success(ALTERNATIVE_VALUE) }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the result of invoking the recovery function" {
                        val result = original.recoverWith { Success(ALTERNATIVE_VALUE) }
                        result shouldBeSuccess ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrNull` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrNull()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the null value" {
                        val result = original.getOrNull()
                        result.shouldBeNull()
                    }
                }
            }

            "the `getOrElse` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrElse` function with a predicate" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return a value from a handler" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `orElse` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val elseResult: ResultK<String, Errors> = createResult(Success(ALTERNATIVE_VALUE))
                        val result = original.orElse { elseResult }
                        result shouldBe original
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val elseResult: ResultK<String, Errors> = createResult(Success(ALTERNATIVE_VALUE))
                        val result = original.orElse { elseResult }
                        result shouldBe elseResult
                    }
                }
            }

            "the `orThrow` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.orThrow { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.orThrow { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `forEach` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then should not thrown exception" {
                        shouldNotThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `merge` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, String> = createResult(Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.merge()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, String> = createResult(Failure(ALTERNATIVE_VALUE))

                    "then this function should return the alternative value" {
                        val result = original.merge()
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `apply` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<MutableList<String>, Errors> =
                        createResult(Success(mutableListOf<String>().apply { add(ORIGINAL_VALUE) }))

                    "when the block returns the `Success` type" - {
                        val doIt: MutableList<String>.() -> ResultK<Unit, Errors> = {
                            add(ALTERNATIVE_VALUE)
                            Success.asUnit
                        }

                        "then this function should return the original value" {
                            val result = original.apply(doIt)
                            result.shouldBeSuccess()
                            result.value shouldContainExactly listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                        }
                    }

                    "when the block returns the `Failure` type" - {
                        val doIt: MutableList<String>.() -> ResultK<Unit, Errors> = {
                            Errors.Empty.asFailure()
                        }

                        "then this function should return the failure value from the block" {
                            val result = original.apply(doIt)
                            result shouldBeFailure Errors.Empty
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<MutableList<String>, Errors> = createResult(Failure(Errors.Empty))
                    val doIt: MutableList<String>.() -> ResultK<Unit, Errors> = {
                        add(ALTERNATIVE_VALUE)
                        Success.asUnit
                    }

                    "then this function should return an original do not invoke the [block]" {
                        val result = original.apply(doIt)
                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `sequence` function" - {

                "when a collection is empty" - {
                    val original: List<ResultK<String, Errors>> = listOf()

                    "then this function should return the value of the asEmptyList property" {
                        val result = original.sequence()
                        result.shouldBeSuccess()
                        result shouldBeSameInstanceAs Success.asEmptyList
                    }
                }

                "when a collection has items only the `Success` type" - {
                    val original: List<ResultK<String, Errors>> = listOf(
                        createResult(Success(ORIGINAL_VALUE)),
                        createResult(Success(ALTERNATIVE_VALUE))
                    )

                    "then this function should return a list with all values" {
                        val result = original.sequence()
                        result.shouldBeSuccess()
                        result.value shouldContainExactly listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    }
                }

                "when a collection has a item of the `Failure` type" - {
                    val original: List<ResultK<String, Errors>> = listOf(
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
                    val transform: (String) -> ResultK<Int, Errors> = { Success(it.toInt()) }

                    "then this function should return the value of the asEmptyList property" {
                        val result: ResultK<List<Int>, Errors> = original.traverse(transform)
                        result.shouldBeSuccess()
                        result shouldBeSameInstanceAs Success.asEmptyList
                    }
                }

                "when a transform function returns items only the `Success` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> ResultK<Int, Errors> = { Success(it.toInt()) }

                    "then this function should return a list with all transformed values" {
                        val result: ResultK<List<Int>, Errors> = original.traverse(transform)
                        result.shouldBeSuccess()
                        result.value shouldContainExactly listOf(ORIGINAL_VALUE.toInt(), ALTERNATIVE_VALUE.toInt())
                    }
                }

                "when a transform function returns any item of the `Failure` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> ResultK<Int, Errors> = {
                        val res = it.toInt()
                        if (res > 10) Failure(Errors.Empty) else Success(res)
                    }

                    "then this function should return the failure value" {
                        val result: ResultK<List<Int>, Errors> = original.traverse(transform)
                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `traverseTo` function for the list type" - {

                "when a collection is empty" - {
                    val original: List<String> = listOf()
                    val transform: (String) -> ResultK<Int, Errors> = { Success(it.toInt()) }

                    "then this function should return the empty list as the value" {
                        val result: ResultK<List<Int>, Errors> = original.traverseTo(mutableListOf(), transform)
                        result.shouldBeSuccess()
                        result.value.shouldBeEmpty()
                    }
                }

                "when a transform function returns items only the `Success` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> ResultK<Int, Errors> = { Success(it.toInt()) }

                    "then this function should return a list with all transformed values" {
                        val result: ResultK<List<Int>, Errors> = original.traverseTo(mutableListOf(), transform)
                        result.shouldBeSuccess()
                        result.value shouldContainExactly listOf(ORIGINAL_VALUE.toInt(), ALTERNATIVE_VALUE.toInt())
                    }
                }

                "when a transform function returns any item of the `Failure` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> ResultK<Int, Errors> = {
                        val res = it.toInt()
                        if (res > 10) Failure(Errors.Empty) else Success(res)
                    }

                    "then this function should return the failure value" {
                        val result: ResultK<List<Int>, Errors> = original.traverseTo(mutableListOf(), transform)
                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `traverseTo` function for the map type" - {

                "when passed only the `transform` parameter" - {

                    "when a collection is empty" - {
                        val original: List<String> = listOf()
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> = { Success(it to it.toInt()) }

                        "then this function should return the empty list as the value" {
                            val result: ResultK<Map<String, Int>, Errors> = original.traverseTo(
                                destination = mutableMapOf(),
                                transform = transform
                            )
                            result.shouldBeSuccess()
                            result.value.shouldBeEmpty()
                        }
                    }

                    "when a transform function returns items only the `Success` type" - {
                        val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> = { Success(it to it.toInt()) }

                        "then this function should return a list with all transformed values" {
                            val result: ResultK<Map<String, Int>, Errors> = original.traverseTo(
                                destination = mutableMapOf(),
                                transform = transform
                            )
                            result.shouldBeSuccess()
                            result.value shouldContainExactly mapOf(
                                ORIGINAL_VALUE to ORIGINAL_VALUE.toInt(),
                                ALTERNATIVE_VALUE to ALTERNATIVE_VALUE.toInt()
                            )
                        }
                    }

                    "when a transform function returns any item of the `Failure` type" - {
                        val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> = {
                            val res = it.toInt()
                            if (res > 10) Failure(Errors.Empty) else Success(it to res)
                        }

                        "then this function should return the failure value" {
                            val result: ResultK<Map<String, Int>, Errors> = original.traverseTo(
                                destination = mutableMapOf(),
                                transform = transform
                            )
                            result shouldBeFailure Errors.Empty
                        }
                    }
                }

                "when passed the `keySelector` and the `transform` parameters" - {

                    "when a collection is empty" - {
                        val original: List<String> = listOf()
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> = { Success(it to it.toInt()) }

                        "then this function should return the empty list as the value" {
                            val result: ResultK<Map<String, Pair<String, Int>>, Errors> = original.traverseTo(
                                destination = mutableMapOf(),
                                keySelector = { it.first },
                                transform = transform
                            )
                            result.shouldBeSuccess()
                            result.value.shouldBeEmpty()
                        }
                    }

                    "when a transform function returns items only the `Success` type" - {
                        val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> = { Success(it to it.toInt()) }

                        "then this function should return a list with all transformed values" {
                            val result: ResultK<Map<String, Pair<String, Int>>, Errors> = original.traverseTo(
                                destination = mutableMapOf(),
                                keySelector = { it.first },
                                transform = transform
                            )
                            result.shouldBeSuccess()
                            result.value shouldContainExactly mapOf(
                                ORIGINAL_VALUE to (ORIGINAL_VALUE to ORIGINAL_VALUE.toInt()),
                                ALTERNATIVE_VALUE to (ALTERNATIVE_VALUE to ALTERNATIVE_VALUE.toInt())
                            )
                        }
                    }

                    "when a transform function returns any item of the `Failure` type" - {
                        val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> = {
                            val res = it.toInt()
                            if (res > 10) Failure(Errors.Empty) else Success(it to res)
                        }

                        "then this function should return the failure value" {
                            val result: ResultK<Map<String, Pair<String, Int>>, Errors> = original.traverseTo(
                                destination = mutableMapOf(),
                                keySelector = { it.first },
                                transform = transform
                            )
                            result shouldBeFailure Errors.Empty
                        }
                    }
                }

                "when passed the `keySelector` and the `valueTransform` and the `transform` parameters" - {

                    "when a collection is empty" - {
                        val original: List<String> = listOf()
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> = { Success(it to it.toInt()) }

                        "then this function should return the empty list as the value" {
                            val result: ResultK<Map<String, Int>, Errors> = original.traverseTo(
                                destination = mutableMapOf(),
                                keySelector = { it.first },
                                valueTransform = { it.second },
                                transform = transform
                            )
                            result.shouldBeSuccess()
                            result.value.shouldBeEmpty()
                        }
                    }

                    "when a transform function returns items only the `Success` type" - {
                        val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> = { Success(it to it.toInt()) }

                        "then this function should return a list with all transformed values" {
                            val result: ResultK<Map<String, Int>, Errors> = original.traverseTo(
                                destination = mutableMapOf(),
                                keySelector = { it.first },
                                valueTransform = { it.second },
                                transform = transform
                            )
                            result.shouldBeSuccess()
                            result.value shouldContainExactly mapOf(
                                ORIGINAL_VALUE to ORIGINAL_VALUE.toInt(),
                                ALTERNATIVE_VALUE to ALTERNATIVE_VALUE.toInt()
                            )
                        }
                    }

                    "when a transform function returns any item of the `Failure` type" - {
                        val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> = {
                            val res = it.toInt()
                            if (res > 10) Failure(Errors.Empty) else Success(it to res)
                        }

                        "then this function should return the failure value" {
                            val result: ResultK<Map<String, Int>, Errors> = original.traverseTo(
                                destination = mutableMapOf(),
                                keySelector = { it.first },
                                valueTransform = { it.second },
                                transform = transform
                            )
                            result shouldBeFailure Errors.Empty
                        }
                    }
                }
            }

            "the `filterNotNull` function" - {

                "when a variable has the `Success` type" - {

                    "when a value is not null" - {
                        val original: ResultK<String?, Errors> = createResult(Success(ORIGINAL_VALUE))

                        "then this function should return the `Success` type with the value" {
                            val result = original.filterNotNull { Errors.Blank }
                            result shouldBeSuccess ORIGINAL_VALUE
                        }
                    }

                    "when a value is null" - {
                        val original: ResultK<String?, Errors> = createResult(Success.Companion.asNull)

                        "then this function should return the `Failure` type with the failure" {
                            val result = original.filterNotNull { Errors.Blank }
                            result shouldBeFailure Errors.Blank
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

                    "then this function should return an original value" {
                        val result = original.filterNotNull { Errors.Blank }
                        result.shouldBeSameInstanceAs(original)
                    }
                }
            }

            "the `filterOrElse` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(Success(ORIGINAL_VALUE))

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
                    val original: ResultK<String, Errors> = createResult(Failure(Errors.Empty))

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
            val result: ResultK<String, Errors.Empty> = createResult(ORIGINAL_VALUE.asSuccess())
            result shouldBeSuccess ORIGINAL_VALUE
        }

        "The `asFailure` function should return the `Failure` type with the passed value" {
            val result: ResultK<String, Errors.Empty> = createResult(Errors.Empty.asFailure())
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

    private fun <T, E> createResult(value: ResultK<T, E>): ResultK<T, E> = value
}
