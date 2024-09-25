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

package io.github.airflux.commons.types.result

import io.github.airflux.commons.types.failure
import io.kotest.matchers.shouldBe

public fun <T, E> Result<T, E>.getValue(): T = this.shouldBeSuccess().value

public fun <T, E> Result<T, E>.shouldBeSuccess(message: (E) -> String = { it.toString() }): Success<T> {
    if (this.isFailure())
        failure(
            expectedType = Success::class.qualifiedName!!,
            actualType = this::class.qualifiedName!!,
            message = message(this.cause)
        )

    return this as Success<T>
}

public infix fun <T> Result<T, *>.shouldBeSuccess(expected: T) {
    this.shouldBeSuccess().value shouldBe expected
}

public fun <T, E> Result<T, E>.shouldBeSuccess(expected: T, message: (E) -> String) {
    this.shouldBeSuccess(message).value shouldBe expected
}

public fun <T, E> Result<T, E>.shouldBeFailure(message: (T) -> String = { it.toString() }): Failure<E> {
    if (this.isSuccess())
        failure(
            expectedType = Failure::class.qualifiedName!!,
            actualType = this::class.simpleName!!,
            message = message(this.value)
        )

    return this as Failure<E>
}

public infix fun <E> Result<*, E>.shouldBeFailure(expected: E) {
    this.shouldBeFailure().cause shouldBe expected
}

public fun <T, E> Result<T, E>.shouldBeFailure(expected: E, message: (T) -> String) {
    this.shouldBeFailure(message).cause shouldBe expected
}
