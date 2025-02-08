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

package io.github.airflux.commons.types.resultk.matcher

import io.github.airflux.commons.assertionCorrect
import io.github.airflux.commons.assertionIncorrect
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class ResultKMatchersTest : FreeSpec() {

    init {
        "The Kotest matchers for the Result type" - {

            "when a result is the Success" - {
                val result: ResultK<Int, String> = ResultK.Success(SUCCESS_VALUE)

                "then the Success expectation assertion should be correct" {
                    assertionCorrect {
                        result.shouldBeSuccess()
                    }

                    assertionCorrect {
                        result shouldBeSuccess SUCCESS_VALUE
                    }

                    assertionCorrect {
                        val success = shouldBeSuccess { result }
                        success shouldBe SUCCESS_VALUE
                    }

                    assertionCorrect {
                        val success = result.shouldContainSuccessInstance()
                        success shouldBe SUCCESS_VALUE
                    }
                }

                "then the Failure expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<$EXPECTED_FAILURE_TYPE> but was:<$result>") {
                        result.shouldBeFailure()
                    }

                    assertionIncorrect("expected:<$expectedFailureValue> but was:<$result>") {
                        result shouldBeFailure FAILURE_VALUE
                    }

                    assertionIncorrect("expected:<$EXPECTED_FAILURE_TYPE> but was:<$result>") {
                        shouldBeFailure<String> { result }
                    }

                    assertionIncorrect("expected:<$EXPECTED_FAILURE_TYPE> but was:<$result>") {
                        result.shouldContainFailureInstance()
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: ResultK<String, String> = ResultK.Success(EXPECTED_VALUE)
                    val actual: ResultK<String, String> = ResultK.Success(ACTUAL_VALUE)

                    "then the Success expectation assertion should be incorrect" {
                        assertionIncorrect("expected:<$expected> but was:<$actual>") {
                            actual shouldBeSuccess EXPECTED_VALUE
                        }
                    }
                }
            }

            "when a result is the Failure" - {
                val result: ResultK<Int, String> = ResultK.Failure(FAILURE_VALUE)

                "then the Success expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<$EXPECTED_SUCCESS_TYPE> but was:<$result>") {
                        result.shouldBeSuccess()
                    }

                    assertionIncorrect("expected:<$expectedSuccessValue> but was:<$result>") {
                        result shouldBeSuccess SUCCESS_VALUE
                    }

                    assertionIncorrect("expected:<$EXPECTED_SUCCESS_TYPE> but was:<$result>") {
                        shouldBeSuccess<Int> { result }
                    }

                    assertionIncorrect("expected:<$EXPECTED_SUCCESS_TYPE> but was:<$result>") {
                        result.shouldContainSuccessInstance()
                    }
                }

                "then the Failure expectation assertion should be correct" {
                    assertionCorrect {
                        result.shouldBeFailure()
                    }

                    assertionCorrect {
                        result shouldBeFailure FAILURE_VALUE
                    }

                    assertionCorrect {
                        val failure = shouldBeFailure { result }
                        failure shouldBe FAILURE_VALUE
                    }

                    assertionCorrect {
                        val failure = result.shouldContainFailureInstance()
                        failure shouldBe FAILURE_VALUE
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: ResultK<String, String> = ResultK.Failure(EXPECTED_VALUE)
                    val actual: ResultK<String, String> = ResultK.Failure(ACTUAL_VALUE)

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

        private const val EXPECTED_SUCCESS_TYPE = "ResultK.Success<Int>"
        private const val EXPECTED_FAILURE_TYPE = "ResultK.Failure<String>"

        private val expectedSuccessValue = ResultK.Success(value = SUCCESS_VALUE)
        private val expectedFailureValue = ResultK.Failure(FAILURE_VALUE)
    }
}
