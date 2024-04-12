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

internal class MutableSetProxyTest : FreeSpec() {
    init {

        "The type" - {

            "the `size` property" - {

                "when the set is empty" - {
                    val set = createSet()

                    "then this property should return 0" {
                        set.size shouldBe 0
                    }
                }

                "when the set is not empty" - {
                    val set = createSet(FIRST_VALUE)

                    "then this property should return count elements in the set" {
                        set.size shouldBe 1
                    }
                }
            }

            "the `isEmpty` function" - {

                "when the set is empty" - {
                    val set = createSet()

                    "then this function should return true" {
                        set.isEmpty() shouldBe true
                    }
                }

                "when the set is not empty" - {
                    val set = createSet(FIRST_VALUE)

                    "then this function should return false" {
                        set.isEmpty() shouldBe false
                    }
                }
            }

            "the `contains` function" - {

                "when the set is empty" - {
                    val set = createSet()

                    "then this function should return false" {
                        set.contains(FIRST_VALUE) shouldBe false
                    }
                }

                "when the set is not empty" - {
                    val set = createSet(FIRST_VALUE)

                    "when the set contains the value" - {

                        "then this function should return true" {
                            set.contains(FIRST_VALUE) shouldBe true
                        }
                    }

                    "when the set does not contain the value" - {

                        "then this function should return false for the key in the pascal case" {
                            set.contains(SECOND_VALUE) shouldBe false
                        }
                    }
                }
            }

            "the `containsAll` function" - {

                "when the set is empty" - {
                    val set = createSet()

                    "then this function should return false" {
                        set.containsAll(listOf(FIRST_VALUE)) shouldBe false
                    }
                }

                "when the set is not empty" - {

                    "when the set contains the value" - {
                        val set = createSet(FIRST_VALUE, SECOND_VALUE, THIRD_VALUE)

                        "then this function should return true" {
                            set.containsAll(listOf(FIRST_VALUE, THIRD_VALUE)) shouldBe true
                        }
                    }

                    "when the set does not contain the value" - {
                        val set = createSet(FIRST_VALUE, SECOND_VALUE)

                        "then this function should return false" {
                            set.containsAll(listOf(FIRST_VALUE, THIRD_VALUE)) shouldBe false
                        }
                    }
                }
            }

            "the `add` function" - {

                "when the set is empty" - {
                    val set = createSet()
                    val isAdded = set.add(FIRST_VALUE)

                    "then this function should return true" {
                        isAdded shouldBe true
                    }

                    "then the `size` property should return 1" {
                        set.size shouldBe 1
                    }

                    "then the `isEmpty` function should return false" {
                        set.isEmpty() shouldBe false
                    }

                    "then the `contains` function should return true" {
                        set.contains(FIRST_VALUE) shouldBe true
                    }
                }

                "when the set is not empty" - {
                    val set = createSet(FIRST_VALUE)

                    "when adding a value to a set that is already contained there" - {
                        val isAdded = set.add(FIRST_VALUE)

                        "then this function should return false" {
                            isAdded shouldBe false
                        }

                        "then the `size` property should return 1" {
                            set.size shouldBe 1
                        }

                        "then the `contains` function should return true" {
                            set.contains(FIRST_VALUE) shouldBe true
                        }
                    }

                    "when adding a new value to a set" - {
                        val isAdded = set.add(SECOND_VALUE)

                        "then this function should return true" {
                            isAdded shouldBe true
                        }

                        "then the `size` property should return 2" {
                            set.size shouldBe 2
                        }

                        "then the `contains` function should return true" {
                            set.contains(SECOND_VALUE) shouldBe true
                        }
                    }
                }
            }

            "the `addAll` function" - {

                "when the set is empty" - {
                    val items = listOf(FIRST_VALUE)
                    val set = createSet()
                    val isAdded = set.addAll(items)

                    "then this function should return true" {
                        isAdded shouldBe true
                    }

                    "then the `size` property should return the number of added items" {
                        set.size shouldBe items.size
                    }

                    "then the `isEmpty` function should return false" {
                        set.isEmpty() shouldBe false
                    }

                    "then the `contains` function should return true for each added item" {
                        set.contains(FIRST_VALUE) shouldBe true
                    }
                }

                "when the set is not empty" - {

                    "when adding a value to a set that is already contained there" - {
                        val set = createSet(FIRST_VALUE)
                        val isAdded = set.addAll(listOf(FIRST_VALUE))

                        "then this function should return false" {
                            isAdded shouldBe false
                        }

                        "then the `size` property should return 1" {
                            set.size shouldBe 1
                        }

                        "then the `contains` function should return true" {
                            set.contains(FIRST_VALUE) shouldBe true
                        }
                    }

                    "when adding a new value to a set" - {
                        val set = createSet(FIRST_VALUE)
                        val isAdded = set.addAll(listOf(SECOND_VALUE))

                        "then this function should return true" {
                            isAdded shouldBe true
                        }

                        "then the `size` property should return full size of the set" {
                            set.size shouldBe 2
                        }

                        "then the `contains` function should return true for each old item" {
                            set.contains(FIRST_VALUE) shouldBe true
                        }

                        "then the `contains` function should return true for each added item" {
                            set.contains(SECOND_VALUE) shouldBe true
                        }
                    }
                }
            }

            "the `remove` function" - {

                "when the set is empty" - {
                    val set = createSet()
                    val isRemoved = set.remove(FIRST_VALUE)

                    "then this function should return false" {
                        isRemoved shouldBe false
                    }

                    "then the `size` property should return 1" {
                        set.size shouldBe 0
                    }

                    "then the `isEmpty` function should return true" {
                        set.isEmpty() shouldBe true
                    }
                }

                "when the set is not empty" - {
                    val set = createSet(FIRST_VALUE, SECOND_VALUE)
                    val isRemoved = set.remove(FIRST_VALUE)

                    "then this function should return true" {
                        isRemoved shouldBe true
                    }

                    "then the `size` property should return 1" {
                        set.size shouldBe 1
                    }

                    "then the `isEmpty` function should return false" {
                        set.isEmpty() shouldBe false
                    }

                    "then the `contains` function should return false for the removed value" {
                        set.contains(FIRST_VALUE) shouldBe false
                    }

                    "then the `contains` function should return true for the remaining value" {
                        set.contains(SECOND_VALUE) shouldBe true
                    }
                }
            }

            "the `removeAll` function" - {

                "when the set is empty" - {
                    val set = createSet()
                    val isRemoved = set.removeAll(listOf(FIRST_VALUE))

                    "then this function should return false" {
                        isRemoved shouldBe false
                    }

                    "then the `size` property should return 1" {
                        set.size shouldBe 0
                    }

                    "then the `isEmpty` function should return true" {
                        set.isEmpty() shouldBe true
                    }
                }

                "when the set is not empty" - {
                    val set = createSet(FIRST_VALUE, SECOND_VALUE, THIRD_VALUE)
                    val isRemoved = set.removeAll(listOf(FIRST_VALUE, THIRD_VALUE))

                    "then this function should return true" {
                        isRemoved shouldBe true
                    }

                    "then the `size` property should return 1" {
                        set.size shouldBe 1
                    }

                    "then the `isEmpty` function should return false" {
                        set.isEmpty() shouldBe false
                    }

                    "then the `contains` function should return false for each removed value" {
                        set.contains(FIRST_VALUE) shouldBe false
                        set.contains(THIRD_VALUE) shouldBe false
                    }

                    "then the `contains` function should return true for each remaining value" {
                        set.contains(SECOND_VALUE) shouldBe true
                    }
                }
            }

            "the `retainAll` function" - {

                "when the set is empty" - {
                    val set = createSet()
                    val isRemoved = set.retainAll(listOf(FIRST_VALUE))

                    "then this function should return false" {
                        isRemoved shouldBe false
                    }

                    "then the `size` property should return 1" {
                        set.size shouldBe 0
                    }

                    "then the `isEmpty` function should return true" {
                        set.isEmpty() shouldBe true
                    }
                }

                "when the set is not empty" - {

                    "when the original collection contains elements for retains" - {
                        val items = listOf(FIRST_VALUE)
                        val set = createSet(FIRST_VALUE, SECOND_VALUE)
                        val isRemoved = set.retainAll(items)

                        "then this function should return true" {
                            isRemoved shouldBe true
                        }

                        "then the `size` property should return 1" {
                            set.size shouldBe 1
                        }

                        "then the `isEmpty` function should return false" {
                            set.isEmpty() shouldBe false
                        }

                        "then the `contains` function should return true for each retain value" {
                            set.contains(FIRST_VALUE) shouldBe true
                        }

                        "then the `contains` function should return false for each removed value" {
                            set.contains(SECOND_VALUE) shouldBe false
                        }
                    }

                    "when the original collection does not contains elements for retains" - {
                        val items = listOf(THIRD_VALUE)
                        val set = createSet(FIRST_VALUE, SECOND_VALUE)
                        val isRemoved = set.retainAll(items)

                        "then this function should return true" {
                            isRemoved shouldBe true
                        }

                        "then the `size` property should return 0" {
                            set.size shouldBe 0
                        }

                        "then the `isEmpty` function should return ture" {
                            set.isEmpty() shouldBe true
                        }

                        "then the `contains` function should return false for each removed value" {
                            set.contains(FIRST_VALUE) shouldBe false
                            set.contains(SECOND_VALUE) shouldBe false
                        }
                    }
                }
            }

            "the `clean` function" - {

                "when the set is empty" - {
                    val set = createSet()
                    set.clear()

                    "then the `size` property should return 0" {
                        set.size shouldBe 0
                    }

                    "then the `isEmpty` function should return true" {
                        set.isEmpty() shouldBe true
                    }
                }

                "when the set is not empty" - {
                    val set = createSet(FIRST_VALUE)
                    set.clear()

                    "then the `size` property should return 0" {
                        set.size shouldBe 0
                    }

                    "then the `isEmpty` function should return true" {
                        set.isEmpty() shouldBe true
                    }
                }
            }

            "when instances were initialized with the same values" - {
                val left = createSet(SECOND_VALUE)
                val right = createSet(SECOND_VALUE)

                "then the instances should be equal" {
                    left shouldBe right
                }

                "then the instances should have the same hash codes" {
                    left.hashCode() shouldBe right.hashCode()
                }
            }

            "when instances were initialized with different values" - {
                val left = createSet(FIRST_VALUE)
                val right = createSet(SECOND_VALUE)

                "then the instances should not be equal" {
                    left shouldNotBe right
                }

                "then the instances should have different hash codes" {
                    left.hashCode() shouldNotBe right.hashCode()
                }
            }

            "should comply with equals() and hashCode() contract" {
                createSet(FIRST_VALUE, SECOND_VALUE)
                    .shouldComplyWithContractOfEquality(
                        b = createSet(FIRST_VALUE, SECOND_VALUE),
                        c = createSet(FIRST_VALUE, SECOND_VALUE),
                        notEquals = listOf(
                            createSet(FIRST_VALUE),
                            createSet(SECOND_VALUE),
                            createSet(THIRD_VALUE),
                            createSet(SECOND_VALUE, THIRD_VALUE),
                            createSet(FIRST_VALUE, THIRD_VALUE),
                        )
                    )
            }

            "the `toString` function should return the string representation" {
                createSet(FIRST_VALUE, SECOND_VALUE).toString() shouldBe """[$FIRST_VALUE, $SECOND_VALUE]"""
            }
        }
    }

    companion object {
        private const val FIRST_VALUE = "key-1"
        private const val SECOND_VALUE = "key-2"
        private const val THIRD_VALUE = "key-3"

        private fun createSet(vararg items: String): MutableSet<String> =
            AbstractCaseInsensitiveMutableMap.MutableSetProxy(
                target = mutableSetOf<String>().apply { addAll(items) },
                convertToExternal = { this },
                convertToInternal = { this }
            )
    }
}
