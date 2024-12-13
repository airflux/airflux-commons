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

package io.github.airflux.commons.types.resultk.matcher

import io.github.airflux.commons.types.resultk.Failure
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

public fun beSuccess(): Matcher<ResultK<*, *>> = TypeMatcher(Success::class)

public fun <T> beSuccess(expected: T): Matcher<ResultK<T, *>> = ValueMatcher(Success(expected))

@OptIn(ExperimentalContracts::class)
public fun <T> ResultK<T, *>.shouldBeSuccess() {
    contract {
        returns() implies (this@shouldBeSuccess is Success<T>)
    }
    this should beSuccess()
}

public infix fun <T> ResultK<T, *>.shouldBeSuccess(expected: T) {
    this should beSuccess(expected)
}

public fun beFailure(): Matcher<ResultK<*, *>> = TypeMatcher(Failure::class)

public fun <E> beFailure(expected: E): Matcher<ResultK<*, E>> = ValueMatcher(Failure(expected))

@OptIn(ExperimentalContracts::class)
public fun <E> ResultK<*, E>.shouldBeFailure() {
    contract {
        returns() implies (this@shouldBeFailure is Failure<E>)
    }
    this should beFailure()
}

public infix fun <E> ResultK<*, E>.shouldBeFailure(expected: E) {
    this should beFailure(expected)
}

private class ValueMatcher<T, E>(private val expected: ResultK<T, E>) : Matcher<ResultK<T, E>> {

    override fun test(value: ResultK<T, E>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

private class TypeMatcher<T, E, C : ResultK<T, E>>(private val expected: KClass<C>) : Matcher<ResultK<T, E>> {

    override fun test(value: ResultK<T, E>) = comparableMatcherResult(
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
