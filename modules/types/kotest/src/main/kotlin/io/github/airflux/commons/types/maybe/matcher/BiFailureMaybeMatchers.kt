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

package io.github.airflux.commons.types.maybe.matcher

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.maybe.BiFailureMaybe
import io.github.airflux.commons.types.maybe.Maybe
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ErrorT, reified FailT, reified FailureT> BiFailureMaybe<ErrorT, *>.shouldBeError()
    where ErrorT : Any,
          FailT : Fail.Error<ErrorT>,
          FailureT : Maybe.Some<FailT> {
    contract {
        returns() implies (this@shouldBeError is FailureT)
    }
    this should BiFailureMaybeTypeMatcher(
        expectedType = FailT::class,
        expectedTypeName = "Maybe.Some<Fail.Error<${ErrorT::class.simpleName}>>"
    )
}

@AirfluxTypesExperimental
public infix fun <ErrorT> BiFailureMaybe<ErrorT, *>.shouldBeError(expected: ErrorT)
    where ErrorT : Any {
    this should BiFailureMaybeValueMatcher(Maybe.Some(Fail.error(expected)))
}

@AirfluxTypesExperimental
public inline fun <reified ErrorT> BiFailureMaybe<ErrorT, *>.shouldContainErrorInstance(): ErrorT
    where ErrorT : Any {
    this.shouldBeError<ErrorT, Fail.Error<ErrorT>, Maybe.Some<Fail.Error<ErrorT>>>()
    return this.value.value
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ExceptionT, reified FailT, reified FailureT>
    BiFailureMaybe<*, ExceptionT>.shouldBeException()
    where ExceptionT : Any,
          FailT : Fail.Exception<ExceptionT>,
          FailureT : Maybe.Some<FailT> {
    contract {
        returns() implies (this@shouldBeException is FailureT)
    }
    this should BiFailureMaybeTypeMatcher(
        expectedType = FailT::class,
        expectedTypeName = "Maybe.Some<Fail.Exception<${ExceptionT::class.simpleName}>>"
    )
}

@AirfluxTypesExperimental
public infix fun <ExceptionT> BiFailureMaybe<*, ExceptionT>.shouldBeException(expected: ExceptionT)
    where ExceptionT : Any {
    this should BiFailureMaybeValueMatcher(Maybe.Some(Fail.exception(expected)))
}

@AirfluxTypesExperimental
public inline fun <reified ExceptionT> BiFailureMaybe<*, ExceptionT>.shouldContainExceptionInstance(): ExceptionT
    where ExceptionT : Any {
    this.shouldBeException<ExceptionT, Fail.Exception<ExceptionT>, Maybe.Some<Fail.Exception<ExceptionT>>>()
    return this.value.value
}

@PublishedApi
internal class BiFailureMaybeValueMatcher<ErrorT : Any, ExceptionT : Any>(
    private val expected: BiFailureMaybe<ErrorT, ExceptionT>
) : Matcher<BiFailureMaybe<ErrorT, ExceptionT>> {

    override fun test(value: BiFailureMaybe<ErrorT, ExceptionT>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

@PublishedApi
internal class BiFailureMaybeTypeMatcher<ErrorT : Any, ExceptionT : Any, FailT : Fail<ErrorT, ExceptionT>>(
    private val expectedType: KClass<FailT>,
    private val expectedTypeName: String
) : Matcher<BiFailureMaybe<ErrorT, ExceptionT>> {

    override fun test(value: BiFailureMaybe<ErrorT, ExceptionT>) = comparableMatcherResult(
        passed = value is Maybe.Some<*> && expectedType.isInstance(value.value),
        actual = value.toString(),
        expected = expectedTypeName
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
