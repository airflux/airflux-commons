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
package io.github.airflux.commons.types.maybe

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldBeSome
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class MaybeTest : FreeSpec() {

    init {

        "The `Maybe` type" - {

            "the `some` function" - {

                "when a parameter is a nullable type" - {
                    val result: Maybe<String> = null.asSome()

                    "then this function should return the the `None` type" {
                        result.shouldBeNone()
                    }
                }

                "when a parameter is a non-nullable type" - {
                    val result: Maybe<String> = VALUE.asSome()

                    "then this function should return the the `Some` type with the value" {
                        result shouldBeSome VALUE
                    }
                }
            }

            "the `none` function" - {
                val result: Maybe<String> = none()

                "then this function should return the the `None` type" {
                    result.shouldBeInstanceOf<Maybe.None>()
                }
            }
        }
    }

    companion object {
        private const val VALUE = "10"
    }
}
