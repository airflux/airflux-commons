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

package io.github.airflux.commons.collections.map

public fun <T> caseInsensitiveMutableHashMapOf(vararg pairs: Pair<String, T>): MutableMap<String, T> =
    caseInsensitiveMutableHashMapOf(pairs.iterator())

public fun <T> caseInsensitiveMutableHashMapOf(pairs: Iterable<Pair<String, T>>): MutableMap<String, T> =
    caseInsensitiveMutableHashMapOf(pairs.iterator())

public fun <T> caseInsensitiveMutableHashMapOf(pairs: Iterator<Pair<String, T>>): MutableMap<String, T> =
    CaseInsensitiveMutableHashMap<T>()
        .apply { pairs.forEach { (key, value) -> put(key, value) } }

public class CaseInsensitiveMutableHashMap<T> : AbstractCaseInsensitiveMutableMap<T>({ mutableMapOf() }) {

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is CaseInsensitiveMutableHashMap<*> -> target == other.target
        else -> false
    }

    override fun hashCode(): Int = target.hashCode()

    override fun toString(): String = target.toString()
}
