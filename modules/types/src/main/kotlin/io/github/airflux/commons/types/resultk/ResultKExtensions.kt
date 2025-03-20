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

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

public fun <ValueT> ValueT.asSuccess(): ResultK.Success<ValueT> = success(this)

public fun <FailureT : Any> FailureT.asFailure(): ResultK.Failure<FailureT> = failure(this)

public fun <ValueT> success(value: ValueT): ResultK.Success<ValueT> = ResultK.success(value)

public fun <FailureT : Any> failure(cause: FailureT): ResultK.Failure<FailureT> = ResultK.failure(cause)

@OptIn(ExperimentalContracts::class)
public fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is ResultK.Success<ValueT>)
        returns(false) implies (this@isSuccess is ResultK.Failure<FailureT>)
    }
    return this is ResultK.Success<ValueT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.isSuccess(
    predicate: (ValueT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is ResultK.Success<ValueT> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.isFailure(): Boolean {
    contract {
        returns(false) implies (this@isFailure is ResultK.Success<ValueT>)
        returns(true) implies (this@isFailure is ResultK.Failure<FailureT>)
    }
    return this is ResultK.Failure<FailureT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.isFailure(
    predicate: (FailureT) -> Boolean
): Boolean {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
    }
    return this is ResultK.Failure<FailureT> && predicate(cause)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, FailureT : Any> ResultK<ValueT, FailureT>.fold(
    onSuccess: (ValueT) -> SuccessR,
    onFailure: (FailureT) -> SuccessR
): SuccessR {
    contract {
        callsInPlace(onFailure, AT_MOST_ONCE)
        callsInPlace(onSuccess, AT_MOST_ONCE)
    }

    return if (isSuccess()) onSuccess(value) else onFailure(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, SuccessR, FailureT : Any> ResultK<ValueT, FailureT>.map(
    transform: (ValueT) -> SuccessR
): ResultK<SuccessR, FailureT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return flatMap { value -> transform(value).asSuccess() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, SuccessR, FailureT : Any> ResultK<ValueT, FailureT>.flatMap(
    transform: (ValueT) -> ResultK<SuccessR, FailureT>
): ResultK<SuccessR, FailureT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, SuccessR, FailureT : Any> ResultK<ValueT, FailureT>.andThen(
    block: (ValueT) -> ResultK<SuccessR, FailureT>
): ResultK<SuccessR, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailureT : Any, FailureR : Any> ResultK<ValueT, FailureT>.mapFailure(
    transform: (FailureT) -> FailureR
): ResultK<ValueT, FailureR> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else transform(cause).asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailureT : Any> ResultK<Boolean, FailureT>.flatMapBoolean(
    ifTrue: () -> ResultK<ValueT, FailureT>,
    ifFalse: () -> ResultK<ValueT, FailureT>
): ResultK<ValueT, FailureT> {
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
public inline fun <ValueT, ResultT : Any, FailureT : Any> ResultK<ValueT?, FailureT>.flatMapNotNull(
    transform: (ValueT) -> ResultK<ResultT, FailureT>
): ResultK<ResultT?, FailureT> {
    contract {
        callsInPlace(transform, AT_MOST_ONCE)
    }

    return if (this.isSuccess() && this.value != null)
        transform(value)
    else
        @Suppress("UNCHECKED_CAST")
        this as ResultK<ResultT?, FailureT>
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, ResultT : Any, FailureT : Any> ResultK<ValueT?, FailureT>.flatMapNullable(
    ifNull: () -> ResultK<ResultT, FailureT>,
    ifNonNull: (ValueT) -> ResultK<ResultT, FailureT>
): ResultK<ResultT, FailureT> {
    contract {
        callsInPlace(ifNull, AT_MOST_ONCE)
        callsInPlace(ifNonNull, AT_MOST_ONCE)
    }

    return if (this.isFailure())
        this
    else if (this.value != null)
        ifNonNull(value)
    else
        ifNull()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.onSuccess(
    block: (ValueT) -> Unit
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isSuccess()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.onFailure(
    block: (FailureT) -> Unit
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return also { if (it.isFailure()) block(it.cause) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.recover(
    block: (FailureT) -> ValueT
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause).asSuccess()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.recoverWith(
    block: (FailureT) -> ResultK<ValueT, FailureT>
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.getOrForward(
    block: (ResultK.Failure<FailureT>) -> Nothing
): ValueT {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else block(this)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.getOrNull(): ValueT? {
    contract {
        returns(null) implies (this@getOrNull is ResultK.Failure<FailureT>)
        returnsNotNull() implies (this@getOrNull is ResultK.Success<ValueT>)
    }

    return if (isSuccess()) value else null
}

public infix fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.getOrElse(default: ValueT): ValueT =
    if (isSuccess()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.getOrElse(
    default: (FailureT) -> ValueT
): ValueT {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else default(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.orElse(
    default: () -> ResultK<ValueT, FailureT>
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(default, AT_MOST_ONCE)
    }
    return if (isSuccess()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.orThrow(
    builder: (FailureT) -> Throwable
): ValueT {
    contract {
        callsInPlace(builder, AT_MOST_ONCE)
    }
    return if (isSuccess()) value else throw builder(cause)
}

@OptIn(ExperimentalContracts::class)
public fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.getFailureOrNull(): FailureT? {
    contract {
        returns(null) implies (this@getFailureOrNull is ResultK.Success<ValueT>)
        returnsNotNull() implies (this@getFailureOrNull is ResultK.Failure<FailureT>)
    }

    return if (isFailure()) cause else null
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.forEach(block: (ValueT) -> Unit) {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else Unit
}

public fun <T : Any> ResultK<T, T>.merge(): T = fold(onSuccess = ::identity, onFailure = ::identity)

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.apply(
    block: ValueT.() -> ResultK<Unit, FailureT>
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (this.isSuccess()) {
        val result = block(this.value)
        if (result.isSuccess()) this else result
    } else
        this
}

public fun <ValueT, FailureT : Any> Iterable<ResultK<ValueT, FailureT>>.sequence(): ResultK<List<ValueT>, FailureT> =
    traverse(::identity)

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@OverloadResolutionByLambdaReturnType
public inline fun <ValueT, SuccessR, FailureT : Any> Iterable<ValueT>.traverse(
    transform: (ValueT) -> ResultK<SuccessR, FailureT>
): ResultK<List<SuccessR>, FailureT> {
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
    return if (items.isNotEmpty()) items.asSuccess() else ResultK.Success.asEmptyList
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, FailureT : Any, M : MutableList<SuccessR>> Iterable<ValueT>.traverseTo(
    destination: M,
    transform: (ValueT) -> ResultK<SuccessR, FailureT>
): ResultK<M, FailureT> {
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
public inline fun <ValueT, FailureT : Any, K, V, M : MutableMap<K, V>> Iterable<ValueT>.traverseTo(
    destination: M,
    transform: (ValueT) -> ResultK<Pair<K, V>, FailureT>
): ResultK<M, FailureT> {
    contract {
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, { it.first }, { it.second }, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, FailureT : Any, K, M : MutableMap<K, SuccessR>> Iterable<ValueT>.traverseTo(
    destination: M,
    keySelector: (SuccessR) -> K,
    transform: (ValueT) -> ResultK<SuccessR, FailureT>
): ResultK<M, FailureT> {
    contract {
        callsInPlace(keySelector, InvocationKind.UNKNOWN)
        callsInPlace(transform, InvocationKind.UNKNOWN)
    }
    return traverseTo(destination, keySelector, ::identity, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, SuccessR, FailureT : Any, K, V, M : MutableMap<K, V>> Iterable<ValueT>.traverseTo(
    destination: M,
    keySelector: (SuccessR) -> K,
    valueTransform: (SuccessR) -> V,
    transform: (ValueT) -> ResultK<SuccessR, FailureT>
): ResultK<M, FailureT> {
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
public inline fun <ValueT : Any, FailureT : Any> ResultK<ValueT?, FailureT>.filterNotNull(
    failureBuilder: () -> FailureT
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(failureBuilder, AT_MOST_ONCE)
    }
    return if (this.isFailure())
        this
    else if (this.value != null) {
        @Suppress("UNCHECKED_CAST")
        this as ResultK<ValueT, FailureT>
    } else
        failureBuilder().asFailure()
}

@OptIn(ExperimentalContracts::class)
public inline fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.filterOrElse(
    predicate: (ValueT) -> Boolean,
    default: () -> FailureT
): ResultK<ValueT, FailureT> {
    contract {
        callsInPlace(predicate, AT_MOST_ONCE)
        callsInPlace(default, AT_MOST_ONCE)
    }

    return if (isFailure() || predicate(this.value)) this else default().asFailure()
}

public fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.liftToError():
    ResultK<ValueT, Fail<FailureT, Nothing>> =
    mapToError { it }

public fun <ValueT, FailureT : Any> ResultK<ValueT, FailureT>.liftToException():
    ResultK<ValueT, Fail<Nothing, FailureT>> =
    mapToException { it }

public inline infix fun <ValueT, FailureT : Any, ErrorT : Any> ResultK<ValueT, FailureT>.mapToError(
    transform: (FailureT) -> ErrorT
): ResultK<ValueT, Fail<ErrorT, Nothing>> =
    if (isSuccess()) this else Fail.error(transform(cause)).asFailure()

public inline infix fun <ValueT, FailureT : Any, ExceptionT : Any> ResultK<ValueT, FailureT>.mapToException(
    transform: (FailureT) -> ExceptionT
): ResultK<ValueT, Fail<Nothing, ExceptionT>> =
    if (isSuccess()) this else Fail.exception(transform(cause)).asFailure()
