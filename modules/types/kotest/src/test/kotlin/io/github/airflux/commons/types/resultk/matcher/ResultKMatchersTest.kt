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
import io.github.airflux.commons.types.resultk.ResultK
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

internal class ResultKMatchersTest : FreeSpec() {

    init {
        "The Kotest matchers for the Result type" - {

            "when a result is the Success" - {
                val result: ResultK<Int, String> = ResultK.Success(SUCCESS_VALUE)

                "then the Success expectation assertion should be correct" {
                    assertionCorrect {
                        result should beSuccess()
                    }

                    assertionCorrect {
                        val success = result.shouldBeSuccess()
                        success shouldBe SUCCESS_VALUE
                    }

                    assertionCorrect {
                        result should beSuccess(SUCCESS_VALUE)
                    }

                    assertionCorrect {
                        result shouldBeSuccess SUCCESS_VALUE
                    }
                }

                "then the Failure expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<${ResultK.Failure::class.simpleName}> but was:<$result>") {
                        result.shouldBeFailure()
                    }
                    assertionIncorrect("expected:<${ResultK.Failure(cause = FAILURE_VALUE)}> but was:<$result>") {
                        result shouldBeFailure FAILURE_VALUE
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
                    assertionIncorrect("expected:<${ResultK.Success::class.simpleName}> but was:<$result>") {
                        result.shouldBeSuccess()
                    }
                    assertionIncorrect("expected:<${ResultK.Success(value = SUCCESS_VALUE)}> but was:<$result>") {
                        result shouldBeSuccess SUCCESS_VALUE
                    }
                }

                "then the Failure expectation assertion should be correct" {
                    assertionCorrect {
                        result should beFailure()
                    }

                    assertionCorrect {
                        val failure = result.shouldBeFailure()
                        failure shouldBe FAILURE_VALUE
                    }

                    assertionCorrect {
                        result should beFailure(FAILURE_VALUE)
                    }

                    assertionCorrect {
                        result shouldBeFailure FAILURE_VALUE
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
    }
}
