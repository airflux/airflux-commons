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
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

@OptIn(AirfluxTypesExperimental::class)
internal class ResultKDSLTest : FreeSpec() {

    init {
        "The DSL" - {

            "the function `result`" - {

                "when using one level of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): ResultK<Int, Error> = FIRST_VALUE.asSuccess()
                        fun second(): ResultK<Int, Error> = SECOND_VALUE.asSuccess()

                        "when the execution result of a block is successful" - {

                            "then should return a successful value" {
                                val result: ResultK<Int, Error> = result {
                                    val (a) = first()
                                    val (b) = second()
                                    a + b
                                }

                                result shouldBeSuccess FIRST_VALUE + SECOND_VALUE
                            }
                        }

                        "when in a block calling the `raise` method" - {

                            "then should return a failure value" {
                                val result = result {
                                    val (a) = first()
                                    val (b) = second()
                                    if (b == SECOND_VALUE) raise(Error.First)
                                    a + b
                                }

                                result shouldBeFailure Error.First
                            }
                        }
                    }

                    "when some function returns a failure" - {
                        fun first(): ResultK<Int, Error> = FIRST_VALUE.asSuccess()
                        fun second(): ResultK<Int, Error> = Error.First.asFailure()
                        fun third(): ResultK<Int, Error> = Error.Second.asFailure()

                        "then should return a first returned failure" {
                            val result = result {
                                val (a) = first()
                                val (b) = second()
                                val (c) = third()
                                a + b + c
                            }

                            result shouldBeFailure Error.First
                        }
                    }
                }

                "when using a few levels of DSL" - {

                    "when every function returns a successful" - {
                        fun first(): ResultK<Int, Error> = FIRST_VALUE.asSuccess()
                        fun second(): ResultK<Int, Error> = SECOND_VALUE.asSuccess()
                        fun third(): ResultK<String, Error> = "3".asSuccess()

                        "then should return a successful value" {
                            val result = result {
                                val (a) = first()
                                val (b) = second()
                                val (d) = result {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result shouldBeSuccess 6
                        }
                    }

                    "when some function at an internal nesting level returns an error" - {
                        fun first(): ResultK<Int, Error> = FIRST_VALUE.asSuccess()
                        fun second(): ResultK<Int, Error> = SECOND_VALUE.asSuccess()
                        fun third(): ResultK<String, Error> = Error.First.asFailure()

                        "then should return failure of an internal nesting level" {
                            val result = result {
                                val (a) = first()
                                val (b) = second()
                                val (d) = result {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result shouldBeFailure Error.First
                        }
                    }

                    "when every function at all nesting levels returns an error" - {
                        fun first(): ResultK<Int, Error> = FIRST_VALUE.asSuccess()
                        fun second(): ResultK<Int, Error> = Error.First.asFailure()
                        fun third(): ResultK<String, Error> = Error.Second.asFailure()

                        "then should return failure of a top-level" {
                            val result = result {
                                val (a) = first()
                                val (b) = second()
                                val (d) = result {
                                    val (c) = third()
                                    c.toInt()
                                }
                                a + b + d
                            }

                            result shouldBeFailure Error.First
                        }
                    }
                }

                "when a function returns a successful" - {
                    fun first(): ResultK<Int, Error> = FIRST_VALUE.asSuccess()

                    "then calling the `raise` function should have no effect" {
                        val result: ResultK<Int, Error> = result {
                            first().raise()
                            SECOND_VALUE
                        }

                        result shouldBeSuccess SECOND_VALUE
                    }
                }

                "when a function returns an error" - {
                    fun first(): ResultK<Int, Error> = Error.First.asFailure()

                    "then calling the `raise` function should return an error" {
                        val result: ResultK<Int, Error> = result {
                            first().raise()
                            SECOND_VALUE
                        }

                        result shouldBeFailure Error.First
                    }
                }
            }
        }
    }

    internal sealed class Error {
        data object First : Error()
        data object Second : Error()
    }

    private companion object {
        private const val FIRST_VALUE = 1
        private const val SECOND_VALUE = 2
    }
}
