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

package io.github.airflux.commons.kotest.assertions

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@Suppress("ReplaceCallWithBinaryOperator")
public fun <T : Any> T.shouldComplyWithContractOfEquality(b: T, c: T, notEquals: Collection<Any> = emptyList()) {
    val a = this
    assertSoftly {
        a.shouldComplyWithReflexivityContract()
        a.shouldComplyWithSymmetryContract(b)
        a.shouldComplyWithTransitivityContract(b, c)
        a.shouldComplyWithNullComparisonContract()

        notEquals.forEach { other ->
            withClue("checking the reflexive property") {
                withClue("`A` ($a) should not equal `B` ($other)") {
                    a.equals(other) shouldBe false
                }
                withClue("`B` ($other) should not equal `A` ($a)") {
                    other.equals(a) shouldNotBe true
                }
            }
        }
    }
}

@Suppress("ReplaceCallWithBinaryOperator")
private fun <T : Any> T.shouldComplyWithReflexivityContract() {
    val a = this
    withClue("checking the reflexive property") {
        withClue("`A` should equal `A`") {
            a.equals(a) shouldBe true
        }
        withClue("the hash code of `A` should be equal to the hash code of `A`") {
            a.hashCode() shouldBe a.hashCode()
        }
    }
}

@Suppress("ReplaceCallWithBinaryOperator")
private fun <T : Any> T.shouldComplyWithSymmetryContract(b: T) {
    val a = this
    withClue("checking the symmetric property") {
        withClue("`A` should equal `B`") {
            a.equals(b) shouldBe true
        }
        withClue("`B` should equal `A`") {
            b.equals(a) shouldBe true
        }
        withClue("the hash code of `A` should be equal to the hash code of `B`") {
            a.hashCode() shouldBe b.hashCode()
        }
    }
}

@Suppress("ReplaceCallWithBinaryOperator")
private fun <T : Any> T.shouldComplyWithTransitivityContract(b: T, c: T) {
    val a = this
    withClue("checking the transitive property") {
        withClue("`A` should equal `B`") {
            a.equals(b) shouldBe true
        }
        withClue("`B` should equal `C`") {
            b.equals(c) shouldBe true
        }
        withClue("`A` should equal `C`") {
            a.equals(c) shouldBe true
        }
        withClue("the hash code of `A` should be equal to the hash code of `B`") {
            a.hashCode() shouldBe b.hashCode()
        }
        withClue("the hash code of `B` should be equal to the hash code of `C`") {
            b.hashCode() shouldBe c.hashCode()
        }
        withClue("the hash code of `A` should be equal to the hash code of `C`") {
            a.hashCode() shouldBe c.hashCode()
        }
    }
}

@Suppress("EqualsNullCall")
private fun <T : Any> T.shouldComplyWithNullComparisonContract() {
    val x = this
    withClue("comparison null") {
        x.equals(null) shouldBe false
    }
}
