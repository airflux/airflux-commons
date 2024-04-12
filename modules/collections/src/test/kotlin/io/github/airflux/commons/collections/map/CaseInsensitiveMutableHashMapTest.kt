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
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@Suppress("LargeClass")
internal class CaseInsensitiveMutableHashMapTest : FreeSpec() {

    init {

        "The CaseInsensitiveMutableHashMap type" - {

            "the `size` property" - {

                "when the map is empty" - {
                    val map = createMap()

                    "then this property should return 0" {
                        map.size shouldBe 0
                    }
                }

                "when the map is not empty" - {
                    val map = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)

                    "then this property should return count elements in the map" {
                        map.size shouldBe 1
                    }
                }
            }

            "the `isEmpty` function" - {

                "when the map is empty" - {
                    val map = createMap()

                    "then this function should return true" {
                        map.isEmpty() shouldBe true
                    }
                }

                "when the map is not empty" - {
                    val map = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)

                    "then this function should return false" {
                        map.isEmpty() shouldBe false
                    }
                }
            }

            "the `containsKey` function" - {

                "when the map is empty" - {
                    val map = createMap()

                    "then this function should return false for the key in the pascal case" {
                        map.containsKey(PASCAL_FIRST_KEY_VALUE) shouldBe false
                    }

                    "then this function should return false for the key in the lower case" {
                        map.containsKey(LOWERCASE_FIRST_KEY_VALUE) shouldBe false
                    }

                    "then this function should return false for the key in the upper case" {
                        map.containsKey(UPPERCASE_FIRST_KEY_VALUE) shouldBe false
                    }
                }

                "when the map is not empty" - {
                    val map = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)

                    "when the map contains the value" - {

                        "then this function should return true for the key in the pascal case" {
                            map.containsKey(PASCAL_FIRST_KEY_VALUE) shouldBe true
                        }

                        "then this function should return true for the key in the lower case" {
                            map.containsKey(LOWERCASE_FIRST_KEY_VALUE) shouldBe true
                        }

                        "then this function should return true for the key in the upper case" {
                            map.containsKey(UPPERCASE_FIRST_KEY_VALUE) shouldBe true
                        }
                    }

                    "when the map does not contain the value" - {

                        "then this function should return false for the key in the pascal case" {
                            map.containsKey(PASCAL_SECOND_KEY_VALUE) shouldBe false
                        }

                        "then this function should return false for the key in the lower case" {
                            map.containsKey(LOWERCASE_SECOND_KEY_VALUE) shouldBe false
                        }

                        "then this function should return false for the key in the upper case" {
                            map.containsKey(UPPERCASE_SECOND_KEY_VALUE) shouldBe false
                        }
                    }
                }
            }

            "the `containsValue` function" - {

                "when the map is empty" - {
                    val map = createMap()

                    "then this function should return false" {
                        map.containsValue(PASCAL_FIRST_KEY_VALUE) shouldBe false
                    }
                }

                "when the map is not empty" - {
                    val map =
                        createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)

                    "when the map contains the value" - {

                        "then this function should return true" {
                            map.containsValue(FIRST_VALUE) shouldBe true
                        }
                    }

                    "when the map does not contain the value" - {

                        "then this function should return false" {
                            map.containsValue(SECOND_VALUE) shouldBe false
                        }
                    }
                }
            }

            "the `get` function" - {

                "when the map is empty" - {
                    val map = createMap()

                    "then this function should return null for the key in the pascal case" {
                        map[PASCAL_FIRST_KEY_VALUE].shouldBeNull()
                    }

                    "then this function should return null for the key in the lower case" {
                        map[LOWERCASE_FIRST_KEY_VALUE].shouldBeNull()
                    }

                    "then this function should return null for the key in the upper case" {
                        map[UPPERCASE_FIRST_KEY_VALUE].shouldBeNull()
                    }
                }

                "when the map is not empty" - {
                    val map = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)

                    "when the map contains the value" - {

                        "then this function should return value for the key in the pascal case" {
                            map[PASCAL_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                        }

                        "then this function should return value for the key in the lower case" {
                            map[LOWERCASE_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                        }

                        "then this function should return value for the key in the upper case" {
                            map[UPPERCASE_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                        }
                    }

                    "when the map does not contain the value" - {

                        "then this function should return null for the key in the pascal case" {
                            map[PASCAL_SECOND_KEY_VALUE].shouldBeNull()
                        }

                        "then this function should return null for the key in the lower case" {
                            map[LOWERCASE_SECOND_KEY_VALUE].shouldBeNull()
                        }

                        "then this function should return null for the key in the upper case" {
                            map[UPPERCASE_SECOND_KEY_VALUE].shouldBeNull()
                        }
                    }
                }
            }

            "the `put` function" - {

                "when the map is empty" - {

                    "when the key in the pascal case" - {
                        val map = createMap()
                        val previous = map.put(PASCAL_FIRST_KEY_VALUE, FIRST_VALUE)

                        "then this function should return null as the previous value" {
                            previous.shouldBeNull()
                        }

                        "then the `get` function should return the inserted value for the key" {
                            map[PASCAL_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                        }
                    }

                    "when the key in the lower case" - {
                        val map = createMap()
                        val previous = map.put(LOWERCASE_FIRST_KEY_VALUE, FIRST_VALUE)

                        "then this function should return null as the previous value" {
                            previous.shouldBeNull()
                        }

                        "then the `get` function should return the inserted value for the key" {
                            map[LOWERCASE_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                        }
                    }

                    "when the key in the upper case" - {
                        val map = createMap()
                        val previous = map.put(UPPERCASE_FIRST_KEY_VALUE, FIRST_VALUE)

                        "then this function should return null as the previous value" {
                            previous.shouldBeNull()
                        }

                        "then the `get` function should return the inserted value for the key" {
                            map[UPPERCASE_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                        }
                    }
                }

                "when the map is not empty" - {

                    "when the map contains the key" - {

                        "when the key for inserting in the pascal case" - {
                            val map = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)
                            val previous = map.put(PASCAL_FIRST_KEY_VALUE, SECOND_VALUE)

                            "then this function should return the previous value" {
                                previous shouldBe FIRST_VALUE
                            }

                            "then the `get` function should return the inserted value for the key" {
                                map[PASCAL_FIRST_KEY_VALUE] shouldBe SECOND_VALUE
                            }
                        }

                        "when the key for inserting in the lower case" - {
                            val map = createMap(LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                            val previous = map.put(LOWERCASE_FIRST_KEY_VALUE, SECOND_VALUE)

                            "then this function should return the previous value" {
                                previous shouldBe FIRST_VALUE
                            }

                            "then the `get` function should return the inserted value for the key" {
                                map[LOWERCASE_FIRST_KEY_VALUE] shouldBe SECOND_VALUE
                            }
                        }

                        "when the key for inserting in the upper case" - {
                            val map = createMap(UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                            val previous = map.put(UPPERCASE_FIRST_KEY_VALUE, SECOND_VALUE)

                            "then this function should return the previous value" {
                                previous shouldBe FIRST_VALUE
                            }

                            "then the `get` function should return the inserted value for the key" {
                                map[UPPERCASE_FIRST_KEY_VALUE] shouldBe SECOND_VALUE
                            }
                        }
                    }

                    "when the map does not contain the key" - {

                        "when the key for inserting in the pascal case" - {
                            val map = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)
                            val previous = map.put(PASCAL_SECOND_KEY_VALUE, SECOND_VALUE)

                            "then this function should return null as the previous value" {
                                previous.shouldBeNull()
                            }

                            "then the `get` function should return the inserted value for the key" {
                                map[PASCAL_SECOND_KEY_VALUE] shouldBe SECOND_VALUE
                            }
                        }

                        "when the key for inserting in the lower case" - {
                            val map = createMap(LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                            val previous = map.put(LOWERCASE_SECOND_KEY_VALUE, SECOND_VALUE)

                            "then this function should return null as the previous value" {
                                previous.shouldBeNull()
                            }

                            "then the `get` function should return the inserted value for the key" {
                                map[LOWERCASE_SECOND_KEY_VALUE] shouldBe SECOND_VALUE
                            }
                        }

                        "when the key for inserting in the upper case" - {
                            val map = createMap(UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                            val previous = map.put(UPPERCASE_SECOND_KEY_VALUE, SECOND_VALUE)

                            "then this function should return null as the previous value" {
                                previous.shouldBeNull()
                            }

                            "then the `get` function should return the inserted value for the key" {
                                map[UPPERCASE_SECOND_KEY_VALUE] shouldBe SECOND_VALUE
                            }
                        }
                    }
                }
            }

            "the `putAll` function" - {

                "when the map is empty" - {

                    "when the key in the pascal case" - {
                        val items = mapOf(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)
                        val map = createMap()
                        map.putAll(items)

                        "then the `get` function should return the inserted value for the key" {
                            map[PASCAL_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                        }

                        "then the `size` property should return the number of added items" {
                            map.size shouldBe items.size
                        }

                        "then the `isEmpty` function should return false" {
                            map.isEmpty() shouldBe false
                        }
                    }

                    "when the key in the lower case" - {
                        val items = mapOf(LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                        val map = createMap()
                        map.putAll(items)

                        "then the `get` function should return the inserted value for the key" {
                            map[LOWERCASE_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                        }

                        "then the `size` property should return the number of added items" {
                            map.size shouldBe items.size
                        }

                        "then the `isEmpty` function should return false" {
                            map.isEmpty() shouldBe false
                        }
                    }

                    "when the key in the upper case" - {
                        val items = mapOf(UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                        val map = createMap()
                        map.putAll(items)

                        "then the `get` function should return the inserted value for the key" {
                            map[UPPERCASE_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                        }

                        "then the `size` property should return the number of added items" {
                            map.size shouldBe items.size
                        }

                        "then the `isEmpty` function should return false" {
                            map.isEmpty() shouldBe false
                        }
                    }
                }

                "when the map is not empty" - {

                    "when the map contains the key" - {

                        "when the key for inserting in the pascal case" - {
                            val items = mapOf(PASCAL_FIRST_KEY_VALUE to SECOND_VALUE)
                            val map = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)
                            map.putAll(items)

                            "then the `get` function should return a new value for a key that was already contained in the map" {
                                map[PASCAL_FIRST_KEY_VALUE] shouldBe SECOND_VALUE
                            }
                        }

                        "when the key for inserting in the lower case" - {
                            val items = mapOf(LOWERCASE_FIRST_KEY_VALUE to SECOND_VALUE)
                            val map = createMap(LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                            map.putAll(items)

                            "then the `get` function should return a new value for a key that was already contained in the map" {
                                map[LOWERCASE_FIRST_KEY_VALUE] shouldBe SECOND_VALUE
                            }
                        }

                        "when the key for inserting in the upper case" - {
                            val items = mapOf(UPPERCASE_FIRST_KEY_VALUE to SECOND_VALUE)
                            val map = createMap(UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                            map.putAll(items)

                            "then the `get` function should return a new value for a key that was already contained in the map" {
                                map[UPPERCASE_FIRST_KEY_VALUE] shouldBe SECOND_VALUE
                            }
                        }
                    }

                    "when the map does not contain the key" - {

                        "when the key for inserting in the pascal case" - {
                            val items = mapOf(PASCAL_SECOND_KEY_VALUE to SECOND_VALUE)
                            val map = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)
                            map.putAll(items)

                            "then the `get` function should return the inserted value for the key" {
                                map[PASCAL_SECOND_KEY_VALUE] shouldBe SECOND_VALUE
                            }

                            "then the `get` function should return the value for the key that was already contained in the map" {
                                map[PASCAL_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                            }
                        }

                        "when the key for inserting in the lower case" - {
                            val items = mapOf(LOWERCASE_SECOND_KEY_VALUE to SECOND_VALUE)
                            val map = createMap(LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                            map.putAll(items)

                            "then the `get` function should return the inserted value for the key" {
                                map[LOWERCASE_SECOND_KEY_VALUE] shouldBe SECOND_VALUE
                            }

                            "then the `get` function should return the value for the key that was already contained in the map" {
                                map[LOWERCASE_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                            }
                        }

                        "when the key for inserting in the upper case" - {
                            val items = mapOf(UPPERCASE_SECOND_KEY_VALUE to SECOND_VALUE)
                            val map = createMap(UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE)
                            map.putAll(items)

                            "then the `get` function should return the inserted value for the key" {
                                map[UPPERCASE_SECOND_KEY_VALUE] shouldBe SECOND_VALUE
                            }

                            "then the `get` function should return the value for the key that was already contained in the map" {
                                map[UPPERCASE_FIRST_KEY_VALUE] shouldBe FIRST_VALUE
                            }
                        }
                    }
                }
            }

            "the `remove` function" - {

                "when the map is empty" - {

                    "when the key in the pascal case" - {
                        val map = createMap()
                        val previous = map.remove(PASCAL_FIRST_KEY_VALUE)

                        "then this function should return null as the previous value" {
                            previous.shouldBeNull()
                        }
                    }

                    "when the key in the lower case" - {
                        val map = createMap()
                        val previous = map.remove(LOWERCASE_FIRST_KEY_VALUE)

                        "then this function should return null as the previous value" {
                            previous.shouldBeNull()
                        }
                    }

                    "when the key in the upper case" - {
                        val map = createMap()
                        val previous = map.remove(UPPERCASE_FIRST_KEY_VALUE)

                        "then this function should return null as the previous value" {
                            previous.shouldBeNull()
                        }
                    }
                }

                "when the map is not empty" - {

                    "when the key in the pascal case" - {
                        val key = PASCAL_FIRST_KEY_VALUE

                        "when the key for removing the item in the pascal case" - {
                            val map = createMap()
                                .apply { put(key, FIRST_VALUE) }
                            val previous = map.remove(PASCAL_FIRST_KEY_VALUE)

                            "then this function should return the removed value" {
                                previous shouldBe FIRST_VALUE
                            }
                        }

                        "when the key for removing the item in the lower case" - {
                            val map = createMap()
                                .apply { put(key, FIRST_VALUE) }
                            val previous = map.remove(LOWERCASE_FIRST_KEY_VALUE)

                            "then this function should return the removed value" {
                                previous shouldBe FIRST_VALUE
                            }
                        }

                        "when the key for removing the item in the lower case" - {
                            val map = createMap()
                                .apply { put(key, FIRST_VALUE) }
                            val previous = map.remove(UPPERCASE_FIRST_KEY_VALUE)

                            "then this function should return the removed value" {
                                previous shouldBe FIRST_VALUE
                            }
                        }
                    }

                    "when the key in the lower case" - {
                        val key = LOWERCASE_FIRST_KEY_VALUE

                        "when the key for removing the item in the pascal case" - {
                            val map = createMap()
                                .apply { put(key, FIRST_VALUE) }
                            val previous = map.remove(PASCAL_FIRST_KEY_VALUE)

                            "then this function should return the removed value" {
                                previous shouldBe FIRST_VALUE
                            }
                        }

                        "when the key for removing the item in the lower case" - {
                            val map = createMap()
                                .apply { put(key, FIRST_VALUE) }
                            val previous = map.remove(LOWERCASE_FIRST_KEY_VALUE)

                            "then this function should return the removed value" {
                                previous shouldBe FIRST_VALUE
                            }
                        }

                        "when the key for removing the item in the lower case" - {
                            val map = createMap(key to FIRST_VALUE)
                            val previous = map.remove(UPPERCASE_FIRST_KEY_VALUE)

                            "then this function should return the removed value" {
                                previous shouldBe FIRST_VALUE
                            }
                        }
                    }

                    "when the key in the upper case" - {
                        val key = UPPERCASE_FIRST_KEY_VALUE

                        "when the key for removing the item in the pascal case" - {
                            val map = createMap(key to FIRST_VALUE)
                            val previous = map.remove(PASCAL_FIRST_KEY_VALUE)

                            "then this function should return the removed value" {
                                previous shouldBe FIRST_VALUE
                            }
                        }

                        "when the key for removing the item in the lower case" - {
                            val map = createMap(key to FIRST_VALUE)
                            val previous = map.remove(LOWERCASE_FIRST_KEY_VALUE)

                            "then this function should return the removed value" {
                                previous shouldBe FIRST_VALUE
                            }
                        }

                        "when the key for removing the item in the lower case" - {
                            val map = createMap()
                                .apply { put(key, FIRST_VALUE) }
                            val previous = map.remove(UPPERCASE_FIRST_KEY_VALUE)

                            "then this function should return the removed value" {
                                previous shouldBe FIRST_VALUE
                            }
                        }
                    }
                }
            }

            "the `clean` function" - {

                "when the map is empty" - {
                    val map = createMap()
                    map.clear()

                    "then the `size` property should return 0" {
                        map.size shouldBe 0
                    }

                    "then the `isEmpty` function should return true" {
                        map.isEmpty() shouldBe true
                    }
                }

                "when the map is not empty" - {
                    val map = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)
                    map.clear()

                    "then the `size` property should return 0" {
                        map.size shouldBe 0
                    }

                    "then the `isEmpty` function should return true" {
                        map.isEmpty() shouldBe true
                    }
                }
            }

            "the `keys` property" - {

                "when the map is empty" - {
                    val map = createMap()

                    "then this property should return an empty set" {
                        map.keys.isEmpty() shouldBe true
                    }
                }

                "when the map is not empty" - {

                    "when the keys in the pascal case" - {
                        val map = createMap(
                            PASCAL_FIRST_KEY_VALUE to FIRST_VALUE,
                            PASCAL_SECOND_KEY_VALUE to SECOND_VALUE
                        )

                        "then this property should return a not empty set with the keys in the pascal case" {
                            val keys = map.keys
                            keys.shouldNotBeEmpty()
                            keys shouldContainExactly setOf(PASCAL_FIRST_KEY_VALUE, PASCAL_SECOND_KEY_VALUE)
                        }
                    }

                    "when the keys in the lower case" - {
                        val map = createMap(
                            LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                            LOWERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                        )

                        "then this property should return a not empty set with the keys in the lower case" {
                            val keys = map.keys
                            keys.shouldNotBeEmpty()
                            keys shouldContainExactly setOf(LOWERCASE_FIRST_KEY_VALUE, LOWERCASE_SECOND_KEY_VALUE)
                        }
                    }

                    "when the keys in the upper case" - {
                        val map = createMap(
                            UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                            UPPERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                        )

                        "then this property should return a not empty set with the keys in the upper case" {
                            val keys = map.keys
                            keys.shouldNotBeEmpty()
                            keys shouldContainExactly setOf(UPPERCASE_FIRST_KEY_VALUE, UPPERCASE_SECOND_KEY_VALUE)
                        }
                    }
                }
            }

            "the `entries` property" - {

                "when the map is empty" - {
                    val map = createMap()

                    "then this property should return an empty set" {
                        map.entries.isEmpty() shouldBe true
                    }
                }

                "when the map is not empty" - {

                    "when the keys in the pascal case" - {
                        val map = createMap(
                            PASCAL_FIRST_KEY_VALUE to FIRST_VALUE,
                            PASCAL_SECOND_KEY_VALUE to SECOND_VALUE
                        )

                        "then this property should return a not empty set with the keys in the pascal case" {
                            val entries = map.entries
                            entries.shouldNotBeEmpty()
                            entries.size shouldBe map.size

                            val pairs = entries.map { it.key to it.value }
                            pairs.shouldContainExactly(
                                PASCAL_FIRST_KEY_VALUE to FIRST_VALUE,
                                PASCAL_SECOND_KEY_VALUE to SECOND_VALUE
                            )
                        }
                    }

                    "when the keys in the lower case" - {
                        val map = createMap(
                            LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                            LOWERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                        )

                        "then this property should return a not empty set with the keys in the pascal case" {
                            val entries = map.entries
                            entries.shouldNotBeEmpty()
                            entries.size shouldBe map.size

                            val pairs = entries.map { it.key to it.value }
                            pairs.shouldContainExactly(
                                LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                                LOWERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                            )
                        }
                    }

                    "when the keys in the upper case" - {
                        val map = createMap(
                            UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                            UPPERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                        )

                        "then this property should return a not empty set with the keys in the pascal case" {
                            val entries = map.entries
                            entries.shouldNotBeEmpty()
                            entries.size shouldBe map.size

                            val pairs = entries.map { it.key to it.value }
                            pairs.shouldContainExactly(
                                UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                                UPPERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                            )
                        }
                    }
                }
            }

            "the `values` property" - {

                "when the map is empty" - {
                    val map = createMap()

                    "then this property should return an empty collection" {
                        map.values.isEmpty() shouldBe true
                    }
                }

                "when the map is not empty" - {

                    "when the values in the pascal case" - {
                        val map = createMap(
                            PASCAL_FIRST_KEY_VALUE to FIRST_VALUE,
                            PASCAL_SECOND_KEY_VALUE to SECOND_VALUE
                        )

                        "then this property should return a not empty set with the keys in the pascal case" {
                            val values = map.values
                            values.shouldNotBeEmpty()
                            values.size shouldBe map.size
                            values.shouldContainExactly(FIRST_VALUE, SECOND_VALUE)
                        }
                    }

                    "when the keys in the lower case" - {
                        val map = createMap(
                            LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                            LOWERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                        )

                        "then this property should return a not empty set with the keys in the pascal case" {
                            val values = map.values
                            values.shouldNotBeEmpty()
                            values.size shouldBe map.size
                            values.shouldContainExactly(FIRST_VALUE, SECOND_VALUE)
                        }
                    }

                    "when the keys in the upper case" - {
                        val map = createMap(
                            UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                            UPPERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                        )

                        "then this property should return a not empty set with the keys in the pascal case" {
                            val values = map.values
                            values.shouldNotBeEmpty()
                            values.size shouldBe map.size
                            values.shouldContainExactly(FIRST_VALUE, SECOND_VALUE)
                        }
                    }
                }
            }

            "when instances were initialized with different case keys and the same values" - {
                val left = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)
                val right = createMap(LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE)

                "then the instances should be equal" {
                    left shouldBe right
                }

                "then the instances should have the same hash codes" {
                    left.hashCode() shouldBe right.hashCode()
                }
            }

            "when instances were initialized with the same keys and different values" - {
                val left = createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE)
                val right = createMap(PASCAL_FIRST_KEY_VALUE to SECOND_VALUE)

                "then the instances should not be equal" {
                    left shouldNotBe right
                }

                "then the instances should have different hash codes" {
                    left.hashCode() shouldNotBe right.hashCode()
                }
            }

            "should comply with equals() and hashCode() contract" {
                val pairs = listOf(
                    PASCAL_FIRST_KEY_VALUE to FIRST_VALUE,
                    PASCAL_SECOND_KEY_VALUE to SECOND_VALUE
                )
                createMap(pairs).shouldComplyWithContractOfEquality(
                    b = createMap(
                        LOWERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                        LOWERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                    ),
                    c = createMap(
                        UPPERCASE_FIRST_KEY_VALUE to FIRST_VALUE,
                        UPPERCASE_SECOND_KEY_VALUE to SECOND_VALUE
                    )
                )
            }

            "the `toString` function should return the string representation" {
                createMap(PASCAL_FIRST_KEY_VALUE to FIRST_VALUE).toString() shouldBe
                    """{$PASCAL_FIRST_KEY_VALUE=$FIRST_VALUE}"""
            }
        }
    }

    companion object {
        private const val PASCAL_FIRST_KEY_VALUE = "Key-1"
        private val LOWERCASE_FIRST_KEY_VALUE = PASCAL_FIRST_KEY_VALUE.lowercase()
        private val UPPERCASE_FIRST_KEY_VALUE = PASCAL_FIRST_KEY_VALUE.uppercase()

        private const val PASCAL_SECOND_KEY_VALUE = "Key-2"
        private val LOWERCASE_SECOND_KEY_VALUE = PASCAL_SECOND_KEY_VALUE.lowercase()
        private val UPPERCASE_SECOND_KEY_VALUE = PASCAL_SECOND_KEY_VALUE.uppercase()

        private const val FIRST_VALUE = "value-1"
        private const val SECOND_VALUE = "value-2"

        private fun createMap(vararg items: Pair<String, String>): MutableMap<String, String> =
            createMap(items.toList())

        private fun createMap(items: List<Pair<String, String>>): MutableMap<String, String> =
            caseInsensitiveMutableHashMapOf(items.toList())
    }
}
