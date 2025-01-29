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
package io.github.airflux.commons.types.resultk

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs

@OptIn(AirfluxTypesExperimental::class)
internal class ResultKTest : FreeSpec() {

    init {

        "The `ResultK` type" - {

            "the `success` function" - {

                "when a parameter has the `Unit` type" - {
                    val result: ResultK<Unit, String> = ResultK.success(Unit)

                    "then this function should return the value of the asUnit property of the `Success` type" {
                        result shouldBeSameInstanceAs Success.asUnit
                    }
                }

                "when a parameter has the `Boolean` type" - {

                    "when a parameter value has the `true` value" - {
                        val result: ResultK<Boolean, String> = ResultK.success(true)

                        "then this function should return the value of the asTrue property of the `Success` type" {
                            result shouldBeSameInstanceAs Success.asTrue
                        }
                    }

                    "when a parameter value has the `false` value" - {
                        val result: ResultK<Boolean, String> = ResultK.success(false)

                        "then this function should return the value of the asFalse property of the `Success` type" {
                            result shouldBeSameInstanceAs Success.asFalse
                        }
                    }
                }

                "when a parameter has the `List` type" - {

                    "when a parameter value has the empty list" - {
                        val result: ResultK<List<Nothing>, String> = ResultK.success(emptyList())

                        "then this function should return the value of the asEmptyList property of the `Success` type" {
                            result shouldBeSameInstanceAs Success.asEmptyList
                        }
                    }

                    "when a parameter value has a non-empty list" - {
                        val result: ResultK<List<String>, String> = ResultK.success(listOf(FIRST_VALUE, SECOND_VALUE))

                        "then this function should return the passed value of the `Success` type" {
                            result shouldBeSuccess listOf(FIRST_VALUE, SECOND_VALUE)
                        }
                    }
                }

                "when a parameter has another type" - {

                    "when a parameter value has the `null` value" - {
                        val result: ResultK<String?, String> = ResultK.success(null)

                        "then this function should return the value of the asNull property of the `Success` type" {
                            result shouldBeSameInstanceAs Success.asNull
                        }
                    }

                    "when a parameter value has the non-null value" - {
                        val result: ResultK<String, String> = ResultK.success(FIRST_VALUE)

                        "then this function should return the passed value of the `Success` type" {
                            result shouldBeSuccess FIRST_VALUE
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val FIRST_VALUE = "10"
        private const val SECOND_VALUE = "20"
    }
}
