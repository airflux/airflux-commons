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

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class SuccessTest : FreeSpec() {

    init {

        "The `Success` type" - {

            "the `asNull` property should return the `Success` type with the `null` value" {
                val result: ResultK<String?, String> = Success.Companion.asNull
                result shouldBeSuccess null
            }

            "the `asTrue` property should return the `Success` type with the `true` value" {
                val result: ResultK<Boolean, String> = Success.Companion.asTrue
                result shouldBeSuccess true
            }

            "the `asFalse` property should return the `Success` type with the `false` value" {
                val result: ResultK<Boolean, String> = Success.Companion.asFalse
                result shouldBeSuccess false
            }

            "the `asUnit` property should return the `Success` type with the `Unit` value" {
                val result: ResultK<Unit, String> = Success.Companion.asUnit
                result shouldBeSuccess Unit
            }

            "the `asEmptyList` property should return the `Success` type with the `empty list` value" {
                val result: ResultK<List<String>, String> = Success.Companion.asEmptyList
                result shouldBeSuccess emptyList()
            }

            "the `of` function" - {

                "when a parameter has the `true` value" - {
                    val param = true

                    "then this function should return the `Success` type with the `true` value" {
                        val result: ResultK<Boolean, String> = Success.Companion.of(param)
                        result shouldBeSuccess true
                    }
                }

                "when a parameter has the `false` value" - {
                    val param = false

                    "then this function should return the `Success` type with the `true` value" {
                        val result: ResultK<Boolean, String> = Success.Companion.of(param)
                        result shouldBeSuccess false
                    }
                }
            }
        }
    }
}
