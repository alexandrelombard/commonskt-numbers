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
@file:Suppress("NAME_SHADOWING")

package com.github.alexandrelombard.commonskt.numbers.core

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

import org.apache.commonskt.math.BigInteger
import org.apache.commonskt.math.multiplyExact

/**
 * Some useful, arithmetics related, additions to the built-in functions in
 * [Math].
 *
 */
object ArithmeticUtils {
    /** Overflow gcd exception message for 2^63.  */
    private const val OVERFLOW_GCD_MESSAGE_2_POWER_63 = "overflow: gcd({0}, {1}) is 2^63"

    /** Negative exponent exception message part 1.  */
    private const val NEGATIVE_EXPONENT_1 = "negative exponent ({"

    /** Negative exponent exception message part 2.  */
    private const val NEGATIVE_EXPONENT_2 = "})"

    /**
     * Computes the greatest common divisor of the absolute value of two
     * numbers, using a modified version of the "binary gcd" method.
     * See Knuth 4.5.2 algorithm B.
     * The algorithm is due to Josef Stein (1961).
     * <br></br>
     * Special cases:
     *
     *  * The invocations
     * `gcd(Integer.MIN_VALUE, Integer.MIN_VALUE)`,
     * `gcd(Integer.MIN_VALUE, 0)` and
     * `gcd(0, Integer.MIN_VALUE)` throw an
     * `ArithmeticException`, because the result would be 2^31, which
     * is too large for an int value.
     *  * The result of `gcd(x, x)`, `gcd(0, x)` and
     * `gcd(x, 0)` is the absolute value of `x`, except
     * for the special cases above.
     *  * The invocation `gcd(0, 0)` is the only one which returns
     * `0`.
     *
     *
     * @param p Number.
     * @param q Number.
     * @return the greatest common divisor (never negative).
     * @throws ArithmeticException if the result cannot be represented as
     * a non-negative `int` value.
     */
    @ExperimentalStdlibApi
    fun gcd(p: Int, q: Int): Int {
        // Perform the gcd algorithm on negative numbers, so that -2^31 does not
        // need to be handled separately
        var a = if (p > 0) -p else p
        var b = if (q > 0) -q else q
        val negatedGcd: Int
        if (a == 0) {
            negatedGcd = b
        } else if (b == 0) {
            negatedGcd = a
        } else {
            // Make "a" and "b" odd, keeping track of common power of 2.
            val aTwos: Int = a.countTrailingZeroBits()
            val bTwos: Int = b.countTrailingZeroBits()
            a = a shr aTwos
            b = b shr bTwos
            val shift: Int = min(aTwos, bTwos)

            // "a" and "b" are negative and odd.
            // If a < b then "gdc(a, b)" is equal to "gcd(a - b, b)".
            // If a > b then "gcd(a, b)" is equal to "gcd(b - a, a)".
            // Hence, in the successive iterations:
            //  "a" becomes the negative absolute difference of the current values,
            //  "b" becomes that value of the two that is closer to zero.
            while (a != b) {
                val delta = a - b
                b = max(a, b)
                a = if (delta > 0) -delta else delta

                // Remove any power of 2 in "a" ("b" is guaranteed to be odd).
                a = a shr a.countTrailingZeroBits()
            }

            // Recover the common power of 2.
            negatedGcd = a shl shift
        }
        return if (negatedGcd == Int.MIN_VALUE) {
            throw NumbersArithmeticException(
                "overflow: gcd($p, $q) is 2^31"
            )
        } else {
            -negatedGcd
        }
    }

    /**
     *
     *
     * Gets the greatest common divisor of the absolute value of two numbers,
     * using the "binary gcd" method which avoids division and modulo
     * operations. See Knuth 4.5.2 algorithm B. This algorithm is due to Josef
     * Stein (1961).
     *
     * Special cases:
     *
     *  * The invocations
     * `gcd(Long.MIN_VALUE, Long.MIN_VALUE)`,
     * `gcd(Long.MIN_VALUE, 0L)` and
     * `gcd(0L, Long.MIN_VALUE)` throw an
     * `ArithmeticException`, because the result would be 2^63, which
     * is too large for a long value.
     *  * The result of `gcd(x, x)`, `gcd(0L, x)` and
     * `gcd(x, 0L)` is the absolute value of `x`, except
     * for the special cases above.
     *  * The invocation `gcd(0L, 0L)` is the only one which returns
     * `0L`.
     *
     *
     * @param p Number.
     * @param q Number.
     * @return the greatest common divisor, never negative.
     * @throws ArithmeticException if the result cannot be represented as
     * a non-negative `long` value.
     */
    fun gcd(p: Long, q: Long): Long {
        var u = p
        var v = q
        if (u == 0L || v == 0L) {
            if (u == Long.MIN_VALUE || v == Long.MIN_VALUE) {
                throw NumbersArithmeticException(
                    "overflow: gcd($p, $q) is 2^63"
                )
            }
            return abs(u) + abs(v)
        }
        // keep u and v negative, as negative integers range down to
        // -2^63, while positive numbers can only be as large as 2^63-1
        // (i.e. we can't necessarily negate a negative number without
        // overflow)
        /* assert u!=0 && v!=0; */if (u > 0) {
            u = -u
        } // make u negative
        if (v > 0) {
            v = -v
        } // make v negative
        // B1. [Find power of 2]
        var k = 0
        while (u and 1 == 0L && v and 1 == 0L && k < 63) { // while u and v are
            // both even...
            u /= 2
            v /= 2
            k++ // cast out twos.
        }
        if (k == 63) {
            throw NumbersArithmeticException(
                "overflow: gcd($p, $q) is 2^63"
            )
        }
        // B2. Initialize: u and v have been divided by 2^k and at least
        // one is odd.
        var t = if (u and 1 == 1L) v else -(u / 2) /* B3 */
        // t negative: u was odd, v may be even (t replaces v)
        // t positive: u was even, v is odd (t replaces u)
        do {
            /* assert u<0 && v<0; */
            // B4/B3: cast out twos from t.
            while (t and 1 == 0L) { // while t is even..
                t /= 2 // cast out twos
            }
            // B5 [reset max(u,v)]
            if (t > 0) {
                u = -t
            } else {
                v = t
            }
            // B6/B3. at this point both u and v should be odd.
            t = (v - u) / 2
            // |u| larger: t positive (replace u)
            // |v| larger: t negative (replace v)
        } while (t != 0L)
        return -u * (1L shl k) // gcd is u*2^k
    }

    /**
     *
     *
     * Returns the least common multiple of the absolute value of two numbers,
     * using the formula `lcm(a,b) = (a / gcd(a,b)) * b`.
     *
     * Special cases:
     *
     *  * The invocations `lcm(Integer.MIN_VALUE, n)` and
     * `lcm(n, Integer.MIN_VALUE)`, where `abs(n)` is a
     * power of 2, throw an `ArithmeticException`, because the result
     * would be 2^31, which is too large for an int value.
     *  * The result of `lcm(0, x)` and `lcm(x, 0)` is
     * `0` for any `x`.
     *
     *
     * @param a Number.
     * @param b Number.
     * @return the least common multiple, never negative.
     * @throws ArithmeticException if the result cannot be represented as
     * a non-negative `int` value.
     */
    @ExperimentalStdlibApi
    fun lcm(a: Int, b: Int): Int {
        if (a == 0 || b == 0) {
            return 0
        }
        val lcm: Int = abs(multiplyExact(a / gcd(
            a,
            b
        ), b))
        if (lcm == Int.MIN_VALUE) {
            throw NumbersArithmeticException(
                "overflow: lcm($a, $b) is 2^31"
            )
        }
        return lcm
    }

    /**
     *
     *
     * Returns the least common multiple of the absolute value of two numbers,
     * using the formula `lcm(a,b) = (a / gcd(a,b)) * b`.
     *
     * Special cases:
     *
     *  * The invocations `lcm(Long.MIN_VALUE, n)` and
     * `lcm(n, Long.MIN_VALUE)`, where `abs(n)` is a
     * power of 2, throw an `ArithmeticException`, because the result
     * would be 2^63, which is too large for an int value.
     *  * The result of `lcm(0L, x)` and `lcm(x, 0L)` is
     * `0L` for any `x`.
     *
     *
     * @param a Number.
     * @param b Number.
     * @return the least common multiple, never negative.
     * @throws ArithmeticException if the result cannot be represented
     * as a non-negative `long` value.
     */
    fun lcm(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) {
            return 0
        }
        val lcm: Long = abs(multiplyExact(a / gcd(
            a,
            b
        ), b))
        if (lcm == Long.MIN_VALUE) {
            throw NumbersArithmeticException(
                "overflow: lcm($a, $b) is 2^63"
            )
        }
        return lcm
    }

    /**
     * Raise an int to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return \( k^e \)
     * @throws IllegalArgumentException if `e < 0`.
     * @throws ArithmeticException if the result would overflow.
     */
    fun pow(
        k: Int,
        e: Int
    ): Int {
        if (e < 0) {
            throw IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2)
        }
        var exp = e
        var result = 1
        var k2p = k
        while (true) {
            if (exp and 0x1 != 0) {
                result = multiplyExact(result, k2p)
            }
            exp = exp shr 1
            if (exp == 0) {
                break
            }
            k2p = multiplyExact(k2p, k2p)
        }
        return result
    }

    /**
     * Raise a long to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return \( k^e \)
     * @throws IllegalArgumentException if `e < 0`.
     * @throws ArithmeticException if the result would overflow.
     */
    fun pow(
        k: Long,
        e: Int
    ): Long {
        if (e < 0) {
            throw IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2)
        }
        var exp = e
        var result: Long = 1
        var k2p = k
        while (true) {
            if (exp and 0x1 != 0) {
                result = multiplyExact(result, k2p)
            }
            exp = exp shr 1
            if (exp == 0) {
                break
            }
            k2p = multiplyExact(k2p, k2p)
        }
        return result
    }

    /**
     * Raise a BigInteger to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws IllegalArgumentException if `e < 0`.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    fun pow(k: BigInteger, e: Int): BigInteger {
        if (e < 0) {
            throw IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2)
        }
        return k.pow(e)
    }

    /**
     * Raise a BigInteger to a long power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws IllegalArgumentException if `e < 0`.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    fun pow(k: BigInteger, e: Long): BigInteger {
        if (e < 0) {
            throw IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2)
        }
        var exp = e
        var result: BigInteger = BigInteger.ONE
        var k2p: BigInteger = k
        while (exp != 0L) {
            if (exp and 0x1 != 0L) {
                result = result.multiply(k2p)
            }
            k2p = k2p.multiply(k2p)
            exp = exp shr 1
        }
        return result
    }

    /**
     * Raise a BigInteger to a BigInteger power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws IllegalArgumentException if `e < 0`.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    fun pow(k: BigInteger, e: BigInteger): BigInteger {
        if (e.compareTo(BigInteger.ZERO) < 0) {
            throw IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2)
        }
        var exp: BigInteger = e
        var result: BigInteger = BigInteger.ONE
        var k2p: BigInteger = k
        while (BigInteger.ZERO != exp) {
            if (exp.testBit(0)) {
                result = result.multiply(k2p)
            }
            k2p = k2p.multiply(k2p)
            exp = exp.shiftRight(1)
        }
        return result
    }

    /**
     * Returns true if the argument is a power of two.
     *
     * @param n the number to test
     * @return true if the argument is a power of two
     */
    fun isPowerOfTwo(n: Long): Boolean {
        return n > 0 && n and n - 1 == 0L
    }

    /**
     * Returns the unsigned remainder from dividing the first argument
     * by the second where each argument and the result is interpreted
     * as an unsigned value.
     *
     * This method does not use the `long` datatype.
     *
     * @param dividend the value to be divided
     * @param divisor the value doing the dividing
     * @return the unsigned remainder of the first argument divided by
     * the second argument.
     */
    fun remainderUnsigned(dividend: Int, divisor: Int): Int {
        var dividend = dividend
        if (divisor >= 0) {
            if (dividend >= 0) {
                return dividend % divisor
            }
            // The implementation is a Java port of algorithm described in the book
            // "Hacker's Delight" (section "Unsigned short division from signed division").
            val q = (dividend ushr 1) / divisor shl 1
            dividend -= q * divisor
            if (dividend < 0 || dividend >= divisor) {
                dividend -= divisor
            }
            return dividend
        }
        return if (dividend >= 0 || dividend < divisor) dividend else dividend - divisor
    }

    /**
     * Returns the unsigned remainder from dividing the first argument
     * by the second where each argument and the result is interpreted
     * as an unsigned value.
     *
     * This method does not use the `BigInteger` datatype.
     *
     * @param dividend the value to be divided
     * @param divisor the value doing the dividing
     * @return the unsigned remainder of the first argument divided by
     * the second argument.
     */
    fun remainderUnsigned(dividend: Long, divisor: Long): Long {
        var dividend = dividend
        if (divisor >= 0L) {
            if (dividend >= 0L) {
                return dividend % divisor
            }
            // The implementation is a Java port of algorithm described in the book
            // "Hacker's Delight" (section "Unsigned short division from signed division").
            val q = (dividend ushr 1) / divisor shl 1
            dividend -= q * divisor
            if (dividend < 0L || dividend >= divisor) {
                dividend -= divisor
            }
            return dividend
        }
        return if (dividend >= 0L || dividend < divisor) dividend else dividend - divisor
    }

    /**
     * Returns the unsigned quotient of dividing the first argument by
     * the second where each argument and the result is interpreted as
     * an unsigned value.
     *
     * Note that in two's complement arithmetic, the three other
     * basic arithmetic operations of add, subtract, and multiply are
     * bit-wise identical if the two operands are regarded as both
     * being signed or both being unsigned. Therefore separate `addUnsigned`, etc. methods are not provided.
     *
     * This method does not use the `long` datatype.
     *
     * @param dividend the value to be divided
     * @param divisor the value doing the dividing
     * @return the unsigned quotient of the first argument divided by
     * the second argument
     */
    fun divideUnsigned(dividend: Int, divisor: Int): Int {
        var dividend = dividend
        if (divisor >= 0) {
            if (dividend >= 0) {
                return dividend / divisor
            }
            // The implementation is a Java port of algorithm described in the book
            // "Hacker's Delight" (section "Unsigned short division from signed division").
            var q = (dividend ushr 1) / divisor shl 1
            dividend -= q * divisor
            if (dividend < 0L || dividend >= divisor) {
                q++
            }
            return q
        }
        return if (dividend >= 0 || dividend < divisor) 0 else 1
    }

    /**
     * Returns the unsigned quotient of dividing the first argument by
     * the second where each argument and the result is interpreted as
     * an unsigned value.
     *
     * Note that in two's complement arithmetic, the three other
     * basic arithmetic operations of add, subtract, and multiply are
     * bit-wise identical if the two operands are regarded as both
     * being signed or both being unsigned. Therefore separate `addUnsigned`, etc. methods are not provided.
     *
     * This method does not use the `BigInteger` datatype.
     *
     * @param dividend the value to be divided
     * @param divisor the value doing the dividing
     * @return the unsigned quotient of the first argument divided by
     * the second argument.
     */
    fun divideUnsigned(dividend: Long, divisor: Long): Long {
        var dividend = dividend
        if (divisor >= 0L) {
            if (dividend >= 0L) {
                return dividend / divisor
            }
            // The implementation is a Java port of algorithm described in the book
            // "Hacker's Delight" (section "Unsigned short division from signed division").
            var q = (dividend ushr 1) / divisor shl 1
            dividend -= q * divisor
            if (dividend < 0L || dividend >= divisor) {
                q++
            }
            return q
        }
        return if (dividend >= 0L || dividend < divisor) 0L else 1L
    }

    /**
     * Exception.
     */
    private class NumbersArithmeticException
    /**
     * Constructor with a specific message.
     *
     * @param message Message pattern providing the specific context of
     * the error.
     * @param args Arguments.
     */
    internal constructor(message: String) : ArithmeticException(message)
}