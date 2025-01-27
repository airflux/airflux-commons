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

package io.github.airflux.commons.types.either.matcher

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.either.Either
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified LeftT> Either<LeftT, *>.shouldBeLeft() {
    contract {
        returns() implies (this@shouldBeLeft is Either.Left<LeftT>)
    }
    this should TypeMatcher(
        expectedType = Either.Left::class,
        expectedTypeName = "Either.Left<${LeftT::class.simpleName}>"
    )
}

@AirfluxTypesExperimental
public infix fun <LeftT> Either<LeftT, *>.shouldBeLeft(expected: LeftT) {
    this should ValueMatcher(Either.Left(expected))
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified LeftT> Either<LeftT, *>.shouldContainLeftInstance(): LeftT {
    contract {
        returns() implies (this@shouldContainLeftInstance is Either.Left<LeftT>)
    }
    this.shouldBeLeft()
    return this.value
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified RightT> Either<*, RightT>.shouldBeRight() {
    contract {
        returns() implies (this@shouldBeRight is Either.Right<RightT>)
    }
    this should TypeMatcher(
        expectedType = Either.Right::class,
        expectedTypeName = "Either.Right<${RightT::class.simpleName}>"
    )
}

@AirfluxTypesExperimental
public infix fun <RightT> Either<*, RightT>.shouldBeRight(expected: RightT) {
    this should ValueMatcher(Either.Right(expected))
}

@AirfluxTypesExperimental
@OptIn(ExperimentalContracts::class)
public inline fun <reified RightT> Either<*, RightT>.shouldContainRightInstance(): RightT {
    contract {
        returns() implies (this@shouldContainRightInstance is Either.Right<RightT>)
    }
    this.shouldBeRight()
    return this.value
}

@PublishedApi
internal class ValueMatcher<LeftT, RightT>(
    private val expected: Either<LeftT, RightT>
) : Matcher<Either<LeftT, RightT>> {

    override fun test(value: Either<LeftT, RightT>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

@PublishedApi
internal class TypeMatcher<LeftT, RightT, EitherT : Either<LeftT, RightT>>(
    private val expectedType: KClass<EitherT>,
    private val expectedTypeName: String
) : Matcher<Either<LeftT, RightT>> {

    override fun test(value: Either<LeftT, RightT>) = comparableMatcherResult(
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
