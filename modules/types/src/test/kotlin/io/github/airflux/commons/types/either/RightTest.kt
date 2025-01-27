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

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.either.matcher.shouldBeRight
import io.kotest.core.spec.style.FreeSpec

@OptIn(AirfluxTypesExperimental::class)
internal class RightTest : FreeSpec() {

    init {

        "The `Right` type" - {

            "the `asNull` property should return the `Right` type with the `null` value" {
                val result: Either<String, String?> = Either.Right.asNull
                result shouldBeRight null
            }

            "the `asTrue` property should return the `Right` type with the `true` value" {
                val result: Either<String, Boolean> = Either.Right.asTrue
                result shouldBeRight true
            }

            "the `asFalse` property should return the `Right` type with the `false` value" {
                val result: Either<String, Boolean> = Either.Right.asFalse
                result shouldBeRight false
            }

            "the `asUnit` property should return the `Right` type with the `Unit` value" {
                val result: Either<String, Unit> = Either.Right.asUnit
                result shouldBeRight Unit
            }

            "the `asEmptyList` property should return the `Right` type with the `empty list` value" {
                val result: Either<String, List<String>> = Either.Right.asEmptyList
                result shouldBeRight emptyList()
            }

            "the `of` function" - {

                "when a parameter has the `true` value" - {
                    val param = true

                    "then this function should return the `Right` type with the `true` value" {
                        val result: Either<String, Boolean> = Either.Right.of(param)
                        result shouldBeRight true
                    }
                }

                "when a parameter has the `false` value" - {
                    val param = false

                    "then this function should return the `Right` type with the `true` value" {
                        val result: Either<String, Boolean> = Either.Right.of(param)
                        result shouldBeRight false
                    }
                }
            }
        }
    }
}
