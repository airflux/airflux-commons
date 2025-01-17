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

import io.github.airflux.commons.types.either.Either
import io.github.airflux.commons.types.either.Either.Left
import io.github.airflux.commons.types.either.Either.Right
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

public fun beLeft(): Matcher<Either<*, *>> = TypeMatcher(Left::class)

public fun <LeftT> beLeft(expected: LeftT): Matcher<Either<LeftT, *>> = ValueMatcher(Left(expected))

@OptIn(ExperimentalContracts::class)
public fun <LeftT> Either<LeftT, *>.shouldBeLeft() {
    contract {
        returns() implies (this@shouldBeLeft is Left<LeftT>)
    }
    this should beLeft()
}

public infix fun <LeftT> Either<LeftT, *>.shouldBeLeft(expected: LeftT) {
    this should beLeft(expected)
}

public fun beRight(): Matcher<Either<*, *>> = TypeMatcher(Right::class)

public fun <RightT> beRight(expected: RightT): Matcher<Either<*, RightT>> = ValueMatcher(Right(expected))

@OptIn(ExperimentalContracts::class)
public fun <RightT> Either<*, RightT>.shouldBeRight() {
    contract {
        returns() implies (this@shouldBeRight is Either.Right<RightT>)
    }
    this should beRight()
}

public infix fun <RightT> Either<*, RightT>.shouldBeRight(expected: RightT) {
    this should beRight(expected)
}

private class ValueMatcher<LeftT, RightT>(private val expected: Either<LeftT, RightT>) :
    Matcher<Either<LeftT, RightT>> {

    override fun test(value: Either<LeftT, RightT>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

private class TypeMatcher<LeftT, RightT, C : Either<LeftT, RightT>>(private val expected: KClass<C>) :
    Matcher<Either<LeftT, RightT>> {

    override fun test(value: Either<LeftT, RightT>) = comparableMatcherResult(
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
