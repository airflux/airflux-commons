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

package io.github.airflux.commons.types.fail.matcher

import io.github.airflux.commons.assertionCorrect
import io.github.airflux.commons.assertionIncorrect
import io.github.airflux.commons.types.fail.Fail
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

internal class FailMatchersTest : FreeSpec() {

    init {
        "The Kotest matchers for the Fail type" - {

            "when a result is the Error" - {
                val result: Fail<Int, String> = Fail.Error(ERROR_VALUE)

                "then the Error expectation assertion should be correct" {
                    assertionCorrect {
                        result should beError()
                    }

                    assertionCorrect {
                        val error = result.shouldBeError()
                        error shouldBe ERROR_VALUE
                    }

                    assertionCorrect {
                        result should beError(ERROR_VALUE)
                    }

                    assertionCorrect {
                        result shouldBeError ERROR_VALUE
                    }
                }

                "then the Right expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<${Fail.Exception::class.simpleName}> but was:<$result>") {
                        result.shouldBeException()
                    }
                    assertionIncorrect("expected:<${Fail.Exception(value = EXCEPTION_VALUE)}> but was:<$result>") {
                        result shouldBeException EXCEPTION_VALUE
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: Fail<String, String> = Fail.Error(EXPECTED_VALUE)
                    val actual: Fail<String, String> = Fail.Error(ACTUAL_VALUE)

                    "then the Error expectation assertion should be incorrect" {
                        assertionIncorrect("expected:<$expected> but was:<$actual>") {
                            actual shouldBeError EXPECTED_VALUE
                        }
                    }
                }
            }

            "when a result is the Right" - {
                val result: Fail<Int, String> = Fail.Exception(EXCEPTION_VALUE)

                "then the Error expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<${Fail.Error::class.simpleName}> but was:<$result>") {
                        result.shouldBeError()
                    }
                    assertionIncorrect("expected:<${Fail.Error(value = ERROR_VALUE)}> but was:<$result>") {
                        result shouldBeError ERROR_VALUE
                    }
                }

                "then the Exception expectation assertion should be correct" {
                    assertionCorrect {
                        result should beException()
                    }

                    assertionCorrect {
                        val exception = result.shouldBeException()
                        exception shouldBe EXCEPTION_VALUE
                    }

                    assertionCorrect {
                        result should beException(EXCEPTION_VALUE)
                    }

                    assertionCorrect {
                        result shouldBeException EXCEPTION_VALUE
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: Fail<String, String> = Fail.Exception(EXPECTED_VALUE)
                    val actual: Fail<String, String> = Fail.Exception(ACTUAL_VALUE)

                    "then the Right expectation assertion should be incorrect" {
                        assertionIncorrect("expected:<$expected> but was:<$actual>") {
                            actual shouldBeException EXPECTED_VALUE
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private const val ACTUAL_VALUE = "Actual value"
        private const val EXPECTED_VALUE = "Expected value"
        private const val ERROR_VALUE = 42
        private const val EXCEPTION_VALUE = "Failed"
    }
}
