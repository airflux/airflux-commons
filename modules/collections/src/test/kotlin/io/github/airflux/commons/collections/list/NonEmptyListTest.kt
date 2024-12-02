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

package io.github.airflux.commons.collections.list

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

internal class NonEmptyListTest : FreeSpec() {

    init {

        "The `NonEmptyList` type" - {

            "when only one element is passed to create an instance of the type" - {
                val actual = nonEmptyListOf(FIRST)
                val expected = listOf(FIRST)

                "then the new instance of the type should only contain the passed element" {
                    actual shouldContainExactly expected
                }
            }

            "when a few element is passed to create an instance of the type" - {
                val actual = nonEmptyListOf(FIRST, SECOND)
                val expected = listOf(FIRST, SECOND)

                "then the new instance of the type should only contain the passed elements in the order in which they were passed" {
                    actual shouldContainExactly expected
                }
            }

            "when a head element and empty tail list are passed to create an instance of the type" - {
                val actual = nonEmptyListOf(FIRST, emptyList())
                val expected = listOf(FIRST)

                "then the new instance of the type should only contain the passed head element" {
                    actual shouldContainExactly expected
                }
            }

            "when a head element and non-empty tail list are passed to create an instance of the type" - {
                val actual = nonEmptyListOf(FIRST, listOf(SECOND, THIRD))
                val expected = listOf(FIRST, SECOND, THIRD)

                "then the new instance of the type should all contain the passed elements in the order in which they were passed" {
                    actual shouldContainExactly expected
                }
            }

            "when an empty list is passed to create an instance of the type" - {
                val actual = emptyList<Int>().toNonEmptyListOrNull()

                "then should return the null value" {
                    actual.shouldBeNull()
                }
            }

            "when a non-empty list is passed to create an instance of the type" - {
                val actual = listOf(FIRST, SECOND, THIRD).toNonEmptyListOrNull()
                val expected = listOf(FIRST, SECOND, THIRD)

                "then the new instance of the type should contain all the elements from the list in the order in which they were passed" {
                    actual.shouldNotBeNull()
                    actual shouldContainExactly expected
                }
            }

            "when a new element is added to the instance of the type" - {
                val actual = nonEmptyListOf(FIRST) + SECOND
                val expected = listOf(FIRST, SECOND)

                "then the new instance of the type should contain the original elements and the passed element in the order in which they were passed" {
                    actual shouldContainExactly expected
                }
            }

            "when a list of elements is added to the instance of the type" - {
                val actual = nonEmptyListOf(FIRST) + listOf(SECOND, THIRD)
                val expected = listOf(FIRST, SECOND, THIRD)

                "then the new instance of the type should contain elements from the original instance and the passed elements in the order in which they were passed" {
                    actual shouldContainExactly expected
                }
            }

            "when another instance of the type is added to the instance of the type" - {
                val actual = nonEmptyListOf(FIRST) + nonEmptyListOf(SECOND, THIRD)
                val expected = listOf(FIRST, SECOND, THIRD)

                "then the new instance of the type should contain elements from both instances in the order in which they were in the originals" {
                    actual shouldContainExactly expected
                }
            }

            "when the `map` function calling" - {
                val actual = nonEmptyListOf(FIRST, SECOND).map { it + 1 }
                val expected = listOf(FIRST + 1, SECOND + 1)

                "then this functions should return the list of transformed values" {
                    actual shouldContainExactly expected
                }
            }

            "when the `flatMap` function calling" - {
                val actual = nonEmptyListOf(FIRST, SECOND).flatMap { nonEmptyListOf(it, it + 1) }
                val expected = listOf(FIRST, FIRST + 1, SECOND, SECOND + 1)

                "then this functions should return the list of transformed values" {
                    actual shouldContainExactly expected
                }
            }
        }
    }

    internal companion object {
        private const val FIRST = 1
        private const val SECOND = 2
        private const val THIRD = 3
    }
}
