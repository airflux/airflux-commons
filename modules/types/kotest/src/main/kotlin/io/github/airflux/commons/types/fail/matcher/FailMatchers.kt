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

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.Fail
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ErrorT : Any> Fail<ErrorT, *>.shouldBeError() {
    contract {
        returns() implies (this@shouldBeError is Fail.Error<ErrorT>)
    }
    this should TypeMatcher(
        expectedType = Fail.Error::class,
        expectedTypeName = "Fail.Error<${ErrorT::class.simpleName}>"
    )
}

@AirfluxTypesExperimental
public infix fun <ErrorT : Any> Fail<ErrorT, *>.shouldBeError(expected: ErrorT) {
    this should ValueMatcher(Fail.Error(expected))
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ErrorT : Any> Fail<ErrorT, *>.shouldContainErrorInstance(): ErrorT {
    contract {
        returns() implies (this@shouldContainErrorInstance is Fail.Error<ErrorT>)
    }
    this.shouldBeError()
    return this.value
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ExceptionT : Any> Fail<*, ExceptionT>.shouldBeException() {
    contract {
        returns() implies (this@shouldBeException is Fail.Exception<ExceptionT>)
    }
    this should TypeMatcher(
        expectedType = Fail.Exception::class,
        expectedTypeName = "Fail.Exception<${ExceptionT::class.simpleName}>"
    )
}

@AirfluxTypesExperimental
public infix fun <ExceptionT : Any> Fail<*, ExceptionT>.shouldBeException(expected: ExceptionT) {
    this should ValueMatcher(Fail.Exception(expected))
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ExceptionT : Any> Fail<*, ExceptionT>.shouldContainExceptionInstance(): ExceptionT {
    contract {
        returns() implies (this@shouldContainExceptionInstance is Fail.Exception<ExceptionT>)
    }
    this.shouldBeException()
    return this.value
}

@PublishedApi
internal class ValueMatcher<ErrorT : Any, ExceptionT : Any>(
    private val expected: Fail<ErrorT, ExceptionT>
) : Matcher<Fail<ErrorT, ExceptionT>> {

    override fun test(value: Fail<ErrorT, ExceptionT>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

@PublishedApi
internal class TypeMatcher<ErrorT : Any, ExceptionT : Any, FailT : Fail<ErrorT, ExceptionT>>(
    private val expectedType: KClass<FailT>,
    private val expectedTypeName: String
) : Matcher<Fail<ErrorT, ExceptionT>> {

    override fun test(value: Fail<ErrorT, ExceptionT>) = comparableMatcherResult(
        passed = expectedType.isInstance(value),
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
