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

package io.github.airflux.commons.collections.map

import io.github.airflux.commons.kotest.assertions.shouldComplyWithContractOfEquality
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal typealias Entry<K, T> = AbstractCaseInsensitiveMutableMap.Entry<K, T>

internal class EntryTest : FreeSpec() {

    init {

        "The AbstractCaseInsensitiveMutableMap#Entry type" - {

            "when instance is created" - {
                val entry = Entry(key = FIRST_KEY, value = FIRST_VALUE)

                "then the `key` property should return its value" {
                    entry.key shouldBe FIRST_KEY
                }

                "then the `value` property should return its value" {
                    entry.value shouldBe FIRST_VALUE
                }

                "then the `toString` function should return the string representation" {
                    entry.toString() shouldBe "$FIRST_KEY=$FIRST_VALUE"
                }

                "when the value is changed" - {
                    val previous = entry.setValue(SECOND_VALUE)

                    "then the `setValue` function should return the previous value" {
                        previous shouldBe FIRST_VALUE
                    }

                    "then the `value` property should return its new value" {
                        entry.value shouldBe SECOND_VALUE
                    }
                }
            }

            "when instances were initialized with the same keys and different values" - {
                val left = Entry(key = FIRST_KEY, value = FIRST_VALUE)
                val right = Entry(key = FIRST_KEY, value = FIRST_VALUE)

                "then the instances should be equal" {
                    left shouldBe right
                }

                "then the instances should have the same hash codes" {
                    left.hashCode() shouldBe right.hashCode()
                }
            }

            "when instances were initialized with different keys and the same values" - {
                val left = Entry(key = FIRST_KEY, value = FIRST_VALUE)
                val right = Entry(key = SECOND_KEY, value = FIRST_VALUE)

                "then the instances should not be equal" {
                    left shouldNotBe right
                }

                "then the instances should have different hash codes" {
                    left.hashCode() shouldNotBe right.hashCode()
                }
            }

            "should comply with equals() and hashCode() contract" {
                Entry(key = FIRST_KEY, value = FIRST_VALUE)
                    .shouldComplyWithContractOfEquality(
                        b = Entry(key = FIRST_KEY, value = SECOND_VALUE),
                        c = Entry(key = FIRST_KEY, value = THIRD_VALUE),
                        notEquals = listOf(
                            Entry(key = SECOND_KEY, value = FIRST_VALUE),
                            Entry(key = SECOND_KEY, value = SECOND_VALUE),
                            Entry(key = SECOND_KEY, value = THIRD_VALUE)
                        )
                    )
            }
        }
    }

    companion object {
        private const val FIRST_KEY = "key-1"
        private const val SECOND_KEY = "key-2"

        private const val FIRST_VALUE = "value-1"
        private const val SECOND_VALUE = "value-2"
        private const val THIRD_VALUE = "value-3"
    }
}
