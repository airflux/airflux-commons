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

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.resultk.BiFailureResultK
import io.github.airflux.commons.types.resultk.ResultK
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ErrorT, reified FailT, reified FailureT> BiFailureResultK<*, ErrorT, *>.shouldBeError()
    where ErrorT : Any,
          FailT : Fail.Error<ErrorT>,
          FailureT : ResultK.Failure<FailT> {
    contract {
        returns() implies (this@shouldBeError is FailureT)
    }
    this should BiFailureResultKTypeMatcher(
        expectedType = FailT::class,
        expectedTypeName = "ResultK.Failure<Fail.Error<${ErrorT::class.simpleName}>>"
    )
}

@AirfluxTypesExperimental
public infix fun <ErrorT : Any> BiFailureResultK<*, ErrorT, *>.shouldBeError(expected: ErrorT) {
    this should BiFailureResultKValueMatcher(ResultK.Failure(Fail.error(expected)))
}

@AirfluxTypesExperimental
public inline fun <reified ErrorT : Any> BiFailureResultK<*, ErrorT, *>.shouldContainErrorInstance(): ErrorT {
    this.shouldBeError<ErrorT, Fail.Error<ErrorT>, ResultK.Failure<Fail.Error<ErrorT>>>()
    return this.cause.value
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ExceptionT, reified FailT, reified FailureT> BiFailureResultK<*, *, ExceptionT>.shouldBeException()
    where ExceptionT : Any,
          FailT : Fail.Exception<ExceptionT>,
          FailureT : ResultK.Failure<FailT> {
    contract {
        returns() implies (this@shouldBeException is FailureT)
    }
    this should BiFailureResultKTypeMatcher(
        expectedType = FailT::class,
        expectedTypeName = "ResultK.Failure<Fail.Exception<${ExceptionT::class.simpleName}>>"
    )
}

@AirfluxTypesExperimental
public infix fun <ExceptionT : Any> BiFailureResultK<*, *, ExceptionT>.shouldBeException(expected: ExceptionT) {
    this should BiFailureResultKValueMatcher(ResultK.Failure(Fail.exception(expected)))
}

@AirfluxTypesExperimental
public inline fun <reified ExceptionT : Any> BiFailureResultK<*, *, ExceptionT>.shouldContainExceptionInstance(): ExceptionT {
    this.shouldBeException()
    return this.cause.value
}

@PublishedApi
internal class BiFailureResultKValueMatcher<ValueT, ErrorT : Any, ExceptionT : Any>(
    private val expected: ResultK<ValueT, Fail<ErrorT, ExceptionT>>
) : Matcher<ResultK<ValueT, Fail<ErrorT, ExceptionT>>> {

    override fun test(value: ResultK<ValueT, Fail<ErrorT, ExceptionT>>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

@PublishedApi
internal class BiFailureResultKTypeMatcher<ValueT, ErrorT : Any, ExceptionT : Any, FailT : Fail<ErrorT, ExceptionT>>(
    private val expectedType: KClass<FailT>,
    private val expectedTypeName: String
) : Matcher<ResultK<ValueT, Fail<ErrorT, ExceptionT>>> {

    override fun test(value: ResultK<ValueT, Fail<ErrorT, ExceptionT>>) = comparableMatcherResult(
        passed = value is ResultK.Failure<*> && expectedType.isInstance(value.cause),
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
