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
package org.apache.commonskt.numbers.fraction

import org.apache.commonskt.math.BigDecimal
import org.apache.commonskt.math.BigInteger
import org.apache.commonskt.math.RoundingMode
import org.apache.commonskt.numbers.core.NativeOperators
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min

/**
 * Representation of a rational number using arbitrary precision.
 *
 *
 * The number is expressed as the quotient `p/q` of two [BigInteger]s,
 * a numerator `p` and a non-zero denominator `q`.
 *
 *
 * This class is immutable.
 *
 * [Rational number](https://en.wikipedia.org/wiki/Rational_number)
 */
@Suppress("unused")
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class BigFraction : Number, Comparable<BigFraction>, NativeOperators<BigFraction> {
    /** The numerator of this fraction reduced to lowest terms.  */
    private val numerator: BigInteger

    /** The denominator of this fraction reduced to lowest terms.  */
    private val denominator: BigInteger

    /**
     * Private constructor: Instances are created using factory methods.
     *
     *
     * This constructor should only be invoked when the fraction is known
     * to be non-zero; otherwise use [.ZERO]. This avoids creating
     * the zero representation `0 / -1`.
     *
     * @param num Numerator, must not be `null`.
     * @param den Denominator, must not be `null`.
     * @throws ArithmeticException if the denominator is zero.
     */
    private constructor(num: BigInteger, den: BigInteger) {
        if (den.signum == 0) {
            throw FractionException(FractionException.ERROR_ZERO_DENOMINATOR)
        }

        // reduce numerator and denominator by greatest common denominator
        val gcd: BigInteger = num.gcd(den)
        if (BigInteger.ONE.compareTo(gcd) < 0) {
            numerator = num.divide(gcd)
            denominator = den.divide(gcd)
        } else {
            numerator = num
            denominator = den
        }
    }

    /**
     * Private constructor: Instances are created using factory methods.
     *
     *
     * This sets the denominator to 1.
     *
     * @param num Numerator (must not be null).
     */
    private constructor(num: BigInteger) {
        numerator = num
        denominator = BigInteger.ONE
    }

    override fun zero(): BigFraction {
        return ZERO
    }

    override fun one(): BigFraction {
        return ONE
    }

    /**
     * Access the numerator as a `BigInteger`.
     *
     * @return the numerator as a `BigInteger`.
     */
    fun getNumerator(): BigInteger {
        return numerator
    }

    /**
     * Access the numerator as an `int`.
     *
     * @return the numerator as an `int`.
     */
    val numeratorAsInt: Int
        get() = numerator.toInt()

    /**
     * Access the numerator as a `long`.
     *
     * @return the numerator as a `long`.
     */
    val numeratorAsLong: Long
        get() = numerator.toLong()

    /**
     * Access the denominator as a `BigInteger`.
     *
     * @return the denominator as a `BigInteger`.
     */
    fun getDenominator(): BigInteger {
        return denominator
    }

    /**
     * Access the denominator as an `int`.
     *
     * @return the denominator as an `int`.
     */
    val denominatorAsInt: Int
        get() = denominator.toInt()

    /**
     * Access the denominator as a `long`.
     *
     * @return the denominator as a `long`.
     */
    val denominatorAsLong: Long
        get() = denominator.toLong()

    /**
     * Retrieves the sign of this fraction.
     *
     * @return -1 if the value is strictly negative, 1 if it is strictly
     * positive, 0 if it is 0.
     */
    fun signum(): Int {
        return numerator.signum * denominator.signum
    }

    /**
     * Returns the absolute value of this fraction.
     *
     * @return the absolute value.
     */
    fun abs(): BigFraction {
        return if (signum() >= 0) this else negate()
    }

    override fun negate(): BigFraction {
        return BigFraction(numerator.negate(), denominator)
    }

    /**
     * {@inheritDoc}
     *
     *
     * Raises an exception if the fraction is equal to zero.
     *
     * @throws ArithmeticException if the current numerator is `zero`
     */
    override fun reciprocal(): BigFraction {
        return BigFraction(denominator, numerator)
    }

    /**
     * Returns the `double` value closest to this fraction.
     *
     * @return the fraction as a `double`.
     */
    override fun toDouble(): Double {
        return Double.fromBits(toFloatingPointBits(11, 52))
    }

    /**
     * Returns the `float` value closest to this fraction.
     *
     * @return the fraction as a `double`.
     */
    override fun toFloat(): Float {
        return Float.fromBits(toFloatingPointBits(8, 23).toInt())
    }

    /**
     * Returns the whole number part of the fraction.
     *
     * @return the largest `int` value that is not larger than this fraction.
     */
    override fun toInt(): Int {
        return numerator.divide(denominator).toInt()
    }

    override fun toByte(): Byte {
        return numerator.divide(denominator).toByte()
    }

    override fun toChar(): Char {
        return numerator.divide(denominator).toChar()
    }

    override fun toShort(): Short {
        return numerator.divide(denominator).toShort()
    }

    /**
     * Returns the whole number part of the fraction.
     *
     * @return the largest `long` value that is not larger than this fraction.
     */
    override fun toLong(): Long {
        return numerator.divide(denominator).toLong()
    }

    /**
     * Returns the `BigDecimal` representation of this fraction.
     * This calculates the fraction as numerator divided by denominator.
     *
     * @return the fraction as a `BigDecimal`.
     * @throws ArithmeticException
     * if the exact quotient does not have a terminating decimal
     * expansion.
     * @see BigDecimal
     */
    fun bigDecimalValue(): BigDecimal {
        return BigDecimal(numerator).divide(BigDecimal(denominator))
    }

    /**
     * Returns the `BigDecimal` representation of this fraction.
     * This calculates the fraction as numerator divided by denominator
     * following the passed rounding mode.
     *
     * @param roundingMode Rounding mode to apply.
     * @return the fraction as a `BigDecimal`.
     * @see BigDecimal
     */
    fun bigDecimalValue(roundingMode: RoundingMode): BigDecimal {
        return BigDecimal(numerator).divide(BigDecimal(denominator), roundingMode)
    }

    /**
     * Returns the `BigDecimal` representation of this fraction.
     * This calculates the fraction as numerator divided by denominator
     * following the passed scale and rounding mode.
     *
     * @param scale
     * scale of the `BigDecimal` quotient to be returned.
     * see [BigDecimal] for more information.
     * @param roundingMode Rounding mode to apply.
     * @return the fraction as a `BigDecimal`.
     * @throws ArithmeticException if `roundingMode` == [RoundingMode.UNNECESSARY] and
     * the specified scale is insufficient to represent the result of the division exactly.
     * @see BigDecimal
     */
    fun bigDecimalValue(scale: Int, roundingMode: RoundingMode): BigDecimal {
        return BigDecimal(numerator).divide(BigDecimal(denominator), scale, roundingMode)
    }

    /**
     * Adds the specified `value` to this fraction, returning
     * the result in reduced form.
     *
     * @param value Value to add.
     * @return `this + value`.
     */
    fun add(value: Int): BigFraction {
        return add(BigInteger.valueOf(value.toLong()))
    }

    /**
     * Adds the specified `value` to this fraction, returning
     * the result in reduced form.
     *
     * @param value Value to add.
     * @return `this + value`.
     */
    fun add(value: Long): BigFraction {
        return add(BigInteger.valueOf(value))
    }

    /**
     * Adds the specified `value` to this fraction, returning
     * the result in reduced form.
     *
     * @param value Value to add.
     * @return `this + value`.
     */
    fun add(value: BigInteger): BigFraction {
        if (value.signum == 0) {
            return this
        }
        return if (isZero) {
            of(value)
        } else of(numerator.add(denominator.multiply(value)), denominator)
    }

    /**
     * Adds the specified `value` to this fraction, returning
     * the result in reduced form.
     *
     * @param a Value to add.
     * @return `this + value`.
     */
    override fun add(a: BigFraction): BigFraction {
        if (a.isZero) {
            return this
        }
        if (isZero) {
            return a
        }
        val num: BigInteger
        val den: BigInteger
        if (denominator == a.denominator) {
            num = numerator.add(a.numerator)
            den = denominator
        } else {
            num = numerator.multiply(a.denominator).add(a.numerator.multiply(denominator))
            den = denominator.multiply(a.denominator)
        }
        return if (num.signum == 0) {
            ZERO
        } else BigFraction(num, den)
    }

    /**
     * Subtracts the specified `value` from this fraction, returning
     * the result in reduced form.
     *
     * @param value Value to subtract.
     * @return `this - value`.
     */
    fun subtract(value: Int): BigFraction {
        return subtract(BigInteger.valueOf(value.toLong()))
    }

    /**
     * Subtracts the specified `value` from this fraction, returning
     * the result in reduced form.
     *
     * @param value Value to subtract.
     * @return `this - value`.
     */
    fun subtract(value: Long): BigFraction {
        return subtract(BigInteger.valueOf(value))
    }

    /**
     * Subtracts the specified `value` from this fraction, returning
     * the result in reduced form.
     *
     * @param value Value to subtract.
     * @return `this - value`.
     */
    fun subtract(value: BigInteger): BigFraction {
        if (value.signum == 0) {
            return this
        }
        return if (isZero) {
            of(value.negate())
        } else of(numerator.subtract(denominator.multiply(value)), denominator)
    }

    /**
     * Subtracts the specified `value` from this fraction, returning
     * the result in reduced form.
     *
     * @param a Value to subtract.
     * @return `this - value`.
     */
    override fun subtract(a: BigFraction): BigFraction {
        if (a.isZero) {
            return this
        }
        if (isZero) {
            return a.negate()
        }
        val num: BigInteger
        val den: BigInteger
        if (denominator == a.denominator) {
            num = numerator.subtract(a.numerator)
            den = denominator
        } else {
            num = numerator.multiply(a.denominator).subtract(a.numerator.multiply(denominator))
            den = denominator.multiply(a.denominator)
        }
        return if (num.signum == 0) {
            ZERO
        } else BigFraction(num, den)
    }

    /**
     * Multiply this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param n Value to multiply by.
     * @return `this * value`.
     */
    override fun multiply(n: Int): BigFraction {
        return if (n == 0 || isZero) {
            ZERO
        } else multiply(BigInteger.valueOf(n.toLong()))
    }

    /**
     * Multiply this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param value Value to multiply by.
     * @return `this * value`.
     */
    fun multiply(value: Long): BigFraction {
        return if (value == 0L || isZero) {
            ZERO
        } else multiply(BigInteger.valueOf(value))
    }

    /**
     * Multiply this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param value Value to multiply by.
     * @return `this * value`.
     */
    fun multiply(value: BigInteger): BigFraction {
        return if (value.signum == 0 || isZero) {
            ZERO
        } else BigFraction(value.multiply(numerator), denominator)
    }

    /**
     * Multiply this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param a Value to multiply by.
     * @return `this * value`.
     */
    override fun multiply(a: BigFraction): BigFraction {
        return if (a.isZero || isZero) {
            ZERO
        } else BigFraction(
            numerator.multiply(a.numerator),
            denominator.multiply(a.denominator)
        )
    }

    /**
     * Divide this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param value Value to divide by
     * @return `this / value`.
     * @throws ArithmeticException if the value to divide by is zero
     */
    fun divide(value: Int): BigFraction {
        return divide(BigInteger.valueOf(value.toLong()))
    }

    /**
     * Divide this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param value Value to divide by
     * @return `this / value`.
     * @throws ArithmeticException if the value to divide by is zero
     */
    fun divide(value: Long): BigFraction {
        return divide(BigInteger.valueOf(value))
    }

    /**
     * Divide this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param value Value to divide by
     * @return `this / value`.
     * @throws ArithmeticException if the value to divide by is zero
     */
    fun divide(value: BigInteger): BigFraction {
        if (value.signum == 0) {
            throw FractionException(FractionException.ERROR_DIVIDE_BY_ZERO)
        }
        return if (isZero) {
            ZERO
        } else BigFraction(numerator, denominator.multiply(value))
    }

    /**
     * Divide this fraction by the passed `value`, returning
     * the result in reduced form.
     *
     * @param a Value to divide by
     * @return `this / value`.
     * @throws ArithmeticException if the value to divide by is zero
     */
    override fun divide(a: BigFraction): BigFraction {
        if (a.isZero) {
            throw FractionException(FractionException.ERROR_DIVIDE_BY_ZERO)
        }
        return if (isZero) {
            ZERO
        } else BigFraction(
            numerator.multiply(a.denominator),
            denominator.multiply(a.numerator)
        )
        // Multiply by reciprocal
    }

    /**
     * Returns a `BigFraction` whose value is
     * `this<sup>exponent</sup>`, returning the result in reduced form.
     *
     * @param n exponent to which this `BigFraction` is to be raised.
     * @return `this<sup>exponent</sup>`.
     * @throws ArithmeticException if the intermediate result would overflow.
     */
    override fun pow(n: Int): BigFraction {
        if (n == 0) {
            return ONE
        }
        if (isZero) {
            return ZERO
        }

        // Note: Raise the BigIntegers to the power and then reduce.
        // The supported range for BigInteger is currently
        // +/-2^(Integer.MAX_VALUE) exclusive thus larger
        // exponents (long, BigInteger) are currently not supported.
        return if (n < 0) {
            BigFraction(
                denominator.pow(-n),
                numerator.pow(-n)
            )
        } else BigFraction(
            numerator.pow(n),
            denominator.pow(n)
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
        } else if (BigInteger.ONE == denominator) {
            numerator.toString()
        } else {
            numerator.toString() + " / " + denominator
        }
        return str
    }

    /**
     * Compares this object with the specified object for order using the signed magnitude.
     *
     * @param other {@inheritDoc}
     * @return {@inheritDoc}
     */
    override operator fun compareTo(other: BigFraction): Int {
        val lhsSigNum = signum()
        val rhsSigNum = other.signum()
        if (lhsSigNum != rhsSigNum) {
            return if (lhsSigNum > rhsSigNum) 1 else -1
        }
        // Same sign.
        // Avoid a multiply if both fractions are zero
        if (lhsSigNum == 0) {
            return 0
        }
        // Compare absolute magnitude
        val nOd: BigInteger = numerator.abs().multiply(other.denominator.abs())
        val dOn: BigInteger = denominator.abs().multiply(other.numerator.abs())
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
        if (other is BigFraction) {
            // Since fractions are always in lowest terms, numerators and
            // denominators can be compared directly for equality.
            val rhs = other
            if (signum() == rhs.signum()) {
                return numerator.abs() == rhs.numerator.abs() && denominator.abs() == rhs.denominator.abs()
            }
        }
        return false
    }

    override fun hashCode(): Int {
        // Incorporate the sign and absolute values of the numerator and denominator.
        // Equivalent to:
        // int hash = 1;
        // hash = 31 * hash + numerator.abs().hashCode();
        // hash = 31 * hash + denominator.abs().hashCode();
        // hash = hash * signum()
        // Note: BigInteger.hashCode() * BigInteger.signum == BigInteger.abs().hashCode().
        val numS: Int = numerator.signum
        val denS: Int = denominator.signum
        return (31 * (31 + numerator.hashCode() * numS) + denominator.hashCode() * denS) * numS * denS
    }

    /**
     * Calculates the sign bit, the biased exponent and the significand for a
     * binary floating-point representation of this `BigFraction`
     * according to the IEEE 754 standard, and encodes these values into a `long`
     * variable. The representative bits are arranged adjacent to each other and
     * placed at the low-order end of the returned `long` value, with the
     * least significant bits used for the significand, the next more
     * significant bits for the exponent, and next more significant bit for the
     * sign.
     *
     *
     * Warning: The arguments are not validated.
     *
     * @param exponentLength the number of bits allowed for the exponent; must be
     * between 1 and 32 (inclusive), and must not be greater
     * than `63 - significandLength`
     * @param significandLength the number of bits allowed for the significand
     * (excluding the implicit leading 1-bit in
     * normalized numbers, e.g. 52 for a double-precision
     * floating-point number); must be between 1 and
     * `63 - exponentLength` (inclusive)
     * @return the bits of an IEEE 754 binary floating-point representation of
     * this fraction encoded in a `long`, as described above.
     */
    private fun toFloatingPointBits(exponentLength: Int, significandLength: Int): Long {
        // Assume the following conditions:
        //assert exponentLength >= 1;
        //assert exponentLength <= 32;
        //assert significandLength >= 1;
        //assert significandLength <= 63 - exponentLength;
        if (isZero) {
            return 0L
        }
        val sign = if (numerator.signum * denominator.signum == -1) 1L else 0L
        val positiveNumerator: BigInteger = numerator.abs()
        val positiveDenominator: BigInteger = denominator.abs()

        /*
         * The most significant 1-bit of a non-zero number is not explicitly
         * stored in the significand of an IEEE 754 normalized binary
         * floating-point number, so we need to round the value of this fraction
         * to (significandLength + 1) bits. In order to do this, we calculate
         * the most significant (significandLength + 2) bits, and then, based on
         * the least significant of those bits, find out whether we need to
         * round up or down.
         *
         * First, we'll remove all powers of 2 from the denominator because they
         * are not relevant for the significand of the prospective binary
         * floating-point value.
         */
        val denRightShift: Int = positiveDenominator.lowestSetBit
        val divisor: BigInteger = positiveDenominator.shiftRight(denRightShift)

        /*
         * Now, we're going to calculate the (significandLength + 2) most
         * significant bits of the fraction's value using integer division. To
         * guarantee that the quotient of the division has at least
         * (significandLength + 2) bits, the bit length of the dividend must
         * exceed that of the divisor by at least that amount.
         *
         * If the denominator has prime factors other than 2, i.e. if the
         * divisor was not reduced to 1, an excess of exactly
         * (significandLength + 2) bits is sufficient, because the knowledge
         * that the fractional part of the precise quotient's binary
         * representation does not terminate is enough information to resolve
         * cases where the most significant (significandLength + 2) bits alone
         * are not conclusive.
         *
         * Otherwise, the quotient must be calculated exactly and the bit length
         * of the numerator can only be reduced as long as no precision is lost
         * in the process (meaning it can have powers of 2 removed, like the
         * denominator).
         */
        var numRightShift: Int = positiveNumerator.bitLength() - divisor.bitLength() - (significandLength + 2)
        if (numRightShift > 0 && divisor == BigInteger.ONE) {
            numRightShift = min(numRightShift, positiveNumerator.lowestSetBit)
        }
        val dividend: BigInteger = positiveNumerator.shiftRight(numRightShift)
        val quotient: BigInteger = dividend.divide(divisor)
        var quotRightShift: Int = quotient.bitLength() - (significandLength + 1)
        var significand: Long = roundAndRightShift(
            quotient,
            quotRightShift,
            divisor != BigInteger.ONE
        ).toLong()

        /*
         * If the significand had to be rounded up, this could have caused the
         * bit length of the significand to increase by one.
         */if (significand and (1L shl significandLength + 1) != 0L) {
            significand = significand shr 1
            quotRightShift++
        }

        /*
         * Now comes the exponent. The absolute value of this fraction based on
         * the current local variables is:
         *
         * significand * 2^(numRightShift - denRightShift + quotRightShift)
         *
         * To get the unbiased exponent for the floating-point value, we need to
         * add (significandLength) to the above exponent, because all but the
         * most significant bit of the significand will be treated as a
         * fractional part. To convert the unbiased exponent to a biased
         * exponent, we also need to add the exponent bias.
         */
        val exponentBias = (1 shl exponentLength - 1) - 1
        var exponent = numRightShift.toLong() - denRightShift + quotRightShift + significandLength + exponentBias
        val maxExponent = (1L shl exponentLength) - 1L //special exponent for infinities and NaN
        if (exponent >= maxExponent) { //infinity
            exponent = maxExponent
            significand = 0L
        } else if (exponent > 0) { //normalized number
            significand = significand and (-1L ushr 64 - significandLength) //remove implicit leading 1-bit
        } else { //smaller than the smallest normalized number
            /*
             * We need to round the quotient to fewer than
             * (significandLength + 1) bits. This must be done with the original
             * quotient and not with the current significand, because the loss
             * of precision in the previous rounding might cause a rounding of
             * the current significand's value to produce a different result
             * than a rounding of the original quotient.
             *
             * So we find out how many high-order bits from the quotient we can
             * transfer into the significand. The absolute value of the fraction
             * is:
             *
             * quotient * 2^(numRightShift - denRightShift)
             *
             * To get the significand, we need to right shift the quotient so
             * that the above exponent becomes (1 - exponentBias - significandLength)
             * (the unbiased exponent of a subnormal floating-point number is
             * defined as equivalent to the minimum unbiased exponent of a
             * normalized floating-point number, and (- significandLength)
             * because the significand will be treated as the fractional part).
             */
            significand = roundAndRightShift(
                quotient,
                1 - exponentBias - significandLength - (numRightShift - denRightShift),
                divisor != BigInteger.ONE
            ).toLong()
            exponent = 0L

            /*
             * Note: It is possible that an otherwise subnormal number will
             * round up to the smallest normal number. However, this special
             * case does not need to be treated separately, because the
             * overflowing highest-order bit of the significand will then simply
             * become the lowest-order bit of the exponent, increasing the
             * exponent from 0 to 1 and thus establishing the implicity of the
             * leading 1-bit.
             */
        }
        return sign shl significandLength + exponentLength or
                (exponent shl significandLength) or
                significand
    }

    /**
     * Returns true if this fraction is zero.
     *
     * @return true if zero
     */
    private val isZero: Boolean
        get() = numerator.signum == 0

    companion object {
        /** A fraction representing "0".  */
        val ZERO = BigFraction(BigInteger.ZERO)

        /** A fraction representing "1".  */
        val ONE = BigFraction(BigInteger.ONE)

        /** Serializable version identifier.  */
        private const val serialVersionUID = 20190701L

        /** Message for non-finite input double argument to factory constructors.  */
        private const val NOT_FINITE = "Not finite: "

        /**
         * Create a fraction given the double value and either the maximum
         * error allowed or the maximum number of denominator digits.
         *
         *
         *
         * NOTE: This method is called with:
         *
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
         * @return a new instance.
         * @throws IllegalArgumentException if the given `value` is NaN or infinite.
         * @throws ArithmeticException if the continued fraction failed to converge.
         */
        private fun from(
            value: Double,
            epsilon: Double,
            maxDenominator: Int,
            maxIterations: Int
        ): BigFraction {
            if (!value.isFinite()) {
                throw IllegalArgumentException(NOT_FINITE + value)
            }
            if (value == 0.0) {
                return ZERO
            }
            val overflow = Int.MAX_VALUE.toLong()
            var r0 = value
            var a0 = floor(r0).toLong()
            if (abs(a0) > overflow) {
                throw FractionException(FractionException.ERROR_CONVERSION_OVERFLOW(value, a0, 1L))
            }

            // check for (almost) integer arguments, which should not go
            // to iterations.
            if (abs(a0 - value) <= epsilon) {
                return BigFraction(
                    BigInteger.valueOf(a0),
                    BigInteger.ONE
                )
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
                if (p2 > overflow ||
                    q2 > overflow
                ) {
                    // in maxDenominator mode, if the last fraction was very close to the actual value
                    // q2 may overflow in the next iteration; in this case return the last one.
                    if (epsilon == 0.0 &&
                        abs(q1) < maxDenominator
                    ) {
                        break
                    }
                    throw FractionException(FractionException.ERROR_CONVERSION_OVERFLOW(value, p2, q2))
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
                throw FractionException(FractionException.ERROR_CONVERSION(value, maxIterations))
            }
            return if (q2 < maxDenominator) BigFraction(
                BigInteger.valueOf(p2),
                BigInteger.valueOf(q2)
            ) else BigFraction(
                BigInteger.valueOf(p1),
                BigInteger.valueOf(q1)
            )
        }

        /**
         * Create a fraction given the double value.
         *
         *
         * This factory method behaves *differently* to the method
         * [.from]. It converts the double value
         * exactly, considering its internal bits representation. This works for all
         * values except NaN and infinities and does not requires any loop or
         * convergence threshold.
         *
         *
         *
         * Since this conversion is exact and since double numbers are sometimes
         * approximated, the fraction created may seem strange in some cases. For example,
         * calling `from(1.0 / 3.0)` does *not* create
         * the fraction \( \frac{1}{3} \), but the fraction \( \frac{6004799503160661}{18014398509481984} \)
         * because the double number passed to the method is not exactly \( \frac{1}{3} \)
         * (which cannot be represented exactly in IEEE754).
         *
         *
         * @param value Value to convert to a fraction.
         * @throws IllegalArgumentException if the given `value` is NaN or infinite.
         * @return a new instance.
         *
         * @see .from
         */
        fun from(value: Double): BigFraction {
            if (!value.isFinite()) {
                throw IllegalArgumentException(NOT_FINITE + value)
            }
            if (value == 0.0) {
                return ZERO
            }
            val bits: Long = value.toBits()
            val sign = bits and (0x8000000000000000UL).toLong()
            val exponent = bits and 0x7ff0000000000000L
            val mantissa = bits and 0x000fffffffffffffL

            // Compute m and k such that value = m * 2^k
            var m: Long
            var k: Int
            if (exponent == 0L) {
                // Subnormal number, the effective exponent bias is 1022, not 1023.
                // Note: mantissa is never zero as that case has been eliminated.
                m = mantissa
                k = -1074
            } else {
                // Normalized number: Add the implicit most significant bit.
                m = mantissa or 0x0010000000000000L
                k = (exponent shr 52).toInt() - 1075 // Exponent bias is 1023.
            }
            if (sign != 0L) {
                m = -m
            }
            while (m and 0x001ffffffffffffeL != 0L && m and 0x1 == 0L) {
                m = m shr 1
                ++k
            }
            return if (k < 0) BigFraction(
                BigInteger.valueOf(m),
                BigInteger.ZERO.flipBit(-k)
            ) else BigFraction(
                BigInteger.valueOf(m).multiply(BigInteger.ZERO.flipBit(k)),
                BigInteger.ONE
            )
        }

        /**
         * Create a fraction given the double value and maximum error allowed.
         *
         *
         * References:
         *
         *  * [
 * Continued Fraction](http://mathworld.wolfram.com/ContinuedFraction.html) equations (11) and (22)-(26)
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
            epsilon: Double,
            maxIterations: Int
        ): BigFraction {
            return from(value, epsilon, Int.MAX_VALUE, maxIterations)
        }

        /**
         * Create a fraction given the double value and maximum denominator.
         *
         *
         * References:
         *
         *  * [
 * Continued Fraction](http://mathworld.wolfram.com/ContinuedFraction.html) equations (11) and (22)-(26)
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
        ): BigFraction {
            return from(value, 0.0, maxDenominator, 100)
        }

        /**
         * Create a fraction given the numerator. The denominator is `1`.
         *
         * @param num
         * the numerator.
         * @return a new instance.
         */
        fun of(num: Int): BigFraction {
            return if (num == 0) {
                ZERO
            } else BigFraction(BigInteger.valueOf(num.toLong()))
        }

        /**
         * Create a fraction given the numerator. The denominator is `1`.
         *
         * @param num Numerator.
         * @return a new instance.
         */
        fun of(num: Long): BigFraction {
            return if (num == 0L) {
                ZERO
            } else BigFraction(BigInteger.valueOf(num))
        }

        /**
         * Create a fraction given the numerator. The denominator is `1`.
         *
         * @param num Numerator.
         * @return a new instance.
         * @throws NullPointerException if numerator is null.
         */
        fun of(num: BigInteger): BigFraction {
            return if (num.signum == 0) {
                ZERO
            } else BigFraction(num)
        }

        /**
         * Create a fraction given the numerator and denominator.
         * The fraction is reduced to lowest terms.
         *
         * @param num Numerator.
         * @param den Denominator.
         * @return a new instance.
         * @throws ArithmeticException if `den` is zero.
         */
        fun of(num: Int, den: Int): BigFraction {
            return if (num == 0) {
                ZERO
            } else BigFraction(BigInteger.valueOf(num.toLong()), BigInteger.valueOf(den.toLong()))
        }

        /**
         * Create a fraction given the numerator and denominator.
         * The fraction is reduced to lowest terms.
         *
         * @param num Numerator.
         * @param den Denominator.
         * @return a new instance.
         * @throws ArithmeticException if `den` is zero.
         */
        fun of(num: Long, den: Long): BigFraction {
            return if (num == 0L) {
                ZERO
            } else BigFraction(BigInteger.valueOf(num), BigInteger.valueOf(den))
        }

        /**
         * Create a fraction given the numerator and denominator.
         * The fraction is reduced to lowest terms.
         *
         * @param num Numerator.
         * @param den Denominator.
         * @return a new instance.
         * @throws NullPointerException if numerator or denominator are null.
         * @throws ArithmeticException if the denominator is zero.
         */
        fun of(num: BigInteger, den: BigInteger): BigFraction {
            return if (num.signum == 0) {
                ZERO
            } else BigFraction(num, den)
        }

        /**
         * Returns a `BigFraction` instance representing the specified string `s`.
         *
         *
         * If `s` is `null`, then a `NullPointerException` is thrown.
         *
         *
         * The string must be in a format compatible with that produced by
         * [BigFraction.toString()][.toString].
         * The format expects an integer optionally followed by a `'/'` character and
         * and second integer. Leading and trailing spaces are allowed around each numeric part.
         * Each numeric part is parsed using [BigInteger]. The parts
         * are interpreted as the numerator and optional denominator of the fraction. If absent
         * the denominator is assumed to be "1".
         *
         *
         * Examples of valid strings and the equivalent `BigFraction` are shown below:
         *
         * <pre>
         * "0"                 = BigFraction.of(0)
         * "42"                = BigFraction.of(42)
         * "0 / 1"             = BigFraction.of(0, 1)
         * "1 / 3"             = BigFraction.of(1, 3)
         * "-4 / 13"           = BigFraction.of(-4, 13)</pre>
         *
         *
         * Note: The fraction is returned in reduced form and the numerator and denominator
         * may not match the values in the input string. For this reason the result of
         * `BigFraction.parse(s).toString().equals(s)` may not be `true`.
         *
         * @param s String representation.
         * @return an instance.
         * @throws NullPointerException if the string is null.
         * @throws NumberFormatException if the string does not contain a parsable fraction.
         * @see BigInteger
         * @see .toString
         */
        fun parse(s: String): BigFraction {
            val stripped = s.replace(",", "")
            val slashLoc = stripped.indexOf('/')
            // if no slash, parse as single number
            if (slashLoc == -1) {
                return of(BigInteger(stripped.trim { it <= ' ' }))
            }
            val num = BigInteger(stripped.substring(0, slashLoc).trim { it <= ' ' })
            val denom = BigInteger(stripped.substring(slashLoc + 1).trim { it <= ' ' })
            return of(num, denom)
        }

        /**
         * Rounds an integer to the specified power of two (i.e. the minimum number of
         * low-order bits that must be zero) and performs a right-shift by this
         * amount. The rounding mode applied is round to nearest, with ties rounding
         * to even (meaning the prospective least significant bit must be zero). The
         * number can optionally be treated as though it contained at
         * least one 0-bit and one 1-bit in its fractional part, to influence the result in cases
         * that would otherwise be a tie.
         * @param value the number to round and right-shift
         * @param bits the power of two to which to round; must be positive
         * @param hasFractionalBits whether the number should be treated as though
         * it contained a non-zero fractional part
         * @return a `BigInteger` as described above
         * @throws IllegalArgumentException if `bits <= 0`
         */
        private fun roundAndRightShift(
            value: BigInteger,
            bits: Int,
            hasFractionalBits: Boolean
        ): BigInteger {
            if (bits <= 0) {
                throw IllegalArgumentException("bits: $bits")
            }
            var result: BigInteger = value.shiftRight(bits)
            if (value.testBit(bits - 1) &&
                (hasFractionalBits ||
                        value.lowestSetBit < bits - 1 ||
                        value.testBit(bits))
            ) {
                result = result.add(BigInteger.ONE) //round up
            }
            return result
        }
    }
}
