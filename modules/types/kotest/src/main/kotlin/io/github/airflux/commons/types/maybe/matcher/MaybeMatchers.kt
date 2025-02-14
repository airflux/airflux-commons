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
import io.github.airflux.commons.types.maybe.Maybe
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ValueT : Any> Maybe<ValueT>.shouldBeSome() {
    contract {
        returns() implies (this@shouldBeSome is Maybe.Some<ValueT>)
    }
    this should TypeMatcher(
        expectedType = Maybe.Some::class,
        expectedTypeName = "Maybe.Some<${ValueT::class.simpleName}>"
    )
}

@AirfluxTypesExperimental
public infix fun <ValueT : Any> Maybe<ValueT>.shouldBeSome(expected: ValueT) {
    this should ValueMatcher(Maybe.Some(expected))
}

@AirfluxTypesExperimental
public inline fun <reified ValueT : Any> shouldBeSome(block: () -> Maybe<ValueT>): ValueT =
    block().shouldContainSomeInstance()

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified ValueT : Any> Maybe<ValueT>.shouldContainSomeInstance(): ValueT {
    contract {
        returns() implies (this@shouldContainSomeInstance is Maybe.Some<ValueT>)
    }
    this.shouldBeSome()
    return this.value
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public fun Maybe<*>.shouldBeNone() {
    contract {
        returns() implies (this@shouldBeNone is Maybe.None)
    }
    this should TypeMatcher(
        expectedType = Maybe.None::class,
        expectedTypeName = "Maybe.None"
    )
}

@PublishedApi
internal class ValueMatcher<ValueT : Any>(private val expected: Maybe<ValueT>) : Matcher<Maybe<ValueT>> {

    override fun test(value: Maybe<ValueT>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

@PublishedApi
internal class TypeMatcher<ValueT : Any, ResultT : Maybe<ValueT>>(
    private val expectedType: KClass<ResultT>,
    private val expectedTypeName: String
) : Matcher<Maybe<ValueT>> {

    override fun test(value: Maybe<ValueT>): MatcherResult = comparableMatcherResult(
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
