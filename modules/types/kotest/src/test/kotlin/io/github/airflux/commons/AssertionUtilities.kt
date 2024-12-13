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

package io.github.airflux.commons

import io.kotest.assertions.AssertionFailedError
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain

internal fun assertionCorrect(block: () -> Unit) {
    shouldNotThrow<AssertionFailedError> { block() }
}

internal fun assertionIncorrect(message: String, block: () -> Unit) {
    shouldThrow<AssertionFailedError> { block() }
        .also { error ->
            error.message shouldContain message
        }
}
