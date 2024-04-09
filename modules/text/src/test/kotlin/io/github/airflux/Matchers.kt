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

package io.github.airflux

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

public fun <T : Any> T.shouldComplyWithContractOfEquality(symmetric: T, transitive: T) {
    val x = this
    assertSoftly {
        x.shouldComplyWithReflexivityContract()
        x.shouldComplyWithSymmetryContract(symmetric)
        x.shouldComplyWithTransitivityContract(symmetric, transitive)
        x.shouldComplyWithNullComparisonContract()
    }
}

@Suppress("ReplaceCallWithBinaryOperator")
public fun <T : Any> T.shouldComplyWithContractOfEquality(symmetric: T, transitive: T, notEquals: Collection<Any>) {
    val x = this
    assertSoftly {
        x.shouldComplyWithReflexivityContract()
        x.shouldComplyWithSymmetryContract(symmetric)
        x.shouldComplyWithTransitivityContract(symmetric, transitive)
        x.shouldComplyWithNullComparisonContract()

        notEquals.forEach { other ->
            withClue("$x never equal to $other") {
                x.equals(other) shouldBe false
            }
            withClue("$other never equal to $x") {
                other.equals(x) shouldBe false
            }
        }
    }
}

@Suppress("ReplaceCallWithBinaryOperator")
public fun <T : Any> T.shouldComplyWithReflexivityContract() {
    val x = this
    assertSoftly {
        withClue("reflexive") {
            withClue("$x equal to $x") {
                x.equals(x) shouldBe true
            }
            withClue("hashCode") {
                x.hashCode() shouldBe x.hashCode()
            }
        }
    }
}

@Suppress("ReplaceCallWithBinaryOperator")
public fun <T : Any> T.shouldComplyWithSymmetryContract(symmetric: T) {
    val x = this
    assertSoftly {
        withClue("symmetric") {
            withClue("$x equal to $symmetric") {
                x.equals(symmetric) shouldBe true
            }
            withClue("$symmetric equal to $x") {
                symmetric.equals(x) shouldBe true
            }
            withClue("hashCode") {
                x.hashCode() shouldBe symmetric.hashCode()
                symmetric.hashCode() shouldBe x.hashCode()
            }
        }
    }
}

@Suppress("ReplaceCallWithBinaryOperator")
public fun <T : Any> T.shouldComplyWithTransitivityContract(symmetric: T, transitive: T) {
    val x = this
    assertSoftly {
        withClue("transitive") {
            withClue("$x equal to $symmetric") {
                x.equals(symmetric) shouldBe true
            }
            withClue("$symmetric equal to $transitive") {
                symmetric.equals(transitive) shouldBe true
            }
            withClue("$x equal to $transitive") {
                x.equals(transitive) shouldBe true
            }
            withClue("hashCode") {
                x.hashCode() shouldBe symmetric.hashCode()
                symmetric.hashCode() shouldBe transitive.hashCode()
                x.hashCode() shouldBe transitive.hashCode()
            }
        }
    }
}

@Suppress("EqualsNullCall")
public fun <T : Any> T.shouldComplyWithNullComparisonContract() {
    val x = this
    assertSoftly {
        withClue("comparison null") {
            x.equals(null) shouldBe false
        }
    }
}
