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

import org.apache.commonskt.numbers.core.Precision
import kotlin.math.abs


/**
 * Provides a generic means to evaluate
 * [continued fractions](https://mathworld.wolfram.com/ContinuedFraction.html).
 *
 *
 * The continued fraction uses the following form for the numerator (`a`) and
 * denominator (`b`) coefficients:
 * <pre>
 * a1
 * b0 + ------------------
 * b1 +      a2
 * -------------
 * b2 +    a3
 * --------
 * b3 + ...
</pre> *
 *
 *
 * Subclasses must provide the [a][.getA] and [b][.getB]
 * coefficients to evaluate the continued fraction.
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
abstract class ContinuedFraction {
    /**
     * Defines the [
 * `n`-th "a" coefficient](https://mathworld.wolfram.com/ContinuedFraction.html) of the continued fraction.
     *
     * @param n Index of the coefficient to retrieve.
     * @param x Evaluation point.
     * @return the coefficient `a<sub>n</sub>`.
     */
    protected abstract fun getA(n: Int, x: Double): Double

    /**
     * Defines the [
 * `n`-th "b" coefficient](https://mathworld.wolfram.com/ContinuedFraction.html) of the continued fraction.
     *
     * @param n Index of the coefficient to retrieve.
     * @param x Evaluation point.
     * @return the coefficient `b<sub>n</sub>`.
     */
    protected abstract fun getB(n: Int, x: Double): Double
    /**
     * Evaluates the continued fraction.
     *
     *
     * The implementation of this method is based on the modified Lentz algorithm as described
     * on page 508 in:
     *
     *
     *
     * I. J. Thompson,  A. R. Barnett (1986).
     * "Coulomb and Bessel Functions of Complex Arguments and Order."
     * Journal of Computational Physics 64, 490-509.
     * [https://www.fresco.org.uk/papers/Thompson-JCP64p490.pdf](https://www.fresco.org.uk/papers/Thompson-JCP64p490.pdf)
     *
     *
     *
     * @param x Point at which to evaluate the continued fraction.
     * @param epsilon Maximum error allowed.
     * @param maxIterations Maximum number of iterations.
     * @return the value of the continued fraction evaluated at `x`.
     * @throws ArithmeticException if the algorithm fails to converge.
     * @throws ArithmeticException if the maximal number of iterations is reached
     * before the expected convergence is achieved.
     */
    fun evaluate(
        x: Double,
        epsilon: Double,
        maxIterations: Int = Int.MAX_VALUE
    ): Double {
        var hPrev = updateIfCloseToZero(getB(0, x))
        var n = 1
        var dPrev = 0.0
        var cPrev = hPrev
        var hN: Double
        while (n <= maxIterations) {
            val a = getA(n, x)
            val b = getB(n, x)
            var dN = updateIfCloseToZero(b + a * dPrev)
            val cN = updateIfCloseToZero(b + a / cPrev)
            dN = 1 / dN
            val deltaN = cN * dN
            hN = hPrev * deltaN
            if (hN.isInfinite()) {
                throw FractionException(
                    "Continued fraction convergents diverged to +/- infinity for value $x"
                )
            }
            if (hN.isNaN()) {
                throw FractionException(
                    "Continued fraction diverged to NaN for value $x"
                )
            }
            if (abs(deltaN - 1) < epsilon) {
                return hN
            }
            dPrev = dN
            cPrev = cN
            hPrev = hN
            ++n
        }
        throw FractionException("maximal count ($maxIterations) exceeded")
    }

    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    companion object {
        /**
         * The value for any number close to zero.
         *
         *
         * "The parameter small should be some non-zero number less than typical values of
         * eps * |b_n|, e.g., 1e-50".
         */
        private const val SMALL = 1e-50

        /**
         * Returns the value, or if close to zero returns a small epsilon.
         *
         *
         * This method is used in Thompson & Barnett to monitor both the numerator and denominator
         * ratios for approaches to zero.
         *
         * @param value the value
         * @return the value (or small epsilon)
         */
        private fun updateIfCloseToZero(value: Double): Double {
            return if (Precision.equals(
                    value,
                    0.0,
                    SMALL
                )
            ) SMALL else value
        }
    }
}
