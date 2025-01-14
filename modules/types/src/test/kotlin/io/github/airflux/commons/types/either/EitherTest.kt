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
package io.github.airflux.commons.types.either

import io.github.airflux.commons.types.either.Either.Left
import io.github.airflux.commons.types.either.Either.Right
import io.github.airflux.commons.types.either.matcher.shouldBeLeft
import io.github.airflux.commons.types.either.matcher.shouldBeRight
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

@Suppress("LargeClass")
internal class EitherTest : FreeSpec() {

    init {

        "The `Either` type functions" - {

            "the `isLeft` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return the true value" {
                        original.isLeft() shouldBe true
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return the false value" {
                        original.isLeft() shouldBe false
                    }
                }
            }

            "the `isLeft` function with predicate" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "when the predicate return the true value" - {
                        val predicate: (String) -> Boolean = { it == ORIGINAL_VALUE }

                        "then this function should return the true value" {
                            original.isLeft(predicate) shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (String) -> Boolean = { it != ORIGINAL_VALUE }

                        "then this function should return the true value" {
                            original.isLeft(predicate) shouldBe false
                        }
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))
                    val predicate: (String) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isLeft(predicate) shouldBe false
                    }
                }
            }

            "the `isRight` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return the false value" {
                        original.isRight() shouldBe false
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return the true value" {
                        original.isRight() shouldBe true
                    }
                }
            }

            "the `isRight` function with predicate" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))
                    val predicate: (Errors) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        original.isRight(predicate) shouldBe false
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "when the predicate return the true value" - {
                        val predicate: (Errors) -> Boolean = { it == Errors.Empty }

                        "then this function should return the true value" {
                            original.isRight(predicate) shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (Errors) -> Boolean = { it != Errors.Empty }

                        "then this function should return the true value" {
                            original.isRight(predicate) shouldBe false
                        }
                    }
                }
            }

            "the `fold` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.fold(onLeft = { it }, onRight = { ALTERNATIVE_VALUE })
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return the null value" {
                        val result = original.fold(onLeft = { it }, onRight = { ALTERNATIVE_VALUE })
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `mapLeft` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.mapLeft { it.toInt() }
                        result shouldBeLeft ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.mapLeft { it.toInt() }
                        result shouldBeSameInstanceAs original
                    }
                }
            }

            "the `flatMapLeft` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.flatMapLeft { result -> Left(result.toInt()) }
                        result shouldBeLeft ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.flatMapLeft { success ->
                            Left(success.toInt())
                        }

                        result shouldBeSameInstanceAs original
                    }
                }
            }

            "the `mapRight` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, String> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.mapRight { it.toInt() }
                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, String> = createEither(Right(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.mapRight { it.toInt() }
                        result shouldBeRight ORIGINAL_VALUE.toInt()
                    }
                }
            }

            "the `flatMapRight` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, String> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return an original do not apply the transform function to a value" {
                        val result = original.flatMapRight { success ->
                            Left(success.toInt())
                        }

                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, String> = createEither(Right(ORIGINAL_VALUE))

                    "then this function should return a result of applying the transform function to the value" {
                        val result = original.flatMapRight { result -> Right(result.toInt()) }
                        result shouldBeRight ORIGINAL_VALUE.toInt()
                    }
                }
            }

            "the `onLeft` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onLeft { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onLeft { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `onRight` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then should not anything do" {
                        shouldNotThrow<IllegalStateException> {
                            original.onRight { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onRight { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getLeftOrNull` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getLeftOrNull()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return the null value" {
                        val result = original.getLeftOrNull()
                        result.shouldBeNull()
                    }
                }
            }

            "the `getRightOrNull` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, String> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return the null value" {
                        val result = original.getRightOrNull()
                        result.shouldBeNull()
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, String> = createEither(Right(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getRightOrNull()
                        result shouldBe ORIGINAL_VALUE
                    }
                }
            }

            "the `getLeftOrElse` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getLeftOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val result = original.getLeftOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getRightOrElse` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, String> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return the defaultValue value" {
                        val result = original.getRightOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, String> = createEither(Right(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getRightOrElse(ALTERNATIVE_VALUE)
                        result shouldBe ORIGINAL_VALUE
                    }
                }
            }

            "the `getLeftOrElse` function with a predicate" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getLeftOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return a value from a handler" {
                        val result = original.getLeftOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getRightOrElse` function with a predicate" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, String> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a value from a handler" {
                        val result = original.getRightOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, String> = createEither(Right(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.getRightOrElse { ALTERNATIVE_VALUE }
                        result shouldBe ORIGINAL_VALUE
                    }
                }
            }

            "the `leftOrElse` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val elseResult: Either<String, Errors> = createEither(Left(ALTERNATIVE_VALUE))
                        val result = original.leftOrElse { elseResult }
                        result shouldBe original
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return the defaultValue value" {
                        val elseResult: Either<String, Errors> = createEither(Left(ALTERNATIVE_VALUE))
                        val result = original.leftOrElse { elseResult }
                        result shouldBe elseResult
                    }
                }
            }

            "the `rightOrElse` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return the defaultValue value" {
                        val elseResult: Either<String, Errors> = createEither(Left(ALTERNATIVE_VALUE))
                        val result = original.rightOrElse { elseResult }
                        result shouldBe elseResult
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return a value" {
                        val elseResult: Either<String, Errors> = createEither(Left(ALTERNATIVE_VALUE))
                        val result = original.rightOrElse { elseResult }
                        result shouldBe original
                    }
                }
            }

            "the `leftOrThrow` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, Errors> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.leftOrThrow { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, Errors> = createEither(Right(Errors.Empty))

                    "then this function should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.leftOrThrow { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `rightOrThrow` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, String> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.rightOrThrow { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, String> = createEither(Right(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.rightOrThrow { throw IllegalStateException() }
                        result shouldBe ORIGINAL_VALUE
                    }
                }
            }

            "the `merge` function" - {

                "when a variable has the `Left` type" - {
                    val original: Either<String, String> = createEither(Left(ORIGINAL_VALUE))

                    "then this function should return a value" {
                        val result = original.merge()
                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Right` type" - {
                    val original: Either<String, String> = createEither(Right(ALTERNATIVE_VALUE))

                    "then this function should return the alternative value" {
                        val result = original.merge()
                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }
        }

        "The `asLeft` function should return the `Left` type with the passed value" {
            val result: Either<String, Errors.Empty> = createEither(ORIGINAL_VALUE.asLeft())
            result shouldBeLeft ORIGINAL_VALUE
        }

        "The `asRight` function should return the `Right` type with the passed value" {
            val result: Either<String, Errors.Empty> = createEither(Errors.Empty.asRight())
            result shouldBeRight Errors.Empty
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

    private fun <LeftT, RightT> createEither(value: Either<LeftT, RightT>): Either<LeftT, RightT> = value
}
