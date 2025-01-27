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

package io.github.airflux.commons.types.either.matcher

import io.github.airflux.commons.assertionCorrect
import io.github.airflux.commons.assertionIncorrect
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.either.Either
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class EitherMatchersTest : FreeSpec() {

    init {
        "The Kotest matchers for the Either type" - {

            "when a result is the Left" - {
                val result: Either<Int, String> = Either.Left(LEFT_VALUE)

                "then the Left expectation assertion should be correct" {
                    assertionCorrect {
                        result.shouldBeLeft()
                    }

                    assertionCorrect {
                        result shouldBeLeft LEFT_VALUE
                    }

                    assertionCorrect {
                        val left = result.shouldContainLeftInstance()
                        left shouldBe LEFT_VALUE
                    }
                }

                "then the Right expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<$EXPECTED_RIGHT_TYPE> but was:<$result>") {
                        result.shouldBeRight()
                    }

                    assertionIncorrect("expected:<$expectedRightValue> but was:<$result>") {
                        result shouldBeRight RIGHT_VALUE
                    }

                    assertionIncorrect("expected:<$EXPECTED_RIGHT_TYPE> but was:<$result>") {
                        val right = result.shouldContainRightInstance()
                        right shouldBe RIGHT_VALUE
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: Either<String, String> = Either.Left(EXPECTED_VALUE)
                    val actual: Either<String, String> = Either.Left(ACTUAL_VALUE)

                    "then the Left expectation assertion should be incorrect" {
                        assertionIncorrect("expected:<$expected> but was:<$actual>") {
                            actual shouldBeLeft EXPECTED_VALUE
                        }
                    }
                }
            }

            "when a result is the Right" - {
                val result: Either<Int, String> = Either.Right(RIGHT_VALUE)

                "then the Left expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<$EXPECTED_LEFT_TYPE> but was:<$result>") {
                        result.shouldBeLeft()
                    }

                    assertionIncorrect("expected:<$expectedLeftValue> but was:<$result>") {
                        result shouldBeLeft LEFT_VALUE
                    }

                    assertionIncorrect("expected:<$EXPECTED_LEFT_TYPE> but was:<$result>") {
                        val left = result.shouldContainLeftInstance()
                        left shouldBe LEFT_VALUE
                    }
                }

                "then the Right expectation assertion should be correct" {
                    assertionCorrect {
                        result.shouldBeRight()
                    }

                    assertionCorrect {
                        result shouldBeRight RIGHT_VALUE
                    }

                    assertionCorrect {
                        val right = result.shouldContainRightInstance()
                        right shouldBe RIGHT_VALUE
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: Either<String, String> = Either.Right(EXPECTED_VALUE)
                    val actual: Either<String, String> = Either.Right(ACTUAL_VALUE)

                    "then the Right expectation assertion should be incorrect" {
                        assertionIncorrect("expected:<$expected> but was:<$actual>") {
                            actual shouldBeRight EXPECTED_VALUE
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private const val ACTUAL_VALUE = "Actual value"
        private const val EXPECTED_VALUE = "Expected value"
        private const val RIGHT_VALUE = "right value"
        private const val LEFT_VALUE = 42

        private const val EXPECTED_LEFT_TYPE = "Either.Left<Int>"
        private const val EXPECTED_RIGHT_TYPE = "Either.Right<String>"

        private val expectedLeftValue = Either.Left(value = LEFT_VALUE)
        private val expectedRightValue = Either.Right(RIGHT_VALUE)
    }
}
