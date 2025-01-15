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

@file:Suppress("TooManyFunctions")

package io.github.airflux.commons.types.resultk

import io.github.airflux.commons.types.RaiseException
import io.github.airflux.commons.types.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

public typealias Success<ValueT> = ResultK.Success<ValueT>
public typealias Failure<CauseT> = ResultK.Failure<CauseT>

public sealed interface ResultK<out ValueT, out CauseT> {

    @Suppress("MemberNameEqualsClassName")
    public class Raise<in CauseT> : io.github.airflux.commons.types.Raise<CauseT> {

        public operator fun <ValueT> ResultK<ValueT, CauseT>.component1(): ValueT = bind()

        public fun <ValueT> ResultK<ValueT, CauseT>.bind(): ValueT = if (isSuccess()) value else raise(this)

        public fun <ValueT> ResultK<ValueT, CauseT>.raise() {
            if (isFailure()) raise(this)
        }

        public override fun raise(cause: CauseT): Nothing {
            raise(Failure(cause))
        }

        private fun raise(failure: Failure<CauseT>): Nothing {
            throw RaiseException(failure, this)
        }
    }

    public data class Success<out ValueT>(public val value: ValueT) : ResultK<ValueT, Nothing> {

        public companion object {

            public val asNull: Success<Nothing?> = Success(null)

            public val asTrue: Success<Boolean> = Success(true)

            public val asFalse: Success<Boolean> = Success(false)

            public val asUnit: Success<Unit> = Success(Unit)

            public val asEmptyList: Success<List<Nothing>> = Success(emptyList())

            public fun of(value: Boolean): Success<Boolean> = if (value) asTrue else asFalse
        }
    }

    public data class Failure<out CauseT>(public val cause: CauseT) : ResultK<Nothing, CauseT>

    public companion object {

        public fun <ValueT> success(value: ValueT): Success<ValueT> = Success(value)

        public fun <CauseT> failure(cause: CauseT): Failure<CauseT> = Failure(cause)
    }
}

public fun <ValueT> ValueT.asSuccess(): Success<ValueT> = ResultK.success(this)

public fun <CauseT> CauseT.asFailure(): Failure<CauseT> = ResultK.failure(this)

public fun <ValueT> success(value: ValueT): Success<ValueT> = ResultK.success(value)

public fun <CauseT> failure(cause: CauseT): Failure<CauseT> = ResultK.failure(cause)

@OptIn(ExperimentalContracts::class)
public fun <ValueT, CauseT> ResultK<ValueT, CauseT>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Success<ValueT>)
        returns(false) implies (this@isSuccess is Failure<CauseT>)
    }
    return this is Success<ValueT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, CauseT> ResultK<ValueT, CauseT>.isSuccess(
    predicate: (ValueT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Success<ValueT> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT, CauseT> ResultK<ValueT, CauseT>.isFailure(): Boolean {
    contract {
        returns(false) implies (this@isFailure is Success<ValueT>)
        returns(true) implies (this@isFailure is Failure<CauseT>)
    }
    return this is Failure<CauseT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, CauseT> ResultK<ValueT, CauseT>.isFailure(
    predicate: (CauseT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is Failure<CauseT> && predicate(cause)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, CauseT> ResultK<ValueT, CauseT>.fold(
    onSuccess: (ValueT) -> SuccessR,
    onFailure: (CauseT) -> SuccessR
): SuccessR {
    contract {
        callsInPlace(onFailure, AT_MOST_ONCE)
        callsInPlace(onSuccess, AT_MOST_ONCE)
    }

    return if (isSuccess()) onSuccess(value) else onFailure(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, SuccessR, CauseT> ResultK<ValueT, CauseT>.map(
    transform: (ValueT) -> SuccessR
): ResultK<SuccessR, CauseT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMap { value -> transform(value).asSuccess() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, SuccessR, CauseT> ResultK<ValueT, CauseT>.flatMap(
    transform: (ValueT) -> ResultK<SuccessR, CauseT>
): ResultK<SuccessR, CauseT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, SuccessR, CauseT> ResultK<ValueT, CauseT>.andThen(
    block: (ValueT) -> ResultK<SuccessR, CauseT>
): ResultK<SuccessR, CauseT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, CauseT, FailureR> ResultK<ValueT, CauseT>.mapFailure(
    transform: (CauseT) -> FailureR
): ResultK<ValueT, FailureR> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else transform(cause).asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, CauseT> ResultK<Boolean, CauseT>.flatMapBoolean(
    ifTrue: () -> ResultK<ValueT, CauseT>,
    ifFalse: () -> ResultK<ValueT, CauseT>
): ResultK<ValueT, CauseT> {
    contract {
        callsInPlace(ifTrue, AT_MOST_ONCE)
        callsInPlace(ifFalse, AT_MOST_ONCE)
    }
    return if (this.isFailure())
        this
    else if (this.value)
        ifTrue()
    else
        ifFalse()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, CauseT> ResultK<ValueT, CauseT>.onSuccess(
    block: (ValueT) -> Unit
): ResultK<ValueT, CauseT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isSuccess()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, CauseT> ResultK<ValueT, CauseT>.onFailure(
    block: (CauseT) -> Unit
): ResultK<ValueT, CauseT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isFailure()) block(it.cause) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, CauseT> ResultK<ValueT, CauseT>.recover(
    block: (CauseT) -> ValueT
): ResultK<ValueT, CauseT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause).asSuccess()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, CauseT> ResultK<ValueT, CauseT>.recoverWith(
    block: (CauseT) -> ResultK<ValueT, CauseT>
): ResultK<ValueT, CauseT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, CauseT> ResultK<ValueT, CauseT>.getOrForward(
    block: (Failure<CauseT>) -> Nothing
): ValueT {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else block(this)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT, CauseT> ResultK<ValueT, CauseT>.getOrNull(): ValueT? {
    contract {
        returns(null) implies (this@getOrNull is Failure<CauseT>)
        returnsNotNull() implies (this@getOrNull is Success<ValueT>)
    }

    return if (isSuccess()) value else null
}

public infix fun <ValueT, CauseT> ResultK<ValueT, CauseT>.getOrElse(default: ValueT): ValueT =
    if (isSuccess()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, CauseT> ResultK<ValueT, CauseT>.getOrElse(
    default: (CauseT) -> ValueT
): ValueT {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else default(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, CauseT> ResultK<ValueT, CauseT>.orElse(
    default: () -> ResultK<ValueT, CauseT>
): ResultK<ValueT, CauseT> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, CauseT> ResultK<ValueT, CauseT>.orThrow(
    exceptionBuilder: (CauseT) -> Throwable
): ValueT {
    contract {
        callsInPlace(exceptionBuilder, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else throw exceptionBuilder(cause)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT, CauseT> ResultK<ValueT, CauseT>.getFailureOrNull(): CauseT? {
    contract {
        returns(null) implies (this@getFailureOrNull is Success<ValueT>)
        returnsNotNull() implies (this@getFailureOrNull is Failure<CauseT>)
    }

    return if (isFailure()) cause else null
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, CauseT> ResultK<ValueT, CauseT>.forEach(block: (ValueT) -> Unit) {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else Unit
}

public fun <T> ResultK<T, T>.merge(): T = fold(onSuccess = ::identity, onFailure = ::identity)

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, CauseT> ResultK<ValueT, CauseT>.apply(
    block: ValueT.() -> ResultK<Unit, CauseT>
): ResultK<ValueT, CauseT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (this.isSuccess()) {
        val result = block(this.value)
        if (result.isSuccess()) this else result
    } else
        this
}

public fun <ValueT, CauseT> Iterable<ResultK<ValueT, CauseT>>.sequence(): ResultK<List<ValueT>, CauseT> =
    traverse(::identity)

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@OverloadResolutionByLambdaReturnType
public inline fun <ValueT, SuccessR, CauseT> Iterable<ValueT>.traverse(
    transform: (ValueT) -> ResultK<SuccessR, CauseT>
): ResultK<List<SuccessR>, CauseT> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    val items = buildList {
        val iter = this@traverse.iterator()
        while (iter.hasNext()) {
            val item = transform(iter.next())
            if (item.isFailure()) return item
            add(item.value)
        }
    }
    return if (items.isNotEmpty()) items.asSuccess() else Success.asEmptyList
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, CauseT, M : MutableList<SuccessR>> Iterable<ValueT>.traverseTo(
    destination: M,
    transform: (ValueT) -> ResultK<SuccessR, CauseT>
): ResultK<M, CauseT> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }

    for (item in this@traverseTo) {
        val result = transform(item)
        if (result.isFailure()) return result
        destination.add(result.value)
    }
    return destination.asSuccess()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, CauseT, K, V, M : MutableMap<K, V>> Iterable<ValueT>.traverseTo(
    destination: M,
    transform: (ValueT) -> ResultK<Pair<K, V>, CauseT>
): ResultK<M, CauseT> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, { it.first }, { it.second }, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, CauseT, K, M : MutableMap<K, SuccessR>> Iterable<ValueT>.traverseTo(
    destination: M,
    keySelector: (SuccessR) -> K,
    transform: (ValueT) -> ResultK<SuccessR, CauseT>
): ResultK<M, CauseT> {
    contract {
        callsInPlace(keySelector, InvocationKind.UNKNOWN)
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, keySelector, ::identity, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, CauseT, K, V, M : MutableMap<K, V>> Iterable<ValueT>.traverseTo(
    destination: M,
    keySelector: (SuccessR) -> K,
    valueTransform: (SuccessR) -> V,
    transform: (ValueT) -> ResultK<SuccessR, CauseT>
): ResultK<M, CauseT> {
    contract {
        callsInPlace(keySelector, InvocationKind.UNKNOWN)
        callsInPlace(valueTransform, InvocationKind.UNKNOWN)
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }

    for (item in this@traverseTo) {
        val result = transform(item)
        if (result.isFailure()) return result
        val key = keySelector(result.value)
        val value = valueTransform(result.value)
        destination[key] = value
    }
    return destination.asSuccess()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT : Any, CauseT : Any> ResultK<ValueT?, CauseT>.filterNotNull(
    failureBuilder: () -> CauseT
): ResultK<ValueT, CauseT> {
    contract {
        callsInPlace(failureBuilder, AT_MOST_ONCE)
    }
    return if (this.isFailure())
        this
    else if (this.value != null) {
        @Suppress("UNCHECKED_CAST")
        this as ResultK<ValueT, CauseT>
    } else
        failureBuilder().asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, CauseT> ResultK<ValueT, CauseT>.filterOrElse(
    predicate: (ValueT) -> Boolean,
    default: () -> CauseT
): ResultK<ValueT, CauseT> {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
        callsInPlace(default, AT_MOST_ONCE)
    }

    return if (isFailure() || predicate(this.value)) this else default().asFailure()
}
