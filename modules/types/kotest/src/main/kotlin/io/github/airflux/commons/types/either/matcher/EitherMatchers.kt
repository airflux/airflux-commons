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
import io.github.airflux.commons.types.either.Left
import io.github.airflux.commons.types.either.Right
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

public fun beLeft(): Matcher<Either<*, *>> = TypeMatcher(Left::class)

public fun <T> beLeft(expected: T): Matcher<Either<T, *>> = ValueMatcher(Left(expected))

@OptIn(ExperimentalContracts::class)
public fun <L> Either<L, *>.shouldBeLeft() {
    contract {
        returns() implies (this@shouldBeLeft is Left<L>)
    }
    this should beLeft()
}

public infix fun <L> Either<L, *>.shouldBeLeft(expected: L) {
    this should beLeft(expected)
}

public fun beRight(): Matcher<Either<*, *>> = TypeMatcher(Right::class)

public fun <R> beRight(expected: R): Matcher<Either<*, R>> = ValueMatcher(Right(expected))

@OptIn(ExperimentalContracts::class)
public fun <R> Either<*, R>.shouldBeRight() {
    contract {
        returns() implies (this@shouldBeRight is Right<R>)
    }
    this should beRight()
}

public infix fun <R> Either<*, R>.shouldBeRight(expected: R) {
    this should beRight(expected)
}

private class ValueMatcher<L, R>(private val expected: Either<L, R>) : Matcher<Either<L, R>> {

    override fun test(value: Either<L, R>) = comparableMatcherResult(
        passed = expected == value,
        actual = value.toString(),
        expected = expected.toString()
    )
}

private class TypeMatcher<L, R, C : Either<L, R>>(private val expected: KClass<C>) : Matcher<Either<L, R>> {

    override fun test(value: Either<L, R>) = comparableMatcherResult(
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
