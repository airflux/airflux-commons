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
package io.github.airflux.commons.types.raise

import io.github.airflux.commons.types.catch
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class CatchTest : FreeSpec() {

    init {

        "The `catch` function" - {

            "when a block does not throw an exception" - {
                val result = catch({ ALTERNATIVE_VALUE }) {
                    VALUE
                }

                "then this function should return a value" {
                    result shouldBe VALUE
                }
            }

            "when a block throws an exception" - {

                "when an exception is non fatal" - {
                    val exception = IllegalStateException()

                    "then this function should return a value after handling an exception" {
                        val result = catch({ ALTERNATIVE_VALUE }) {
                            throw exception
                        }

                        result shouldBe ALTERNATIVE_VALUE
                    }
                }

                "when an exception is fatal" - {
                    val exception = StackOverflowError()

                    "then this function should throw the same exception" {
                        shouldThrow<StackOverflowError> {
                            catch({ ALTERNATIVE_VALUE }) {
                                throw exception
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val VALUE = "10"
        private const val ALTERNATIVE_VALUE = "20"
    }
}
