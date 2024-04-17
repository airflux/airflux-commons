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

package io.github.airflux.commons.types.assertions

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.assertions.print.Printed

internal fun failure(expectedType: String, actualType: String, message: String) {
    fun String.escape(): String = this.replace(System.lineSeparator(), ". ")

    fun String.makeDescription(): String = escape()
        .takeIf { it.isNotBlank() }
        ?.let { " ($it). " }
        ?: ". "

    fun failure(expected: String, actual: String, message: String): Throwable =
        io.kotest.assertions.failure(Expected(Printed(expected)), Actual(Printed(actual)), message)

    errorCollector.collectOrThrow(
        failure(
            expected = expectedType,
            actual = actualType,
            message = "Expected the `$expectedType` type, but got the `$actualType` type${message.makeDescription()}"
        )
    )
}
