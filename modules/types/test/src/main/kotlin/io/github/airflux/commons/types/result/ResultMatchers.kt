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

import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

public fun beSuccess(): Matcher<Result<*, *>> = TypeMatcher(Success::class)

public fun <T> beSuccess(expected: T): Matcher<Result<T, *>> = ValueMatcher(Success(expected))

@OptIn(ExperimentalContracts::class)
public fun <T> Result<T, *>.shouldBeSuccess() {
    contract {
        returns() implies (this@shouldBeSuccess is Success<T>)
    }
    this should beSuccess()
}

public infix fun <T> Result<T, *>.shouldBeSuccess(expected: T) {
    this should beSuccess(expected)
}

public fun beFailure(): Matcher<Result<*, *>> = TypeMatcher(Failure::class)

public fun <E> beFailure(expected: E): Matcher<Result<*, E>> = ValueMatcher(Failure(expected))

@OptIn(ExperimentalContracts::class)
public fun <E> Result<*, E>.shouldBeFailure() {
    contract {
        returns() implies (this@shouldBeFailure is Failure<E>)
    }
    this should beFailure()
}

public infix fun <E> Result<*, E>.shouldBeFailure(expected: E) {
    this should beFailure(expected)
}

private class ValueMatcher<T, E>(private val expected: Result<T, E>) : Matcher<Result<T, E>> {

    override fun test(value: Result<T, E>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

private class TypeMatcher<T, E, C : Result<T, E>>(private val expected: KClass<C>) : Matcher<Result<T, E>> {

    override fun test(value: Result<T, E>) = comparableMatcherResult(
        passed = expected.isInstance(value),
        actual = value.toString(),
        expected = expected.simpleName!!
    )
}

private fun comparableMatcherResult(passed: Boolean, actual: String, expected: String): ComparableMatcherResult =
    ComparableMatcherResult(
        passed = passed,
        failureMessageFn = { "" },
        negatedFailureMessageFn = { "not " },
        actual = actual,
        expected = expected
    )
