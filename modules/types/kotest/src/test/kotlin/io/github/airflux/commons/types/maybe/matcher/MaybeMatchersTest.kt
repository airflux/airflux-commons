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

package io.github.airflux.commons.types.maybe.matcher

import io.github.airflux.commons.assertionCorrect
import io.github.airflux.commons.assertionIncorrect
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.Maybe
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class MaybeMatchersTest : FreeSpec() {

    init {
        "The Kotest matchers for the Maybe type" - {

            "when a result is the Some" - {
                val result: Maybe<Int> = Maybe.Some(SUCCESS_VALUE)

                "then the Success expectation assertion should be correct" {
                    assertionCorrect {
                        result.shouldBeSome()
                    }

                    assertionCorrect {
                        result shouldBeSome SUCCESS_VALUE
                    }

                    assertionCorrect {
                        val success = shouldBeSome { result }
                        success shouldBe SUCCESS_VALUE
                    }

                    assertionCorrect {
                        val success = result.shouldContainSomeInstance()
                        success shouldBe SUCCESS_VALUE
                    }
                }

                "then the Failure expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<$EXPECTED_NONE_TYPE> but was:<$result>") {
                        result.shouldBeNone()
                    }
                }

                "when the expected value and actual value is different" - {
                    val expected: Maybe<String> = Maybe.Some(EXPECTED_VALUE)
                    val actual: Maybe<String> = Maybe.Some(ACTUAL_VALUE)

                    "then the Success expectation assertion should be incorrect" {
                        assertionIncorrect("expected:<$expected> but was:<$actual>") {
                            actual shouldBeSome EXPECTED_VALUE
                        }
                    }
                }
            }

            "when a result is the None" - {
                val result: Maybe<Int> = Maybe.None

                "then the Some expectation assertion should be incorrect" {
                    assertionIncorrect("expected:<$EXPECTED_SOME_TYPE> but was:<$result>") {
                        result.shouldBeSome()
                    }

                    assertionIncorrect("expected:<$expectedSomeValue> but was:<$result>") {
                        result shouldBeSome SUCCESS_VALUE
                    }

                    assertionIncorrect("expected:<$EXPECTED_SOME_TYPE> but was:<$result>") {
                        shouldBeSome<Int> { result }
                    }

                    assertionIncorrect("expected:<$EXPECTED_SOME_TYPE> but was:<$result>") {
                        result.shouldContainSomeInstance()
                    }
                }

                "then the None expectation assertion should be correct" {
                    assertionCorrect {
                        result.shouldBeNone()
                    }
                }
            }
        }
    }

    private companion object {
        private const val ACTUAL_VALUE = "Actual value"
        private const val EXPECTED_VALUE = "Expected value"
        private const val SUCCESS_VALUE = 42

        private const val EXPECTED_SOME_TYPE = "Maybe.Some<Int>"
        private const val EXPECTED_NONE_TYPE = "Maybe.None"

        private val expectedSomeValue = Maybe.Some(value = SUCCESS_VALUE)
    }
}
