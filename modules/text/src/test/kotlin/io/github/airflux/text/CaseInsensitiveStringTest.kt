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

package io.github.airflux.text

import io.github.airflux.shouldComplyWithContractOfEquality
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class CaseInsensitiveStringTest : FreeSpec() {

    init {

        "The CaseInsensitiveString type" - {

            "when instances were initialized with the same non-empty value" - {
                val left = NON_EMPTY_VALUE.caseInsensitive()
                val right = NON_EMPTY_VALUE.caseInsensitive()

                "then the hash codes of the values should be equal" {
                    left.hashCode() shouldBe right.hashCode()
                }

                "then the values should be equal" {
                    left shouldBe right
                }

                "then the string representation of the values should be equal" {
                    left.toString() shouldBe right.toString()
                }

                "then the values should be equal to the original values" {
                    left.get shouldBe NON_EMPTY_VALUE
                    right.get shouldBe NON_EMPTY_VALUE
                }
            }

            "when instances were initialized with the same non-empty lowercase value" - {
                val left = NON_EMPTY_LOWERCASE_VALUE.caseInsensitive()
                val right = NON_EMPTY_LOWERCASE_VALUE.caseInsensitive()

                "then the hash codes of the values should be equal" {
                    left.hashCode() shouldBe right.hashCode()
                }

                "then the values should be equal" {
                    left shouldBe right
                }

                "then the string representation of the values should be equal" {
                    left.toString() shouldBe right.toString()
                }

                "then the values should be equal to the original values" {
                    left.get shouldBe NON_EMPTY_LOWERCASE_VALUE
                    right.get shouldBe NON_EMPTY_LOWERCASE_VALUE
                }
            }

            "when instances were initialized with the same non-empty uppercase value" - {

                val left = NON_EMPTY_UPPERCASE_VALUE.caseInsensitive()
                val right = NON_EMPTY_UPPERCASE_VALUE.caseInsensitive()

                "then the hash codes of the values should be equal" {
                    left.hashCode() shouldBe right.hashCode()
                }

                "then the values should be equal" {
                    left shouldBe right
                }

                "then the string representation of the values should be equal" {
                    left.toString() shouldBe right.toString()
                }

                "then the values should be equal to the original values" {
                    left.get shouldBe NON_EMPTY_UPPERCASE_VALUE
                    right.get shouldBe NON_EMPTY_UPPERCASE_VALUE
                }
            }

            "when instances were initialized with the same empty value" - {

                val left = EMPTY_VALUE.caseInsensitive()
                val right = EMPTY_VALUE.caseInsensitive()

                "then the hash codes of the values should be equal" {
                    left.hashCode() shouldBe right.hashCode()
                }

                "then the values should be equal" {
                    left shouldBe right
                }

                "then the string representation of the values should be equal" {
                    left.toString() shouldBe right.toString()
                }

                "then the values should be equal to the original values" {
                    left.get shouldBe EMPTY_VALUE
                    right.get shouldBe EMPTY_VALUE
                }
            }

            "when instances were initialized with the same blank value" - {

                val left = BLANK_VALUE.caseInsensitive()
                val right = BLANK_VALUE.caseInsensitive()

                "then the hash codes of the values should be equal" {
                    left.hashCode() shouldBe right.hashCode()
                }

                "then the values should be equal" {
                    left shouldBe right
                }

                "then the string representation of the values should be equal" {
                    left.toString() shouldBe right.toString()
                }

                "then the values should be equal to the original values" {
                    left.get shouldBe BLANK_VALUE
                    right.get shouldBe BLANK_VALUE
                }
            }

            "when instances were initialized with different values" - {
                val left = NON_EMPTY_LOWERCASE_VALUE.caseInsensitive()
                val right = EMPTY_VALUE.caseInsensitive()

                "then the hash codes of the values should not be equal" {
                    left.hashCode() shouldNotBe right.hashCode()
                }

                "then the values should not be equal" {
                    left shouldNotBe right
                }

                "then the string representation of the values should not be equal" {
                    left.toString() shouldNotBe right.toString()
                }

                "then the values should not be equal to the original values" {
                    left.get shouldBe NON_EMPTY_LOWERCASE_VALUE
                    right.get shouldBe EMPTY_VALUE
                }
            }

            "when instances were initialized with values in different registers" - {
                val left = NON_EMPTY_LOWERCASE_VALUE.caseInsensitive()
                val right = NON_EMPTY_UPPERCASE_VALUE.caseInsensitive()

                "then the hash codes of the values should be equal" {
                    left.hashCode() shouldBe right.hashCode()
                }

                "then the values should be equal" {
                    left shouldBe right
                }

                "then the string representation of the values should not be equal" {
                    left.toString() shouldNotBe right.toString()
                }

                "then the values should not be equal to the original values" {
                    left.get shouldBe NON_EMPTY_LOWERCASE_VALUE
                    right.get shouldBe NON_EMPTY_UPPERCASE_VALUE
                }
            }

            "when a CaseInsensitiveString instance is compared to an instance of another type" - {
                val value = NON_EMPTY_LOWERCASE_VALUE.caseInsensitive()

                "then the equals method should return false" {
                    value.equals(NON_EMPTY_VALUE) shouldBe false
                }
            }

            "should comply with equals() and hashCode() contract" - {
                withData(
                    nameFn = { value -> "when instances were initialized with the value '$value'" },
                    listOf(
                        NON_EMPTY_VALUE,
                        NON_EMPTY_LOWERCASE_VALUE,
                        NON_EMPTY_UPPERCASE_VALUE,
                        EMPTY_VALUE,
                        BLANK_VALUE
                    )
                ) { value ->
                    value.caseInsensitive()
                        .shouldComplyWithContractOfEquality(
                            symmetric = value.caseInsensitive(),
                            transitive = value.caseInsensitive()
                        )
                }
            }
        }
    }

    companion object {
        private const val NON_EMPTY_VALUE = "Text"
        private val NON_EMPTY_LOWERCASE_VALUE = NON_EMPTY_VALUE.lowercase()
        private val NON_EMPTY_UPPERCASE_VALUE = NON_EMPTY_VALUE.uppercase()
        private const val EMPTY_VALUE = ""
        private const val BLANK_VALUE = "  "
    }
}
