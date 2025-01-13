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
import io.github.airflux.commons.types.either.Either
import io.github.airflux.commons.types.either.Left
import io.github.airflux.commons.types.either.Right
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should

internal class EitherMatchersTest : FreeSpec() {

    init {
        "The Kotest matchers for the Either type" - {

            "when a result is the Left" - {
                val result: Either<Int, String> = Left(SUCCESS_VALUE)

                "then the Left expectation assertion should be correct" {
                    assertionCorrect {
                        result should beLeft()
                    }

                    assertionCorrect {
                        result.shouldBeLeft()
                    }

                    assertionCorrect {
                        result should beLeft(SUCCESS_VALUE)
                    }

                    assertionCorrect {
                        result shouldBeLeft SUCCESS_VALUE
                    }
                }

                "then the Right expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<${Right::class.simpleName}> but was:<$result>") {
                        result.shouldBeRight()
                    }
                    assertionIncorrect("expected:<${Right(value = SUCCESS_VALUE)}> but was:<$result>") {
                        result shouldBeRight SUCCESS_VALUE
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: Either<String, String> = Left(EXPECTED_VALUE)
                    val actual: Either<String, String> = Left(ACTUAL_VALUE)

                    "then the Left expectation assertion should be incorrect" {
                        assertionIncorrect("expected:<$expected> but was:<$actual>") {
                            actual shouldBeLeft EXPECTED_VALUE
                        }
                    }
                }
            }

            "when a result is the Right" - {
                val result: Either<Int, String> = Right(FAILURE_VALUE)

                "then the Left expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<${Left::class.simpleName}> but was:<$result>") {
                        result.shouldBeLeft()
                    }
                    assertionIncorrect("expected:<${Left(value = FAILURE_VALUE)}> but was:<$result>") {
                        result shouldBeLeft FAILURE_VALUE
                    }
                }

                "then the Right expectation assertion should be correct" {
                    assertionCorrect {
                        result should beRight()
                    }

                    assertionCorrect {
                        result.shouldBeRight()
                    }

                    assertionCorrect {
                        result should beRight(FAILURE_VALUE)
                    }

                    assertionCorrect {
                        result shouldBeRight FAILURE_VALUE
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: Either<String, String> = Right(EXPECTED_VALUE)
                    val actual: Either<String, String> = Right(ACTUAL_VALUE)

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
        private const val FAILURE_VALUE = "Failed"
        private const val SUCCESS_VALUE = 42
    }
}
