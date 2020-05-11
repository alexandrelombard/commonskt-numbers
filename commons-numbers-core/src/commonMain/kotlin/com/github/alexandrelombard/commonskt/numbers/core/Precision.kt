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
package com.github.alexandrelombard.commonskt.numbers.core

import com.github.alexandrelombard.commonskt.math.BigDecimal
import com.github.alexandrelombard.commonskt.math.RoundingMode
import kotlin.math.abs
import kotlin.math.max

/**
 * Utilities for comparing numbers.
 */
object Precision {
    /**
     *
     *
     * Largest double-precision floating-point number such that
     * `1 + EPSILON` is numerically equal to 1. This value is an upper
     * bound on the relative error due to rounding real numbers to double
     * precision floating-point numbers.
     *
     *
     *
     * In IEEE 754 arithmetic, this is 2<sup>-53</sup>.
     *
     *
     * @see [Machine epsilon](http://en.wikipedia.org/wiki/Machine_epsilon)
     */
    var EPSILON = 0.0

    /**
     * Safe minimum, such that `1 / SAFE_MIN` does not overflow.
     * In IEEE 754 arithmetic, this is also the smallest normalized
     * number 2<sup>-1022</sup>.
     */
    var SAFE_MIN = 0.0

    /** Exponent offset in IEEE754 representation.  */
    private const val EXPONENT_OFFSET = 1023L

    /** Offset to order signed double numbers lexicographically.  */
    private val SGN_MASK = 0x8000000000000000UL

    /** Offset to order signed double numbers lexicographically.  */
    private const val SGN_MASK_FLOAT = -0x80000000

    /** Positive zero.  */
    private const val POSITIVE_ZERO = 0.0

    /** Positive zero bits.  */
    private val POSITIVE_ZERO_DOUBLE_BITS: Long = (+0.0).toRawBits()

    /** Negative zero bits.  */
    private val NEGATIVE_ZERO_DOUBLE_BITS: Long = (-0.0).toRawBits()

    /** Positive zero bits.  */
    private val POSITIVE_ZERO_FLOAT_BITS: Int = (+0.0f).toRawBits()

    /** Negative zero bits.  */
    private val NEGATIVE_ZERO_FLOAT_BITS: Int = (-0.0f).toRawBits()

    /**
     * Compares two numbers given some amount of allowed error.
     * The returned value is
     *
     *  *
     * 0 if  [equals(x, y, eps)][.equals],
     *
     *  *
     * negative if ![equals(x, y, eps)][.equals] and `x < y`,
     *
     *  *
     * positive if ![equals(x, y, eps)][.equals] and `x > y` or
     * either argument is `NaN`.
     *
     *
     *
     * @param x First value.
     * @param y Second value.
     * @param eps Allowed error when checking for equality.
     * @return 0 if the value are considered equal, -1 if the first is smaller than
     * the second, 1 is the first is larger than the second.
     */
    fun compareTo(x: Double, y: Double, eps: Double): Int {
        if (equals(x, y, eps)) {
            return 0
        } else if (x < y) {
            return -1
        }
        return 1
    }

    /**
     * Compares two numbers given some amount of allowed error.
     * Two float numbers are considered equal if there are `(maxUlps - 1)`
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from
     * [
 * Bruce Dawson](http://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/). Returns `false` if either of the arguments is NaN.
     * The returned value is
     *
     *  *
     * zero if [equals(x, y, maxUlps)][.equals],
     *
     *  *
     * negative if ![equals(x, y, maxUlps)][.equals] and `x < y`,
     *
     *  *
     * positive if ![equals(x, y, maxUlps)][.equals] and `x > y`
     * or either argument is `NaN`.
     *
     *
     *
     * @param x First value.
     * @param y Second value.
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return 0 if the value are considered equal, -1 if the first is smaller than
     * the second, 1 is the first is larger than the second.
     */
    fun compareTo(x: Double, y: Double, maxUlps: Int): Int {
        if (equals(x, y, maxUlps)) {
            return 0
        } else if (x < y) {
            return -1
        }
        return 1
    }

    /**
     * Returns true if both arguments are NaN or they are
     * equal as defined by [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal or both are NaN.
     */
    fun equalsIncludingNaN(x: Float, y: Float): Boolean {
        val xIsNan: Boolean = x.isNaN()
        val yIsNan: Boolean = y.isNaN()
        // Combine the booleans with bitwise OR
        return if (xIsNan or yIsNan) !(xIsNan xor yIsNan) else equals(
            x,
            y,
            1
        )
    }

    /**
     * Returns `true` if there is no float value strictly between the
     * arguments or the difference between them is within the range of allowed
     * error (inclusive). Returns `false` if either of the arguments
     * is NaN.
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other.
     */
    fun equals(x: Float, y: Float, eps: Float): Boolean {
        return equals(x, y, 1) || abs(y - x) <= eps
    }

    /**
     * Returns true if the arguments are both NaN, there are no float value strictly
     * between the arguments or the difference between them is within the range of allowed
     * error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other,
     * or both are NaN.
     */
    fun equalsIncludingNaN(x: Float, y: Float, eps: Float): Boolean {
        return equalsIncludingNaN(
            x,
            y,
            1
        ) || abs(y - x) <= eps
    }
    /**
     * Returns true if the arguments are equal or within the range of allowed
     * error (inclusive). Returns `false` if either of the arguments is NaN.
     *
     *
     * Two double numbers are considered equal if there are `(maxUlps - 1)`
     * (or fewer) floating point numbers between them, i.e. two adjacent
     * floating point numbers are considered equal.
     *
     *
     *
     * Adapted from [
 * Bruce Dawson](http://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/).
     *
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if there are fewer than `maxUlps` floating
     * point values between `x` and `y`.
     */
    /**
     * Returns true iff they are equal as defined by
     * [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal.
     */
    fun equals(x: Float, y: Float, maxUlps: Int = 1): Boolean {
        val xInt: Int = x.toRawBits()
        val yInt: Int = y.toRawBits()
        val isEqual: Boolean
        if (xInt xor yInt and SGN_MASK_FLOAT == 0) {
            // number have same sign, there is no risk of overflow
            isEqual = abs(xInt - yInt) <= maxUlps
        } else {
            // number have opposite signs, take care of overflow
            val deltaPlus: Int
            val deltaMinus: Int
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_FLOAT_BITS
                deltaMinus = xInt - NEGATIVE_ZERO_FLOAT_BITS
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_FLOAT_BITS
                deltaMinus = yInt - NEGATIVE_ZERO_FLOAT_BITS
            }
            isEqual = if (deltaPlus > maxUlps) {
                false
            } else {
                deltaMinus <= maxUlps - deltaPlus
            }
        }
        return isEqual && !x.isNaN() && !y.isNaN()
    }

    /**
     * Returns true if both arguments are NaN or if they are equal as defined
     * by [equals(x, y, maxUlps)][.equals].
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if both arguments are NaN or if there are less than
     * `maxUlps` floating point values between `x` and `y`.
     */
    fun equalsIncludingNaN(x: Float, y: Float, maxUlps: Int): Boolean {
        val xIsNan: Boolean = x.isNaN()
        val yIsNan: Boolean = y.isNaN()
        // Combine the booleans with bitwise OR
        return if (xIsNan or yIsNan) !(xIsNan xor yIsNan) else equals(
            x,
            y,
            maxUlps
        )
    }

    /**
     * Returns true if the arguments are both NaN or they are
     * equal as defined by [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal or both are NaN.
     */
    fun equalsIncludingNaN(x: Double, y: Double): Boolean {
        val xIsNan: Boolean = x.isNaN()
        val yIsNan: Boolean = y.isNaN()
        // Combine the booleans with bitwise OR
        return if (xIsNan or yIsNan) !(xIsNan xor yIsNan) else equals(
            x,
            y,
            1
        )
    }

    /**
     * Returns `true` if there is no double value strictly between the
     * arguments or the difference between them is within the range of allowed
     * error (inclusive). Returns `false` if either of the arguments
     * is NaN.
     *
     * @param x First value.
     * @param y Second value.
     * @param eps Amount of allowed absolute error.
     * @return `true` if the values are equal or within range of each other.
     */
    fun equals(x: Double, y: Double, eps: Double): Boolean {
        return equals(x, y, 1) || abs(y - x) <= eps
    }

    /**
     * Returns `true` if there is no double value strictly between the
     * arguments or the relative difference between them is less than or equal
     * to the given tolerance. Returns `false` if either of the arguments
     * is NaN.
     *
     * @param x First value.
     * @param y Second value.
     * @param eps Amount of allowed relative error.
     * @return `true` if the values are two adjacent floating point
     * numbers or they are within range of each other.
     */
    fun equalsWithRelativeTolerance(x: Double, y: Double, eps: Double): Boolean {
        if (equals(x, y, 1)) {
            return true
        }
        val absoluteMax: Double = max(abs(x), abs(y))
        val relativeDifference: Double = abs((x - y) / absoluteMax)
        return relativeDifference <= eps
    }

    /**
     * Returns true if the arguments are both NaN, there are no double value strictly
     * between the arguments or the difference between them is within the range of allowed
     * error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other,
     * or both are NaN.
     */
    fun equalsIncludingNaN(x: Double, y: Double, eps: Double): Boolean {
        return equalsIncludingNaN(
            x,
            y
        ) || abs(y - x) <= eps
    }
    /**
     * Returns true if the arguments are equal or within the range of allowed
     * error (inclusive). Returns `false` if either of the arguments is NaN.
     *
     *
     * Two float numbers are considered equal if there are `(maxUlps - 1)`
     * (or fewer) floating point numbers between them, i.e. two adjacent
     * floating point numbers are considered equal.
     *
     *
     *
     * Adapted from [
     * Bruce Dawson](http://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/).
     *
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if there are fewer than `maxUlps` floating
     * point values between `x` and `y`.
     */
    fun equals(x: Double, y: Double, maxUlps: Int = 1): Boolean {
        val xInt: Long = x.toRawBits()
        val yInt: Long = y.toRawBits()
        val isEqual: Boolean
        if (((xInt xor yInt).toULong() and SGN_MASK).toLong() == 0L) {
            // number have same sign, there is no risk of overflow
            isEqual = abs(xInt - yInt) <= maxUlps
        } else {
            // number have opposite signs, take care of overflow
            val deltaPlus: Long
            val deltaMinus: Long
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_DOUBLE_BITS
                deltaMinus = xInt - NEGATIVE_ZERO_DOUBLE_BITS
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_DOUBLE_BITS
                deltaMinus = yInt - NEGATIVE_ZERO_DOUBLE_BITS
            }
            isEqual = if (deltaPlus > maxUlps) {
                false
            } else {
                deltaMinus <= maxUlps - deltaPlus
            }
        }
        return isEqual && !x.isNaN() && !y.isNaN()
    }

    /**
     * Returns true if both arguments are NaN or if they are equal as defined
     * by [equals(x, y, maxUlps)][.equals].
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if both arguments are NaN or if there are less than
     * `maxUlps` floating point values between `x` and `y`.
     */
    fun equalsIncludingNaN(x: Double, y: Double, maxUlps: Int): Boolean {
        val xIsNan: Boolean = x.isNaN()
        val yIsNan: Boolean = y.isNaN()
        // Combine the booleans with bitwise OR
        return if (xIsNan or yIsNan) !(xIsNan xor yIsNan) else equals(
            x,
            y,
            maxUlps
        )
    }
    /**
     * Rounds the given value to the specified number of decimal places.
     * The value is rounded using the given method which is any method defined
     * in [BigDecimal].
     * If `x` is infinite or `NaN`, then the value of `x` is
     * returned unchanged, regardless of the other parameters.
     *
     * @param x Value to round.
     * @param scale Number of digits to the right of the decimal point.
     * @param roundingMethod Rounding method as defined in [BigDecimal].
     * @return the rounded value.
     * @throws ArithmeticException if `roundingMethod` is
     * [RoundingMode.UNNECESSARY] and the specified scaling operation
     * would require rounding.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    fun round(
        x: Double,
        scale: Int,
        roundingMethod: RoundingMode = RoundingMode.HALF_UP
    ): Double {
        return try {
            val rounded: Double = BigDecimal(x.toString())
                .setScale(scale, roundingMethod)
                .toDouble()
            // MATH-1089: negative values rounded to zero should result in negative zero
            if (rounded == POSITIVE_ZERO) POSITIVE_ZERO * x else rounded
        } catch (ex: NumberFormatException) {
            if (x.isInfinite()) {
                x
            } else Double.NaN
        }
    }

    /**
     * Computes a number `delta` close to `originalDelta` with
     * the property that <pre>`
     * x + delta - x
    `</pre> *
     * is exactly machine-representable.
     * This is useful when computing numerical derivatives, in order to reduce
     * roundoff errors.
     *
     * @param x Value.
     * @param originalDelta Offset value.
     * @return a number `delta` so that `x + delta` and `x`
     * differ by a representable floating number.
     */
    fun representableDelta(
        x: Double,
        originalDelta: Double
    ): Double {
        return x + originalDelta - x
    }

    init {
        /*
         *  This was previously expressed as = 0x1.0p-53
         *  However, OpenJDK (Sparc Solaris) cannot handle such small
         *  constants: MATH-721
         */
        EPSILON = Double.fromBits(
            EXPONENT_OFFSET - 53L shl 52)

        /*
         * This was previously expressed as = 0x1.0p-1022
         * However, OpenJDK (Sparc Solaris) cannot handle such small
         * constants: MATH-721
         */SAFE_MIN = Double.fromBits(
            EXPONENT_OFFSET - 1022L shl 52)
    }
}