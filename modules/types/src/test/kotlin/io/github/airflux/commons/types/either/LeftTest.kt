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
package io.github.airflux.commons.types.either

import io.github.airflux.commons.types.either.matcher.shouldBeLeft
import io.kotest.core.spec.style.FreeSpec

internal class LeftTest : FreeSpec() {

    init {

        "The `Left` type" - {

            "the `asNull` property should return the `Left` type with the `null` value" {
                val result: Either<String?, String> = Left.asNull
                result shouldBeLeft null
            }

            "the `asTrue` property should return the `Left` type with the `true` value" {
                val result: Either<Boolean, String> = Left.asTrue
                result shouldBeLeft true
            }

            "the `asFalse` property should return the `Left` type with the `false` value" {
                val result: Either<Boolean, String> = Left.asFalse
                result shouldBeLeft false
            }

            "the `asUnit` property should return the `Left` type with the `Unit` value" {
                val result: Either<Unit, String> = Left.asUnit
                result shouldBeLeft Unit
            }

            "the `asEmptyList` property should return the `Left` type with the `empty list` value" {
                val result: Either<List<String>, String> = Left.asEmptyList
                result shouldBeLeft emptyList()
            }

            "the `of` function" - {

                "when a parameter has the `true` value" - {
                    val param = true

                    "then this function should return the `Left` type with the `true` value" {
                        val result: Either<Boolean, String> = Left.of(param)
                        result shouldBeLeft true
                    }
                }

                "when a parameter has the `false` value" - {
                    val param = false

                    "then this function should return the `Left` type with the `true` value" {
                        val result: Either<Boolean, String> = Left.of(param)
                        result shouldBeLeft false
                    }
                }
            }
        }
    }
}
