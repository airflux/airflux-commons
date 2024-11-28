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

package io.github.airflux.commons.text

public fun String.caseInsensitive(): CaseInsensitiveString = CaseInsensitiveString(this)

public class CaseInsensitiveString(public val get: String) {
    private val hashCode = get.lowercase().hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> EQUAL
        other is CaseInsensitiveString -> this.get.equals(other.get, ignoreCase = true)
        other is String -> this.get.equals(other, ignoreCase = true)
        else -> NOT_EQUAL
    }

    override fun hashCode(): Int = hashCode

    override fun toString(): String = get

    public companion object {
        private const val EQUAL = true
        private const val NOT_EQUAL = false
    }
}
