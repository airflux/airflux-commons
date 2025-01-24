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

import io.github.airflux.commons.types.resultk.ResultK
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

public fun beSuccess(): Matcher<ResultK<*, *>> = TypeMatcher(ResultK.Success::class)

public fun <ValueT> beSuccess(expected: ValueT): Matcher<ResultK<ValueT, *>> =
    ValueMatcher(ResultK.Success(expected))

@OptIn(ExperimentalContracts::class)
public fun <ValueT> ResultK<ValueT, *>.shouldBeSuccess(): ValueT {
    contract {
        returns() implies (this@shouldBeSuccess is ResultK.Success<ValueT>)
    }
    this should beSuccess()
    return (this as ResultK.Success<ValueT>).value
}

public infix fun <ValueT> ResultK<ValueT, *>.shouldBeSuccess(expected: ValueT) {
    this should beSuccess(expected)
}

public fun beFailure(): Matcher<ResultK<*, *>> = TypeMatcher(ResultK.Failure::class)

public fun <FailureT : Any> beFailure(expected: FailureT): Matcher<ResultK<*, FailureT>> =
    ValueMatcher(ResultK.Failure(expected))

@OptIn(ExperimentalContracts::class)
public fun <FailureT : Any> ResultK<*, FailureT>.shouldBeFailure(): FailureT {
    contract {
        returns() implies (this@shouldBeFailure is ResultK.Failure<FailureT>)
    }
    this should beFailure()
    return (this as ResultK.Failure<FailureT>).cause
}

public infix fun <FailureT : Any> ResultK<*, FailureT>.shouldBeFailure(expected: FailureT) {
    this should beFailure(expected)
}

private class ValueMatcher<ValueT, FailureT : Any>(private val expected: ResultK<ValueT, FailureT>) :
    Matcher<ResultK<ValueT, FailureT>> {

    override fun test(value: ResultK<ValueT, FailureT>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

private class TypeMatcher<ValueT, FailureT : Any, C : ResultK<ValueT, FailureT>>(
    private val expected: KClass<C>
) : Matcher<ResultK<ValueT, FailureT>> {

    override fun test(value: ResultK<ValueT, FailureT>) = comparableMatcherResult(
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
