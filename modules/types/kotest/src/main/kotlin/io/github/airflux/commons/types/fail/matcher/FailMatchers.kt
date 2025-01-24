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

package io.github.airflux.commons.types.fail.matcher

import io.github.airflux.commons.types.fail.Fail
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

public fun beError(): Matcher<Fail<*, *>> = TypeMatcher(Fail.Error::class)

public fun <ErrorT : Any> beError(expected: ErrorT): Matcher<Fail<ErrorT, *>> = ValueMatcher(Fail.Error(expected))

@OptIn(ExperimentalContracts::class)
public fun <ErrorT : Any> Fail<ErrorT, *>.shouldBeError(): ErrorT {
    contract {
        returns() implies (this@shouldBeError is Fail.Error<ErrorT>)
    }
    this should beError()
    return (this as Fail.Error<ErrorT>).value
}

public infix fun <ErrorT : Any> Fail<ErrorT, *>.shouldBeError(expected: ErrorT) {
    this should beError(expected)
}

public fun beException(): Matcher<Fail<*, *>> = TypeMatcher(Fail.Exception::class)

public fun <ExceptionT : Any> beException(expected: ExceptionT): Matcher<Fail<*, ExceptionT>> =
    ValueMatcher(Fail.Exception(expected))

@OptIn(ExperimentalContracts::class)
public fun <ExceptionT : Any> Fail<*, ExceptionT>.shouldBeException(): ExceptionT {
    contract {
        returns() implies (this@shouldBeException is Fail.Exception<ExceptionT>)
    }
    this should beException()
    return (this as Fail.Exception<ExceptionT>).value
}

public infix fun <ExceptionT : Any> Fail<*, ExceptionT>.shouldBeException(expected: ExceptionT) {
    this should beException(expected)
}

private class ValueMatcher<ErrorT : Any, ExceptionT : Any>(private val expected: Fail<ErrorT, ExceptionT>) :
    Matcher<Fail<ErrorT, ExceptionT>> {

    override fun test(value: Fail<ErrorT, ExceptionT>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

private class TypeMatcher<ErrorT : Any, ExceptionT : Any, C : Fail<ErrorT, ExceptionT>>(
    private val expected: KClass<C>
) : Matcher<Fail<ErrorT, ExceptionT>> {

    override fun test(value: Fail<ErrorT, ExceptionT>) = comparableMatcherResult(
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
