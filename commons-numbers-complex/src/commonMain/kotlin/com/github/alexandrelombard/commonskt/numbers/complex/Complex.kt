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
package com.github.alexandrelombard.commonskt.numbers.complex

import com.github.alexandrelombard.commonskt.lang.DoubleConsts
import com.github.alexandrelombard.commonskt.math.exponent
import com.github.alexandrelombard.commonskt.math.scalb
import kotlin.math.*

/**
 * Cartesian representation of a complex number, i.e. a number which has both a
 * real and imaginary part.
 *
 *
 * This class is immutable. All arithmetic will create a new instance for the
 * result.
 *
 *
 * Arithmetic in this class conforms to the C99 standard for complex numbers
 * defined in ISO/IEC 9899, Annex G. Methods have been named using the equivalent
 * method in ISO C99. The behavior for special cases is listed as defined in C99.
 *
 *
 * For functions \( f \) which obey the conjugate equality \( conj(f(z)) = f(conj(z)) \),
 * the specifications for the upper half-plane imply the specifications for the lower
 * half-plane.
 *
 *
 * For functions that are either odd, \( f(z) = -f(-z) \), or even, \( f(z) =  f(-z) \),
 * the specifications for the first quadrant imply the specifications for the other three
 * quadrants.
 *
 *
 * Special cases of [branch cuts](http://mathworld.wolfram.com/BranchCut.html)
 * for multivalued functions adopt the principle value convention from C99. Specials cases
 * from C99 that raise the "invalid" or "divide-by-zero"
 * [floating-point
 * exceptions](https://en.cppreference.com/w/c/numeric/fenv/FE_exceptions) return the documented value without an explicit mechanism to notify
 * of the exception case, that is no exceptions are thrown during computations in-line with
 * the convention of the corresponding single-valued functions in
 * [kotlin.math].
 * These cases are documented in the method special cases as "invalid" or "divide-by-zero"
 * floating-point operation.
 * Note: Invalid floating-point exception cases will result in a complex number where the
 * cardinality of NaN component parts has increased as a real or imaginary part could
 * not be computed and is set to NaN.
 *
 * @see [
 * ISO/IEC 9899 - Programming languages - C](http://www.open-std.org/JTC1/SC22/WG14/www/standards)
 */
@Suppress("unused")
class Complex
    /**
     * Private default constructor.
     *
     * @param real Real part.
     * @param imaginary Imaginary part.
     */
    private constructor(
        /** The real part.  */
        val real: Double,
        /** The imaginary part.  */
        val imaginary: Double) {

    /**
     * Returns the absolute value of this complex number. This is also called complex norm, modulus,
     * or magnitude.
     *
     *
     * \[ \text{abs}(x + i y) = \sqrt{(x^2 + y^2)} \]
     *
     *
     * Special cases:
     *
     *
     *  * `abs(x + iy) == abs(y + ix) == abs(x - iy)`.
     *  * If `z` is ±∞ + iy for any y, returns +∞.
     *  * If `z` is x + iNaN for non-infinite x, returns NaN.
     *  * If `z` is x + i0, returns |x|.
     *
     *
     *
     * The cases ensure that if either component is infinite then the result is positive
     * infinity. If either component is NaN and this is not [infinite][.isInfinite] then
     * the result is NaN.
     *
     *
     * This method follows the
     * [ISO C Standard](http://www.iso-9899.info/wiki/The_Standard), Annex G,
     * in calculating the returned value without intermediate overflow or underflow.
     *
     *
     * The computed result will be within 1 ulp of the exact result.
     *
     * @return The absolute value.
     * @see .isInfinite
     * @see .isNaN
     * @see [Complex modulus](http://mathworld.wolfram.com/ComplexModulus.html)
     */
    fun abs(): Double {
        return abs(
            real,
            imaginary
        )
    }

    /**
     * Returns the argument of this complex number.
     *
     *
     * The argument is the angle phi between the positive real axis and
     * the point representing this number in the complex plane.
     * The value returned is between \( -\pi \) (not inclusive)
     * and \( \pi \) (inclusive), with negative values returned for numbers with
     * negative imaginary parts.
     *
     *
     * If either real or imaginary part (or both) is NaN, then the result is NaN.
     * Infinite parts are handled as [atan2] handles them,
     * essentially treating finite parts as zero in the presence of an
     * infinite coordinate and returning a multiple of \( \frac{\pi}{4} \) depending on
     * the signs of the infinite parts.
     *
     *
     * This code follows the
     * [ISO C Standard](http://www.iso-9899.info/wiki/The_Standard), Annex G,
     * in calculating the returned value using the `atan2(y, x)` method for complex
     * \( x + iy \).
     *
     * @return The argument of this complex number.
     * @see atan2
     */
    fun arg(): Double {
        // Delegate
        return atan2(imaginary, real)
    }

    /**
     * Returns the squared norm value of this complex number. This is also called the absolute
     * square.
     *
     *
     * \[ \text{norm}(x + i y) = x^2 + y^2 \]
     *
     *
     * If either component is infinite then the result is positive infinity. If either
     * component is NaN and this is not [infinite][.isInfinite] then the result is NaN.
     *
     *
     * Note: This method may not return the same value as the square of [.abs] as
     * that method uses an extended precision computation.
     *
     *
     * `norm()` can be used as a faster alternative than `abs()` for ranking by
     * magnitude. If used for ranking any overflow to infinity will create an equal ranking for
     * values that may be still distinguished by `abs()`.
     *
     * @return The square norm value.
     * @see .isInfinite
     * @see .isNaN
     * @see .abs
     * @see [Absolute square](http://mathworld.wolfram.com/AbsoluteSquare.html)
     */
    fun norm(): Double {
        return if (isInfinite) {
            Double.POSITIVE_INFINITY
        } else real * real + imaginary * imaginary
    }

    /**
     * Returns `true` if either the real *or* imaginary component of the complex number is NaN
     * *and* the complex number is not infinite.
     *
     *
     * Note that:
     *
     *  * There is more than one complex number that can return `true`.
     *  * Different representations of NaN can be distinguished by the
     * [Complex.equals(Object)][.equals] method.
     *
     *
     * @return `true` if this instance contains NaN and no infinite parts.
     * @see Double.isNaN
     * @see .isInfinite
     * @see .equals
     */
    val isNaN: Boolean
        get() = if (real.isNaN() || imaginary.isNaN()) {
            !isInfinite
        } else false

    /**
     * Returns `true` if either real or imaginary component of the complex number is infinite.
     *
     *
     * Note: A complex number with at least one infinite part is regarded
     * as an infinity (even if its other part is a NaN).
     *
     * @return `true` if this instance contains an infinite value.
     * @see Double.isInfinite
     */
    val isInfinite: Boolean
        get() = real.isInfinite() || imaginary.isInfinite()

    /**
     * Returns `true` if both real and imaginary component of the complex number are finite.
     *
     * @return `true` if this instance contains finite values.
     * @see Double.isFinite
     */
    val isFinite: Boolean
        get() = real.isFinite() && imaginary.isFinite()

    /**
     * Returns the
     * [conjugate](http://mathworld.wolfram.com/ComplexConjugate.html)
     * \( \overline{z} \) of this complex number \( z \).
     *
     *
     * \[ z           = a + i b \\
     * \overline{z} = a - i b \]
     *
     * @return The conjugate (\( \overline{z} \)) of this complex number.
     */
    fun conj(): Complex {
        return Complex(real, -imaginary)
    }

    /**
     * Returns a `Complex` whose value is the negation of both the real and imaginary parts
     * of complex number \( z \).
     *
     *
     * \[ z  =  a + i b \\
     * -z  = -a - i b \]
     *
     * @return \( -z \).
     */
    fun negate(): Complex {
        return Complex(-real, -imaginary)
    }

    /**
     * Returns the projection of this complex number onto the Riemann sphere.
     *
     *
     * \( z \) projects to \( z \), except that all complex infinities (even those
     * with one infinite part and one NaN part) project to positive infinity on the real axis.
     *
     * If \( z \) has an infinite part, then `z.proj()` shall be equivalent to:
     *
     * <pre>return Complex.ofCartesian(Double.POSITIVE_INFINITY, Math.copySign(0.0, z.imag());</pre>
     *
     * @return \( z \) projected onto the Riemann sphere.
     * @see .isInfinite
     * @see [
     * IEEE and ISO C standards: cproj](http://pubs.opengroup.org/onlinepubs/9699919799/functions/cproj.html)
     */
    fun proj(): Complex {
        return if (isInfinite) {
            Complex(
                Double.POSITIVE_INFINITY,
                0.0.withSign(imaginary)
            )
        } else this
    }

    /**
     * Returns a `Complex` whose value is `(this + addend)`.
     * Implements the formula:
     *
     *
     * \[ (a + i b) + (c + i d) = (a + c) + i (b + d) \]
     *
     * @param  addend Value to be added to this complex number.
     * @return `this + addend`.
     * @see [Complex Addition](http://mathworld.wolfram.com/ComplexAddition.html)
     */
    fun add(addend: Complex): Complex {
        return Complex(
            real + addend.real,
            imaginary + addend.imaginary
        )
    }

    /**
     * Returns a `Complex` whose value is `(this + addend)`,
     * with `addend` interpreted as a real number.
     * Implements the formula:
     *
     *
     * \[ (a + i b) + c = (a + c) + i b \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * real-only and complex numbers.
     *
     *
     * Note: This method preserves the sign of the imaginary component \( b \) if it is `-0.0`.
     * The sign would be lost if adding \( (c + i 0) \) using
     * [add(Complex.ofCartesian(addend, 0))][.add] since
     * `-0.0 + 0.0 = 0.0`.
     *
     * @param addend Value to be added to this complex number.
     * @return `this + addend`.
     * @see .add
     * @see .ofCartesian
     */
    fun add(addend: Double): Complex {
        return Complex(
            real + addend,
            imaginary
        )
    }

    /**
     * Returns a `Complex` whose value is `(this + addend)`,
     * with `addend` interpreted as an imaginary number.
     * Implements the formula:
     *
     *
     * \[ (a + i b) + i d = a + i (b + d) \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * imaginary-only and complex numbers.
     *
     *
     * Note: This method preserves the sign of the real component \( a \) if it is `-0.0`.
     * The sign would be lost if adding \( (0 + i d) \) using
     * [add(Complex.ofCartesian(0, addend))][.add] since
     * `-0.0 + 0.0 = 0.0`.
     *
     * @param addend Value to be added to this complex number.
     * @return `this + addend`.
     * @see .add
     * @see .ofCartesian
     */
    fun addImaginary(addend: Double): Complex {
        return Complex(
            real,
            imaginary + addend
        )
    }

    /**
     * Returns a `Complex` whose value is `(this - subtrahend)`.
     * Implements the formula:
     *
     *
     * \[ (a + i b) - (c + i d) = (a - c) + i (b - d) \]
     *
     * @param  subtrahend Value to be subtracted from this complex number.
     * @return `this - subtrahend`.
     * @see [Complex Subtraction](http://mathworld.wolfram.com/ComplexSubtraction.html)
     */
    fun subtract(subtrahend: Complex): Complex {
        return Complex(
            real - subtrahend.real,
            imaginary - subtrahend.imaginary
        )
    }

    /**
     * Returns a `Complex` whose value is `(this - subtrahend)`,
     * with `subtrahend` interpreted as a real number.
     * Implements the formula:
     *
     *
     * \[ (a + i b) - c = (a - c) + i b \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * real-only and complex numbers.
     *
     * @param  subtrahend Value to be subtracted from this complex number.
     * @return `this - subtrahend`.
     * @see .subtract
     */
    fun subtract(subtrahend: Double): Complex {
        return Complex(
            real - subtrahend,
            imaginary
        )
    }

    /**
     * Returns a `Complex` whose value is `(this - subtrahend)`,
     * with `subtrahend` interpreted as an imaginary number.
     * Implements the formula:
     *
     *
     * \[ (a + i b) - i d = a + i (b - d) \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * imaginary-only and complex numbers.
     *
     * @param  subtrahend Value to be subtracted from this complex number.
     * @return `this - subtrahend`.
     * @see .subtract
     */
    fun subtractImaginary(subtrahend: Double): Complex {
        return Complex(
            real,
            imaginary - subtrahend
        )
    }

    /**
     * Returns a `Complex` whose value is `(minuend - this)`,
     * with `minuend` interpreted as a real number.
     * Implements the formula:
     * \[ c - (a + i b) = (c - a) - i b \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * real-only and complex numbers.
     *
     *
     * Note: This method inverts the sign of the imaginary component \( b \) if it is `0.0`.
     * The sign would not be inverted if subtracting from \( c + i 0 \) using
     * [Complex.ofCartesian(minuend, 0).subtract(this)][.subtract] since
     * `0.0 - 0.0 = 0.0`.
     *
     * @param  minuend Value this complex number is to be subtracted from.
     * @return `minuend - this`.
     * @see .subtract
     * @see .ofCartesian
     */
    fun subtractFrom(minuend: Double): Complex {
        return Complex(
            minuend - real,
            -imaginary
        )
    }

    /**
     * Returns a `Complex` whose value is `(this - subtrahend)`,
     * with `minuend` interpreted as an imaginary number.
     * Implements the formula:
     * \[ i d - (a + i b) = -a + i (d - b) \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * imaginary-only and complex numbers.
     *
     *
     * Note: This method inverts the sign of the real component \( a \) if it is `0.0`.
     * The sign would not be inverted if subtracting from \( 0 + i d \) using
     * [Complex.ofCartesian(0, minuend).subtract(this)][.subtract] since
     * `0.0 - 0.0 = 0.0`.
     *
     * @param  minuend Value this complex number is to be subtracted from.
     * @return `this - subtrahend`.
     * @see .subtract
     * @see .ofCartesian
     */
    fun subtractFromImaginary(minuend: Double): Complex {
        return Complex(
            -real,
            minuend - imaginary
        )
    }

    /**
     * Returns a `Complex` whose value is `this * factor`.
     * Implements the formula:
     *
     *
     * \[ (a + i b)(c + i d) = (ac - bd) + i (ad + bc) \]
     *
     *
     * Recalculates to recover infinities as specified in C99 standard G.5.1.
     *
     * @param  factor Value to be multiplied by this complex number.
     * @return `this * factor`.
     * @see [Complex Muliplication](http://mathworld.wolfram.com/ComplexMultiplication.html)
     */
    fun multiply(factor: Complex): Complex {
        return multiply(
            real,
            imaginary,
            factor.real,
            factor.imaginary
        )
    }

    /**
     * Returns a `Complex` whose value is `this * factor`, with `factor`
     * interpreted as a real number.
     * Implements the formula:
     *
     *
     * \[ (a + i b) c =  (ac) + i (bc) \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * real-only and complex numbers.
     *
     *
     * Note: This method should be preferred over using
     * [multiply(Complex.ofCartesian(factor, 0))][.multiply]. Multiplication
     * can generate signed zeros if either `this` complex has zeros for the real
     * and/or imaginary component, or if the factor is zero. The summation of signed zeros
     * in [.multiply] may create zeros in the result that differ in sign
     * from the equivalent call to multiply by a real-only number.
     *
     * @param  factor Value to be multiplied by this complex number.
     * @return `this * factor`.
     * @see .multiply
     */
    fun multiply(factor: Double): Complex {
        return Complex(
            real * factor,
            imaginary * factor
        )
    }

    /**
     * Returns a `Complex` whose value is `this * factor`, with `factor`
     * interpreted as an imaginary number.
     * Implements the formula:
     *
     *
     * \[ (a + i b) id = (-bd) + i (ad) \]
     *
     *
     * This method can be used to compute the multiplication of this complex number \( z \)
     * by \( i \) using a factor with magnitude 1.0. This should be used in preference to
     * [multiply(Complex.I)][.multiply] with or without [negation][.negate]:
     *
     * \[ iz = (-b + i a) \\
     * -iz = (b - i a) \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * imaginary-only and complex numbers.
     *
     *
     * Note: This method should be preferred over using
     * [multiply(Complex.ofCartesian(0, factor))][.multiply]. Multiplication
     * can generate signed zeros if either `this` complex has zeros for the real
     * and/or imaginary component, or if the factor is zero. The summation of signed zeros
     * in [.multiply] may create zeros in the result that differ in sign
     * from the equivalent call to multiply by an imaginary-only number.
     *
     * @param  factor Value to be multiplied by this complex number.
     * @return `this * factor`.
     * @see .multiply
     */
    fun multiplyImaginary(factor: Double): Complex {
        return Complex(
            -imaginary * factor,
            real * factor
        )
    }

    /**
     * Returns a `Complex` whose value is `(this / divisor)`.
     * Implements the formula:
     *
     *
     * \[ \frac{a + i b}{c + i d} = \frac{(ac + bd) + i (bc - ad)}{c^2+d^2} \]
     *
     *
     * Re-calculates NaN result values to recover infinities as specified in C99 standard G.5.1.
     *
     * @param divisor Value by which this complex number is to be divided.
     * @return `this / divisor`.
     * @see [Complex Division](http://mathworld.wolfram.com/ComplexDivision.html)
     */
    @ExperimentalStdlibApi
    fun divide(divisor: Complex): Complex {
        return divide(
            real,
            imaginary,
            divisor.real,
            divisor.imaginary
        )
    }

    /**
     * Returns a `Complex` whose value is `(this / divisor)`,
     * with `divisor` interpreted as a real number.
     * Implements the formula:
     *
     *
     * \[ \frac{a + i b}{c} = \frac{a}{c} + i \frac{b}{c} \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * real-only and complex numbers.
     *
     *
     * Note: This method should be preferred over using
     * [divide(Complex.ofCartesian(divisor, 0))][.divide]. Division
     * can generate signed zeros if `this` complex has zeros for the real
     * and/or imaginary component, or the divisor is infinite. The summation of signed zeros
     * in [.divide] may create zeros in the result that differ in sign
     * from the equivalent call to divide by a real-only number.
     *
     * @param  divisor Value by which this complex number is to be divided.
     * @return `this / divisor`.
     * @see .divide
     */
    fun divide(divisor: Double): Complex {
        return Complex(
            real / divisor,
            imaginary / divisor
        )
    }

    /**
     * Returns a `Complex` whose value is `(this / divisor)`,
     * with `divisor` interpreted as an imaginary number.
     * Implements the formula:
     *
     *
     * \[ \frac{a + i b}{id} = \frac{b}{d} - i \frac{a}{d} \]
     *
     *
     * This method is included for compatibility with ISO C99 which defines arithmetic between
     * imaginary-only and complex numbers.
     *
     *
     * Note: This method should be preferred over using
     * [divide(Complex.ofCartesian(0, divisor))][.divide]. Division
     * can generate signed zeros if `this` complex has zeros for the real
     * and/or imaginary component, or the divisor is infinite. The summation of signed zeros
     * in [.divide] may create zeros in the result that differ in sign
     * from the equivalent call to divide by an imaginary-only number.
     *
     *
     * Warning: This method will generate a different result from
     * [divide(Complex.ofCartesian(0, divisor))][.divide] if the divisor is zero.
     * In this case the divide method using a zero-valued Complex will produce the same result
     * as dividing by a real-only zero. The output from dividing by imaginary zero will create
     * infinite and NaN values in the same component parts as the output from
     * `this.divide(Complex.ZERO).multiplyImaginary(1)`, however the sign
     * of some infinite values may be negated.
     *
     * @param  divisor Value by which this complex number is to be divided.
     * @return `this / divisor`.
     * @see .divide
     * @see .divide
     */
    fun divideImaginary(divisor: Double): Complex {
        return Complex(
            imaginary / divisor,
            -real / divisor
        )
    }

    /**
     * Returns the
     * [
 * exponential function](http://mathworld.wolfram.com/ExponentialFunction.html) of this complex number.
     *
     *
     * \[ \exp(z) = e^z \]
     *
     *
     * The exponential function of \( z \) is an entire function in the complex plane.
     * Special cases:
     *
     *
     *  * `z.conj().exp() == z.exp().conj()`.
     *  * If `z` is ±0 + i0, returns 1 + i0.
     *  * If `z` is x + i∞ for finite x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is x + iNaN for finite x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is +∞ + i0, returns +∞ + i0.
     *  * If `z` is −∞ + iy for finite y, returns +0 cis(y) (see [.ofCis]).
     *  * If `z` is +∞ + iy for finite nonzero y, returns +∞ cis(y).
     *  * If `z` is −∞ + i∞, returns ±0 ± i0 (where the signs of the real and imaginary parts of the result are unspecified).
     *  * If `z` is +∞ + i∞, returns ±∞ + iNaN (where the sign of the real part of the result is unspecified; "invalid" floating-point operation).
     *  * If `z` is −∞ + iNaN, returns ±0 ± i0 (where the signs of the real and imaginary parts of the result are unspecified).
     *  * If `z` is +∞ + iNaN, returns ±∞ + iNaN (where the sign of the real part of the result is unspecified).
     *  * If `z` is NaN + i0, returns NaN + i0.
     *  * If `z` is NaN + iy for all nonzero numbers y, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * Implements the formula:
     *
     *
     * \[ \exp(x + iy) = e^x (\cos(y) + i \sin(y)) \]
     *
     * @return The exponential of this complex number.
     * @see [exp](http://functions.wolfram.com/ElementaryFunctions/Exp/)
     */
    fun exp(): Complex {
        if (real.isInfinite()) {
            // Set the scale factor applied to cis(y)
            val zeroOrInf: Double
            zeroOrInf = if (real < 0) {
                if (!imaginary.isFinite()) {
                    // (−∞ + i∞) or (−∞ + iNaN) returns (±0 ± i0) (where the signs of the
                    // real and imaginary parts of the result are unspecified).
                    // Here we preserve the conjugate equality.
                    return Complex(
                        0.0,
                        0.0.withSign(imaginary)
                    )
                }
                // (−∞ + iy) returns +0 cis(y), for finite y
                0.0
            } else {
                // (+∞ + i0) returns +∞ + i0.
                if (imaginary == 0.0) {
                    return this
                }
                // (+∞ + i∞) or (+∞ + iNaN) returns (±∞ + iNaN) and raises the invalid
                // floating-point exception (where the sign of the real part of the
                // result is unspecified).
                if (!imaginary.isFinite()) {
                    return Complex(
                        real,
                        Double.NaN
                    )
                }
                // (+∞ + iy) returns (+∞ cis(y)), for finite nonzero y.
                real
            }
            return Complex(
                zeroOrInf * cos(imaginary),
                zeroOrInf * sin(imaginary)
            )
        } else if (real.isNaN()) {
            // (NaN + i0) returns (NaN + i0)
            // (NaN + iy) returns (NaN + iNaN) and optionally raises the invalid floating-point exception
            // (NaN + iNaN) returns (NaN + iNaN)
            return if (imaginary == 0.0) this else NAN
        } else if (!imaginary.isFinite()) {
            // (x + i∞) or (x + iNaN) returns (NaN + iNaN) and raises the invalid
            // floating-point exception, for finite x.
            return NAN
        }
        // real and imaginary are finite.
        // Compute e^a * (cos(b) + i sin(b)).

        // Special case:
        // (±0 + i0) returns (1 + i0)
        val exp: Double = exp(real)
        return if (imaginary == 0.0) {
            Complex(exp, imaginary)
        } else Complex(
            exp * cos(imaginary),
            exp * sin(imaginary)
        )
    }

    /**
     * Returns the
     * [natural logarithm](http://mathworld.wolfram.com/NaturalLogarithm.html) of this complex number.
     *
     *
     * The natural logarithm of \( z \) is unbounded along the real axis and
     * in the range \( [-\pi, \pi] \) along the imaginary axis. The imaginary part of the
     * natural logarithm has a branch cut along the negative real axis \( (-infty,0] \).
     * Special cases:
     *
     *
     *  * `z.conj().log() == z.log().conj()`.
     *  * If `z` is −0 + i0, returns −∞ + iπ ("divide-by-zero" floating-point operation).
     *  * If `z` is +0 + i0, returns −∞ + i0 ("divide-by-zero" floating-point operation).
     *  * If `z` is x + i∞ for finite x, returns +∞ + iπ/2.
     *  * If `z` is x + iNaN for finite x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is −∞ + iy for finite positive-signed y, returns +∞ + iπ.
     *  * If `z` is +∞ + iy for finite positive-signed y, returns +∞ + i0.
     *  * If `z` is −∞ + i∞, returns +∞ + i3π/4.
     *  * If `z` is +∞ + i∞, returns +∞ + iπ/4.
     *  * If `z` is ±∞ + iNaN, returns +∞ + iNaN.
     *  * If `z` is NaN + iy for finite y, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + i∞, returns +∞ + iNaN.
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * Implements the formula:
     *
     *
     * \[ \ln(z) = \ln |z| + i \arg(z) \]
     *
     *
     * where \( |z| \) is the absolute and \( \arg(z) \) is the argument.
     *
     *
     * The implementation is based on the method described in:
     * <blockquote>
     * T E Hull, Thomas F Fairgrieve and Ping Tak Peter Tang (1994)
     * Implementing complex elementary functions using exception handling.
     * ACM Transactions on Mathematical Software, Vol 20, No 2, pp 215-244.
    </blockquote> *
     *
     * @return The natural logarithm of this complex number.
     * @see ln
     * @see .abs
     * @see .arg
     * @see [ln](http://functions.wolfram.com/ElementaryFunctions/Log/)
     */
    fun log(): Complex {
        return log({ a: Double -> ln(a) },
            HALF,
            LN_2,
            { real: Double, imaginary: Double ->
                ofCartesian(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the base 10
     * [
 * common logarithm](http://mathworld.wolfram.com/CommonLogarithm.html) of this complex number.
     *
     *
     * The common logarithm of \( z \) is unbounded along the real axis and
     * in the range \( [-\pi, \pi] \) along the imaginary axis. The imaginary part of the
     * common logarithm has a branch cut along the negative real axis \( (-infty,0] \).
     * Special cases are as defined in the [natural logarithm][.log]:
     *
     *
     * Implements the formula:
     *
     *
     * \[ \log_{10}(z) = \log_{10} |z| + i \arg(z) \]
     *
     *
     * where \( |z| \) is the absolute and \( \arg(z) \) is the argument.
     *
     * @return The base 10 logarithm of this complex number.
     * @see log10
     * @see .abs
     * @see .arg
     */
    fun log10(): Complex {
        return log({ a: Double -> log10(a) },
            LOG_10E_O_2,
            LOG10_2,
            { real: Double, imaginary: Double ->
                ofCartesian(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the logarithm of this complex number using the provided function.
     * Implements the formula:
     *
     * <pre>
     * log(x + i y) = log(|x + i y|) + i arg(x + i y)</pre>
     *
     *
     * Warning: The argument `logOf2` must be equal to `log(2)` using the
     * provided log function otherwise scaling using powers of 2 in the case of overflow
     * will be incorrect. This is provided as an internal optimisation.
     *
     * @param log Log function.
     * @param logOfeOver2 The log function applied to e, then divided by 2.
     * @param logOf2 The log function applied to 2.
     * @param constructor Constructor for the returned complex.
     * @return The logarithm of this complex number.
     * @see .abs
     * @see .arg
     */
    @ExperimentalUnsignedTypes
    private fun log(
        log: (Double)->Double,
        logOfeOver2: Double,
        logOf2: Double,
        constructor: (Double, Double)-> Complex
    ): Complex {
        // Handle NaN
        if (real.isNaN() || imaginary.isNaN()) {
            // Return NaN unless infinite
            return if (isInfinite) {
                constructor.invoke(Double.POSITIVE_INFINITY, Double.NaN)
            } else NAN
            // No-use of the input constructor
        }

        // Returns the real part:
        // log(sqrt(x^2 + y^2))
        // log(x^2 + y^2) / 2

        // Compute with positive values
        var x: Double = abs(real)
        var y: Double = abs(imaginary)

        // Find the larger magnitude.
        if (x < y) {
            val tmp = x
            x = y
            y = tmp
        }
        if (x == 0.0) {
            // Handle zero: raises the ‘‘divide-by-zero’’ floating-point exception.
            return constructor.invoke(
                Double.NEGATIVE_INFINITY,
                if (negative(
                        real
                    )
                ) PI.withSign(imaginary) else imaginary
            )
        }
        var re: Double

        // This alters the implementation of Hull et al (1994) which used a standard
        // precision representation of |z|: sqrt(x*x + y*y).
        // This formula should use the same definition of the magnitude returned
        // by Complex.abs() which is a high precision computation with scaling.
        // The checks for overflow thus only require ensuring the output of |z|
        // will not overflow or underflow.
        if (x > HALF && x < ROOT2) {
            // x^2+y^2 close to 1. Use log1p(x^2+y^2 - 1) / 2.
            re = ln1p(
                x2y2m1(
                    x,
                    y
                )
            ) * logOfeOver2
        } else {
            // Check for over/underflow in |z|
            // When scaling:
            // log(a / b) = log(a) - log(b)
            // So initialise the result with the log of the scale factor.
            re = 0.0
            if (x > Double.MAX_VALUE / 2) {
                // Potential overflow.
                if (isPosInfinite(
                        x
                    )
                ) {
                    // Handle infinity
                    return constructor.invoke(x, arg())
                }
                // Scale down.
                x /= 2.0
                y /= 2.0
                // log(2)
                re = logOf2
            } else if (y < DoubleConsts.MIN_NORMAL) {
                // Potential underflow.
                if (y == 0.0) {
                    // Handle real only number
                    return constructor.invoke(log.invoke(x), arg())
                }
                // Scale up sub-normal numbers to make them normal by scaling by 2^54,
                // i.e. more than the mantissa digits.
                x *= 2.0.pow(54.0)
                y *= 2.0.pow(54.0)
                // log(2^-54) = -54 * log(2)
                re = -54 * logOf2
            }
            re += log.invoke(
                abs(
                    x,
                    y
                )
            )
        }

        // All ISO C99 edge cases for the imaginary are satisfied by the Math library.
        return constructor.invoke(re, arg())
    }

    /**
     * Returns the complex power of this complex number raised to the power of `x`.
     * Implements the formula:
     *
     *
     * \[ z^x = e^{x \ln(z)} \]
     *
     *
     * If this complex number is zero then this method returns zero if `x` is positive
     * in the real component and zero in the imaginary component;
     * otherwise it returns NaN + iNaN.
     *
     * @param  x The exponent to which this complex number is to be raised.
     * @return This complex number raised to the power of `x`.
     * @see .log
     * @see .multiply
     * @see .exp
     * @see [Complex exponentiation](http://mathworld.wolfram.com/ComplexExponentiation.html)
     *
     * @see [pow](http://functions.wolfram.com/ElementaryFunctions/Power/)
     */
    fun pow(x: Complex): Complex {
        return if (real == 0.0 && imaginary == 0.0) {
            // This value is zero. Test the other.
            if (x.real > 0 && x.imaginary == 0.0) {
                // 0 raised to positive number is 0
                ZERO
            } else NAN
            // 0 raised to anything else is NaN
        } else log().multiply(x).exp()
    }

    /**
     * Returns the complex power of this complex number raised to the power of `x`,
     * with `x` interpreted as a real number.
     * Implements the formula:
     *
     *
     * \[ z^x = e^{x \ln(z)} \]
     *
     *
     * If this complex number is zero then this method returns zero if `x` is positive;
     * otherwise it returns NaN + iNaN.
     *
     * @param  x The exponent to which this complex number is to be raised.
     * @return This complex number raised to the power of `x`.
     * @see .log
     * @see .multiply
     * @see .exp
     * @see .pow
     * @see [pow](http://functions.wolfram.com/ElementaryFunctions/Power/)
     */
    fun pow(x: Double): Complex {
        return if (real == 0.0 && imaginary == 0.0) {
            // This value is zero. Test the other.
            if (x > 0) {
                // 0 raised to positive number is 0
                ZERO
            } else NAN
            // 0 raised to anything else is NaN
        } else log().multiply(x).exp()
    }

    /**
     * Returns the
     * [
 * square root](http://mathworld.wolfram.com/SquareRoot.html) of this complex number.
     *
     *
     * \[ \sqrt{x + iy} = \frac{1}{2} \sqrt{2} \left( \sqrt{ \sqrt{x^2 + y^2} + x } + i\ \text{sgn}(y) \sqrt{ \sqrt{x^2 + y^2} - x } \right) \]
     *
     *
     * The square root of \( z \) is in the range \( [0, +\infty) \) along the real axis and
     * is unbounded along the imaginary axis. The imaginary part of the square root has a
     * branch cut along the negative real axis \( (-infty,0) \). Special cases:
     *
     *
     *  * `z.conj().sqrt() == z.sqrt().conj()`.
     *  * If `z` is ±0 + i0, returns +0 + i0.
     *  * If `z` is x + i∞ for all x (including NaN), returns +∞ + i∞.
     *  * If `z` is x + iNaN for finite x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is −∞ + iy for finite positive-signed y, returns +0 + i∞.
     *  * If `z` is +∞ + iy for finite positive-signed y, returns +∞ + i0.
     *  * If `z` is −∞ + iNaN, returns NaN ± i∞ (where the sign of the imaginary part of the result is unspecified).
     *  * If `z` is +∞ + iNaN, returns +∞ + iNaN.
     *  * If `z` is NaN + iy for finite y, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * Implements the following algorithm to compute \( \sqrt{x + iy} \):
     *
     *  1. Let \( t = \sqrt{2 (|x| + |x + iy|)} \)
     *  1. if \( x \geq 0 \) return \( \frac{t}{2} + i \frac{y}{t} \)
     *  1. else return \( \frac{|y|}{t} + i\ \text{sgn}(y) \frac{t}{2} \)
     *
     * where:
     *
     *  * \( |x| =\ \)[abs][Math.abs](x)
     *  * \( |x + y i| =\ \)[Complex.abs]
     *  * \( \text{sgn}(y) =\ \)[copySign][Math.copySign](1.0, y)
     *
     *
     *
     * The implementation is overflow and underflow safe based on the method described in:
     * <blockquote>
     * T E Hull, Thomas F Fairgrieve and Ping Tak Peter Tang (1994)
     * Implementing complex elementary functions using exception handling.
     * ACM Transactions on Mathematical Software, Vol 20, No 2, pp 215-244.
    </blockquote> *
     *
     * @return The square root of this complex number.
     * @see [sqrt](http://functions.wolfram.com/ElementaryFunctions/Sqrt/)
     */
    fun sqrt(): Complex {
        return sqrt(
            real,
            imaginary
        )
    }

    /**
     * Returns the
     * [
 * sine](http://mathworld.wolfram.com/Sine.html) of this complex number.
     *
     *
     * \[ \sin(z) = \frac{1}{2} i \left( e^{-iz} - e^{iz} \right) \]
     *
     *
     * This is an odd function: \( \sin(z) = -\sin(-z) \).
     * The sine is an entire function and requires no branch cuts.
     *
     *
     * This is implemented using real \( x \) and imaginary \( y \) parts:
     *
     *
     * \[ \sin(x + iy) = \sin(x)\cosh(y) + i \cos(x)\sinh(y) \]
     *
     *
     * As per the C99 standard this function is computed using the trigonomic identity:
     *
     *
     * \[ \sin(z) = -i \sinh(iz) \]
     *
     * @return The sine of this complex number.
     * @see [sin](http://functions.wolfram.com/ElementaryFunctions/Sin/)
     */
    fun sin(): Complex {
        // Define in terms of sinh
        // sin(z) = -i sinh(iz)
        // Multiply this number by I, compute sinh, then multiply by back
        return sinh(-imaginary,
            real,
            { real: Double, imaginary: Double ->
                multiplyNegativeI(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * cosine](http://mathworld.wolfram.com/Cosine.html) of this complex number.
     *
     *
     * \[ \cos(z) = \frac{1}{2} \left( e^{iz} + e^{-iz} \right) \]
     *
     *
     * This is an even function: \( \cos(z) = \cos(-z) \).
     * The cosine is an entire function and requires no branch cuts.
     *
     *
     * This is implemented using real \( x \) and imaginary \( y \) parts:
     *
     *
     * \[ \cos(x + iy) = \cos(x)\cosh(y) - i \sin(x)\sinh(y) \]
     *
     *
     * As per the C99 standard this function is computed using the trigonomic identity:
     *
     *
     * \[ cos(z) = cosh(iz) \]
     *
     * @return The cosine of this complex number.
     * @see [cos](http://functions.wolfram.com/ElementaryFunctions/Cos/)
     */
    fun cos(): Complex {
        // Define in terms of cosh
        // cos(z) = cosh(iz)
        // Multiply this number by I and compute cosh.
        return cosh(-imaginary,
            real,
            { real: Double, imaginary: Double ->
                ofCartesian(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * tangent](http://mathworld.wolfram.com/Tangent.html) of this complex number.
     *
     *
     * \[ \tan(z) = \frac{i(e^{-iz} - e^{iz})}{e^{-iz} + e^{iz}} \]
     *
     *
     * This is an odd function: \( \tan(z) = -\tan(-z) \).
     * The tangent is an entire function and requires no branch cuts.
     *
     *
     * This is implemented using real \( x \) and imaginary \( y \) parts:
     * \[ \tan(x + iy) = \frac{\sin(2x)}{\cos(2x)+\cosh(2y)} + i \frac{\sinh(2y)}{\cos(2x)+\cosh(2y)} \]
     *
     *
     * As per the C99 standard this function is computed using the trigonomic identity:
     * \[ \tan(z) = -i \tanh(iz) \]
     *
     * @return The tangent of this complex number.
     * @see [tan](http://functions.wolfram.com/ElementaryFunctions/Tan/)
     */
    fun tan(): Complex {
        // Define in terms of tanh
        // tan(z) = -i tanh(iz)
        // Multiply this number by I, compute tanh, then multiply by back
        return tanh(-imaginary,
            real,
            { real: Double, imaginary: Double ->
                multiplyNegativeI(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * inverse sine](http://mathworld.wolfram.com/InverseSine.html) of this complex number.
     *
     *
     * \[ \sin^{-1}(z) = - i \left(\ln{iz + \sqrt{1 - z^2}}\right) \]
     *
     *
     * The inverse sine of \( z \) is unbounded along the imaginary axis and
     * in the range \( [-\pi, \pi] \) along the real axis. Special cases are handled
     * as if the operation is implemented using \( \sin^{-1}(z) = -i \sinh^{-1}(iz) \).
     *
     *
     * The inverse sine is a multivalued function and requires a branch cut in
     * the complex plane; the cut is conventionally placed at the line segments
     * \( (\infty,-1) \) and \( (1,\infty) \) of the real axis.
     *
     *
     * This is implemented using real \( x \) and imaginary \( y \) parts:
     *
     *
     * \[ \sin^{-1}(z) = \sin^{-1}(B) + i\ \text{sgn}(y)\ln \left(A + \sqrt{A^2-1} \right) \\
     * A = \frac{1}{2} \left[ \sqrt{(x+1)^2+y^2} + \sqrt{(x-1)^2+y^2} \right] \\
     * B = \frac{1}{2} \left[ \sqrt{(x+1)^2+y^2} - \sqrt{(x-1)^2+y^2} \right] \]
     *
     *
     * where \( \text{sgn}(y) \) is the sign function implemented using
     * [copySign(1.0, y)][withSign].
     *
     *
     * The implementation is based on the method described in:
     * <blockquote>
     * T E Hull, Thomas F Fairgrieve and Ping Tak Peter Tang (1997)
     * Implementing the complex Arcsine and Arccosine Functions using Exception Handling.
     * ACM Transactions on Mathematical Software, Vol 23, No 3, pp 299-335.
    </blockquote> *
     *
     *
     * The code has been adapted from the [Boost](https://www.boost.org/)
     * `c++` implementation `<boost/math/complex/asin.hpp>`.
     *
     * @return The inverse sine of this complex number.
     * @see [asin](http://functions.wolfram.com/ElementaryFunctions/ArcSin/)
     */
    fun asin(): Complex {
        return asin(
            real, imaginary,
            { real: Double, imaginary: Double ->
                ofCartesian(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * inverse cosine](http://mathworld.wolfram.com/InverseCosine.html) of this complex number.
     *
     *
     * \[ \cos^{-1}(z) = \frac{\pi}{2} + i \left(\ln{iz + \sqrt{1 - z^2}}\right) \]
     *
     *
     * The inverse cosine of \( z \) is in the range \( [0, \pi) \) along the real axis and
     * unbounded along the imaginary axis. Special cases:
     *
     *
     *  * `z.conj().acos() == z.acos().conj()`.
     *  * If `z` is ±0 + i0, returns π/2 − i0.
     *  * If `z` is ±0 + iNaN, returns π/2 + iNaN.
     *  * If `z` is x + i∞ for finite x, returns π/2 − i∞.
     *  * If `z` is x + iNaN, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is −∞ + iy for positive-signed finite y, returns π − i∞.
     *  * If `z` is +∞ + iy for positive-signed finite y, returns +0 − i∞.
     *  * If `z` is −∞ + i∞, returns 3π/4 − i∞.
     *  * If `z` is +∞ + i∞, returns π/4 − i∞.
     *  * If `z` is ±∞ + iNaN, returns NaN ± i∞ where the sign of the imaginary part of the result is unspecified.
     *  * If `z` is NaN + iy for finite y, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + i∞, returns NaN − i∞.
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * The inverse cosine is a multivalued function and requires a branch cut in
     * the complex plane; the cut is conventionally placed at the line segments
     * \( (-\infty,-1) \) and \( (1,\infty) \) of the real axis.
     *
     *
     * This function is implemented using real \( x \) and imaginary \( y \) parts:
     *
     *
     * \[ \cos^{-1}(z) = \cos^{-1}(B) - i\ \text{sgn}(y) \ln\left(A + \sqrt{A^2-1}\right) \\
     * A = \frac{1}{2} \left[ \sqrt{(x+1)^2+y^2} + \sqrt{(x-1)^2+y^2} \right] \\
     * B = \frac{1}{2} \left[ \sqrt{(x+1)^2+y^2} - \sqrt{(x-1)^2+y^2} \right] \]
     *
     *
     * where \( \text{sgn}(y) \) is the sign function implemented using
     * [copySign(1.0, y)][withSign].
     *
     *
     * The implementation is based on the method described in:
     * <blockquote>
     * T E Hull, Thomas F Fairgrieve and Ping Tak Peter Tang (1997)
     * Implementing the complex Arcsine and Arccosine Functions using Exception Handling.
     * ACM Transactions on Mathematical Software, Vol 23, No 3, pp 299-335.
    </blockquote> *
     *
     *
     * The code has been adapted from the [Boost](https://www.boost.org/)
     * `c++` implementation `<boost/math/complex/acos.hpp>`.
     *
     * @return The inverse cosine of this complex number.
     * @see [acos](http://functions.wolfram.com/ElementaryFunctions/ArcCos/)
     */
    fun acos(): Complex {
        return acos(
            real, imaginary,
            { real: Double, imaginary: Double ->
                ofCartesian(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * inverse tangent](http://mathworld.wolfram.com/InverseTangent.html) of this complex number.
     *
     *
     * \[ \tan^{-1}(z) = \frac{i}{2} \ln \left( \frac{i + z}{i - z} \right) \]
     *
     *
     * The inverse hyperbolic tangent of \( z \) is unbounded along the imaginary axis and
     * in the range \( [-\pi/2, \pi/2] \) along the real axis.
     *
     *
     * The inverse tangent is a multivalued function and requires a branch cut in
     * the complex plane; the cut is conventionally placed at the line segments
     * \( (i \infty,-i] \) and \( [i,i \infty) \) of the imaginary axis.
     *
     *
     * As per the C99 standard this function is computed using the trigonomic identity:
     * \[ \tan^{-1}(z) = -i \tanh^{-1}(iz) \]
     *
     * @return The inverse tangent of this complex number.
     * @see [atan](http://functions.wolfram.com/ElementaryFunctions/ArcTan/)
     */
    fun atan(): Complex {
        // Define in terms of atanh
        // atan(z) = -i atanh(iz)
        // Multiply this number by I, compute atanh, then multiply by back
        return atanh(-imaginary,
            real,
            { real: Double, imaginary: Double ->
                multiplyNegativeI(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * hyperbolic sine](http://mathworld.wolfram.com/HyperbolicSine.html) of this complex number.
     *
     *
     * \[ \sinh(z) = \frac{1}{2} \left( e^{z} - e^{-z} \right) \]
     *
     *
     * The hyperbolic sine of \( z \) is an entire function in the complex plane
     * and is periodic with respect to the imaginary component with period \( 2\pi i \).
     * Special cases:
     *
     *
     *  * `z.conj().sinh() == z.sinh().conj()`.
     *  * This is an odd function: \( \sinh(z) = -\sinh(-z) \).
     *  * If `z` is +0 + i0, returns +0 + i0.
     *  * If `z` is +0 + i∞, returns ±0 + iNaN (where the sign of the real part of the result is unspecified; "invalid" floating-point operation).
     *  * If `z` is +0 + iNaN, returns ±0 + iNaN (where the sign of the real part of the result is unspecified).
     *  * If `z` is x + i∞ for positive finite x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is x + iNaN for finite nonzero x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is +∞ + i0, returns +∞ + i0.
     *  * If `z` is +∞ + iy for positive finite y, returns +∞ cis(y) (see [.ofCis].
     *  * If `z` is +∞ + i∞, returns ±∞ + iNaN (where the sign of the real part of the result is unspecified; "invalid" floating-point operation).
     *  * If `z` is +∞ + iNaN, returns ±∞ + iNaN (where the sign of the real part of the result is unspecified).
     *  * If `z` is NaN + i0, returns NaN + i0.
     *  * If `z` is NaN + iy for all nonzero numbers y, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * This is implemented using real \( x \) and imaginary \( y \) parts:
     *
     *
     * \[ \sinh(x + iy) = \sinh(x)\cos(y) + i \cosh(x)\sin(y) \]
     *
     * @return The hyperbolic sine of this complex number.
     * @see [sinh](http://functions.wolfram.com/ElementaryFunctions/Sinh/)
     */
    fun sinh(): Complex {
        return sinh(
            real, imaginary,
            { real: Double, imaginary: Double ->
                ofCartesian(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * hyperbolic cosine](http://mathworld.wolfram.com/HyperbolicCosine.html) of this complex number.
     *
     *
     * \[ \cosh(z) = \frac{1}{2} \left( e^{z} + e^{-z} \right) \]
     *
     *
     * The hyperbolic cosine of \( z \) is an entire function in the complex plane
     * and is periodic with respect to the imaginary component with period \( 2\pi i \).
     * Special cases:
     *
     *
     *  * `z.conj().cosh() == z.cosh().conj()`.
     *  * This is an even function: \( \cosh(z) = \cosh(-z) \).
     *  * If `z` is +0 + i0, returns 1 + i0.
     *  * If `z` is +0 + i∞, returns NaN ± i0 (where the sign of the imaginary part of the result is unspecified; "invalid" floating-point operation).
     *  * If `z` is +0 + iNaN, returns NaN ± i0 (where the sign of the imaginary part of the result is unspecified).
     *  * If `z` is x + i∞ for finite nonzero x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is x + iNaN for finite nonzero x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is +∞ + i0, returns +∞ + i0.
     *  * If `z` is +∞ + iy for finite nonzero y, returns +∞ cis(y) (see [.ofCis]).
     *  * If `z` is +∞ + i∞, returns ±∞ + iNaN (where the sign of the real part of the result is unspecified).
     *  * If `z` is +∞ + iNaN, returns +∞ + iNaN.
     *  * If `z` is NaN + i0, returns NaN ± i0 (where the sign of the imaginary part of the result is unspecified).
     *  * If `z` is NaN + iy for all nonzero numbers y, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * This is implemented using real \( x \) and imaginary \( y \) parts:
     *
     *
     * \[ \cosh(x + iy) = \cosh(x)\cos(y) + i \sinh(x)\sin(y) \]
     *
     * @return The hyperbolic cosine of this complex number.
     * @see [cosh](http://functions.wolfram.com/ElementaryFunctions/Cosh/)
     */
    fun cosh(): Complex {
        return cosh(
            real, imaginary,
            { real: Double, imaginary: Double ->
                ofCartesian(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * hyperbolic tangent](http://mathworld.wolfram.com/HyperbolicTangent.html) of this complex number.
     *
     *
     * \[ \tanh(z) = \frac{e^z - e^{-z}}{e^z + e^{-z}} \]
     *
     *
     * The hyperbolic tangent of \( z \) is an entire function in the complex plane
     * and is periodic with respect to the imaginary component with period \( \pi i \)
     * and has poles of the first order along the imaginary line, at coordinates
     * \( (0, \pi(\frac{1}{2} + n)) \).
     * Note that the `double` floating-point representation is unable to exactly represent
     * \( \pi/2 \) and there is no value for which a pole error occurs. Special cases:
     *
     *
     *  * `z.conj().tanh() == z.tanh().conj()`.
     *  * This is an odd function: \( \tanh(z) = -\tanh(-z) \).
     *  * If `z` is +0 + i0, returns +0 + i0.
     *  * If `z` is 0 + i∞, returns 0 + iNaN.
     *  * If `z` is x + i∞ for finite non-zero x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is 0 + iNaN, returns 0 + iNAN.
     *  * If `z` is x + iNaN for finite non-zero x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is +∞ + iy for positive-signed finite y, returns 1 + i0 sin(2y).
     *  * If `z` is +∞ + i∞, returns 1 ± i0 (where the sign of the imaginary part of the result is unspecified).
     *  * If `z` is +∞ + iNaN, returns 1 ± i0 (where the sign of the imaginary part of the result is unspecified).
     *  * If `z` is NaN + i0, returns NaN + i0.
     *  * If `z` is NaN + iy for all nonzero numbers y, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * Special cases include the technical corrigendum
     * [
 * DR 471: Complex math functions cacosh and ctanh](http://www.open-std.org/jtc1/sc22/wg14/www/docs/n1892.htm#dr_471).
     *
     *
     * This is defined using real \( x \) and imaginary \( y \) parts:
     *
     *
     * \[ \tan(x + iy) = \frac{\sinh(2x)}{\cosh(2x)+\cos(2y)} + i \frac{\sin(2y)}{\cosh(2x)+\cos(2y)} \]
     *
     *
     * The implementation uses double-angle identities to avoid overflow of `2x`
     * and `2y`.
     *
     * @return The hyperbolic tangent of this complex number.
     * @see [tanh](http://functions.wolfram.com/ElementaryFunctions/Tanh/)
     */
    fun tanh(): Complex {
        return tanh(
            real, imaginary,
            { real: Double, imaginary: Double ->
                ofCartesian(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * inverse hyperbolic sine](http://mathworld.wolfram.com/InverseHyperbolicSine.html) of this complex number.
     *
     *
     * \[ \sinh^{-1}(z) = \ln \left(z + \sqrt{1 + z^2} \right) \]
     *
     *
     * The inverse hyperbolic sine of \( z \) is unbounded along the real axis and
     * in the range \( [-\pi, \pi] \) along the imaginary axis. Special cases:
     *
     *
     *  * `z.conj().asinh() == z.asinh().conj()`.
     *  * This is an odd function: \( \sinh^{-1}(z) = -\sinh^{-1}(-z) \).
     *  * If `z` is +0 + i0, returns 0 + i0.
     *  * If `z` is x + i∞ for positive-signed finite x, returns +∞ + iπ/2.
     *  * If `z` is x + iNaN for finite x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is +∞ + iy for positive-signed finite y, returns +∞ + i0.
     *  * If `z` is +∞ + i∞, returns +∞ + iπ/4.
     *  * If `z` is +∞ + iNaN, returns +∞ + iNaN.
     *  * If `z` is NaN + i0, returns NaN + i0.
     *  * If `z` is NaN + iy for finite nonzero y, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + i∞, returns ±∞ + iNaN (where the sign of the real part of the result is unspecified).
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * The inverse hyperbolic sine is a multivalued function and requires a branch cut in
     * the complex plane; the cut is conventionally placed at the line segments
     * \( (-i \infty,-i) \) and \( (i,i \infty) \) of the imaginary axis.
     *
     *
     * This function is computed using the trigonomic identity:
     *
     *
     * \[ \sinh^{-1}(z) = -i \sin^{-1}(iz) \]
     *
     * @return The inverse hyperbolic sine of this complex number.
     * @see [asinh](http://functions.wolfram.com/ElementaryFunctions/ArcSinh/)
     */
    fun asinh(): Complex {
        // Define in terms of asin
        // asinh(z) = -i asin(iz)
        // Note: This is the opposite to the identity defined in the C99 standard:
        // asin(z) = -i asinh(iz)
        // Multiply this number by I, compute asin, then multiply by back
        return asin(-imaginary,
            real,
            { real: Double, imaginary: Double ->
                multiplyNegativeI(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the
     * [
 * inverse hyperbolic cosine](http://mathworld.wolfram.com/InverseHyperbolicCosine.html) of this complex number.
     *
     *
     * \[ \cosh^{-1}(z) = \ln \left(z + \sqrt{z + 1} \sqrt{z - 1} \right) \]
     *
     *
     * The inverse hyperbolic cosine of \( z \) is in the range \( [0, \infty) \) along the
     * real axis and in the range \( [-\pi, \pi] \) along the imaginary axis. Special cases:
     *
     *
     *  * `z.conj().acosh() == z.acosh().conj()`.
     *  * If `z` is ±0 + i0, returns +0 + iπ/2.
     *  * If `z` is x + i∞ for finite x, returns +∞ + iπ/2.
     *  * If `z` is 0 + iNaN, returns NaN + iπ/2 <sup>[1]</sup>.
     *  * If `z` is x + iNaN for finite non-zero x, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is −∞ + iy for positive-signed finite y, returns +∞ + iπ.
     *  * If `z` is +∞ + iy for positive-signed finite y, returns +∞ + i0.
     *  * If `z` is −∞ + i∞, returns +∞ + i3π/4.
     *  * If `z` is +∞ + i∞, returns +∞ + iπ/4.
     *  * If `z` is ±∞ + iNaN, returns +∞ + iNaN.
     *  * If `z` is NaN + iy for finite y, returns NaN + iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + i∞, returns +∞ + iNaN.
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * Special cases include the technical corrigendum
     * [
 * DR 471: Complex math functions cacosh and ctanh](http://www.open-std.org/jtc1/sc22/wg14/www/docs/n1892.htm#dr_471).
     *
     *
     * The inverse hyperbolic cosine is a multivalued function and requires a branch cut in
     * the complex plane; the cut is conventionally placed at the line segment
     * \( (-\infty,-1) \) of the real axis.
     *
     *
     * This function is computed using the trigonomic identity:
     *
     *
     * \[ \cosh^{-1}(z) = \pm i \cos^{-1}(z) \]
     *
     *
     * The sign of the multiplier is chosen to give `z.acosh().real() >= 0`
     * and compatibility with the C99 standard.
     *
     * @return The inverse hyperbolic cosine of this complex number.
     * @see [acosh](http://functions.wolfram.com/ElementaryFunctions/ArcCosh/)
     */
    fun acosh(): Complex {
        // Define in terms of acos
        // acosh(z) = +-i acos(z)
        // Note the special case:
        // acos(+-0 + iNaN) = π/2 + iNaN
        // acosh(0 + iNaN) = NaN + iπ/2
        // will not appropriately multiply by I to maintain positive imaginary if
        // acos() imaginary computes as NaN. So do this explicitly.
        return if (imaginary.isNaN() && real == 0.0) {
            Complex(
                Double.NaN,
                PI_OVER_2
            )
        } else acos(
            real,
            imaginary,
            { re: Double, im: Double ->  // Set the sign appropriately for real >= 0
                if (negative(
                        im
                    )
                ) // Multiply by I
                    Complex(
                        -im,
                        re
                    ) else  // Multiply by -I
                    Complex(im, -re)
            }
        )
    }

    /**
     * Returns the
     * [
 * inverse hyperbolic tangent](http://mathworld.wolfram.com/InverseHyperbolicTangent.html) of this complex number.
     *
     *
     * \[ \tanh^{-1}(z) = \frac{1}{2} \ln \left( \frac{1 + z}{1 - z} \right) \]
     *
     *
     * The inverse hyperbolic tangent of \( z \) is unbounded along the real axis and
     * in the range \( [-\pi/2, \pi/2] \) along the imaginary axis. Special cases:
     *
     *
     *  * `z.conj().atanh() == z.atanh().conj()`.
     *  * This is an odd function: \( \tanh^{-1}(z) = -\tanh^{-1}(-z) \).
     *  * If `z` is +0 + i0, returns +0 + i0.
     *  * If `z` is +0 + iNaN, returns +0 + iNaN.
     *  * If `z` is +1 + i0, returns +∞ + i0 ("divide-by-zero" floating-point operation).
     *  * If `z` is x + i∞ for finite positive-signed x, returns +0 + iπ/2.
     *  * If `z` is x+iNaN for nonzero finite x, returns NaN+iNaN ("invalid" floating-point operation).
     *  * If `z` is +∞ + iy for finite positive-signed y, returns +0 + iπ/2.
     *  * If `z` is +∞ + i∞, returns +0 + iπ/2.
     *  * If `z` is +∞ + iNaN, returns +0 + iNaN.
     *  * If `z` is NaN+iy for finite y, returns NaN+iNaN ("invalid" floating-point operation).
     *  * If `z` is NaN + i∞, returns ±0 + iπ/2 (where the sign of the real part of the result is unspecified).
     *  * If `z` is NaN + iNaN, returns NaN + iNaN.
     *
     *
     *
     * The inverse hyperbolic tangent is a multivalued function and requires a branch cut in
     * the complex plane; the cut is conventionally placed at the line segments
     * \( (\infty,-1] \) and \( [1,\infty) \) of the real axis.
     *
     *
     * This is implemented using real \( x \) and imaginary \( y \) parts:
     *
     *
     * \[ \tanh^{-1}(z) = \frac{1}{4} \ln \left(1 + \frac{4x}{(1-x)^2+y^2} \right) + \\
     * i \frac{1}{2} \left( \tan^{-1} \left(\frac{2y}{1-x^2-y^2} \right) + \frac{\pi}{2} \left(\text{sgn}(x^2+y^2-1)+1 \right) \text{sgn}(y) \right) \]
     *
     *
     * The imaginary part is computed using [atan2] to ensure the
     * correct quadrant is returned from \( \tan^{-1} \left(\frac{2y}{1-x^2-y^2} \right) \).
     *
     *
     * The code has been adapted from the [Boost](https://www.boost.org/)
     * `c++` implementation `<boost/math/complex/atanh.hpp>`.
     *
     * @return The inverse hyperbolic tangent of this complex number.
     * @see [atanh](http://functions.wolfram.com/ElementaryFunctions/ArcTanh/)
     */
    fun atanh(): Complex {
        return atanh(
            real, imaginary,
            { real: Double, imaginary: Double ->
                ofCartesian(
                    real,
                    imaginary
                )
            }
        )
    }

    /**
     * Returns the n-th roots of this complex number.
     * The nth roots are defined by the formula:
     *
     *
     * \[ z_k = |z|^{\frac{1}{n}} \left( \cos \left(\phi + \frac{2\pi k}{n} \right) + i \sin \left(\phi + \frac{2\pi k}{n} \right) \right) \]
     *
     *
     * for \( k=0, 1, \ldots, n-1 \), where \( |z| \) and \( \phi \)
     * are respectively the [modulus][.abs] and
     * [argument][.arg] of this complex number.
     *
     *
     * If one or both parts of this complex number is NaN, a list with all
     * all elements set to `NaN + i NaN` is returned.
     *
     * @param n Degree of root.
     * @return A list of all `n`-th roots of this complex number.
     * @throws IllegalArgumentException if `n` is zero.
     * @see [Root](http://functions.wolfram.com/ElementaryFunctions/Root/)
     */
    fun nthRoot(n: Int): List<Complex> {
        if (n == 0) {
            throw IllegalArgumentException("cannot compute zeroth root")
        }
        val result: MutableList<Complex> = arrayListOf()

        // nth root of abs -- faster / more accurate to use a solver here?
        val nthRootOfAbs: Double = abs().pow(1.0 / n)

        // Compute nth roots of complex number with k = 0, 1, ... n-1
        val nthPhi = arg() / n
        val slice: Double = 2 * PI / n
        var innerPart = nthPhi
        for (k in 0 until abs(n)) {
            // inner part
            val realPart: Double = nthRootOfAbs * cos(innerPart)
            val imaginaryPart: Double = nthRootOfAbs * sin(innerPart)
            result.add(
                ofCartesian(
                    realPart,
                    imaginaryPart
                )
            )
            innerPart += slice
        }
        return result
    }

    /**
     * Test for equality with another object. If the other object is a `Complex` then a
     * comparison is made of the real and imaginary parts; otherwise `false` is returned.
     *
     *
     * If both the real and imaginary parts of two complex numbers
     * are exactly the same the two `Complex` objects are considered to be equal.
     * For this purpose, two `double` values are considered to be
     * the same if and only if the method [#doubleToLongBits(double)][Double]
     * returns the identical `long` value when applied to each.
     *
     *
     * Note that in most cases, for two instances of class
     * `Complex`, `c1` and `c2`, the
     * value of `c1.equals(c2)` is `true` if and only if
     *
     * <pre>
     * `c1.getReal() == c2.getReal() && c1.getImaginary() == c2.getImaginary()`</pre>
     *
     *
     * also has the value `true`. However, there are exceptions:
     *
     *
     *  *
     * Instances that contain `NaN` values in the same part
     * are considered to be equal for that part, even though `Double.NaN == Double.NaN`
     * has the value `false`.
     *
     *  *
     * Instances that share a `NaN` value in one part
     * but have different values in the other part are *not* considered equal.
     *
     *  *
     * Instances that contain different representations of zero in the same part
     * are *not* considered to be equal for that part, even though `-0.0 == 0.0`
     * has the value `true`.
     *
     *
     *
     *
     * The behavior is the same as if the components of the two complex numbers were passed
     * to [Arrays.equals(double[], double[])][Array.equals]:
     *
     * <pre>
     * Arrays.equals(new double[]{c1.getReal(), c1.getImaginary()},
     * new double[]{c2.getReal(), c2.getImaginary()}); </pre>
     *
     * @param other Object to test for equality with this instance.
     * @return `true` if the objects are equal, `false` if object
     * is `null`, not an instance of `Complex`, or not equal to
     * this instance.
     * @see Double.toBits
     * @see Array.equals
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is Complex) {
            val c = other
            return equals(
                real,
                c.real
            ) &&
                    equals(
                        imaginary,
                        c.imaginary
                    )
        }
        return false
    }

    /**
     * Gets a hash code for the complex number.
     *
     *
     * The behavior is the same as if the components of the complex number were passed
     * to [Arrays.hashCode(double[])][Array.hashCode]:
     *
     * <pre>
     * `Arrays.hashCode(new double[] {getReal(), getImaginary()})`</pre>
     *
     * @return A hash code value for this object.
     * @see Array.hashCode
     */
    override fun hashCode(): Int {
        return 31 * (31 + real.hashCode()) + imaginary.hashCode()
    }

    /**
     * Returns a string representation of the complex number.
     *
     *
     * The string will represent the numeric values of the real and imaginary parts.
     * The values are split by a separator and surrounded by parentheses.
     * The string can be [parsed][.parse] to obtain an instance with the same value.
     *
     *
     * The format for complex number \( x + i y \) is `"(x,y)"`, with \( x \) and
     * \( y \) converted as if using [Double.toString].
     *
     * @return A string representation of the complex number.
     * @see .parse
     * @see Double.toString
     */
    override fun toString(): String {
        return StringBuilder(TO_STRING_SIZE)
            .append(FORMAT_START)
            .append(real).append(FORMAT_SEP)
            .append(imaginary)
            .append(FORMAT_END)
            .toString()
    }

    // Operators
    operator fun plus(addend: Complex) = this.add(addend)
    operator fun minus(subtrahend: Complex) = this.subtract(subtrahend)
    operator fun times(factor: Complex) = this.multiply(factor)
    operator fun times(factor: Double) = this.multiply(factor)
    @ExperimentalStdlibApi
    operator fun div(divisor: Complex) = this.divide(divisor)
    operator fun div(divisor: Double) = this.divide(divisor)

    companion object {
        /**
         * A complex number representing \( i \), the square root of \( -1 \).
         *
         *
         * \( (0 + i 1) \).
         */
        val I = Complex(0.0, 1.0)

        /**
         * A complex number representing one.
         *
         *
         * \( (1 + i 0) \).
         */
        val ONE = Complex(1.0, 0.0)

        /**
         * A complex number representing zero.
         *
         *
         * \( (0 + i 0) \).
         */
        val ZERO = Complex(0.0, 0.0)

        /** A complex number representing `NaN + i NaN`.  */
        private val NAN =
            Complex(Double.NaN, Double.NaN)

        /** /2.  */
        private val PI_OVER_2: Double = 0.5 * PI

        /** /4.  */
        private val PI_OVER_4: Double = 0.25 * PI

        /** Natural logarithm of 2 (ln(2)).  */
        private val LN_2: Double = ln(2.0)

        /** Base 10 logarithm of 10 divided by 2 (log10(e)/2).  */
        private val LOG_10E_O_2: Double = log10(E) / 2

        /** Base 10 logarithm of 2 (log10(2)).  */
        private val LOG10_2: Double = log10(2.0)

        /** `1/2`.  */
        private const val HALF = 0.5

        /** `sqrt(2)`.  */
        private const val ROOT2 = 1.4142135623730951

        /** `1.0 / sqrt(2)`.
         * This is pre-computed to the closest double from the exact result.
         * It is 1 ULP different from 1.0 / Math.sqrt(2) but equal to Math.sqrt(2) / 2.
         */
        private const val ONE_OVER_ROOT2 = 0.7071067811865476

        /** The bit representation of `-0.0`.  */
        private val NEGATIVE_ZERO_LONG_BITS: Long = (-0.0).toBits()

        /** Exponent offset in IEEE754 representation.  */
        private const val EXPONENT_OFFSET = 1023

        /**
         * Largest double-precision floating-point number such that
         * `1 + EPSILON` is numerically equal to 1. This value is an upper
         * bound on the relative error due to rounding real numbers to double
         * precision floating-point numbers.
         *
         *
         * In IEEE 754 arithmetic, this is 2<sup>-53</sup>.
         * Copied from o.a.c.numbers.Precision.
         *
         * @see [Machine epsilon](http://en.wikipedia.org/wiki/Machine_epsilon)
         */
        private val EPSILON: Double =
            Double.fromBits(EXPONENT_OFFSET - 53L shl 52)

        /** Mask to remove the sign bit from a long.  */
        private const val UNSIGN_MASK = 0x7fffffffffffffffL

        /** Mask to extract the 52-bit mantissa from a long representation of a double.  */
        private const val MANTISSA_MASK = 0x000fffffffffffffL

        /** The multiplier used to split the double value into hi and low parts. This must be odd
         * and a value of 2^s + 1 in the range `p/2 <= s <= p-1` where p is the number of
         * bits of precision of the floating point number. Here `s = 27`. */
        private const val MULTIPLIER = 1.34217729E8

        /**
         * Crossover point to switch computation for asin/acos factor A.
         * This has been updated from the 1.5 value used by Hull et al to 10
         * as used in boost::math::complex.
         * @see [Boost ticket 7290](https://svn.boost.org/trac/boost/ticket/7290)
         */
        private const val A_CROSSOVER = 10.0

        /** Crossover point to switch computation for asin/acos factor B.  */
        private const val B_CROSSOVER = 0.6471

        /**
         * The safe maximum double value `x` to avoid loss of precision in asin/acos.
         * Equal to sqrt(M) / 8 in Hull, et al (1997) with M the largest normalised floating-point value.
         */
        private val SAFE_MAX: Double = sqrt(Double.MAX_VALUE) / 8

        /**
         * The safe minimum double value `x` to avoid loss of precision/underflow in asin/acos.
         * Equal to sqrt(u) * 4 in Hull, et al (1997) with u the smallest normalised floating-point value.
         */
        private val SAFE_MIN: Double = sqrt(DoubleConsts.MIN_NORMAL) * 4

        /**
         * The safe maximum double value `x` to avoid loss of precision in atanh.
         * Equal to sqrt(M) / 2 with M the largest normalised floating-point value.
         */
        private val SAFE_UPPER: Double = sqrt(Double.MAX_VALUE) / 2

        /**
         * The safe minimum double value `x` to avoid loss of precision/underflow in atanh.
         * Equal to sqrt(u) * 2 with u the smallest normalised floating-point value.
         */
        private val SAFE_LOWER: Double = sqrt(DoubleConsts.MIN_NORMAL) * 2

        /** The safe maximum double value `x` to avoid overflow in sqrt.  */
        private val SQRT_SAFE_UPPER = Double.MAX_VALUE / 8

        /**
         * A safe maximum double value `m` where `e^m` is not infinite.
         * This can be used when functions require approximations of sinh(x) or cosh(x)
         * when x is large using exp(x):
         * <pre>
         * sinh(x) = (e^x - e^-x) / 2 = sign(x) * e^|x| / 2
         * cosh(x) = (e^x + e^-x) / 2 = e^|x| / 2 </pre>
         *
         *
         * This value can be used to approximate e^x using a product:
         *
         * <pre>
         * e^x = product_n (e^m) * e^(x-nm)
         * n = (int) x/m
         * e.g. e^2000 = e^m * e^m * e^(2000 - 2m) </pre>
         *
         *
         * The value should be below ln(max_value) ~ 709.783.
         * The value m is set to an integer for less error when subtracting m and chosen as
         * even (m=708) as it is used as a threshold in tanh with m/2.
         *
         *
         * The value is used to compute e^x multiplied by a small number avoiding
         * overflow (sinh/cosh) or a small number divided by e^x without underflow due to
         * infinite e^x (tanh). The following conditions are used:
         * <pre>
         * 0.5 * e^m * Double.MIN_VALUE * e^m * e^m = Infinity
         * 2.0 / e^m / e^m = 0.0 </pre>
         */
        private const val SAFE_EXP = 708.0

        /**
         * The value of Math.exp(SAFE_EXP): e^708.
         * To be used in overflow/underflow safe products of e^m to approximate e^x where x > m.
         */
        private val EXP_M: Double = exp(SAFE_EXP)

        /** 54 shifted 20-bits to align with the exponent of the upper 32-bits of a double.  */
        private const val EXP_54 = 0x3600000

        /** Represents an exponent of 500 in unbiased form shifted 20-bits to align with the upper 32-bits of a double.  */
        private const val EXP_500 = 0x5f300000

        /** Represents an exponent of 1024 in unbiased form (infinite or nan)
         * shifted 20-bits to align with the upper 32-bits of a double.  */
        private const val EXP_1024 = 0x7ff00000

        /** Represents an exponent of -500 in unbiased form shifted 20-bits to align with the upper 32-bits of a double.  */
        private const val EXP_NEG_500 = 0x20b00000

        /** 2^600.  */
        private val TWO_POW_600: Double = 2.0.pow(600)

        /** 2^-600.  */
        private val TWO_POW_NEG_600: Double = 2.0.pow(-600)

        /** Serializable version identifier.  */
        private const val serialVersionUID = 20180201L

        /**
         * The size of the buffer for [.toString].
         *
         *
         * The longest double will require a sign, a maximum of 17 digits, the decimal place
         * and the exponent, e.g. for max value this is 24 chars: -1.7976931348623157e+308.
         * Set the buffer size to twice this and round up to a power of 2 thus
         * allowing for formatting characters. The size is 64.
         */
        private const val TO_STRING_SIZE = 64

        /** The minimum number of characters in the format. This is 5, e.g. `"(0,0)"`.  */
        private const val FORMAT_MIN_LEN = 5

        /** [String representation][.toString].  */
        private const val FORMAT_START = '('

        /** [String representation][.toString].  */
        private const val FORMAT_END = ')'

        /** [String representation][.toString].  */
        private const val FORMAT_SEP = ','

        /** The minimum number of characters before the separator. This is 2, e.g. `"(0"`.  */
        private const val BEFORE_SEP = 2

        /**
         * Create a complex number given the real and imaginary parts.
         *
         * @param real Real part.
         * @param imaginary Imaginary part.
         * @return `Complex` number.
         */
        fun ofCartesian(real: Double, imaginary: Double): Complex {
            return Complex(real, imaginary)
        }

        /**
         * Creates a complex number from its polar representation using modulus `rho` (\( \rho \))
         * and phase angle `theta` (\( \theta \)).
         *
         * \[ x = \rho \cos(\theta) \\
         * y = \rho \sin(\theta) \]
         *
         *
         * Requires that `rho` is non-negative and non-NaN and `theta` is finite;
         * otherwise returns a complex with NaN real and imaginary parts. A `rho` value of
         * `-0.0` is considered negative and an invalid modulus.
         *
         *
         * A non-NaN complex number constructed using this method will satisfy the following
         * to within floating-point error when `theta` is in the range
         * \( -\pi\ \lt \theta \leq \pi \):
         *
         * <pre>
         * Complex.ofPolar(rho, theta).abs() == rho
         * Complex.ofPolar(rho, theta).arg() == theta</pre>
         *
         *
         * If `rho` is infinite then the resulting parts may be infinite or NaN
         * following the rules for double arithmetic, for example:
         *
         *
         *  * `ofPolar(`\( -0.0 \)`, `\( 0 \)`) = `\( \text{NaN} + i \text{NaN} \)
         *  * `ofPolar(`\( 0.0 \)`, `\( 0 \)`) = `\( 0 + i 0 \)
         *  * `ofPolar(`\( 1 \)`, `\( 0 \)`) = `\( 1 + i 0 \)
         *  * `ofPolar(`\( 1 \)`, `\( \pi \)`) = `\( -1 + i \sin(\pi) \)
         *  * `ofPolar(`\( \infty \)`, `\( \pi \)`) = `\( -\infty + i \infty \)
         *  * `ofPolar(`\( \infty \)`, `\( 0 \)`) = `\( -\infty + i \text{NaN} \)
         *  * `ofPolar(`\( \infty \)`, `\( -\frac{\pi}{4} \)`) = `\( \infty - i \infty \)
         *  * `ofPolar(`\( \infty \)`, `\( 5\frac{\pi}{4} \)`) = `\( -\infty - i \infty \)
         *
         *
         *
         * This method is the functional equivalent of the C++ method `std::polar`.
         *
         * @param rho The modulus of the complex number.
         * @param theta The argument of the complex number.
         * @return `Complex` number.
         * @see [Polar Coordinates](http://mathworld.wolfram.com/PolarCoordinates.html)
         */
        fun ofPolar(rho: Double, theta: Double): Complex {
            // Require finite theta and non-negative, non-nan rho
            if (!theta.isFinite() || negative(
                    rho
                ) || rho.isNaN()) {
                return NAN
            }
            val x: Double = rho * cos(theta)
            val y: Double = rho * sin(theta)
            return Complex(x, y)
        }

        /**
         * Create a complex cis number. This is also known as the complex exponential:
         *
         * \[ \text{cis}(x) = e^{ix} = \cos(x) + i \sin(x) \]
         *
         * @param x `double` to build the cis number.
         * @return `Complex` cis number.
         * @see [Cis](http://mathworld.wolfram.com/Cis.html)
         */
        fun ofCis(x: Double): Complex {
            return Complex(cos(x), sin(x))
        }

        /**
         * Returns a `Complex` instance representing the specified string `s`.
         *
         *
         * If `s` is `null`, then a `NullPointerException` is thrown.
         *
         *
         * The string must be in a format compatible with that produced by
         * [Complex.toString()][.toString].
         * The format expects a start and end parentheses surrounding two numeric parts split
         * by a separator. Leading and trailing spaces are allowed around each numeric part.
         * Each numeric part is parsed using [String.toDouble]. The parts
         * are interpreted as the real and imaginary parts of the complex number.
         *
         *
         * Examples of valid strings and the equivalent `Complex` are shown below:
         *
         * <pre>
         * "(0,0)"             = Complex.ofCartesian(0, 0)
         * "(0.0,0.0)"         = Complex.ofCartesian(0, 0)
         * "(-0.0, 0.0)"       = Complex.ofCartesian(-0.0, 0)
         * "(-1.23, 4.56)"     = Complex.ofCartesian(-1.23, 4.56)
         * "(1e300,-1.1e-2)"   = Complex.ofCartesian(1e300, -1.1e-2)</pre>
         *
         * @param s String representation.
         * @return `Complex` number.
         * @throws NullPointerException if the string is null.
         * @throws NumberFormatException if the string does not contain a parsable complex number.
         * @see String.toDouble
         * @see .toString
         */
        fun parse(s: String): Complex {
            val len = s.length
            if (len < FORMAT_MIN_LEN) {
                throw NumberFormatException(
                    parsingExceptionMsg(
                        "Input too short, expected format",
                        FORMAT_START.toString() + "x" + FORMAT_SEP + "y" + FORMAT_END,
                        s
                    )
                )
            }

            // Confirm start: '('
            if (s[0] != FORMAT_START) {
                throw NumberFormatException(
                    parsingExceptionMsg(
                        "Expected start delimiter",
                        FORMAT_START,
                        s
                    )
                )
            }

            // Confirm end: ')'
            if (s[len - 1] != FORMAT_END) {
                throw NumberFormatException(
                    parsingExceptionMsg(
                        "Expected end delimiter",
                        FORMAT_END,
                        s
                    )
                )
            }

            // Confirm separator ',' is between at least 2 characters from
            // either end: "(x,x)"
            // Count back from the end ignoring the last 2 characters.
            val sep = s.lastIndexOf(FORMAT_SEP, len - 3)
            if (sep < BEFORE_SEP) {
                throw NumberFormatException(
                    parsingExceptionMsg(
                        "Expected separator between two numbers",
                        FORMAT_SEP,
                        s
                    )
                )
            }

            // Should be no more separators
            if (s.indexOf(FORMAT_SEP, sep + 1) != -1) {
                throw NumberFormatException(
                    parsingExceptionMsg(
                        "Incorrect number of parts, expected only 2 using separator",
                        FORMAT_SEP,
                        s
                    )
                )
            }

            // Try to parse the parts
            val rePart = s.substring(1, sep)
            val re: Double
            re = try {
                rePart.toDouble()
            } catch (ex: NumberFormatException) {
                throw NumberFormatException(
                    parsingExceptionMsg(
                        "Could not parse real part",
                        rePart,
                        s
                    )
                )
            }
            val imPart = s.substring(sep + 1, len - 1)
            val im: Double
            im = try {
                imPart.toDouble()
            } catch (ex: NumberFormatException) {
                throw NumberFormatException(
                    parsingExceptionMsg(
                        "Could not parse imaginary part",
                        imPart,
                        s
                    )
                )
            }
            return ofCartesian(
                re,
                im
            )
        }

        /**
         * Creates an exception message.
         *
         * @param message Message prefix.
         * @param error Input that caused the error.
         * @param s String representation.
         * @return A message.
         */
        private fun parsingExceptionMsg(
            message: String,
            error: Any,
            s: String
        ): String {
            val sb: StringBuilder = StringBuilder(100)
                .append(message)
                .append(" '").append(error)
                .append("' for input \"").append(s).append('"')
            return sb.toString()
        }

        /**
         * Returns the absolute value of the complex number.
         * <pre>abs(x + i y) = sqrt(x^2 + y^2)</pre>
         *
         *
         * This should satisfy the special cases of the hypot function in ISO C99 F.9.4.3:
         * "The hypot functions compute the square root of the sum of the squares of x and y,
         * without undue overflow or underflow."
         *
         *
         *  * hypot(x, y), hypot(y, x), and hypot(x, −y) are equivalent.
         *  * hypot(x, ±0) is equivalent to |x|.
         *  * hypot(±∞, y) returns +∞, even if y is a NaN.
         *
         *
         *
         * This method is called by all methods that require the absolute value of the complex
         * number, e.g. abs(), sqrt() and log().
         *
         * @param real Real part.
         * @param imaginary Imaginary part.
         * @return The absolute value.
         */
        private fun abs(real: Double, imaginary: Double): Double {
            // Specialised implementation of hypot.
            // See NUMBERS-143
            return hypot(
                real,
                imaginary
            )
        }

        /**
         * Returns a `Complex` whose value is:
         * <pre>
         * (a + i b)(c + i d) = (ac - bd) + i (ad + bc)</pre>
         *
         *
         * Recalculates to recover infinities as specified in C99 standard G.5.1.
         *
         * @param re1 Real component of first number.
         * @param im1 Imaginary component of first number.
         * @param re2 Real component of second number.
         * @param im2 Imaginary component of second number.
         * @return (a + b i)(c + d i).
         */
        private fun multiply(re1: Double, im1: Double, re2: Double, im2: Double): Complex {
            var a = re1
            var b = im1
            var c = re2
            var d = im2
            val ac = a * c
            val bd = b * d
            val ad = a * d
            val bc = b * c
            var x = ac - bd
            var y = ad + bc

            // --------------
            // NaN can occur if:
            // - any of (a,b,c,d) are NaN (for NaN or Infinite complex numbers)
            // - a multiplication of infinity by zero (ac,bd,ad,bc).
            // - a subtraction of infinity from infinity (e.g. ac - bd)
            //   Note that (ac,bd,ad,bc) can be infinite due to overflow.
            //
            // Detect a NaN result and perform correction.
            //
            // Modification from the listing in ISO C99 G.5.1 (6)
            // Do not correct infinity multiplied by zero. This is left as NaN.
            // --------------
            if (x.isNaN() && y.isNaN()) {
                // Recover infinities that computed as NaN+iNaN ...
                var recalc = false
                if ((a.isInfinite() || b.isInfinite()) &&
                    isNotZero(
                        c,
                        d
                    )
                ) {
                    // This complex is infinite.
                    // "Box" the infinity and change NaNs in the other factor to 0.
                    a =
                        boxInfinity(
                            a
                        )
                    b =
                        boxInfinity(
                            b
                        )
                    c =
                        changeNaNtoZero(
                            c
                        )
                    d =
                        changeNaNtoZero(
                            d
                        )
                    recalc = true
                }
                if ((c.isInfinite() || d.isInfinite()) &&
                    isNotZero(
                        a,
                        b
                    )
                ) {
                    // The other complex is infinite.
                    // "Box" the infinity and change NaNs in the other factor to 0.
                    c =
                        boxInfinity(
                            c
                        )
                    d =
                        boxInfinity(
                            d
                        )
                    a =
                        changeNaNtoZero(
                            a
                        )
                    b =
                        changeNaNtoZero(
                            b
                        )
                    recalc = true
                }
                if (!recalc && (ac.isInfinite() || bd.isInfinite() ||
                            ad.isInfinite() || bc.isInfinite())
                ) {
                    // The result overflowed to infinity.
                    // Recover infinities from overflow by changing NaNs to 0 ...
                    a =
                        changeNaNtoZero(
                            a
                        )
                    b =
                        changeNaNtoZero(
                            b
                        )
                    c =
                        changeNaNtoZero(
                            c
                        )
                    d =
                        changeNaNtoZero(
                            d
                        )
                    recalc = true
                }
                if (recalc) {
                    x = Double.POSITIVE_INFINITY * (a * c - b * d)
                    y = Double.POSITIVE_INFINITY * (a * d + b * c)
                }
            }
            return Complex(x, y)
        }

        /**
         * Box values for the real or imaginary component of an infinite complex number.
         * Any infinite value will be returned as one. Non-infinite values will be returned as zero.
         * The sign is maintained.
         *
         * <pre>
         * inf  =  1
         * -inf  = -1
         * x    =  0
         * -x    = -0
        </pre> *
         *
         * @param component the component
         * @return The boxed value
         */
        private fun boxInfinity(component: Double): Double {
            return (if (component.isInfinite()) 1.0 else 0.0).withSign(component)
        }

        /**
         * Checks if the complex number is not zero.
         *
         * @param real the real component
         * @param imaginary the imaginary component
         * @return true if the complex is not zero
         */
        private fun isNotZero(real: Double, imaginary: Double): Boolean {
            // The use of equals is deliberate.
            // This method must distinguish NaN from zero thus ruling out:
            // (real != 0.0 || imaginary != 0.0)
            return !(real == 0.0 && imaginary == 0.0)
        }

        /**
         * Change NaN to zero preserving the sign; otherwise return the value.
         *
         * @param value the value
         * @return The new value
         */
        private fun changeNaNtoZero(value: Double): Double {
            return if (value.isNaN()) 0.0.withSign(value) else value
        }

        /**
         * Returns a `Complex` whose value is:
         * <pre>
         * `
         * a + i b     (ac + bd) + i (bc - ad)
         * -------  =  -----------------------
         * c + i d            c<sup>2</sup> + d<sup>2</sup>
        ` *
        </pre> *
         *
         *
         * Recalculates to recover infinities as specified in C99
         * standard G.5.1. Method is fully in accordance with
         * C++11 standards for complex numbers.
         *
         *
         * Note: In the event of divide by zero this method produces the same result
         * as dividing by a real-only zero using [.divide].
         *
         * @param re1 Real component of first number.
         * @param im1 Imaginary component of first number.
         * @param re2 Real component of second number.
         * @param im2 Imaginary component of second number.
         * @return (a + i b) / (c + i d).
         * @see [Complex Division](http://mathworld.wolfram.com/ComplexDivision.html)
         *
         * @see .divide
         */
        @ExperimentalStdlibApi
        private fun divide(re1: Double, im1: Double, re2: Double, im2: Double): Complex {
            var a = re1
            var b = im1
            var c = re2
            var d = im2
            var ilogbw = 0
            // Get the exponent to scale the divisor parts to the range [1, 2).
            val exponent =
                getScale(
                    c,
                    d
                )
            if (exponent <= DoubleConsts.MAX_EXPONENT) {
                ilogbw = exponent
                c = c.scalb(-ilogbw)
                d = d.scalb(-ilogbw)
            }
            val denom = c * c + d * d

            // Note: Modification from the listing in ISO C99 G.5.1 (8):
            // Avoid overflow if a or b are very big.
            // Since (c, d) in the range [1, 2) the sum (ac + bd) could overflow
            // when (a, b) are both above (Double.MAX_VALUE / 4). The same applies to
            // (bc - ad) with large negative values.
            // Use the maximum exponent as an approximation to the magnitude.
            if (getMaxExponent(
                    a,
                    b
                ) > DoubleConsts.MAX_EXPONENT - 2) {
                ilogbw -= 2
                a /= 4.0
                b /= 4.0
            }
            var x: Double = ((a * c + b * d) / denom).scalb(-ilogbw)
            var y: Double = ((b * c - a * d) / denom).scalb(-ilogbw)
            // Recover infinities and zeros that computed as NaN+iNaN
            // the only cases are nonzero/zero, infinite/finite, and finite/infinite, ...
            if (x.isNaN() && y.isNaN()) {
                if (denom == 0.0 &&
                    (!a.isNaN() || !b.isNaN())
                ) {
                    // nonzero/zero
                    // This case produces the same result as divide by a real-only zero
                    // using Complex.divide(+/-0.0)
                    x = Double.POSITIVE_INFINITY.withSign(c) * a
                    y = Double.POSITIVE_INFINITY.withSign(c) * b
                } else if ((a.isInfinite() || b.isInfinite()) &&
                    c.isFinite() && d.isFinite()
                ) {
                    // infinite/finite
                    a =
                        boxInfinity(
                            a
                        )
                    b =
                        boxInfinity(
                            b
                        )
                    x = Double.POSITIVE_INFINITY * (a * c + b * d)
                    y = Double.POSITIVE_INFINITY * (b * c - a * d)
                } else if ((c.isInfinite() || d.isInfinite()) &&
                    a.isFinite() && b.isFinite()
                ) {
                    // finite/infinite
                    c =
                        boxInfinity(
                            c
                        )
                    d =
                        boxInfinity(
                            d
                        )
                    x = 0.0 * (a * c + b * d)
                    y = 0.0 * (b * c - a * d)
                }
            }
            return Complex(x, y)
        }

        /**
         * Returns the square root of the complex number `sqrt(x + i y)`.
         *
         * @param real Real component.
         * @param imaginary Imaginary component.
         * @return The square root of the complex number.
         */
        private fun sqrt(real: Double, imaginary: Double): Complex {
            // Handle NaN
            if (real.isNaN() || imaginary.isNaN()) {
                // Check for infinite
                if (imaginary.isInfinite()) {
                    return Complex(
                        Double.POSITIVE_INFINITY,
                        imaginary
                    )
                }
                return if (real.isInfinite()) {
                    if (real == Double.NEGATIVE_INFINITY) {
                        Complex(
                            Double.NaN,
                            Double.POSITIVE_INFINITY.withSign(imaginary)
                        )
                    } else Complex(
                        Double.POSITIVE_INFINITY,
                        Double.NaN
                    )
                } else NAN
            }

            // Compute with positive values and determine sign at the end
            val x: Double = abs(real)
            val y: Double = abs(imaginary)

            // Compute
            val t: Double

            // This alters the implementation of Hull et al (1994) which used a standard
            // precision representation of |z|: sqrt(x*x + y*y).
            // This formula should use the same definition of the magnitude returned
            // by Complex.abs() which is a high precision computation with scaling.
            // Worry about overflow if 2 * (|z| + |x|) will overflow.
            // Worry about underflow if |z| or |x| are sub-normal components.
            if (inRegion(
                    x,
                    y,
                    DoubleConsts.MIN_NORMAL,
                    SQRT_SAFE_UPPER
                )
            ) {
                // No over/underflow
                t = sqrt(2 * (abs(
                    x,
                    y
                ) + x))
            } else {
                // Potential over/underflow. First check infinites and real/imaginary only.

                // Check for infinite
                if (isPosInfinite(
                        y
                    )
                ) {
                    return Complex(
                        Double.POSITIVE_INFINITY,
                        imaginary
                    )
                } else if (isPosInfinite(
                        x
                    )
                ) {
                    return if (real == Double.NEGATIVE_INFINITY) {
                        Complex(
                            0.0,
                            Double.POSITIVE_INFINITY.withSign(imaginary)
                        )
                    } else Complex(
                        Double.POSITIVE_INFINITY,
                        0.0.withSign(imaginary)
                    )
                } else if (y == 0.0) {
                    // Real only
                    val sqrtAbs: Double = sqrt(x)
                    return if (real < 0) {
                        Complex(
                            0.0,
                            sqrtAbs.withSign(imaginary)
                        )
                    } else Complex(
                        sqrtAbs,
                        imaginary
                    )
                } else if (x == 0.0) {
                    // Imaginary only. This sets the two components to the same magnitude.
                    // Note: In polar coordinates this does not happen:
                    // real = sqrt(abs()) * Math.cos(arg() / 2)
                    // imag = sqrt(abs()) * Math.sin(arg() / 2)
                    // arg() / 2 = pi/4 and cos and sin should both return sqrt(2)/2 but
                    // are different by 1 ULP.
                    val sqrtAbs: Double = sqrt(y) * ONE_OVER_ROOT2
                    return Complex(
                        sqrtAbs,
                        sqrtAbs.withSign(imaginary)
                    )
                } else {
                    // Over/underflow.
                    // Full scaling is not required as this is done in the hypotenuse function.
                    // Keep the number as big as possible for maximum precision in the second sqrt.
                    // Note if we scale by an even power of 2, we can re-scale by sqrt of the number.
                    // a = sqrt(b)
                    // a = sqrt(b/4) * sqrt(4)
                    val rescale: Double
                    val sx: Double
                    val sy: Double
                    if (max(x, y) > SQRT_SAFE_UPPER) {
                        // Overflow. Scale down by 16 and rescale by sqrt(16).
                        sx = x / 16
                        sy = y / 16
                        rescale = 4.0
                    } else {
                        // Sub-normal numbers. Make them normal by scaling by 2^54,
                        // i.e. more than the mantissa digits, and rescale by sqrt(2^54) = 2^27.
                        sx = x * 2.0.pow(54)
                        sy = y * 2.0.pow(54)
                        rescale = 2.0.pow(-27)
                    }
                    t = rescale * sqrt(2 * (abs(
                        sx,
                        sy
                    ) + sx))
                }
            }
            return if (real >= 0) {
                Complex(t / 2, imaginary / t)
            } else Complex(
                y / t,
                (t / 2).withSign(imaginary)
            )
        }

        /**
         * Returns the inverse sine of the complex number.
         *
         *
         * This function exists to allow implementation of the identity
         * `asinh(z) = -i asin(iz)`.
         *
         *
         * Adapted from `<boost/math/complex/asin.hpp>`. This method only (and not
         * invoked methods within) is distributed under the Boost Software License V1.0.
         * The original notice is shown below and the licence is shown in full in LICENSE:
         * <pre>
         * (C) Copyright John Maddock 2005.
         * Distributed under the Boost Software License, Version 1.0. (See accompanying
         * file LICENSE or copy at https://www.boost.org/LICENSE_1_0.txt)
        </pre> *
         *
         * @param real Real part.
         * @param imaginary Imaginary part.
         * @param constructor Constructor.
         * @return The inverse sine of this complex number.
         */
        private fun asin(
            real: Double, imaginary: Double,
            constructor: (Double, Double)-> Complex
        ): Complex {
            // Compute with positive values and determine sign at the end
            val x: Double = abs(real)
            val y: Double = abs(imaginary)
            // The result (without sign correction)
            val re: Double
            val im: Double

            // Handle C99 special cases
            if (x.isNaN()) {
                if (isPosInfinite(
                        y
                    )
                ) {
                    re = x
                    im = y
                } else {
                    // No-use of the input constructor
                    return NAN
                }
            } else if (y.isNaN()) {
                if (x == 0.0) {
                    re = 0.0
                    im = y
                } else if (isPosInfinite(
                        x
                    )
                ) {
                    re = y
                    im = x
                } else {
                    // No-use of the input constructor
                    return NAN
                }
            } else if (isPosInfinite(
                    x
                )
            ) {
                re =
                    if (isPosInfinite(
                            y
                        )
                    ) PI_OVER_4 else PI_OVER_2
                im = x
            } else if (isPosInfinite(
                    y
                )
            ) {
                re = 0.0
                im = y
            } else {
                // Special case for real numbers:
                if (y == 0.0 && x <= 1) {
                    return constructor.invoke(asin(real), imaginary)
                }
                val xp1 = x + 1
                val xm1 = x - 1
                if (inRegion(
                        x,
                        y,
                        SAFE_MIN,
                        SAFE_MAX
                    )
                ) {
                    val yy = y * y
                    val r: Double = sqrt(xp1 * xp1 + yy)
                    val s: Double = sqrt(xm1 * xm1 + yy)
                    val a = 0.5 * (r + s)
                    val b = x / a
                    re = if (b <= B_CROSSOVER) {
                        asin(b)
                    } else {
                        val apx = a + x
                        if (x <= 1) {
                            atan(x / sqrt(0.5 * apx * (yy / (r + xp1) + (s - xm1))))
                        } else {
                            atan(x / (y * sqrt(0.5 * (apx / (r + xp1) + apx / (s + xm1)))))
                        }
                    }
                    if (a <= A_CROSSOVER) {
                        val am1: Double
                        am1 = if (x < 1) {
                            0.5 * (yy / (r + xp1) + yy / (s - xm1))
                        } else {
                            0.5 * (yy / (r + xp1) + (s + xm1))
                        }
                        im = ln1p(am1 + sqrt(am1 * (a + 1)))
                    } else {
                        im = ln(a + sqrt(a * a - 1))
                    }
                } else {
                    // Hull et al: Exception handling code from figure 4
                    if (y <= EPSILON * abs(xm1)) {
                        if (x < 1) {
                            re = asin(x)
                            im = y / sqrt(xp1 * (1 - x))
                        } else {
                            re =
                                PI_OVER_2
                            if (Double.MAX_VALUE / xp1 > xm1) {
                                // xp1 * xm1 won't overflow:
                                im = ln1p(xm1 + sqrt(xp1 * xm1))
                            } else {
                                im = LN_2 + ln(x)
                            }
                        }
                    } else if (y <= SAFE_MIN) {
                        // Hull et al: Assume x == 1.
                        // True if:
                        // E^2 > 8*sqrt(u)
                        //
                        // E = Machine epsilon: (1 + epsilon) = 1
                        // u = Double.MIN_NORMAL
                        re = PI_OVER_2 - sqrt(y)
                        im = sqrt(y)
                    } else if (EPSILON * y - 1 >= x) {
                        // Possible underflow:
                        re = x / y
                        im = LN_2 + ln(y)
                    } else if (x > 1) {
                        re = atan(x / y)
                        val xoy = x / y
                        im = LN_2 + ln(y) + 0.5 * ln1p(xoy * xoy)
                    } else {
                        val a: Double = sqrt(1 + y * y)
                        // Possible underflow:
                        re = x / a
                        im = 0.5 * ln1p(2 * y * (y + a))
                    }
                }
            }
            return constructor.invoke(
                changeSign(
                    re,
                    real
                ),
                changeSign(
                    im,
                    imaginary
                )
            )
        }

        /**
         * Returns the inverse cosine of the complex number.
         *
         *
         * This function exists to allow implementation of the identity
         * `acosh(z) = +-i acos(z)`.
         *
         *
         * Adapted from `<boost/math/complex/acos.hpp>`. This method only (and not
         * invoked methods within) is distributed under the Boost Software License V1.0.
         * The original notice is shown below and the licence is shown in full in LICENSE:
         * <pre>
         * (C) Copyright John Maddock 2005.
         * Distributed under the Boost Software License, Version 1.0. (See accompanying
         * file LICENSE or copy at https://www.boost.org/LICENSE_1_0.txt)
        </pre> *
         *
         * @param real Real part.
         * @param imaginary Imaginary part.
         * @param constructor Constructor.
         * @return The inverse cosine of the complex number.
         */
        private fun acos(
            real: Double, imaginary: Double,
            constructor: (Double, Double)-> Complex
        ): Complex {
            // Compute with positive values and determine sign at the end
            val x: Double = abs(real)
            val y: Double = abs(imaginary)
            // The result (without sign correction)
            val re: Double
            val im: Double

            // Handle C99 special cases
            if (isPosInfinite(
                    x
                )
            ) {
                if (isPosInfinite(
                        y
                    )
                ) {
                    re =
                        PI_OVER_4
                    im = y
                } else if (y.isNaN()) {
                    // sign of the imaginary part of the result is unspecified
                    return constructor.invoke(imaginary, real)
                } else {
                    re = 0.0
                    im = Double.POSITIVE_INFINITY
                }
            } else if (x.isNaN()) {
                return if (isPosInfinite(
                        y
                    )
                ) {
                    constructor.invoke(x, -imaginary)
                } else NAN
                // No-use of the input constructor
            } else if (isPosInfinite(
                    y
                )
            ) {
                re =
                    PI_OVER_2
                im = y
            } else if (y.isNaN()) {
                return constructor.invoke(if (x == 0.0) PI_OVER_2 else y, y)
            } else {
                // Special case for real numbers:
                if (y == 0.0 && x <= 1) {
                    return constructor.invoke(
                        if (x == 0.0) PI_OVER_2 else acos(real),
                        -imaginary
                    )
                }
                val xp1 = x + 1
                val xm1 = x - 1
                if (inRegion(
                        x,
                        y,
                        SAFE_MIN,
                        SAFE_MAX
                    )
                ) {
                    val yy = y * y
                    val r: Double = sqrt(xp1 * xp1 + yy)
                    val s: Double = sqrt(xm1 * xm1 + yy)
                    val a = 0.5 * (r + s)
                    val b = x / a
                    re = if (b <= B_CROSSOVER) {
                        acos(b)
                    } else {
                        val apx = a + x
                        if (x <= 1) {
                            atan(sqrt(0.5 * apx * (yy / (r + xp1) + (s - xm1))) / x)
                        } else {
                            atan(y * sqrt(0.5 * (apx / (r + xp1) + apx / (s + xm1))) / x)
                        }
                    }
                    if (a <= A_CROSSOVER) {
                        val am1: Double
                        am1 = if (x < 1) {
                            0.5 * (yy / (r + xp1) + yy / (s - xm1))
                        } else {
                            0.5 * (yy / (r + xp1) + (s + xm1))
                        }
                        im = ln1p(am1 + sqrt(am1 * (a + 1)))
                    } else {
                        im = ln(a + sqrt(a * a - 1))
                    }
                } else {
                    // Hull et al: Exception handling code from figure 6
                    if (y <= EPSILON * abs(xm1)) {
                        if (x < 1) {
                            re = acos(x)
                            im = y / sqrt(xp1 * (1 - x))
                        } else {
                            // This deviates from Hull et al's paper as per
                            // https://svn.boost.org/trac/boost/ticket/7290
                            if (Double.MAX_VALUE / xp1 > xm1) {
                                // xp1 * xm1 won't overflow:
                                re = y / sqrt(xm1 * xp1)
                                im = ln1p(xm1 + sqrt(xp1 * xm1))
                            } else {
                                re = y / x
                                im = LN_2 + ln(x)
                            }
                        }
                    } else if (y <= SAFE_MIN) {
                        // Hull et al: Assume x == 1.
                        // True if:
                        // E^2 > 8*sqrt(u)
                        //
                        // E = Machine epsilon: (1 + epsilon) = 1
                        // u = Double.MIN_NORMAL
                        re = sqrt(y)
                        im = sqrt(y)
                    } else if (EPSILON * y - 1 >= x) {
                        re =
                            PI_OVER_2
                        im = LN_2 + ln(y)
                    } else if (x > 1) {
                        re = atan(y / x)
                        val xoy = x / y
                        im = LN_2 + ln(y) + 0.5 * ln1p(xoy * xoy)
                    } else {
                        re =
                            PI_OVER_2
                        val a: Double = sqrt(1 + y * y)
                        im = 0.5 * ln1p(2 * y * (y + a))
                    }
                }
            }
            return constructor.invoke(
                if (negative(
                        real
                    )
                ) PI - re else re,
                if (negative(
                        imaginary
                    )
                ) im else -im
            )
        }

        /**
         * Returns the hyperbolic sine of the complex number.
         *
         *
         * This function exists to allow implementation of the identity
         * `sin(z) = -i sinh(iz)`.
         *
         *
         *
         * @param real Real part.
         * @param imaginary Imaginary part.
         * @param constructor Constructor.
         * @return The hyperbolic sine of the complex number.
         */
        private fun sinh(
            real: Double,
            imaginary: Double,
            constructor: (Double, Double)-> Complex
        ): Complex {
            if (real.isInfinite() && !imaginary.isFinite()) {
                return constructor.invoke(real, Double.NaN)
            }
            if (real == 0.0) {
                // Imaginary-only sinh(iy) = i sin(y).
                return if (imaginary.isFinite()) {
                    // Maintain periodic property with respect to the imaginary component.
                    // sinh(+/-0.0) * cos(+/-x) = +/-0 * cos(x)
                    constructor.invoke(
                        changeSign(
                            real,
                            cos(imaginary)
                        ),
                        sin(imaginary)
                    )
                } else constructor.invoke(real, Double.NaN)
                // If imaginary is inf/NaN the sign of the real part is unspecified.
                // Returning the same real value maintains the conjugate equality.
                // It is not possible to also maintain the odd function (hence the unspecified sign).
            }
            if (imaginary == 0.0) {
                // Real-only sinh(x).
                return constructor.invoke(sinh(real), imaginary)
            }
            val x: Double = abs(real)
            return if (x > SAFE_EXP) {
                // Approximate sinh/cosh(x) using exp^|x| / 2
                coshsinh(
                    x,
                    real,
                    imaginary,
                    true,
                    constructor
                )
            } else constructor.invoke(
                sinh(real) * cos(imaginary),
                cosh(real) * sin(imaginary)
            )
            // No overflow of sinh/cosh
        }

        /**
         * Returns the hyperbolic cosine of the complex number.
         *
         *
         * This function exists to allow implementation of the identity
         * `cos(z) = cosh(iz)`.
         *
         *
         *
         * @param real Real part.
         * @param imaginary Imaginary part.
         * @param constructor Constructor.
         * @return The hyperbolic cosine of the complex number.
         */
        private fun cosh(
            real: Double,
            imaginary: Double,
            constructor: (Double, Double)-> Complex
        ): Complex {
            // ISO C99: Preserve the even function by mapping to positive
            // f(z) = f(-z)
            if (real.isInfinite() && !imaginary.isFinite()) {
                return constructor.invoke(abs(real), Double.NaN)
            }
            if (real == 0.0) {
                // Imaginary-only cosh(iy) = cos(y).
                return if (imaginary.isFinite()) {
                    // Maintain periodic property with respect to the imaginary component.
                    // sinh(+/-0.0) * sin(+/-x) = +/-0 * sin(x)
                    constructor.invoke(
                        cos(imaginary),
                        changeSign(
                            real,
                            sin(imaginary)
                        )
                    )
                } else constructor.invoke(Double.NaN,
                    changeSign(
                        real,
                        imaginary
                    )
                )
                // If imaginary is inf/NaN the sign of the imaginary part is unspecified.
                // Although not required by C99 changing the sign maintains the conjugate equality.
                // It is not possible to also maintain the even function (hence the unspecified sign).
            }
            if (imaginary == 0.0) {
                // Real-only cosh(x).
                // Change sign to preserve conjugate equality and even function.
                // sin(+/-0) * sinh(+/-x) = +/-0 * +/-a (sinh is monotonic and same sign)
                // => change the sign of imaginary using real. Handles special case of infinite real.
                // If real is NaN the sign of the imaginary part is unspecified.
                return constructor.invoke(cosh(real),
                    changeSign(
                        imaginary,
                        real
                    )
                )
            }
            val x: Double = abs(real)
            return if (x > SAFE_EXP) {
                // Approximate sinh/cosh(x) using exp^|x| / 2
                coshsinh(
                    x,
                    real,
                    imaginary,
                    false,
                    constructor
                )
            } else constructor.invoke(
                cosh(real) * cos(imaginary),
                sinh(real) * sin(imaginary)
            )
            // No overflow of sinh/cosh
        }

        /**
         * Compute cosh or sinh when the absolute real component |x| is large. In this case
         * cosh(x) and sinh(x) can be approximated by exp(|x|) / 2:
         *
         * <pre>
         * cosh(x+iy) real = (e^|x| / 2) * cos(y)
         * cosh(x+iy) imag = (e^|x| / 2) * sin(y) * sign(x)
         * sinh(x+iy) real = (e^|x| / 2) * cos(y) * sign(x)
         * sinh(x+iy) imag = (e^|x| / 2) * sin(y)
        </pre> *
         *
         * @param x Absolute real component |x|.
         * @param real Real part (x).
         * @param imaginary Imaginary part (y).
         * @param sinh Set to true to compute sinh, otherwise cosh.
         * @param constructor Constructor.
         * @return The hyperbolic sine/cosine of the complex number.
         */
        private fun coshsinh(
            x: Double, real: Double, imaginary: Double, sinh: Boolean,
            constructor: (Double, Double)-> Complex
        ): Complex {
            // Always require the cos and sin.
            var re: Double = cos(imaginary)
            var im: Double = sin(imaginary)
            // Compute the correct function
            if (sinh) {
                re =
                    changeSign(
                        re,
                        real
                    )
            } else {
                im =
                    changeSign(
                        im,
                        real
                    )
            }
            // Multiply by (e^|x| / 2).
            // Overflow safe computation since sin/cos can be very small allowing a result
            // when e^x overflows: e^x / 2 = (e^m / 2) * e^m * e^(x-2m)
            if (x > SAFE_EXP * 3) {
                // e^x > e^m * e^m * e^m
                // y * (e^m / 2) * e^m * e^m will overflow when starting with Double.MIN_VALUE.
                // Note: Do not multiply by +inf to safeguard against sin(y)=0.0 which
                // will create 0 * inf = nan.
                re *= Double.MAX_VALUE * Double.MAX_VALUE * Double.MAX_VALUE
                im *= Double.MAX_VALUE * Double.MAX_VALUE * Double.MAX_VALUE
            } else {
                // Initial part of (e^x / 2) using (e^m / 2)
                re *= EXP_M / 2
                im *= EXP_M / 2
                val xm: Double
                if (x > SAFE_EXP * 2) {
                    // e^x = e^m * e^m * e^(x-2m)
                    re *= EXP_M
                    im *= EXP_M
                    xm = x - SAFE_EXP * 2
                } else {
                    // e^x = e^m * e^(x-m)
                    xm = x - SAFE_EXP
                }
                val exp: Double = exp(xm)
                re *= exp
                im *= exp
            }
            return constructor.invoke(re, im)
        }

        /**
         * Returns the hyperbolic tangent of this complex number.
         *
         *
         * This function exists to allow implementation of the identity
         * `tan(z) = -i tanh(iz)`.
         *
         *
         *
         * @param real Real part.
         * @param imaginary Imaginary part.
         * @param constructor Constructor.
         * @return The hyperbolic tangent of the complex number.
         */
        private fun tanh(
            real: Double,
            imaginary: Double,
            constructor: (Double, Double)-> Complex
        ): Complex {
            // Cache the absolute real value
            val x: Double = abs(real)

            // Handle inf or nan.
            if (!isPosFinite(
                    x
                ) || !imaginary.isFinite()) {
                if (isPosInfinite(
                        x
                    )
                ) {
                    if (imaginary.isFinite()) {
                        // The sign is copied from sin(2y)
                        // The identity sin(2a) = 2 sin(a) cos(a) is used for consistency
                        // with the computation below. Only the magnitude is important
                        // so drop the 2. When |y| is small sign(sin(2y)) = sign(y).
                        val sign =
                            if (abs(imaginary) < PI_OVER_2) imaginary else sin(
                                imaginary
                            ) * cos(imaginary)
                        return constructor.invoke(
                            1.0.withSign(real),
                            0.0.withSign(sign)
                        )
                    }
                    // imaginary is infinite or NaN
                    return constructor.invoke(
                        1.0.withSign(real),
                        0.0.withSign(imaginary)
                    )
                }
                // Remaining cases:
                // (0 + i inf), returns (0 + i NaN)
                // (0 + i NaN), returns (0 + i NaN)
                // (x + i inf), returns (NaN + i NaN) for non-zero x (including infinite)
                // (x + i NaN), returns (NaN + i NaN) for non-zero x (including infinite)
                // (NaN + i 0), returns (NaN + i 0)
                // (NaN + i y), returns (NaN + i NaN) for non-zero y (including infinite)
                // (NaN + i NaN), returns (NaN + i NaN)
                return constructor.invoke(
                    if (real == 0.0) real else Double.NaN,
                    if (imaginary == 0.0) imaginary else Double.NaN
                )
            }

            // Finite components
            // tanh(x+iy) = (sinh(2x) + i sin(2y)) / (cosh(2x) + cos(2y))
            if (real == 0.0) {
                // Imaginary-only tanh(iy) = i tan(y)
                // Identity: sin 2y / (1 + cos 2y) = tan(y)
                return constructor.invoke(real, tan(imaginary))
            }
            if (imaginary == 0.0) {
                // Identity: sinh 2x / (1 + cosh 2x) = tanh(x)
                return constructor.invoke(tanh(real), imaginary)
            }

            // The double angles can be avoided using the identities:
            // sinh(2x) = 2 sinh(x) cosh(x)
            // sin(2y) = 2 sin(y) cos(y)
            // cosh(2x) = 2 sinh^2(x) + 1
            // cos(2y) = 2 cos^2(y) - 1
            // tanh(x+iy) = (sinh(x)cosh(x) + i sin(y)cos(y)) / (sinh^2(x) + cos^2(y))
            // To avoid a junction when swapping between the double angles and the identities
            // the identities are used in all cases.
            if (x > SAFE_EXP / 2) {
                // Potential overflow in sinh/cosh(2x).
                // Approximate sinh/cosh using exp^x.
                // Ignore cos^2(y) in the divisor as it is insignificant.
                // real = sinh(x)cosh(x) / sinh^2(x) = +/-1
                val re: Double = 1.0.withSign(real)
                // imag = sin(2y) / 2 sinh^2(x)
                // sinh(x) -> sign(x) * e^|x| / 2 when x is large.
                // sinh^2(x) -> e^2|x| / 4 when x is large.
                // imag = sin(2y) / 2 (e^2|x| / 4) = 2 sin(2y) / e^2|x|
                //      = 4 * sin(y) cos(y) / e^2|x|
                // Underflow safe divide as e^2|x| may overflow:
                // imag = 4 * sin(y) cos(y) / e^m / e^(2|x| - m)
                // (|im| is a maximum of 2)
                var im: Double = sin(imaginary) * cos(imaginary)
                if (x > SAFE_EXP) {
                    // e^2|x| > e^m * e^m
                    // This will underflow 2.0 / e^m / e^m
                    im = 0.0.withSign(im)
                } else {
                    // e^2|x| = e^m * e^(2|x| - m)
                    im = 4 * im / EXP_M / exp(2 * x - SAFE_EXP)
                }
                return constructor.invoke(re, im)
            }

            // No overflow of sinh(2x) and cosh(2x)

            // Note: This does not use the definitional formula but uses the identity:
            // tanh(x+iy) = (sinh(x)cosh(x) + i sin(y)cos(y)) / (sinh^2(x) + cos^2(y))
            val sinhx: Double = sinh(real)
            val coshx: Double = cosh(real)
            val siny: Double = sin(imaginary)
            val cosy: Double = cos(imaginary)
            val divisor = sinhx * sinhx + cosy * cosy
            return constructor.invoke(
                sinhx * coshx / divisor,
                siny * cosy / divisor
            )
        }

        /**
         * Returns the inverse hyperbolic tangent of this complex number.
         *
         *
         * This function exists to allow implementation of the identity
         * `atan(z) = -i atanh(iz)`.
         *
         *
         * Adapted from `<boost/math/complex/atanh.hpp>`. This method only (and not
         * invoked methods within) is distributed under the Boost Software License V1.0.
         * The original notice is shown below and the licence is shown in full in LICENSE:
         * <pre>
         * (C) Copyright John Maddock 2005.
         * Distributed under the Boost Software License, Version 1.0. (See accompanying
         * file LICENSE or copy at https://www.boost.org/LICENSE_1_0.txt)
        </pre> *
         *
         * @param real Real part.
         * @param imaginary Imaginary part.
         * @param constructor Constructor.
         * @return The inverse hyperbolic tangent of the complex number.
         */
        private fun atanh(
            real: Double, imaginary: Double,
            constructor: (Double, Double)-> Complex
        ): Complex {
            // Compute with positive values and determine sign at the end
            var x: Double = abs(real)
            var y: Double = abs(imaginary)
            // The result (without sign correction)
            var re: Double
            var im: Double

            // Handle C99 special cases
            if (x.isNaN()) {
                return if (isPosInfinite(
                        y
                    )
                ) {
                    // The sign of the real part of the result is unspecified
                    constructor.invoke(0.0, PI_OVER_2.withSign(imaginary))
                } else NAN
                // No-use of the input constructor.
                // Optionally raises the ‘‘invalid’’ floating-point exception, for finite y.
            } else if (y.isNaN()) {
                if (isPosInfinite(
                        x
                    )
                ) {
                    return constructor.invoke(0.0.withSign(real), Double.NaN)
                }
                return if (x == 0.0) {
                    constructor.invoke(real, Double.NaN)
                } else NAN
                // No-use of the input constructor
            } else {
                // x && y are finite or infinite.

                // Check the safe region.
                // The lower and upper bounds have been copied from boost::math::atanh.
                // They are different from the safe region for asin and acos.
                // x >= SAFE_UPPER: (1-x) == -x
                // x <= SAFE_LOWER: 1 - x^2 = 1
                if (inRegion(
                        x,
                        y,
                        SAFE_LOWER,
                        SAFE_UPPER
                    )
                ) {
                    // Normal computation within a safe region.

                    // minus x plus 1: (-x+1)
                    val mxp1 = 1 - x
                    val yy = y * y
                    // The definition of real component is:
                    // real = log( ((x+1)^2+y^2) / ((1-x)^2+y^2) ) / 4
                    // This simplifies by adding 1 and subtracting 1 as a fraction:
                    //      = log(1 + ((x+1)^2+y^2) / ((1-x)^2+y^2) - ((1-x)^2+y^2)/((1-x)^2+y^2) ) / 4
                    //
                    // real(atanh(z)) == log(1 + 4*x / ((1-x)^2+y^2)) / 4
                    // imag(atanh(z)) == tan^-1 (2y, (1-x)(1+x) - y^2) / 2
                    // imag(atanh(z)) == tan^-1 (2y, (1 - x^2 - y^2) / 2
                    // The division is done at the end of the function.
                    re = ln1p(4 * x / (mxp1 * mxp1 + yy))
                    // Modified from boost which does not switch the magnitude of x and y.
                    // The denominator for atan2 is 1 - x^2 - y^2.
                    // This can be made more precise if |x| > |y|.
                    val numerator = 2 * y
                    val denominator: Double
                    if (x < y) {
                        val tmp = x
                        x = y
                        y = tmp
                    }
                    // 1 - x is precise if |x| >= 1
                    denominator = if (x >= 1) {
                        (1 - x) * (1 + x) - y * y
                    } else {
                        // |x| < 1: Use high precision if possible:
                        // 1 - x^2 - y^2 = -(x^2 + y^2 - 1)
                        // Modified from boost to use the custom high precision method.
                        -x2y2m1(
                            x,
                            y
                        )
                    }
                    im = atan2(numerator, denominator)
                } else {
                    // This section handles exception cases that would normally cause
                    // underflow or overflow in the main formulas.

                    // C99. G.7: Special case for imaginary only numbers
                    if (x == 0.0) {
                        return if (imaginary == 0.0) {
                            constructor.invoke(real, imaginary)
                        } else constructor.invoke(real, atan(imaginary))
                        // atanh(iy) = i atan(y)
                    }

                    // Real part:
                    // real = Math.log1p(4x / ((1-x)^2 + y^2))
                    // real = Math.log1p(4x / (1 - 2x + x^2 + y^2))
                    // real = Math.log1p(4x / (1 + x(x-2) + y^2))
                    // without either overflow or underflow in the squared terms.
                    if (x >= SAFE_UPPER) {
                        // (1-x) = -x to machine precision:
                        // log1p(4x / (x^2 + y^2))
                        re = if (isPosInfinite(
                                x
                            ) || isPosInfinite(
                                y
                            )
                        ) {
                            0.0
                        } else if (y >= SAFE_UPPER) {
                            // Big x and y: divide by x*y
                            ln1p(4 / y / (x / y + y / x))
                        } else if (y > 1) {
                            // Big x: divide through by x:
                            ln1p(4 / (x + y * y / x))
                        } else {
                            // Big x small y, as above but neglect y^2/x:
                            ln1p(4 / x)
                        }
                    } else if (y >= SAFE_UPPER) {
                        re = if (x > 1) {
                            // Big y, medium x, divide through by y:
                            val mxp1 = 1 - x
                            ln1p(4 * x / y / (mxp1 * mxp1 / y + y))
                        } else {
                            // Big y, small x, as above but neglect (1-x)^2/y:
                            // Note: log1p(v) == v - v^2/2 + v^3/3 ... Taylor series when v is small.
                            // Here v is so small only the first term matters.
                            4 * x / y / y
                        }
                    } else if (x == 1.0) {
                        // x = 1, small y:
                        // Special case when x == 1 as (1-x) is invalid.
                        // Simplify the following formula:
                        // real = log( sqrt((x+1)^2+y^2) ) / 2 - log( sqrt((1-x)^2+y^2) ) / 2
                        //      = log( sqrt(4+y^2) ) / 2 - log(y) / 2
                        // if: 4+y^2 -> 4
                        //      = log( 2 ) / 2 - log(y) / 2
                        //      = (log(2) - log(y)) / 2
                        // Multiply by 2 as it will be divided by 4 at the end.
                        // C99: if y=0 raises the ‘‘divide-by-zero’’ floating-point exception.
                        re = 2 * (LN_2 - ln(y))
                    } else {
                        // Modified from boost which checks y > SAFE_LOWER.
                        // if y*y -> 0 it will be ignored so always include it.
                        val mxp1 = 1 - x
                        re = ln1p(4 * x / (mxp1 * mxp1 + y * y))
                    }

                    // Imaginary part:
                    // imag = atan2(2y, (1-x)(1+x) - y^2)
                    // if x or y are large, then the formula:
                    //   atan2(2y, (1-x)(1+x) - y^2)
                    // evaluates to +(PI - theta) where theta is negligible compared to PI.
                    im = if (x >= SAFE_UPPER || y >= SAFE_UPPER) {
                        PI
                    } else if (x <= SAFE_LOWER) {
                        // (1-x)^2 -> 1
                        if (y <= SAFE_LOWER) {
                            // 1 - y^2 -> 1
                            atan2(2 * y, 1.0)
                        } else {
                            atan2(2 * y, 1 - y * y)
                        }
                    } else {
                        // Medium x, small y.
                        // Modified from boost which checks (y == 0) && (x == 1) and sets re = 0.
                        // This is same as the result from calling atan2(0, 0) so exclude this case.
                        // 1 - y^2 = 1 so ignore subtracting y^2
                        atan2(2 * y, (1 - x) * (1 + x))
                    }
                }
            }
            re /= 4.0
            im /= 2.0
            return constructor.invoke(
                changeSign(
                    re,
                    real
                ),
                changeSign(
                    im,
                    imaginary
                )
            )
        }

        /**
         * Compute `x^2 + y^2 - 1` in high precision.
         * Assumes that the values x and y can be multiplied without overflow; that
         * `x >= y`; and both values are positive.
         *
         * @param x the x value
         * @param y the y value
         * @return `x^2 + y^2 - 1`.
         */
        private fun x2y2m1(x: Double, y: Double): Double {
            // Hull et al used (x-1)*(x+1)+y*y.
            // From the paper on page 236:

            // If x == 1 there is no cancellation.

            // If x > 1, there is also no cancellation, but the argument is now accurate
            // only to within a factor of 1 + 3 EPSILSON (note that x – 1 is exact),
            // so that error = 3 EPSILON.

            // If x < 1, there can be serious cancellation:

            // If 4 y^2 < |x^2 – 1| the cancellation is not serious ... the argument is accurate
            // only to within a factor of 1 + 4 EPSILSON so that error = 4 EPSILON.

            // Otherwise there can be serious cancellation and the relative error in the real part
            // could be enormous.
            val xx = x * x
            val yy = y * y
            // Modify to use high precision before the threshold set by Hull et al.
            // This is to preserve the monotonic output of the computation at the switch.
            // Set the threshold when x^2 + y^2 is above 0.5 thus subtracting 1 results in a number
            // that can be expressed with a higher precision than any number in the range 0.5-1.0
            // due to the variable exponent used below 0.5.
            if (x < 1 && xx + yy > 0.5) {
                // Large relative error.
                // This does not use o.a.c.numbers.LinearCombination.value(x, x, y, y, 1, -1).
                // It is optimised knowing that:
                // - the products are squares
                // - the final term is -1 (which does not require split multiplication and addition)
                // - The answer will not be NaN as the terms are not NaN components
                // - The order is known to be 1 > |x| >= |y|
                // The squares are computed using a split multiply algorithm and
                // the summation using an extended precision summation algorithm.

                // Split x and y as one 26 bits number and one 27 bits number
                val xHigh =
                    splitHigh(
                        x
                    )
                val xLow = x - xHigh
                val yHigh =
                    splitHigh(
                        y
                    )
                val yLow = y - yHigh

                // Accurate split multiplication x * x and y * y
                val x2Low =
                    squareLow(
                        xLow,
                        xHigh,
                        xx
                    )
                val y2Low =
                    squareLow(
                        yLow,
                        yHigh,
                        yy
                    )
                return sumx2y2m1(
                    xx,
                    x2Low,
                    yy,
                    y2Low
                )
            }
            return (x - 1) * (x + 1) + yy
        }

        /**
         * Implement Dekker's method to split a value into two parts. Multiplying by (2^s + 1) create
         * a big value from which to derive the two split parts.
         * <pre>
         * c = (2^s + 1) * a
         * a_big = c - a
         * a_hi = c - a_big
         * a_lo = a - a_hi
         * a = a_hi + a_lo
         * </pre>
         *
         *
         * The multiplicand must be odd allowing a p-bit value to be split into
         * (p-s)-bit value `a_hi` and a non-overlapping (s-1)-bit value `a_lo`.
         * Combined they have (p􏰔-1) bits of significand but the sign bit of `a_lo`
         * contains a bit of information.
         *
         * @param a Value.
         * @return the high part of the value.
         * @see [
         * Dekker
        ](https://doi.org/10.1007/BF01397083) */
        private fun splitHigh(a: Double): Double {
            val c = MULTIPLIER * a
            return c - (c - a)
        }

        /**
         * Compute the round-off from the square of a split number with `low` and `high`
         * components. Uses Dekker's algorithm for split multiplication modified for a square product.
         *
         *
         * Note: This is candidate to be replaced with `Math.fma(x, x, -x * x)` to compute
         * the round-off from the square product `x * x`. This would remove the requirement
         * to compute the split number and make this method redundant. `Math.fma` requires
         * JDK 9 and FMA hardware support.
         *
         * @param low Low part of number.
         * @param high High part of number.
         * @param square Square of the number.
         * @return `low * low - (((product - high * high) - low * high) - high * low)`
         * @see [
         * Shewchuk
        ](http://www-2.cs.cmu.edu/afs/cs/project/quake/public/papers/robust-arithmetic.ps) */
        private fun squareLow(low: Double, high: Double, square: Double): Double {
            val lh = low * high
            return low * low - (square - high * high - lh - lh)
        }

        /**
         * Compute the round-off from the sum of two numbers `a` and `b` using
         * Dekker's two-sum algorithm. The values are required to be ordered by magnitude:
         * `|a| >= |b|`.
         *
         * @param a First part of sum.
         * @param b Second part of sum.
         * @param x Sum.
         * @return `b - (x - a)`
         * @see [
         * Shewchuk
        ](http://www-2.cs.cmu.edu/afs/cs/project/quake/public/papers/robust-arithmetic.ps) */
        private fun fastSumLow(a: Double, b: Double, x: Double): Double {
            // x = a + b
            // bVirtual = x - a
            // y = b - bVirtual
            return b - (x - a)
        }

        /**
         * Compute the round-off from the sum of two numbers `a` and `b` using
         * Knuth's two-sum algorithm. The values are not required to be ordered by magnitude.
         *
         * @param a First part of sum.
         * @param b Second part of sum.
         * @param x Sum.
         * @return `(a - (x - (x - a))) + (b - (x - a))`
         * @see [
         * Shewchuk
        ](http://www-2.cs.cmu.edu/afs/cs/project/quake/public/papers/robust-arithmetic.ps) */
        private fun sumLow(a: Double, b: Double, x: Double): Double {
            // x = a + b
            // bVirtual = x - a
            // aVirtual = x - bVirtual
            // bRoundoff = b - bVirtual
            // aRoundoff = a - aVirtual
            // y = aRoundoff + bRoundoff
            val bVirtual = x - a
            return a - (x - bVirtual) + (b - bVirtual)
        }

        /**
         * Sum x^2 + y^2 - 1. It is assumed that `y <= x < 1`.
         *
         *
         * Implement Shewchuk's expansion-sum algorithm: [x2Low, x2High] + [-1] + [y2Low, y2High].
         *
         * @param x2High High part of x^2.
         * @param x2Low Low part of x^2.
         * @param y2High High part of y^2.
         * @param y2Low Low part of y^2.
         * @return x^2 + y^2 - 1
         * @see [
         * Shewchuk
        ](http://www-2.cs.cmu.edu/afs/cs/project/quake/public/papers/robust-arithmetic.ps) */
        private fun sumx2y2m1(
            x2High: Double,
            x2Low: Double,
            y2High: Double,
            y2Low: Double
        ): Double {
            // Let e and f be non-overlapping expansions of components of length m and n.
            // The following algorithm will produce a non-overlapping expansion h where the
            // sum h_i = e + f and components of h are in increasing order of magnitude.

            // Expansion-sum proceeds by a grow-expansion of the first part from one expansion
            // into the other, extending its length by 1. The process repeats for the next part
            // but the grow-expansion starts at the previous merge position + 1.
            // Thus expansion-sum requires mn two-sum operations to merge length m into length n
            // resulting in length m+n-1.

            // Variables numbered from 1 as per Figure 7 (p.12). The output expansion h is placed
            // into e increasing its length for each grow expansion.

            // We have two expansions for x^2 and y^2 and the whole number -1.
            // Expecting (x^2 + y^2) close to 1 we generate first the intermediate expansion
            // (x^2 - 1) moving the result away from 1 where there are sparse floating point
            // representations. This is then added to a similar magnitude y^2. Leaving the -1
            // until last suffers from 1 ulp rounding errors more often and the requirement
            // for a distillation sum to reduce rounding error frequency.

            // Note: Do not use the alternative fast-expansion-sum of the parts sorted by magnitude.
            // The parts can be ordered with a single comparison into:
            // [y2Low, (y2High|x2Low), x2High, -1]
            // The fast-two-sum saves 1 fast-two-sum and 3 two-sum operations (21 additions) and
            // adds a penalty of a single branch condition.
            // However the order in not "strongly non-overlapping" and the fast-expansion-sum
            // output will not be strongly non-overlapping. The sum of the output has 1 ulp error
            // on random cis numbers approximately 1 in 160 events. This can be removed by a
            // distillation two-sum pass over the final expansion as a cost of 1 fast-two-sum and
            // 3 two-sum operations! So we use the expansion sum with the same operations and
            // no branches.

            // q=running sum
            var q = x2Low - 1
            var e1 =
                fastSumLow(
                    -1.0,
                    x2Low,
                    q
                )
            var e3 = q + x2High
            var e2 = sumLow(
                q,
                x2High,
                e3
            )

            // Grow expansion of f1 into e
            q = y2Low + e1
            e1 = sumLow(
                y2Low,
                e1,
                q
            )
            var p = q + e2
            e2 = sumLow(
                q,
                e2,
                p
            )
            var e4 = p + e3
            e3 = sumLow(
                p,
                e3,
                e4
            )

            // Grow expansion of f2 into e (only required to start at e2)
            q = y2High + e2
            e2 = sumLow(
                y2High,
                e2,
                q
            )
            p = q + e3
            e3 = sumLow(
                q,
                e3,
                p
            )
            val e5 = p + e4
            e4 = sumLow(
                p,
                e4,
                e5
            )

            // Final summation:
            // The sum of the parts is within 1 ulp of the true expansion value e:
            // |e - sum| < ulp(sum).
            // To achieve the exact result requires iteration of a distillation two-sum through
            // the expansion until convergence, i.e. no smaller term changes higher terms.
            // This requires (n-1) iterations for length n. Here we neglect this as
            // although the method is not ensured to be exact is it robust on random
            // cis numbers.
            return e1 + e2 + e3 + e4 + e5
        }

        /**
         * Returns `true` if the values are equal according to semantics of
         * [Double.equals].
         *
         * @param x Value
         * @param y Value
         * @return `Double.valueof(x).equals(Double.valueOf(y))`.
         */
        private fun equals(x: Double, y: Double): Boolean {
            return x.toBits() == y.toBits()
        }

        /**
         * Check that a value is negative. It must meet all the following conditions:
         *
         *  * it is not `NaN`,
         *  * it is negative signed,
         *
         *
         *
         * Note: This is true for negative zero.
         *
         * @param d Value.
         * @return `true` if `d` is negative.
         */
        private fun negative(d: Double): Boolean {
            return d < 0 || d.toBits() == NEGATIVE_ZERO_LONG_BITS
        }

        /**
         * Check that a value is positive infinity. Used to replace [Double.isInfinite]
         * when the input value is known to be positive (i.e. in the case where it has been
         * set using [abs]).
         *
         * @param d Value.
         * @return `true` if `d` is +inf.
         */
        private fun isPosInfinite(d: Double): Boolean {
            return d == Double.POSITIVE_INFINITY
        }

        /**
         * Check that an absolute value is finite. Used to replace [Double.isFinite]
         * when the input value is known to be positive (i.e. in the case where it has been
         * set using [abs]).
         *
         * @param d Value.
         * @return `true` if `d` is +finite.
         */
        private fun isPosFinite(d: Double): Boolean {
            return d <= Double.MAX_VALUE
        }

        /**
         * Create a complex number given the real and imaginary parts, then multiply by `-i`.
         * This is used in functions that implement trigonomic identities. It is the functional
         * equivalent of:
         *
         * <pre>
         * z = new Complex(real, imaginary).multiplyImaginary(-1);</pre>
         *
         * @param real Real part.
         * @param imaginary Imaginary part.
         * @return `Complex` object.
         */
        private fun multiplyNegativeI(real: Double, imaginary: Double): Complex {
            return Complex(imaginary, -real)
        }

        /**
         * Change the sign of the magnitude based on the signed value.
         *
         *
         * If the signed value is negative then the result is `-magnitude`; otherwise
         * return `magnitude`.
         *
         *
         * A signed value of `-0.0` is treated as negative. A signed value of `NaN`
         * is treated as positive.
         *
         *
         * This is not the same as [withSign] as this method
         * will change the sign based on the signed value rather than copy the sign.
         *
         * @param magnitude the magnitude
         * @param signedValue the signed value
         * @return magnitude or -magnitude.
         * @see .negative
         */
        private fun changeSign(magnitude: Double, signedValue: Double): Double {
            return if (negative(
                    signedValue
                )
            ) -magnitude else magnitude
        }

        /**
         * Returns a scale suitable for use with [scalb] to normalise
         * the number to the interval `[1, 2)`.
         *
         *
         * The scale is typically the largest unbiased exponent used in the representation of the
         * two numbers. In contrast to [exponent] this handles
         * sub-normal numbers by computing the number of leading zeros in the mantissa
         * and shifting the unbiased exponent. The result is that for all finite, non-zero,
         * numbers `a, b`, the magnitude of `scalb(x, -getScale(a, b))` is
         * always in the range `[1, 2)`, where `x = max(|a|, |b|)`.
         *
         *
         * This method is a functional equivalent of the c function ilogb(double) adapted for
         * two input arguments.
         *
         *
         * The result is to be used to scale a complex number using [scalb].
         * Hence the special case of both zero arguments is handled using the return value for NaN
         * as zero cannot be scaled. This is different from [exponent]
         * or [.getMaxExponent].
         *
         *
         * Special cases:
         *
         *
         *  * If either argument is NaN or infinite, then the result is
         * [DoubleConsts.MAX_EXPONENT] + 1.
         *  * If both arguments are zero, then the result is
         * [DoubleConsts.MAX_EXPONENT] + 1.
         *
         *
         * @param a the first value
         * @param b the second value
         * @return The maximum unbiased exponent of the values to be used for scaling
         * @see exponent
         * @see scalb
         * @see [ilogb](http://www.cplusplus.com/reference/cmath/ilogb/)
         */
        @ExperimentalUnsignedTypes
        @ExperimentalStdlibApi
        private fun getScale(a: Double, b: Double): Int {
            // Only interested in the exponent and mantissa so remove the sign bit
            val x: Long = a.toRawBits() and UNSIGN_MASK
            val y: Long = b.toRawBits() and UNSIGN_MASK
            // Only interested in the maximum
            val bits: Long = max(x, y)
            // Get the unbiased exponent
            var exp = (bits ushr 52).toInt() - EXPONENT_OFFSET

            // No case to distinguish nan/inf
            // Handle sub-normal numbers
            if (exp == DoubleConsts.MIN_EXPONENT - 1) {
                // Special case for zero, return as nan/inf to indicate scaling is not possible
                if (bits == 0L) {
                    return DoubleConsts.MAX_EXPONENT + 1
                }
                // A sub-normal number has an exponent below -1022. The amount below
                // is defined by the number of shifts of the most significant bit in
                // the mantissa that is required to get a 1 at position 53 (i.e. as
                // if it were a normal number with assumed leading bit)
                val mantissa = bits and MANTISSA_MASK
                exp -= (mantissa shl 12).countLeadingZeroBits()
            }
            return exp
        }

        /**
         * Returns the largest unbiased exponent used in the representation of the
         * two numbers. Special cases:
         *
         *
         *  * If either argument is NaN or infinite, then the result is
         * [DoubleConsts.MAX_EXPONENT] + 1.
         *  * If both arguments are zero or subnormal, then the result is
         * [DoubleConsts.MIN_EXPONENT] -1.
         *
         *
         *
         * This is used by [.divide] as
         * a simple detection that a number may overflow if multiplied
         * by a value in the interval [1, 2).
         *
         * @param a the first value
         * @param b the second value
         * @return The maximum unbiased exponent of the values.
         * @see exponent
         * @see .divide
         */
        private fun getMaxExponent(a: Double, b: Double): Int {
            // This could return:
            // Math.getExponent(Math.max(Math.abs(a), Math.abs(b)))
            // A speed test is required to determine performance.
            return max(a.exponent(), b.exponent())
        }

        /**
         * Checks if both x and y are in the region defined by the minimum and maximum.
         *
         * @param x x value.
         * @param y y value.
         * @param min the minimum (exclusive).
         * @param max the maximum (exclusive).
         * @return true if inside the region.
         */
        private fun inRegion(x: Double, y: Double, min: Double, max: Double): Boolean {
            return x < max && x > min && y < max && y > min
        }

        /**
         * Returns `sqrt(x^2 + y^2)` without intermediate overflow or underflow.
         *
         *
         * Special cases:
         *
         *  * If either argument is infinite, then the result is positive infinity.
         *  * If either argument is NaN and neither argument is infinite, then the result is NaN.
         *
         *
         *
         * The computed result is expected to be within 1 ulp of the exact result.
         *
         *
         * This method is a replacement for [hypot]. There
         * will be differences between this method and `Math.hypot(double, double)` due
         * to the use of a different algorithm to compute the high precision sum of
         * `x^2 + y^2`. This method has been tested to have a lower maximum error from
         * the exact result; any differences are expected to be 1 ULP indicating a rounding
         * change in the sum.
         *
         *
         * JDK9 ported the hypot function to Java for bug JDK-7130085 due to the slow performance
         * of the method as a native function. Benchmarks of the Complex class for functions that
         * use hypot confirm this is slow pre-Java 9. This implementation outperforms the new faster
         * `Math.hypot(double, double)` on JDK 11 (LTS). See the Commons numbers examples JMH
         * module for benchmarks. Comparisons with alternative implementations indicate
         * performance gains are related to edge case handling and elimination of an unpredictable
         * branch in the computation of `x^2 + y^2`.
         *
         *
         * This port was adapted from the "Freely Distributable Math Library" hypot function.
         * This method only (and not invoked methods within) is distributed under the terms of the
         * original notice as shown below:
         * <pre>
         * ====================================================
         * Copyright (C) 1993 by Sun Microsystems, Inc. All rights reserved.
         *
         * Developed at SunSoft, a Sun Microsystems, Inc. business.
         * Permission to use, copy, modify, and distribute this
         * software is freely granted, provided that this notice
         * is preserved.
         * ====================================================
        </pre> *
         *
         *
         * Note: The fdlibm c code makes use of the language ability to read and write directly
         * to the upper and lower 32-bits of the 64-double. The function performs
         * checking on the upper 32-bits for the magnitude of the two numbers by accessing
         * the exponent and 20 most significant bits of the mantissa. These upper bits
         * are manipulated during scaling and then used to perform extended precision
         * computation of the sum `x^2 + y^2` where the high part of the number has 20-bit
         * precision. Manipulation of direct bits has no equivalent in Java
         * other than use of [Double.toBits] and
         * [Double.fromBits]. To avoid conversion to and from long and double
         * representations this implementation only scales the double representation. The high
         * and low parts of a double for the extended precision computation are extracted
         * using the method of Dekker (1971) to create two 26-bit numbers. This works for sub-normal
         * numbers and reduces the maximum error in comparison to fdlibm hypot which does not
         * use a split number algorithm for sub-normal numbers.
         *
         * @param x Value x
         * @param y Value y
         * @return sqrt(x^2 + y^2)
         * @see hypot
         * @see [fdlibm e_hypot.c](https://www.netlib.org/fdlibm/e_hypot.c)
         *
         * @see [JDK-7130085 : Port fdlibm hypot to Java](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=7130085)
         */
        private fun hypot(x: Double, y: Double): Double {
            // Differences to the fdlibm reference:
            //
            // 1. fdlibm orders the two parts using the magnitude of the upper 32-bits.
            // This incorrectly orders numbers which differ only in the lower 32-bits.
            // This invalidates hypot(x, y) = hypot(y, x) for small sub-normal numbers and a minority
            // of cases of normal numbers. This implementation forces the |x| >= |y| order
            // using the entire 63-bits of the unsigned doubles to ensure the function
            // is commutative.
            //
            // 2. fdlibm computed scaling by directly writing changes to the exponent bits
            // and maintained the high part (ha) during scaling for use in the high
            // precision sum x^2 + y^2. Since exponent scaling cannot be applied to sub-normals
            // the original version dropped the split number representation for sub-normals
            // and can produce maximum errors above 1 ULP for sub-normal numbers.
            // This version uses Dekker's method to split the number. This can be applied to
            // sub-normals and allows dropping the condition to check for sub-normal numbers
            // since all small numbers are handled with a single scaling factor.
            // The effect is increased precision for the majority of sub-normal cases where
            // the implementations compute a different result.
            //
            // 3. An alteration is done here to add an 'else if' instead of a second
            // 'if' statement. Thus you cannot scale down and up at the same time.
            //
            // 4. There is no use of the absolute double value. The magnitude comparison is
            // performed using the long bit representation. The computation x^2+y^2 is
            // insensitive to the sign bit. Thus use of Math.abs(double) is only in edge-case
            // branches.
            //
            // 5. The exponent different to ignore the smaller component has changed from 60 to 54.
            //
            // Original comments from fdlibm are in c style: /* */
            // Extra comments added for reference.
            //
            // Note that the high 32-bits are compared to constants.
            // The lowest 20-bits are the upper bits of the 52-bit mantissa.
            // The next 11-bits are the biased exponent. The sign bit has been cleared.
            // Scaling factors are powers of two for exact scaling.
            // For clarity the values have been refactored to named constants.

            // The mask is used to remove the sign bit.
            val xbits: Long = x.toRawBits() and UNSIGN_MASK
            val ybits: Long = y.toRawBits() and UNSIGN_MASK

            // Order by magnitude: |a| >= |b|
            var a: Double
            var b: Double
            /* High word of x & y */
            val ha: Int
            val hb: Int
            if (ybits > xbits) {
                a = y
                b = x
                ha = (ybits ushr 32).toInt()
                hb = (xbits ushr 32).toInt()
            } else {
                a = x
                b = y
                ha = (xbits ushr 32).toInt()
                hb = (ybits ushr 32).toInt()
            }

            // Check if the smaller part is significant.
            // a^2 is computed in extended precision for an effective mantissa of 106-bits.
            // An exponent difference of 54 is where b^2 will not overlap a^2.
            if (ha - hb > EXP_54) {
                /* a/b > 2**54 */
                // or a is Inf or NaN.
                // No addition of a + b for sNaN.
                return abs(a)
            }
            var rescale = 1.0
            if (ha > EXP_500) {
                /* a > 2^500 */
                if (ha >= EXP_1024) {
                    /* Inf or NaN */
                    // Check b is infinite for the IEEE754 result.
                    // No addition of a + b for sNaN.
                    return if (abs(b) == Double.POSITIVE_INFINITY) Double.POSITIVE_INFINITY else abs(
                        a
                    )
                }
                /* scale a and b by 2^-600 */
                // Before scaling: a in [2^500, 2^1023].
                // After scaling: a in [2^-100, 2^423].
                // After scaling: b in [2^-154, 2^423].
                a *= TWO_POW_NEG_600
                b *= TWO_POW_NEG_600
                rescale =
                    TWO_POW_600
            } else if (hb < EXP_NEG_500) {
                // No special handling of sub-normals.
                // These do not matter when we do not manipulate the exponent bits
                // for scaling the split representation.

                // Intentional comparison with zero.
                if (b == 0.0) {
                    return abs(a)
                }

                /* scale a and b by 2^600 */
                // Effective min exponent of a sub-normal = -1022 - 52 = -1074.
                // Before scaling: b in [2^-1074, 2^-501].
                // After scaling: b in [2^-474, 2^99].
                // After scaling: a in [2^-474, 2^153].
                a *= TWO_POW_600
                b *= TWO_POW_600
                rescale =
                    TWO_POW_NEG_600
            }

            // High precision x^2 + y^2
            return sqrt(
                x2y2(
                    a,
                    b
                )
            ) * rescale
        }

        /**
         * Return `x^2 + y^2` with high accuracy.
         *
         *
         * It is assumed that `2^500 > |x| >= |y| > 2^-500`. Thus there will be no
         * overflow or underflow of the result. The inputs are not assumed to be unsigned.
         *
         *
         * The computation is performed using Dekker's method for extended precision
         * multiplication of x and y and then summation of the extended precision squares.
         *
         * @param x Value x.
         * @param y Value y
         * @return x^2 + y^2
         * @see [
         * Dekker
        ](https://doi.org/10.1007/BF01397083) */
        private fun x2y2(x: Double, y: Double): Double {
            // Note:
            // This method is different from the high-accuracy summation used in fdlibm for hypot.
            // The summation could be any valid computation of x^2+y^2. However since this follows
            // the re-scaling logic in hypot(x, y) the use of high precision has relatively
            // less performance overhead than if used without scaling.
            // The Dekker algorithm is branchless for better performance
            // than the fdlibm method with a maximum ULP error of approximately 0.86.
            //
            // See NUMBERS-143 for analysis.

            // Do a Dekker summation of double length products x*x and y*y
            // (10 multiply and 20 additions).
            val xx = x * x
            val yy = y * y
            // Compute the round-off from the products.
            // With FMA hardware support in JDK 9+ this can be replaced with the much faster:
            // xxLow = Math.fma(x, x, -xx)
            // yyLow = Math.fma(y, y, -yy)
            // Dekker mul12
            val xHigh =
                splitHigh(x)
            val xLow = x - xHigh
            val xxLow =
                squareLow(
                    xLow,
                    xHigh,
                    xx
                )
            // Dekker mul12
            val yHigh =
                splitHigh(y)
            val yLow = y - yHigh
            val yyLow =
                squareLow(
                    yLow,
                    yHigh,
                    yy
                )
            // Dekker add2
            val r = xx + yy
            // Note: The order is important. Assume xx > yy and drop Dekker's conditional
            // check for which is the greater magnitude.
            // s = xx - r + yy + yyLow + xxLow
            // z = r + s
            // zz = r - z + s
            // Here we compute z inline and ignore computing the round-off zz.
            // Note: The round-off could be used with Dekker's sqrt2 method.
            // That adds 7 multiply, 1 division and 19 additions doubling the cost
            // and reducing error to < 0.5 ulp for the final sqrt.
            return xx - r + yy + yyLow + xxLow + r
        }
    }

}
