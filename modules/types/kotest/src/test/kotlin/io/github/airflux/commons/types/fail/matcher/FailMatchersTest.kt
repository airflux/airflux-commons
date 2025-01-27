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
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.Fail
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class FailMatchersTest : FreeSpec() {

    init {
        "The Kotest matchers for the Fail type" - {

            "when a result is the Error" - {
                val result: Fail<Int, String> = Fail.Error(ERROR_VALUE)

                "then the Error expectation assertion should be correct" {
                    assertionCorrect {
                        result.shouldBeError()
                    }

                    assertionCorrect {
                        result shouldBeError ERROR_VALUE
                    }

                    assertionCorrect {
                        val error = result.shouldContainErrorInstance()
                        error shouldBe ERROR_VALUE
                    }
                }

                "then the Exception expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<$EXPECTED_EXCEPTION_TYPE> but was:<$result>") {
                        result.shouldBeException()
                    }

                    assertionIncorrect("expected:<$expectedExceptionValue> but was:<$result>") {
                        result shouldBeException EXCEPTION_VALUE
                    }

                    assertionIncorrect("expected:<$EXPECTED_EXCEPTION_TYPE> but was:<$result>") {
                        result.shouldContainExceptionInstance()
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

            "when a result is the Exception" - {
                val result: Fail<Int, String> = Fail.Exception(EXCEPTION_VALUE)

                "then the Exception expectation assertion should be correct" {
                    assertionCorrect {
                        result.shouldBeException()
                    }

                    assertionCorrect {
                        result shouldBeException EXCEPTION_VALUE
                    }

                    assertionCorrect {
                        val exception = result.shouldContainExceptionInstance()
                        exception shouldBe EXCEPTION_VALUE
                    }
                }

                "then the Error expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<$EXPECTED_ERROR_TYPE> but was:<$result>") {
                        result.shouldBeError()
                    }

                    assertionIncorrect("expected:<$expectedErrorValue> but was:<$result>") {
                        result shouldBeError ERROR_VALUE
                    }

                    assertionIncorrect("expected:<$EXPECTED_ERROR_TYPE> but was:<$result>") {
                        result.shouldContainErrorInstance()
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

        private const val EXPECTED_ERROR_TYPE = "Fail.Error<Int>"
        private const val EXPECTED_EXCEPTION_TYPE = "Fail.Exception<String>"

        private val expectedErrorValue = Fail.Error(value = ERROR_VALUE)
        private val expectedExceptionValue = Fail.Exception(EXCEPTION_VALUE)
    }
}
