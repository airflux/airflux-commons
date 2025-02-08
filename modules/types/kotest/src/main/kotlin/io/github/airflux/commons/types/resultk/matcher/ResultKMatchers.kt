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
import io.github.airflux.commons.types.resultk.ResultK
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ValueT> ResultK<ValueT, *>.shouldBeSuccess() {
    contract {
        returns() implies (this@shouldBeSuccess is ResultK.Success<ValueT>)
    }
    this should TypeMatcher(
        expectedType = ResultK.Success::class,
        expectedTypeName = "ResultK.Success<${ValueT::class.simpleName}>"
    )
}

@AirfluxTypesExperimental
public infix fun <ValueT> ResultK<ValueT, *>.shouldBeSuccess(expected: ValueT) {
    this should ValueMatcher(ResultK.Success(expected))
}

@AirfluxTypesExperimental
public inline fun <reified ValueT> shouldBeSuccess(block: () -> ResultK<ValueT, *>): ValueT =
    block().shouldContainSuccessInstance()

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ValueT> ResultK<ValueT, *>.shouldContainSuccessInstance(): ValueT {
    contract {
        returns() implies (this@shouldContainSuccessInstance is ResultK.Success<ValueT>)
    }
    this.shouldBeSuccess()
    return this.value
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified FailureT : Any> ResultK<*, FailureT>.shouldBeFailure() {
    contract {
        returns() implies (this@shouldBeFailure is ResultK.Failure<FailureT>)
    }
    this should TypeMatcher(
        expectedType = ResultK.Failure::class,
        expectedTypeName = "ResultK.Failure<${FailureT::class.simpleName}>"
    )
}

@AirfluxTypesExperimental
public infix fun <FailureT : Any> ResultK<*, FailureT>.shouldBeFailure(expected: FailureT) {
    this should ValueMatcher(ResultK.Failure(expected))
}

@AirfluxTypesExperimental
public inline fun <reified FailureT : Any> shouldBeFailure(block: () -> ResultK<*, FailureT>): FailureT =
    block().shouldContainFailureInstance()

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified FailureT : Any> ResultK<*, FailureT>.shouldContainFailureInstance(): FailureT {
    contract {
        returns() implies (this@shouldContainFailureInstance is ResultK.Failure<FailureT>)
    }
    this.shouldBeFailure()
    return this.cause
}

@PublishedApi
internal class ValueMatcher<ValueT, FailureT : Any>(
    private val expected: ResultK<ValueT, FailureT>
) : Matcher<ResultK<ValueT, FailureT>> {

    override fun test(value: ResultK<ValueT, FailureT>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

@PublishedApi
internal class TypeMatcher<ValueT, FailureT : Any, ResultT : ResultK<ValueT, FailureT>>(
    private val expectedType: KClass<ResultT>,
    private val expectedTypeName: String
) : Matcher<ResultK<ValueT, FailureT>> {

    override fun test(value: ResultK<ValueT, FailureT>) = comparableMatcherResult(
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
