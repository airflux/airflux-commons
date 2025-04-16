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
import io.github.airflux.commons.types.fail.matcher.shouldBeError
import io.github.airflux.commons.types.fail.matcher.shouldBeException
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldBeSome
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.kotest.assertions.failure
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

@OptIn(AirfluxTypesExperimental::class)
@Suppress("LargeClass")
internal class MaybeExtensionsTest : FreeSpec() {

    init {

        "The extension functions of the `ResultK` type" - {

            "the `isSome` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return the true value" {
                        original.isSome() shouldBe true
                    }

                    "then this function should do a smart cast of receiver type" {
                        if (original.isSome())
                            original.value shouldBe ORIGINAL_VALUE
                        else
                            failure("The result is not a some")
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return the false value" {
                        original.isSome() shouldBe false
                    }
                }
            }

            "the `isSome` function with predicate" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "when the predicate return the true value" - {
                        val predicate: (String) -> Boolean = { it == ORIGINAL_VALUE }

                        "then this function should return the true value" {
                            original.isSome(predicate) shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (String) -> Boolean = { it != ORIGINAL_VALUE }

                        "then this function should return the true value" {
                            original.isSome(predicate) shouldBe false
                        }
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)
                    val predicate: (String) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isSome(predicate) shouldBe false
                    }
                }
            }

            "the `isNone` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return the false value" {
                        original.isNone() shouldBe false
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return the true value" {
                        original.isNone() shouldBe true
                    }
                }
            }

            "the `fold` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.fold(onNone = { ALTERNATIVE_VALUE }, onSome = { it })
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return the alternative value" {
                        val result = original.fold(onNone = { ALTERNATIVE_VALUE }, onSome = { it })
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `flatten` function" - {

                "when a variable has the `Some` type" - {

                    "when nested value has the `Some` type" - {
                        val original: Maybe<Maybe<String>> = create(Maybe.Some(Maybe.Some(ORIGINAL_VALUE)))

                        "then this function should return this nested value" {
                            val result: Maybe<String> = original.flatten()
                            result shouldBeSome ORIGINAL_VALUE
                        }
                    }

                    "when nested value has the `None` type" - {
                        val original: Maybe<Maybe<String>> = create(Maybe.Some(Maybe.None))

                        "then this function should return this nested value" {
                            val result: Maybe<String> = original.flatten()

                            result.shouldBeNone()
                        }
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<Maybe<String>> = create(Maybe.None)

                    "then this function should return the value of the `None` type" {
                        val result: Maybe<String> = original.flatten()

                        result.shouldBeNone()
                    }
                }
            }

            "the `map` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.map { it.toInt() }
                        result shouldBeSome ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.map { it.toInt() }
                        result shouldBe original
                    }
                }
            }

            "the `flatMap` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.flatMap { Maybe.Some(it.toInt()) }
                        result shouldBeSome ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.flatMap { Maybe.Some(it.toInt()) }

                        result shouldBe original
                    }
                }
            }

            "the `andThen` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return a result of invoke the [block]" {
                        val result = original.andThen { Maybe.Some(it.toInt()) }
                        result shouldBeSome ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return an original do not invoke the [block]" {
                        val result = original.andThen { Maybe.Some(it.toInt()) }

                        result shouldBe original
                    }
                }
            }

            "the `onSome` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onSome { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onSome { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `onNone` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onNone { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onNone { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getOrNull` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrNull()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return the null value" {
                        val result = original.getOrNull()
                        result.shouldBeNull()
                    }
                }
            }

            "the `getOrElse` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return the defaultValue value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrElse` function with lambda `default`" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return a value from a handler" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `orElse` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val elseResult: Maybe<String> = create(Maybe.Some(ALTERNATIVE_VALUE))
                        val result = original.orElse { elseResult }
                        result shouldBe original
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return the defaultValue value" {
                        val elseResult: Maybe<String> = create(Maybe.Some(ALTERNATIVE_VALUE))
                        val result = original.orElse { elseResult }
                        result shouldBe elseResult
                    }
                }
            }

            "the `orThrow` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.orThrow { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.orThrow { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `forEach` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then should not thrown exception" {
                        shouldNotThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `filter` function" - {

                "when a variable has the `Success` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "when predicate return the true value" - {
                        val predicate: (String) -> Boolean = { true }

                        "then this function should return the `Some` type with the value" {
                            val result = original.filter(predicate)
                            result shouldBeSome ORIGINAL_VALUE
                        }
                    }

                    "when predicate return the false value" - {
                        val predicate: (String) -> Boolean = { false }

                        "then this function should return the `None` type" {
                            val result = original.filter(predicate)
                            result.shouldBeNone()
                        }
                    }
                }

                "when a variable has the `Failure` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "when predicate return the true value" - {
                        val predicate: (String) -> Boolean = { true }

                        "then this function should return original value" {
                            val result = original.filter(predicate)
                            result.shouldBeSameInstanceAs(original)
                        }
                    }

                    "when predicate return the false value" - {
                        val predicate: (String) -> Boolean = { false }

                        "then this function should return original value" {
                            val result = original.filter(predicate)
                            result.shouldBeSameInstanceAs(original)
                        }
                    }
                }
            }

            "the `liftToError` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return the `Some` type with the value of the `Fail.Error` type" {
                        val result = original.liftToError()
                        result.shouldBeSome()
                        result.value shouldBeError ORIGINAL_VALUE
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return original value" {
                        val result = original.liftToError()
                        result.shouldBeSameInstanceAs(original)
                    }
                }
            }

            "the `liftToException` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return the `Some` type with the value of the `Fail.Exception` type" {
                        val result = original.liftToException()
                        result.shouldBeSome()
                        result.value shouldBeException ORIGINAL_VALUE
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return original value" {
                        val result = original.liftToException()
                        result.shouldBeSameInstanceAs(original)
                    }
                }
            }

            "the `toResultAsSuccessOr` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return the `Success` type with the value from the original value" {
                        val result: ResultK<String, Error> = original toResultAsSuccessOr Error.First.asFailure()

                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return the `Failure` type with the value from the function `onNone`" {
                        val result: ResultK<String, Error> = original toResultAsSuccessOr Error.First.asFailure()

                        result shouldBeFailure Error.First
                    }
                }
            }

            "the `toResultAsSuccessOr` function with lambda `onNone`" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<String> = create(Maybe.Some(ORIGINAL_VALUE))

                    "then this function should return the `Success` type with the value from the original value" {
                        val result: ResultK<String, Error> =
                            original.toResultAsSuccessOr(onNone = { Error.First.asFailure() })

                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<String> = create(Maybe.None)

                    "then this function should return the `Failure` type with the value from the function `onNone`" {
                        val result: ResultK<String, Error> =
                            original.toResultAsSuccessOr(onNone = { Error.First.asFailure() })

                        result shouldBeFailure Error.First
                    }
                }
            }

            "the `toResultAsFailureOr` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<Error> = create(Maybe.Some(Error.First))

                    "then this function should return the `Failure` type with the value from the original value" {
                        val result: ResultK<String, Error> = original toResultAsFailureOr ORIGINAL_VALUE.asSuccess()

                        result shouldBeFailure Error.First
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<Error> = create(Maybe.None)

                    "then this function should return the `Success` type with the value from the function `onNone`" {
                        val result: ResultK<String, Error> = original toResultAsFailureOr ORIGINAL_VALUE.asSuccess()

                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }
            }

            "the `toResultAsFailureOr` function with lambda `onNone`" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<Error> = create(Maybe.Some(Error.First))

                    "then this function should return the `Failure` type with the value from the original value" {
                        val result: ResultK<String, Error> =
                            original.toResultAsFailureOr(onNone = { ORIGINAL_VALUE.asSuccess() })

                        result shouldBeFailure Error.First
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<Error> = create(Maybe.None)

                    "then this function should return the `Success` type with the value from the function `onNone`" {
                        val result: ResultK<String, Error> =
                            original.toResultAsFailureOr(onNone = { ORIGINAL_VALUE.asSuccess() })

                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }
            }

            "the `toResultAsErrorOr` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<Error> = create(Maybe.Some(Error.First))

                    "then this function should return a failure of type `Fail.Error` containing the original value" {
                        val result: ResultK<String, Fail.Error<Error>> =
                            original toResultAsErrorOr ORIGINAL_VALUE.asSuccess()

                        val failure = result.shouldContainFailureInstance()
                        failure shouldBeError Error.First
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<Error> = create(Maybe.None)

                    "then this function should return the `Success` type with the value from the function `onNone`" {
                        val result: ResultK<String, Fail.Error<Error>> =
                            original toResultAsErrorOr ORIGINAL_VALUE.asSuccess()

                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }
            }

            "the `toResultAsErrorOr` function with lambda `onNone`" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<Error> = create(Maybe.Some(Error.First))

                    "then this function should return a failure of type `Fail.Error` containing the original value" {
                        val result: ResultK<String, Fail.Error<Error>> =
                            original.toResultAsErrorOr(onNone = { ORIGINAL_VALUE.asSuccess() })

                        val failure = result.shouldContainFailureInstance()
                        failure shouldBeError Error.First
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<Error> = create(Maybe.None)

                    "then this function should return the `Success` type with the value from the function `onNone`" {
                        val result: ResultK<String, Fail.Error<Error>> =
                            original.toResultAsErrorOr(onNone = { ORIGINAL_VALUE.asSuccess() })

                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }
            }

            "the `toResultAsExceptionOr` function" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<Error> = create(Maybe.Some(Error.First))

                    "then this function should return a failure of type `Fail.Exception` containing the original value" {
                        val result: ResultK<String, Fail.Exception<Error>> =
                            original toResultAsExceptionOr ORIGINAL_VALUE.asSuccess()

                        val failure = result.shouldContainFailureInstance()
                        failure shouldBeException Error.First
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<Error> = create(Maybe.None)

                    "then this function should return the `Success` type with the value from the default value" {
                        val result: ResultK<String, Fail.Exception<Error>> =
                            original toResultAsExceptionOr ORIGINAL_VALUE.asSuccess()

                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }
            }

            "the `toResultAsExceptionOr` function with lambda `onNone`" - {

                "when a variable has the `Some` type" - {
                    val original: Maybe<Error> = create(Maybe.Some(Error.First))

                    "then this function should return a failure of type `Fail.Exception` containing the original value" {
                        val result: ResultK<String, Fail.Exception<Error>> =
                            original.toResultAsExceptionOr(onNone = { ORIGINAL_VALUE.asSuccess() })

                        val failure = result.shouldContainFailureInstance()
                        failure shouldBeException Error.First
                    }
                }

                "when a variable has the `None` type" - {
                    val original: Maybe<Error> = create(Maybe.None)

                    "then this function should return the `Success` type with the value from the function `onNone`" {
                        val result: ResultK<String, Fail.Exception<Error>> =
                            original.toResultAsExceptionOr(onNone = { ORIGINAL_VALUE.asSuccess() })

                        result shouldBeSuccess ORIGINAL_VALUE
                    }
                }
            }
        }
    }

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ALTERNATIVE_VALUE = "20"
    }

    private fun <ValueT : Any> create(value: Maybe<ValueT>): Maybe<ValueT> = value

    private sealed class Error {
        data object First : Error()
    }
}
