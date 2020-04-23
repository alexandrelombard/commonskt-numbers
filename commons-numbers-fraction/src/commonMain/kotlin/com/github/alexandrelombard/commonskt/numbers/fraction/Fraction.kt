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
package com.github.alexandrelombard.commonskt.numbers.fraction

import org.apache.commonskt.math.multiplyExact
import org.apache.commonskt.math.toIntExact
import com.github.alexandrelombard.commonskt.numbers.core.ArithmeticUtils.gcd
import com.github.alexandrelombard.commonskt.numbers.core.ArithmeticUtils.pow
import com.github.alexandrelombard.commonskt.numbers.core.NativeOperators
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

/**
 * Representation of a rational number.
 *
 *
 * The number is expressed as the quotient `p/q` of two 32-bit integers,
 * a numerator `p` and a non-zero denominator `q`.
 *
 *
 * This class is immutable.
 *
 * [Rational number](https://en.wikipedia.org/wiki/Rational_number)
 */
@ExperimentalStdlibApi
class Fraction : Number, Comparable<Fraction>,
    NativeOperators<Fraction> {
    /**
     * Access the numerator as an `int`.
     *
     * @return the numerator as an `int`.
     */
    /** The numerator of this fraction reduced to lowest terms.  */
    val numerator: Int

    /**
     * Access the denominator as an `int`.
     *
     * @return the denominator as an `int`.
     */
    /** The denominator of this fraction reduced to lowest terms.  */
    val denominator: Int

    /**
     * Private constructor: Instances are created using factory methods.
     *
     *
     * This constructor should only be invoked when the fraction is known
     * to be non-zero; otherwise use [.ZERO]. This avoids creating
     * the zero representation `0 / -1`.
     *
     * @param num Numerator.
     * @param den Denominator.
     * @throws ArithmeticException if the denominator is `zero`.
     */
    private constructor(num: Int, den: Int) {
        if (den == 0) {
            throw FractionException(
                FractionException.ERROR_ZERO_DENOMINATOR
            )
        }
        if (num == den) {
            numerator = 1
            denominator = 1
        } else {
            // Reduce numerator (p) and denominator (q) by greatest common divisor.
            val p: Int
            val q: Int

            // If num and den are both 2^-31, or if one is 0 and the other is 2^-31,
            // the calculation of the gcd below will fail. Ensure that this does not
            // happen by dividing both by 2 in case both are even.
            if (num or den and 1 == 0) {
                p = num shr 1
                q = den shr 1
            } else {
                p = num
                q = den
            }

            // Will not throw.
            // Cannot return 0 as gcd(0, 0) has been eliminated.
            val d = gcd(p, q)
            numerator = p / d
            denominator = q / d
        }
    }

    /**
     * Private constructor: Instances are created using factory methods.
     *
     *
     * This sets the denominator to 1.
     *
     * @param num Numerator.
     */
    private constructor(num: Int) {
        numerator = num
        denominator = 1
    }

    /**
     * Create a fraction given the double value and either the maximum error
     * allowed or the maximum number of denominator digits.
     *
     *
     *
     * NOTE: This constructor is called with:
     *
     *  * EITHER a valid epsilon value and the maxDenominator set to
     * Integer.MAX_VALUE (that way the maxDenominator has no effect)
     *  * OR a valid maxDenominator value and the epsilon value set to
     * zero (that way epsilon only has effect if there is an exact
     * match before the maxDenominator value is reached).
     *
     *
     *
     * It has been done this way so that the same code can be reused for
     * both scenarios. However this could be confusing to users if it
     * were part of the public API and this method should therefore remain
     * PRIVATE.
     *
     *
     *
     *
     * See JIRA issue ticket MATH-181 for more details:
     * https://issues.apache.org/jira/browse/MATH-181
     *
     *
     * @param value Value to convert to a fraction.
     * @param epsilon Maximum error allowed.
     * The resulting fraction is within `epsilon` of `value`,
     * in absolute terms.
     * @param maxDenominator Maximum denominator value allowed.
     * @param maxIterations Maximum number of convergents.
     * @throws IllegalArgumentException if the given `value` is NaN or infinite.
     * @throws ArithmeticException if the continued fraction failed to converge.
     */
    private constructor(
        value: Double,
        epsilon: Double,
        maxDenominator: Int,
        maxIterations: Int
    ) {
        if (!value.isFinite()) {
            throw IllegalArgumentException(NOT_FINITE + value)
        }
        val overflow = Int.MAX_VALUE.toLong()
        var r0 = value
        var a0 = floor(r0).toLong()
        if (abs(a0) > overflow) {
            throw FractionException(
                FractionException.ERROR_CONVERSION_OVERFLOW(
                    value,
                    a0,
                    1L
                )
            )
        }

        // check for (almost) integer arguments, which should not go to iterations.
        if (abs(a0 - value) <= epsilon) {
            numerator = a0.toInt()
            denominator = 1
            return
        }
        var p0: Long = 1
        var q0: Long = 0
        var p1 = a0
        var q1: Long = 1
        var p2: Long
        var q2: Long
        var n = 0
        var stop = false
        do {
            ++n
            val r1 = 1.0 / (r0 - a0)
            val a1 = floor(r1).toLong()
            p2 = a1 * p1 + p0
            q2 = a1 * q1 + q0
            if (abs(p2) > overflow ||
                abs(q2) > overflow
            ) {
                // in maxDenominator mode, if the last fraction was very close to the actual value
                // q2 may overflow in the next iteration; in this case return the last one.
                if (epsilon == 0.0 &&
                    abs(q1) < maxDenominator
                ) {
                    break
                }
                throw FractionException(
                    FractionException.ERROR_CONVERSION_OVERFLOW(
                        value,
                        p2,
                        q2
                    )
                )
            }
            val convergent = p2.toDouble() / q2.toDouble()
            if (n < maxIterations && abs(convergent - value) > epsilon && q2 < maxDenominator
            ) {
                p0 = p1
                p1 = p2
                q0 = q1
                q1 = q2
                a0 = a1
                r0 = r1
            } else {
                stop = true
            }
        } while (!stop)
        if (n >= maxIterations) {
            throw FractionException(
                FractionException.ERROR_CONVERSION(
                    value,
                    maxIterations
                )
            )
        }
        if (q2 < maxDenominator) {
            numerator = p2.toInt()
            denominator = q2.toInt()
        } else {
            numerator = p1.toInt()
            denominator = q1.toInt()
        }
    }

    override fun zero(): Fraction {
        return ZERO
    }

    override fun one(): Fraction {
        return ONE
    }

    /**
     * Retrieves the sign of this fraction.
     *
     * @return -1 if the value is strictly negative, 1 if it is strictly
     * positive, 0 if it is 0.
     */
    fun signum(): Int {
        return numerator.sign * denominator.sign
    }

    /**
     * Returns the absolute value of this fraction.
     *
     * @return the absolute value.
     */
    fun abs(): Fraction {
        return if (signum() >= 0) this else negate()
    }

    override fun negate(): Fraction {
        return if (numerator == Int.MIN_VALUE) Fraction(
            numerator,
            -denominator
        ) else Fraction(
            -numerator,
            denominator
        )
    }

    /**
     * {@inheritDoc}
     *
     *
     * Raises an exception if the fraction is equal to zero.
     *
     * @throws ArithmeticException if the current numerator is `zero`
     */
    override fun reciprocal(): Fraction {
        return Fraction(
            denominator,
            numerator
        )
    }

    /**
     * Returns the `double` value closest to this fraction.
     * This calculates the fraction as numerator divided by denominator.
     *
     * @return the fraction as a `double`.
     */
    override fun toDouble(): Double {
        return numerator.toDouble() / denominator.toDouble()
    }

    /**
     * Returns the `float` value closest to this fraction.
     * This calculates the fraction as numerator divided by denominator.
     *
     * @return the fraction as a `float`.
     */
    override fun toFloat(): Float {
        return toDouble().toFloat()
    }

    /**
     * Returns the whole number part of the fraction.
     *
     * @return the largest `int` value that is not larger than this fraction.
     */
    override fun toInt(): Int {
        // Note: numerator / denominator fails for Integer.MIN_VALUE / -1.
        // Casting the double value handles this case.
        return toDouble().toInt()
    }

    override fun toShort(): Short {
        return toDouble().toShort()
    }

    override fun toChar(): Char {
        return toDouble().toChar()
    }

    override fun toByte(): Byte {
        return toDouble().toByte()
    }

    /**
     * Returns the whole number part of the fraction.
     *
     * @return the largest `long` value that is not larger than this fraction.
     */
    override fun toLong(): Long {
        return numerator.toLong() / denominator
    }

    /**
     * Adds the specified `value` to this fraction, returning
     * the result in reduced form.
     *
     * @param value Value to add.
     * @return `this + value`.
     * @throws ArithmeticException if the resulting numerator
     * cannot be represented in an `int`.
     */
    fun add(value: Int): Fraction {
        if (value == 0) {
            return this
        }
        if (isZero) {
            return Fraction(value)
        }
        // Convert to numerator with same effective denominator
        val num = value.toLong() * denominator
        return of(
            toIntExact(numerator + num),
            denominator
        )
    }

    /**
     * Adds the specified `value` to this fraction, returning
     * the result in reduced form.
     *
     * @param a Value to add.
     * @return `this + value`.
     * @throws ArithmeticException if the resulting numerator or denominator
     * cannot be represented in an `int`.
     */
    override fun add(a: Fraction): Fraction {
        return addSub(a, true /* add */)
    }

    /**
     * Subtracts the specified `value` from this fraction, returning
     * the result in reduced form.
     *
     * @param value Value to subtract.
     * @return `this - value`.
     * @throws ArithmeticException if the resulting numerator
     * cannot be represented in an `int`.
     */
    fun subtract(value: Int): Fraction {
        if (value == 0) {
            return this
        }
        if (isZero) {
            // Special case for min value
            return if (value == Int.MIN_VALUE) Fraction(
                Int.MIN_VALUE,
                -1
            ) else Fraction(-value)
        }
        // Convert to numerator with same effective denominator
        val num = value.toLong() * denominator
        return of(
            toIntExact(numerator - num),
            denominator
        )
    }

    /**
     * Subtracts the specified `value` from this fraction, returning
     * the result in reduced form.
     *
     * @param a Value to subtract.
     * @return `this - value`.
     * @throws ArithmeticException if the resulting numerator or denominator
     * cannot be represented in an `int`.
     */
    override fun subtract(a: Fraction): Fraction {
        return addSub(a, false /* subtract */)
    }

    /**
     * Implements add and subtract using algorithm described in Knuth 4.5.1.
     *
     * @param value Fraction to add or subtract.
     * @param isAdd Whether the operation is "add" or "subtract".
     * @return a new instance.
     * @throws ArithmeticException if the resulting numerator or denominator
     * cannot be represented in an `int`.
     */
    private fun addSub(value: Fraction, isAdd: Boolean): Fraction {
        if (value.isZero) {
            return this
        }
        // Zero is identity for addition.
        if (isZero) {
            return if (isAdd) value else value.negate()
        }

        /*
         * Let the two fractions be u/u' and v/v', and d1 = gcd(u', v').
         * First, compute t, defined as:
         *
         * t = u(v'/d1) +/- v(u'/d1)
         */
        val d1 = gcd(denominator, value.denominator)
        val uvp = numerator.toLong() * (value.denominator / d1).toLong()
        val upv = value.numerator.toLong() * (denominator / d1).toLong()

        /*
         * The largest possible absolute value of a product of two ints is 2^62,
         * which can only happen as a result of -2^31 * -2^31 = 2^62, so a
         * product of -2^62 is not possible. It follows that (uvp - upv) cannot
         * overflow, and (uvp + upv) could only overflow if uvp = upv = 2^62.
         * But for this to happen, the terms u, v, v'/d1 and u'/d1 would all
         * have to be -2^31, which is not possible because v'/d1 and u'/d1
         * are necessarily coprime.
         */
        val t = if (isAdd) uvp + upv else uvp - upv

        /*
         * Because u is coprime to u' and v is coprime to v', t is necessarily
         * coprime to both v'/d1 and u'/d1. However, it might have a common
         * factor with d1.
         */
        val d2 = gcd(t, d1.toLong())
        // result is (t/d2) / (u'/d1)(v'/d2)
        return of(
            toIntExact(t / d2),
            multiplyExact(
                denominator / d1,
                value.denominator / d2.toInt()
            )
        )
    }

    /**
     * Multiply this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param n Value to multiply by.
     * @return `this * value`.
     * @throws ArithmeticException if the resulting numerator
     * cannot be represented in an `int`.
     */
    override fun multiply(n: Int): Fraction {
        if (n == 0 || isZero) {
            return ZERO
        }

        // knuth 4.5.1
        // Make sure we don't overflow unless the result *must* overflow.
        // (see multiply(Fraction) using value / 1 as the argument).
        val d2 = gcd(n, denominator)
        return Fraction(
            multiplyExact(numerator, n / d2),
            denominator / d2
        )
    }

    /**
     * Multiply this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param a Value to multiply by.
     * @return `this * value`.
     * @throws ArithmeticException if the resulting numerator or denominator
     * cannot be represented in an `int`.
     */
    override fun multiply(a: Fraction): Fraction {
        return if (a.isZero || isZero) {
            ZERO
        } else multiply(a.numerator, a.denominator)
    }

    /**
     * Multiply this fraction by the passed fraction decomposed into a numerator and
     * denominator, returning the result in reduced form.
     *
     *
     * This is a utility method to be used by multiply and divide. The decomposed
     * fraction arguments and this fraction are not checked for zero.
     *
     * @param num Fraction numerator.
     * @param den Fraction denominator.
     * @return `this * num / den`.
     * @throws ArithmeticException if the resulting numerator or denominator cannot
     * be represented in an `int`.
     */
    private fun multiply(num: Int, den: Int): Fraction {
        // knuth 4.5.1
        // Make sure we don't overflow unless the result *must* overflow.
        val d1 = gcd(numerator, den)
        val d2 = gcd(num, denominator)
        return Fraction(
            multiplyExact(numerator / d1, num / d2),
            multiplyExact(denominator / d2, den / d1)
        )
    }

    /**
     * Divide this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param value Value to divide by
     * @return `this / value`.
     * @throws ArithmeticException if the value to divide by is zero
     * or if the resulting numerator or denominator cannot be represented
     * by an `int`.
     */
    fun divide(value: Int): Fraction {
        if (value == 0) {
            throw FractionException(
                FractionException.ERROR_DIVIDE_BY_ZERO
            )
        }
        if (isZero) {
            return ZERO
        }
        // Multiply by reciprocal

        // knuth 4.5.1
        // Make sure we don't overflow unless the result *must* overflow.
        // (see multiply(Fraction) using 1 / value as the argument).
        val d1 = gcd(numerator, value)
        return Fraction(
            numerator / d1,
            multiplyExact(denominator, value / d1)
        )
    }

    /**
     * Divide this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param a Value to divide by
     * @return `this / value`.
     * @throws ArithmeticException if the value to divide by is zero
     * or if the resulting numerator or denominator cannot be represented
     * by an `int`.
     */
    override fun divide(a: Fraction): Fraction {
        if (a.isZero) {
            throw FractionException(
                FractionException.ERROR_DIVIDE_BY_ZERO
            )
        }
        return if (isZero) {
            ZERO
        } else multiply(a.denominator, a.numerator)
        // Multiply by reciprocal
    }

    /**
     * Returns a `Fraction` whose value is
     * `this<sup>exponent</sup>`, returning the result in reduced form.
     *
     * @param n exponent to which this `Fraction` is to be raised.
     * @return `this<sup>exponent</sup>`.
     * @throws ArithmeticException if the intermediate result would overflow.
     */
    override fun pow(n: Int): Fraction {
        if (n == 0) {
            return ONE
        }
        if (isZero) {
            return ZERO
        }
        return if (n < 0) {
            Fraction(
                pow(denominator, -n),
                pow(numerator, -n)
            )
        } else Fraction(
            pow(numerator, n),
            pow(denominator, n)
        )
    }

    /**
     * Returns the `String` representing this fraction.
     * Uses:
     *
     *  * `"0"` if `numerator` is zero.
     *  * `"numerator"` if `denominator` is one.
     *  * `"numerator / denominator"` for all other cases.
     *
     *
     * @return a string representation of the fraction.
     */
    override fun toString(): String {
        val str: String
        str = if (isZero) {
            "0"
        } else if (denominator == 1) {
            numerator.toString()
        } else {
            "$numerator / $denominator"
        }
        return str
    }

    /**
     * Compares this object with the specified object for order using the signed magnitude.
     *
     * @param other {@inheritDoc}
     * @return {@inheritDoc}
     */
    override operator fun compareTo(other: Fraction): Int {
        // Compute the sign of each part
        val lns: Int = numerator.sign
        val lds: Int = denominator.sign
        val rns: Int = other.numerator.sign
        val rds: Int = other.denominator.sign
        val lhsSigNum = lns * lds
        val rhsSigNum = rns * rds
        if (lhsSigNum != rhsSigNum) {
            return if (lhsSigNum > rhsSigNum) 1 else -1
        }
        // Same sign.
        // Avoid a multiply if both fractions are zero
        if (lhsSigNum == 0) {
            return 0
        }
        // Compare absolute magnitude.
        // Multiplication by the signum is equal to the absolute.
        val nOd = numerator.toLong() * lns * other.denominator * rds
        val dOn = denominator.toLong() * lds * other.numerator * rns
        return nOd.compareTo(dOn)
    }

    /**
     * Test for equality with another object. If the other object is a `Fraction` then a
     * comparison is made of the sign and magnitude; otherwise `false` is returned.
     *
     * @param other {@inheritDoc}
     * @return {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is Fraction) {
            // Since fractions are always in lowest terms, numerators and
            // denominators can be compared directly for equality.
            val rhs = other
            if (signum() == rhs.signum()) {
                return abs(numerator) == abs(rhs.numerator) &&
                        abs(denominator) == abs(rhs.denominator)
            }
        }
        return false
    }

    override fun hashCode(): Int {
        // Incorporate the sign and absolute values of the numerator and denominator.
        // Equivalent to:
        // int hash = 1;
        // hash = 31 * hash + Math.abs(numerator);
        // hash = 31 * hash + Math.abs(denominator);
        // hash = hash * signum()
        // Note: x * Integer.signum(x) == Math.abs(x).
        val numS: Int = numerator.sign
        val denS: Int = denominator.sign
        return (31 * (31 + numerator * numS) + denominator * denS) * numS * denS
    }

    /**
     * Returns true if this fraction is zero.
     *
     * @return true if zero
     */
    private val isZero: Boolean
        get() = numerator == 0

    companion object {
        /** A fraction representing "0".  */
        val ZERO = Fraction(0)

        /** A fraction representing "1".  */
        val ONE = Fraction(1)

        /** Serializable version identifier.  */
        private const val serialVersionUID = 20190701L

        /** The default epsilon used for convergence.  */
        private const val DEFAULT_EPSILON = 1e-5

        /** Message for non-finite input double argument to factory constructors.  */
        private const val NOT_FINITE = "Not finite: "
        /**
         * Create a fraction given the double value and maximum error allowed.
         *
         *
         *
         * References:
         *
         * [Continued Fraction](http://mathworld.wolfram.com/ContinuedFraction.html) equations (11) and (22)-(26)
         *
         *
         * @param value Value to convert to a fraction.
         * @param epsilon Maximum error allowed. The resulting fraction is within
         * `epsilon` of `value`, in absolute terms.
         * @param maxIterations Maximum number of convergents.
         * @throws IllegalArgumentException if the given `value` is NaN or infinite.
         * @throws ArithmeticException if the continued fraction failed to converge.
         * @return a new instance.
         */
        fun from(
            value: Double,
            epsilon: Double = DEFAULT_EPSILON,
            maxIterations: Int = 100
        ): Fraction {
            return if (value == 0.0) {
                ZERO
            } else Fraction(
                value,
                epsilon,
                Int.MAX_VALUE,
                maxIterations
            )
        }

        /**
         * Create a fraction given the double value and maximum denominator.
         *
         *
         *
         * References:
         *
         *  [Continued Fraction](http://mathworld.wolfram.com/ContinuedFraction.html) equations (11) and (22)-(26)
         *
         *
         * @param value Value to convert to a fraction.
         * @param maxDenominator Maximum allowed value for denominator.
         * @throws IllegalArgumentException if the given `value` is NaN or infinite.
         * @throws ArithmeticException if the continued fraction failed to converge.
         * @return a new instance.
         */
        fun from(
            value: Double,
            maxDenominator: Int
        ): Fraction {
            return if (value == 0.0) {
                ZERO
            } else Fraction(
                value,
                0.0,
                maxDenominator,
                100
            )
        }

        /**
         * Create a fraction given the numerator. The denominator is `1`.
         *
         * @param num Numerator.
         * @return a new instance.
         */
        fun of(num: Int): Fraction {
            return if (num == 0) {
                ZERO
            } else Fraction(num)
        }

        /**
         * Create a fraction given the numerator and denominator.
         * The fraction is reduced to lowest terms.
         *
         * @param num Numerator.
         * @param den Denominator.
         * @throws ArithmeticException if the denominator is `zero`.
         * @return a new instance.
         */
        fun of(num: Int, den: Int): Fraction {
            return if (num == 0) {
                ZERO
            } else Fraction(num, den)
        }

        /**
         * Returns a `Fraction` instance representing the specified string `s`.
         *
         *
         * If `s` is `null`, then a `NullPointerException` is thrown.
         *
         *
         * The string must be in a format compatible with that produced by
         * [Fraction.toString()][.toString].
         * The format expects an integer optionally followed by a `'/'` character and
         * and second integer. Leading and trailing spaces are allowed around each numeric part.
         * Each numeric part is parsed using [Integer.parseInt]. The parts
         * are interpreted as the numerator and optional denominator of the fraction. If absent
         * the denominator is assumed to be "1".
         *
         *
         * Examples of valid strings and the equivalent `Fraction` are shown below:
         *
         * <pre>
         * "0"                 = Fraction.of(0)
         * "42"                = Fraction.of(42)
         * "0 / 1"             = Fraction.of(0, 1)
         * "1 / 3"             = Fraction.of(1, 3)
         * "-4 / 13"           = Fraction.of(-4, 13)</pre>
         *
         *
         * Note: The fraction is returned in reduced form and the numerator and denominator
         * may not match the values in the input string. For this reason the result of
         * `Fraction.parse(s).toString().equals(s)` may not be `true`.
         *
         * @param s String representation.
         * @return an instance.
         * @throws NullPointerException if the string is null.
         * @throws NumberFormatException if the string does not contain a parsable fraction.
         * @see Integer.parseInt
         * @see .toString
         */
        fun parse(s: String): Fraction {
            val stripped = s.replace(",", "")
            val slashLoc = stripped.indexOf('/')
            // if no slash, parse as single number
            if (slashLoc == -1) {
                return of(
                    stripped.trim { it <= ' ' }.toInt()
                )
            }
            val num = stripped.substring(0, slashLoc).trim { it <= ' ' }.toInt()
            val denom = stripped.substring(slashLoc + 1).trim { it <= ' ' }.toInt()
            return of(
                num,
                denom
            )
        }
    }
}
