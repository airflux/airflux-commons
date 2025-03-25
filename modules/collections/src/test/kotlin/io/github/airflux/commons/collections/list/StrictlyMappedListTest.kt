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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class StrictlyMappedListTest : FreeSpec() {

    companion object {
        private const val FIRST_USER_ID = "9a222dba-a59b-4df8-b118-8c7bc68db501"
        private const val SECOND_USER_ID = "b0fdc91b-ef91-49d5-bed3-20f4bb47e93d"
    }

    init {

        "The StrictlyMappedList type" - {

            "when the collection does not contain any elements" - {
                val list = StrictlyMappedList.Empty

                "then the `isEmpty` property should return true" {
                    list.isEmpty shouldBe true
                }

                "then the `isNotEmpty` property should return false" {
                    list.isNotEmpty shouldBe false
                }

                "then the `get` function should return null by any key" {
                    val result = list[UserBundle]
                    result.shouldBeNull()
                }

                "then the `contains` function should return false by any key" {
                    val result = UserBundle in list
                    result shouldBe false
                }

                "then the `fold` function should return initial value" {
                    val result = list.fold(mutableListOf<String>()) { acc, elem ->
                        acc.apply { add(elem.toString()) }
                    }

                    result shouldBe emptyList()
                }
            }

            "when the collection contains some elements" - {
                val list = StrictlyMappedList.Empty + UserBundle(FIRST_USER_ID) + OrderBundle()

                "then the `isEmpty` property should return false" {
                    list.isEmpty shouldBe false
                }

                "then the `isNotEmpty` property should return true" {
                    list.isNotEmpty shouldBe true
                }

                "then the `get` function should return the value for every element in the collection" {
                    list[UserBundle].shouldNotBeNull()
                    list[OrderBundle].shouldNotBeNull()
                }

                "then the `get` function should return null for any element not contained in the collection" {
                    list[BillBundle].shouldBeNull()
                }

                "then the `contains` function should return true for every element in the collection" {
                    val result = UserBundle in list
                    result shouldBe true
                }

                "then the `contains` function should return false for any element not contained in the collection" {
                    val result = BillBundle in list
                    result shouldBe false
                }

                "then the `fold` function should return the list of names of all elements" {
                    val result = list.fold(mutableListOf<String>()) { acc, elem ->
                        acc.apply { add(elem.toString()) }
                    }

                    result shouldBe listOf(OrderBundle.NAME, UserBundle.NAME)
                }
            }

            "when an element is a collection" - {
                val list: StrictlyMappedList = UserBundle(FIRST_USER_ID)

                "then the `isEmpty` property should return false" {
                    list.isEmpty shouldBe false
                }

                "then the `isNotEmpty` property should return true" {
                    list.isNotEmpty shouldBe true
                }

                "then the `get` function should return the value if the key of the element match" {
                    list[UserBundle].shouldNotBeNull()
                }

                "then the `get` function should return null if the element's key does not match" {
                    list[OrderBundle].shouldBeNull()
                }

                "then the `contains` function should return true if the key of the element match" {
                    val result = UserBundle in list
                    result shouldBe true
                }

                "then the `contains` function should return false if the element's key does not match" {
                    val result = OrderBundle in list
                    result shouldBe false
                }

                "then the `fold` function should return the name of the element" {
                    val result = list.fold(mutableListOf<String>()) { acc, elem ->
                        acc.apply { add(elem.toString()) }
                    }

                    result shouldBe listOf(UserBundle.NAME)
                }

                "when a new element is added to the collection" - {
                    val newDataBundle = list + OrderBundle()

                    "then the `isEmpty` property should return false" {
                        newDataBundle.isEmpty shouldBe false
                    }

                    "then the `isNotEmpty` property should return true" {
                        newDataBundle.isNotEmpty shouldBe true
                    }

                    "then the `get` function should return the value for every element in the collection" {
                        newDataBundle[UserBundle].shouldNotBeNull()
                        newDataBundle[OrderBundle].shouldNotBeNull()
                    }

                    "then the `get` function should return null for any element not contained in the collection" {
                        newDataBundle[BillBundle].shouldBeNull()
                    }

                    "then the `contains` function should return true for every element in the collection" {
                        val result = UserBundle in newDataBundle
                        result shouldBe true
                    }

                    "then the `contains` function should return false for any element not contained in the collection" {
                        val result = BillBundle in newDataBundle
                        result shouldBe false
                    }

                    "then the `fold` function should return the list of names of all elements" {
                        val result = newDataBundle.fold(mutableListOf<String>()) { acc, elem ->
                            acc.apply { add(elem.toString()) }
                        }

                        result shouldBe listOf(OrderBundle.NAME, UserBundle.NAME)
                    }
                }
            }

            "when a duplicate element is added to the collection" - {
                val listWithDuplicate =
                    StrictlyMappedList.Empty + UserBundle(FIRST_USER_ID) + UserBundle(SECOND_USER_ID)

                "then the `get` function should return the value of the last added element" {
                    val result = listWithDuplicate[UserBundle]
                    result.shouldNotBeNull()
                    result.id shouldBe SECOND_USER_ID
                }
            }
        }
    }

    class UserBundle(val id: String) : AbstractStrictlyMappedListElement<UserBundle>(UserBundle) {
        override fun toString(): String = NAME
        override fun equals(other: Any?): Boolean = this === other || other is UserBundle
        override fun hashCode(): Int = id.hashCode()

        companion object Key : StrictlyMappedList.Key<UserBundle> {
            const val NAME = "UserBundle"
        }
    }

    class OrderBundle : AbstractStrictlyMappedListElement<OrderBundle>(OrderBundle) {
        override fun toString(): String = NAME
        override fun equals(other: Any?): Boolean = this === other || other is OrderBundle
        override fun hashCode(): Int = OrderBundle.hashCode()

        companion object Key : StrictlyMappedList.Key<OrderBundle> {
            const val NAME = "OrderBundle"
        }
    }

    class BillBundle : AbstractStrictlyMappedListElement<BillBundle>(BillBundle) {
        override fun toString(): String = NAME
        override fun equals(other: Any?): Boolean = this === other || other is BillBundle
        override fun hashCode(): Int = BillBundle.hashCode()

        companion object Key : StrictlyMappedList.Key<BillBundle> {
            const val NAME = "BillBundle"
        }
    }
}
