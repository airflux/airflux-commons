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
import io.github.airflux.commons.types.fail.matcher.shouldBeError
import io.github.airflux.commons.types.fail.matcher.shouldBeException
import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.kotest.assertions.failure
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

@OptIn(AirfluxTypesExperimental::class)
@Suppress("LargeClass")
internal class ResultKExtensionsTest : FreeSpec() {

    init {

        "The extension functions of the `ResultK` type" - {

            "the `isSuccess` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return the true value" {
                        original.isSuccess() shouldBe true
                    }

                    "then this function should do a smart cast of receiver type" {
                        if (original.isSuccess())
                            original.value shouldBe ORIGINAL_VALUE
                        else
                            failure("The result is not a success")
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the false value" {
                        original.isSuccess() shouldBe false
                    }
                }
            }

            "the `isSuccess` function with predicate" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

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
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))
                    val predicate: (String) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isSuccess(predicate) shouldBe false
                    }
                }
            }

            "the `isFailure` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return the false value" {
                        original.isFailure() shouldBe false
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the true value" {
                        original.isFailure() shouldBe true
                    }

                    "then this function should do a smart cast of receiver type" {
                        if (original.isFailure())
                            original.cause shouldBe Errors.Empty
                        else
                            failure("The result is not a failure")
                    }
                }
            }

            "the `isFailure` function with predicate" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))
                    val predicate: (Errors) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isFailure(predicate) shouldBe false
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

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
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.fold(onFailure = { ALTERNATIVE_VALUE }, onSuccess = { it })
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the alternative value" {
                        val result = original.fold(onFailure = { ALTERNATIVE_VALUE }, onSuccess = { it })
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `map` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.map { it.toInt() }
                        result shouldBeSuccess ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.map { it.toInt() }
                        result shouldBe original
                    }
                }
            }

            "the `flatMap` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.flatMap { ResultK.Success(it.toInt()) }
                        result shouldBeSuccess ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.flatMap { ResultK.Success(it.toInt()) }

                        result shouldBe original
                    }
                }
            }

            "the `flatten` function" - {

                "when a variable has the `Success` type" - {

                    "when nested value has the `Success` type" - {
                        val original: ResultK<ResultK<String, Errors.Blank>, Errors.Empty> =
                            createResult(ResultK.Success(ResultK.Success(ORIGINAL_VALUE)))

                        "then this function should return this nested value" {
                            val result: ResultK<String, Errors> = original.flatten()
                            result shouldBeSuccess ORIGINAL_VALUE
                        }
                    }

                    "when nested value has the `Failure` type" - {
                        val original: ResultK<ResultK<String, Errors.Blank>, Errors.Empty> =
                            createResult(ResultK.Success(ResultK.Failure(Errors.Blank)))

                        "then this function should return this nested value" {
                            val result: ResultK<String, Errors> = original.flatten()

                            result shouldBeFailure Errors.Blank
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<ResultK<String, Errors.Blank>, Errors.Empty> =
                        createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the value of the `Failure` type" {
                        val result: ResultK<String, Errors> = original.flatten()

                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `andThen` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a result of invoke the [block]" {
                        val result = original.andThen { ResultK.Success(it.toInt()) }
                        result shouldBeSuccess ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return an original do not invoke the [block]" {
                        val result = original.andThen { ResultK.Success(it.toInt()) }

                        result shouldBe original
                    }
                }
            }

            "the `mapFailure` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return an original do not apply the transform function to a failure" {
                        val result = original.mapFailure { Errors.Blank }
                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return a result of applying the transform function to a failure" {
                        val result = original.mapFailure { Errors.Blank }
                        result shouldBeFailure Errors.Blank
                    }
                }
            }

            "the `flatMapBoolean` function" - {

                "when a variable has the `Success` type" - {

                    "when a value equals true" - {
                        val original: ResultK<Boolean, Errors> = createResult(ResultK.Success.of(true))

                        "then this function should return a result of calling the ifTrue lambda" {
                            val result = original.flatMapBoolean(
                                ifTrue = { ResultK.Success(ORIGINAL_VALUE) },
                                ifFalse = { ResultK.Success(ALTERNATIVE_VALUE) }
                            )
                            result shouldBeSuccess ORIGINAL_VALUE
                        }
                    }

                    "when a value equals false" - {
                        val original: ResultK<Boolean, Errors> = createResult(ResultK.Success.of(false))

                        "then this function should return a result of calling the ifFalse lambda" {
                            val result = original.flatMapBoolean(
                                ifTrue = { ResultK.Success(ORIGINAL_VALUE) },
                                ifFalse = { ResultK.Success(ALTERNATIVE_VALUE) }
                            )
                            result shouldBeSuccess ALTERNATIVE_VALUE
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<Boolean, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return an original failure" {
                        val result = original.flatMapBoolean(
                            ifTrue = { ResultK.Success(ORIGINAL_VALUE) },
                            ifFalse = { ResultK.Success(ALTERNATIVE_VALUE) }
                        )
                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `flatMapNullable` function" - {

                "when a variable has the `Success` type" - {

                    "when a value is not null" - {
                        val original: ResultK<Boolean?, Errors> = createResult(ResultK.Success.of(true))

                        "then this function should return a result of calling the ifNonNull lambda" {
                            val result: ResultK<String, Errors> = original.flatMapNullable(
                                ifNull = { ResultK.Success(ALTERNATIVE_VALUE) },
                                ifNonNull = { ResultK.Success(ORIGINAL_VALUE) }
                            )
                            result shouldBeSuccess ORIGINAL_VALUE
                        }
                    }

                    "when a value is null" - {
                        val original: ResultK<Boolean?, Errors> = createResult(ResultK.Success(null))

                        "then this function should return a result of calling the ifNull lambda" {
                            val result: ResultK<String, Errors> = original.flatMapNullable(
                                ifNull = { ResultK.Success(ALTERNATIVE_VALUE) },
                                ifNonNull = { ResultK.Success(ORIGINAL_VALUE) }
                            )
                            result shouldBeSuccess ALTERNATIVE_VALUE
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<Boolean, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return an original failure" {
                        val result: ResultK<String, Errors> = original.flatMapNullable(
                            ifNull = { ResultK.Success(ALTERNATIVE_VALUE) },
                            ifNonNull = { ResultK.Success(ORIGINAL_VALUE) }
                        )
                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `flatMapNotNull` function" - {

                "when a variable has the `Success` type" - {

                    "when a value is not null" - {
                        val original: ResultK<Boolean?, Errors> = createResult(ResultK.Success.of(true))

                        "then this function should return a result of calling lambda" {
                            val result: ResultK<String?, Errors> =
                                original.flatMapNotNull { ResultK.Success(ORIGINAL_VALUE) }

                            result shouldBeSuccess ORIGINAL_VALUE
                        }
                    }

                    "when a value is null" - {
                        val original: ResultK<Boolean?, Errors> = createResult(ResultK.Success(null))

                        "then this function should return the null value" {
                            val result: ResultK<String?, Errors> =
                                original.flatMapNotNull { ResultK.Success(ORIGINAL_VALUE) }

                            result shouldBeSuccess null
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<Boolean, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return an original failure" {
                        val result: ResultK<String?, Errors> =
                            original.flatMapNotNull { ResultK.Success(ORIGINAL_VALUE) }

                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `onSuccess` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onSuccess { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onSuccess { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `onFailure` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onFailure { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onFailure { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getOrForward` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrForward { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.getOrForward { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getFailureOrNull` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return the null value" {
                        val result = original.getFailureOrNull()
                        result.shouldBeNull()
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return a failure" {
                        val result = original.getFailureOrNull()
                        result shouldBe Errors.Empty
                    }
                }
            }

            "the `recover` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return an original value" {
                        val result = original.recover { ALTERNATIVE_VALUE }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the result of invoking the recovery function" {
                        val result = original.recover { ALTERNATIVE_VALUE }
                        result shouldBeSuccess ALTERNATIVE_VALUE
                    }
                }
            }

            "the `recoverWith` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return an original value" {
                        val result = original.recoverWith { ResultK.Success(ALTERNATIVE_VALUE) }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the result of invoking the recovery function" {
                        val result = original.recoverWith { ResultK.Success(ALTERNATIVE_VALUE) }
                        result shouldBeSuccess ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrNull` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrNull()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the null value" {
                        val result = original.getOrNull()
                        result.shouldBeNull()
                    }
                }
            }

            "the `getOrElse` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrElse` function with a predicate" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return a value from a handler" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `orElse` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val elseResult: ResultK<String, Errors> = createResult(ResultK.Success(ALTERNATIVE_VALUE))
                        val result = original.orElse { elseResult }
                        result shouldBe original
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val elseResult: ResultK<String, Errors> = createResult(ResultK.Success(ALTERNATIVE_VALUE))
                        val result = original.orElse { elseResult }
                        result shouldBe elseResult
                    }
                }
            }

            "the `orThrow` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.orThrow { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.orThrow { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `forEach` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then should not thrown exception" {
                        shouldNotThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `merge` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, String> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.merge()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, String> = createResult(ResultK.Failure(ALTERNATIVE_VALUE))

                    "then this function should return the alternative value" {
                        val result = original.merge()
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `apply` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<MutableList<String>, Errors> =
                        createResult(ResultK.Success(mutableListOf<String>().apply { add(ORIGINAL_VALUE) }))

                    "when the block returns the `Success` type" - {
                        val doIt: MutableList<String>.() -> Maybe<Errors> = {
                            add(ALTERNATIVE_VALUE)
                            Maybe.none()
                        }

                        "then this function should return the original value" {
                            val result = original.apply(doIt)
                            result.shouldBeSuccess()
                            result.value shouldContainExactly listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                        }
                    }

                    "when the block returns the `Failure` type" - {
                        val doIt: MutableList<String>.() -> Maybe<Errors> = {
                            Maybe.some(Errors.Empty)
                        }

                        "then this function should return the failure value from the block" {
                            val result = original.apply(doIt)
                            result shouldBeFailure Errors.Empty
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<MutableList<String>, Errors> = createResult(ResultK.Failure(Errors.Empty))
                    val doIt: MutableList<String>.() -> Maybe<Errors> = {
                        add(ALTERNATIVE_VALUE)
                        Maybe.none()
                    }

                    "then this function should return an original do not invoke the [block]" {
                        val result = original.apply(doIt)
                        result shouldBeFailure Errors.Empty
                    }
                }
            }

            "the `mapAll` function" - {

                "when a collection is empty" - {
                    val original: ResultK<List<String>, Errors> = createResult(ResultK.Success(listOf()))

                    "then this function should return the value of the asEmptyList property" {
                        val result = original.mapAll {
                            it.toInt().asSuccess()
                        }
                        result shouldBeSuccess emptyList()
                    }
                }

                "when a collection is not empty" - {
                    val original: ResultK<List<String>, Errors> =
                        createResult(ResultK.Success(listOf(FIRST_STRING_VALUE, SECOND_STRING_VALUE)))

                    "when a transform function returns items only the `Success` type" - {
                        val result = original.mapAll {
                            it.toInt().asSuccess()
                        }

                        "then result should contain list with all transformed values" {
                            result.shouldContainSuccessInstance()
                                .shouldContainExactly(listOf(FIRST_INT_VALUE, SECOND_INT_VALUE))
                        }
                    }

                    "when a transform function returns any item of the `Failure` type" - {
                        val result = original.mapAll {
                            if (it == FIRST_STRING_VALUE)
                                it.toInt().asSuccess()
                            else
                                ResultK.Failure(Errors.Empty)
                        }

                        "then result should contain the failure value" {
                            result shouldBeFailure Errors.Empty
                        }
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
                        createResult(ResultK.Success(ORIGINAL_VALUE)),
                        createResult(ResultK.Success(ALTERNATIVE_VALUE))
                    )

                    "then this function should return a list with all values" {
                        val result = original.sequence()
                        result.shouldBeSuccess()
                        result.value shouldContainExactly listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    }
                }

                "when a collection has a item of the `Failure` type" - {
                    val original: List<ResultK<String, Errors>> = listOf(
                        createResult(ResultK.Success(ORIGINAL_VALUE)),
                        createResult(ResultK.Failure(Errors.Empty))
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
                    val transform: (String) -> ResultK<Int, Errors> = { ResultK.Success(it.toInt()) }

                    "then this function should return the value of the asEmptyList property" {
                        val result: ResultK<List<Int>, Errors> = original.traverse(transform)
                        result.shouldBeSuccess()
                        result shouldBeSameInstanceAs Success.asEmptyList
                    }
                }

                "when a transform function returns items only the `Success` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> ResultK<Int, Errors> = { ResultK.Success(it.toInt()) }

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
                        if (res > 10) ResultK.Failure(Errors.Empty) else ResultK.Success(res)
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
                    val transform: (String) -> ResultK<Int, Errors> = { ResultK.Success(it.toInt()) }

                    "then this function should return the empty list as the value" {
                        val result: ResultK<List<Int>, Errors> = original.traverseTo(mutableListOf(), transform)
                        result.shouldBeSuccess()
                        result.value.shouldBeEmpty()
                    }
                }

                "when a transform function returns items only the `Success` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> ResultK<Int, Errors> = { ResultK.Success(it.toInt()) }

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
                        if (res > 10) ResultK.Failure(Errors.Empty) else ResultK.Success(res)
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
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> =
                            { ResultK.Success(it to it.toInt()) }

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
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> =
                            { ResultK.Success(it to it.toInt()) }

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
                            if (res > 10) ResultK.Failure(Errors.Empty) else ResultK.Success(it to res)
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
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> =
                            { ResultK.Success(it to it.toInt()) }

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
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> =
                            { ResultK.Success(it to it.toInt()) }

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
                            if (res > 10) ResultK.Failure(Errors.Empty) else ResultK.Success(it to res)
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
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> =
                            { ResultK.Success(it to it.toInt()) }

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
                        val transform: (String) -> ResultK<Pair<String, Int>, Errors> =
                            { ResultK.Success(it to it.toInt()) }

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
                            if (res > 10) ResultK.Failure(Errors.Empty) else ResultK.Success(it to res)
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
                        val original: ResultK<String?, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                        "then this function should return the `Success` type with the value" {
                            val result = original.filterNotNull { Errors.Blank }
                            result shouldBeSuccess ORIGINAL_VALUE
                        }
                    }

                    "when a value is null" - {
                        val original: ResultK<String?, Errors> = createResult(ResultK.Success.asNull)

                        "then this function should return the `Failure` type with the failure" {
                            val result = original.filterNotNull { Errors.Blank }
                            result shouldBeFailure Errors.Blank
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return an original value" {
                        val result = original.filterNotNull { Errors.Blank }
                        result.shouldBeSameInstanceAs(original)
                    }
                }
            }

            "the `filterOrElse` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

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
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

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

            "the `liftToError` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return original value" {
                        val result = original.liftToError()
                        result.shouldBeSameInstanceAs(original)
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the `Failure` type with the value of the `Fail.Error` type" {
                        val result = original.liftToError()
                        result.shouldBeFailure()
                        result.cause shouldBeError Errors.Empty
                    }
                }
            }

            "the `liftToException` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return original value" {
                        val result = original.liftToException()
                        result.shouldBeSameInstanceAs(original)
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the `Failure` type with the value of the `Fail.Exception` type" {
                        val result = original.liftToException()
                        result.shouldBeFailure()
                        result.cause shouldBeException Errors.Empty
                    }
                }
            }

            "the `mapToError` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return original value" {
                        val result = original.mapToError { Errors.Blank }
                        result.shouldBeSameInstanceAs(original)
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the `Failure` type with a mapped value of the `Fail.Error` type" {
                        val result = original.mapToError { Errors.Blank }
                        result.shouldBeFailure()
                        result.cause shouldBeError Errors.Blank
                    }
                }
            }

            "the `mapToException` function" - {

                "when a variable has the `Success` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Success(ORIGINAL_VALUE))

                    "then this function should return original value" {
                        val result = original.mapToException { Errors.Blank }
                        result.shouldBeSameInstanceAs(original)
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: ResultK<String, Errors> = createResult(ResultK.Failure(Errors.Empty))

                    "then this function should return the `Failure` type with a mapped value of the `Fail.Exception` type" {
                        val result = original.mapToException { Errors.Blank }
                        result.shouldBeFailure()
                        result.cause shouldBeException Errors.Blank
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
        private const val FIRST_INT_VALUE = 10
        private const val SECOND_INT_VALUE = 20

        private const val FIRST_STRING_VALUE = FIRST_INT_VALUE.toString()
        private const val SECOND_STRING_VALUE = SECOND_INT_VALUE.toString()
    }

    private fun <ValueT, FailureT : Any> createResult(
        value: ResultK<ValueT, FailureT>
    ): ResultK<ValueT, FailureT> = value
}
