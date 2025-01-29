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
import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.resultk.BiFailureResultK
import io.github.airflux.commons.types.resultk.ResultK
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class BiFailureResultKMatchersTest : FreeSpec() {

    init {
        "The Kotest matchers for the BiFailureResultK type" - {

            "when a result is the Success" - {
                val result: BiFailureResultK<String, Errors, Exceptions> = ResultK.Success(SUCCESS_VALUE)

                "then the Error expectation assertion should be incorrect" {
                    assertionIncorrect(
                        "expected:<$EXPECTED_ERROR_TYPE> but was:<$result>"
                    ) {
                        result.shouldBeError()
                    }

                    assertionIncorrect(
                        "expected:<$expectedErrorValue> but was:<$result>"
                    ) {
                        result shouldBeError Errors
                    }

                    assertionIncorrect("expected:<$EXPECTED_ERROR_TYPE> but was:<$result>") {
                        result.shouldContainErrorInstance()
                    }
                }

                "then the Exception expectation assertion should be incorrect" {
                    assertionIncorrect(
                        "expected:<$EXPECTED_EXCEPTION_TYPE> but was:<$result>"
                    ) {
                        result.shouldBeException()
                    }

                    assertionIncorrect(
                        "expected:<$expectedExceptionValue> but was:<$result>"
                    ) {
                        result shouldBeException Exceptions
                    }

                    assertionIncorrect("expected:<$EXPECTED_EXCEPTION_TYPE> but was:<$result>") {
                        result.shouldContainExceptionInstance()
                    }
                }
            }

            "when a result is the Failure" - {

                "when the failure is the Error type" - {
                    val error = Fail.error(Errors)
                    val result: BiFailureResultK<String, Errors, Exceptions> = ResultK.Failure(error)

                    "then the Error expectation assertion should be correct" {
                        assertionCorrect {
                            result.shouldBeError()
                        }

                        assertionCorrect {
                            result shouldBeError Errors
                        }

                        assertionCorrect {
                            val error = result.shouldContainErrorInstance()
                            error shouldBe Errors
                        }
                    }

                    "then the Exception expectation assertion should be incorrect" {
                        assertionIncorrect(
                            "expected:<$EXPECTED_EXCEPTION_TYPE> but was:<$result>"
                        ) {
                            result.shouldBeException()
                        }

                        assertionIncorrect(
                            "expected:<$expectedExceptionValue> but was:<$result>"
                        ) {
                            result shouldBeException Exceptions
                        }

                        assertionIncorrect("expected:<$EXPECTED_EXCEPTION_TYPE> but was:<$result>") {
                            result.shouldContainExceptionInstance()
                        }
                    }
                }

                "when the failure is the Exception type" - {
                    val exception = Fail.exception(Exceptions)
                    val result: BiFailureResultK<String, Errors, Exceptions> = ResultK.Failure(exception)

                    "then the Exception expectation assertion should be correct" {
                        assertionCorrect {
                            result.shouldBeException()
                        }

                        assertionCorrect {
                            result shouldBeException Exceptions
                        }

                        assertionCorrect {
                            val exception = result.shouldContainExceptionInstance()
                            exception shouldBe Exceptions
                        }
                    }

                    "then the Error expectation assertion should be incorrect" {
                        assertionIncorrect(
                            "expected:<$EXPECTED_ERROR_TYPE> but was:<$result>"
                        ) {
                            result.shouldBeError()
                        }

                        assertionIncorrect(
                            "expected:<$expectedErrorValue> but was:<$result>"
                        ) {
                            result shouldBeError Errors
                        }

                        assertionIncorrect("expected:<$EXPECTED_ERROR_TYPE> but was:<$result>") {
                            result.shouldContainErrorInstance()
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private const val SUCCESS_VALUE = "success"

        private const val EXPECTED_ERROR_TYPE = "ResultK.Failure<Fail.Error<Errors>>"
        private const val EXPECTED_EXCEPTION_TYPE = "ResultK.Failure<Fail.Exception<Exceptions>>"

        private val expectedErrorValue = ResultK.Failure(Fail.Error(Errors))
        private val expectedExceptionValue = ResultK.Failure(Fail.Exception(Exceptions))
    }

    internal data object Errors

    internal data object Exceptions
}
