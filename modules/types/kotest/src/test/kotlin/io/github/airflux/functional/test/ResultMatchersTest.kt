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

package io.github.airflux.functional.test

import io.github.airflux.commons.types.result.Failure
import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.Success
import io.github.airflux.commons.types.result.beFailure
import io.github.airflux.commons.types.result.beSuccess
import io.github.airflux.commons.types.result.shouldBeFailure
import io.github.airflux.commons.types.result.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should

internal class ResultMatchersTest : FreeSpec() {

    init {
        "The Kotest matchers for the Result type" - {

            "when a result is the Success" - {
                val result: Result<Int, String> = Success(SUCCESS_VALUE)

                "then the Success expectation assertion should be correct" {
                    assertionCorrect {
                        result should beSuccess()
                    }

                    assertionCorrect {
                        result.shouldBeSuccess()
                    }

                    assertionCorrect {
                        result should beSuccess(SUCCESS_VALUE)
                    }

                    assertionCorrect {
                        result shouldBeSuccess SUCCESS_VALUE
                    }
                }

                "then the Failure expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<${Failure::class.simpleName}> but was:<$result>") {
                        result.shouldBeFailure()
                    }
                    assertionIncorrect("expected:<${Failure(cause = SUCCESS_VALUE)}> but was:<$result>") {
                        result shouldBeFailure SUCCESS_VALUE
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: Result<String, String> = Success(EXPECTED_VALUE)
                    val actual: Result<String, String> = Success(ACTUAL_VALUE)

                    "then the Success expectation assertion should be incorrect" {
                        assertionIncorrect("expected:<$expected> but was:<$actual>") {
                            actual shouldBeSuccess EXPECTED_VALUE
                        }
                    }
                }
            }

            "when a result is the Failure" - {
                val result: Result<Int, String> = Failure(FAILURE_VALUE)

                "then the Success expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<${Success::class.simpleName}> but was:<$result>") {
                        result.shouldBeSuccess()
                    }
                    assertionIncorrect("expected:<${Success(value = FAILURE_VALUE)}> but was:<$result>") {
                        result shouldBeSuccess FAILURE_VALUE
                    }
                }

                "then the Failure expectation assertion should be correct" {
                    assertionCorrect {
                        result should beFailure()
                    }

                    assertionCorrect {
                        result.shouldBeFailure()
                    }

                    assertionCorrect {
                        result should beFailure(FAILURE_VALUE)
                    }

                    assertionCorrect {
                        result shouldBeFailure FAILURE_VALUE
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: Result<String, String> = Failure(EXPECTED_VALUE)
                    val actual: Result<String, String> = Failure(ACTUAL_VALUE)

                    "then the Failure expectation assertion should be incorrect" {
                        assertionIncorrect("expected:<$expected> but was:<$actual>") {
                            actual shouldBeFailure EXPECTED_VALUE
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
