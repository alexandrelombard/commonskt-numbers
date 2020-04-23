/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.alexandrelombard.commonskt.numbers.field

import com.github.alexandrelombard.commonskt.numbers.core.NativeOperators
import com.github.alexandrelombard.commonskt.numbers.core.Precision
import kotlin.math.pow


/**
 * Wraps a `double` value in order to be used as a field
 * element.
 */
class FP64
/**
 * @param value Value.
 */ private constructor(
    /** Value.  */
    private val value: Double
) : Number(), NativeOperators<FP64>, Comparable<FP64> {

    /** {@inheritDoc}  */
    override fun add(a: FP64): FP64 {
        return FP64(value + a.value)
    }

    /** {@inheritDoc}  */
    override fun negate(): FP64 {
        return FP64(-value)
    }

    /** {@inheritDoc}  */
    override fun multiply(a: FP64): FP64 {
        return FP64(value * a.value)
    }

    /** {@inheritDoc}  */
    override fun reciprocal(): FP64 {
        return FP64(1 / value)
    }

    /** {@inheritDoc}  */
    override fun subtract(a: FP64): FP64 {
        return FP64(value - a.value)
    }

    /** {@inheritDoc}  */
    override fun divide(a: FP64): FP64 {
        return FP64(value / a.value)
    }

    /** {@inheritDoc}  */
    override fun multiply(n: Int): FP64 {
        return FP64(value * n)
    }

    /** {@inheritDoc}  */
    override fun pow(n: Int): FP64 {
        return if (n == 0) {
            ONE
        } else FP64(value.pow(n.toDouble()))
    }

    /** {@inheritDoc}  */
    override fun equals(other: Any?): Boolean {
        if (other is FP64) {
            return Precision.equals(value, other.value, 1)
        }
        return false
    }

    /** {@inheritDoc}  */
    override fun hashCode(): Int {
        return value.hashCode()
    }

    /** {@inheritDoc}  */
    override fun toString(): String {
        return value.toString()
    }

    /** {@inheritDoc}  */
    override fun toDouble(): Double {
        return value
    }

    /** {@inheritDoc}  */
    override fun toFloat(): Float {
        return value.toFloat()
    }

    /** {@inheritDoc}  */
    override fun toInt(): Int {
        return value.toInt()
    }

    /** {@inheritDoc}  */
    override fun toLong(): Long {
        return value.toLong()
    }

    /** {@inheritDoc}  */
    override fun toByte(): Byte {
        return value.toByte()
    }

    /** {@inheritDoc}  */
    override fun toShort(): Short {
        return value.toShort()
    }

    /** {@inheritDoc}  */
    override fun toChar(): Char {
        return value.toChar()
    }

    /** {@inheritDoc}  */
    override operator fun compareTo(other: FP64): Int {
        return value.compareTo(other.value)
    }

    /** {@inheritDoc}  */
    override fun zero(): FP64 {
        return ZERO
    }

    /** {@inheritDoc}  */
    override fun one(): FP64 {
        return ONE
    }

    companion object {
        private const val serialVersionUID = 1L

        /** Additive neutral.  */
        private val ZERO = FP64(0.0)

        /** Multiplicative neutral.  */
        private val ONE = FP64(1.0)

        /**
         * Factory.
         *
         * @param value Value.
         * @return a new instance.
         */
        fun of(value: Double): FP64 {
            return FP64(value)
        }
    }

}
