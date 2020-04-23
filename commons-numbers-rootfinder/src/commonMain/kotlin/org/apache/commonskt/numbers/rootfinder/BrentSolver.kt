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
package org.apache.commonskt.numbers.rootfinder

import kotlin.math.abs

import org.apache.commonskt.numbers.core.Precision

/**
 * This class implements the [Brent algorithm](http://mathworld.wolfram.com/BrentsMethod.html) for finding zeros of real
 * univariate functions.
 * The function should be continuous but not necessarily smooth.
 * The `solve` method returns a zero `x` of the function `f`
 * in the given interval `[a, b]` to within a tolerance
 * `2 eps abs(x) + t` where `eps` is the relative accuracy and
 * `t` is the absolute accuracy.
 *
 * The given interval must bracket the root.
 *
 *
 * The reference implementation is given in chapter 4 of
 * <blockquote>
 * **Algorithms for Minimization Without Derivatives**,
 * *Richard P. Brent*,
 * Dover, 2002
 * </blockquote>
 */
class BrentSolver
/**
 * Construct a solver.
 *
 * @param relativeAccuracy Relative accuracy.
 * @param absoluteAccuracy Absolute accuracy.
 * @param functionValueAccuracy Function value accuracy.
 */(
    /** Relative accuracy.  */
    private val relativeAccuracy: Double,
    /** Absolute accuracy.  */
    private val absoluteAccuracy: Double,
    /** Function accuracy.  */
    private val functionValueAccuracy: Double
) {

    /**
     * Search the function's zero within the given interval.
     *
     * @param func Function to solve.
     * @param min Lower bound.
     * @param max Upper bound.
     * @return the root.
     * @throws IllegalArgumentException if `min > max`.
     * @throws IllegalArgumentException if the given interval does
     * not bracket the root.
     */
    fun findRoot(
        func: (Double)->Double,
        min: Double,
        max: Double
    ): Double {
        return findRoot(func, min, 0.5 * (min + max), max)
    }

    /**
     * Search the function's zero within the given interval,
     * starting from the given estimate.
     *
     * @param func Function to solve.
     * @param min Lower bound.
     * @param initial Initial guess.
     * @param max Upper bound.
     * @return the root.
     * @throws IllegalArgumentException if `min > max` or
     * `initial` is not in the `[min, max]` interval.
     * @throws IllegalArgumentException if the given interval does
     * not bracket the root.
     */
    fun findRoot(
        func: (Double)->Double,
        min: Double,
        initial: Double,
        max: Double
    ): Double {
        if (min > max) {
            throw SolverException(
                SolverException.TOO_LARGE(
                    min,
                    max
                )
            )
        }
        if (initial < min ||
            initial > max
        ) {
            throw SolverException(
                SolverException.OUT_OF_RANGE(
                    initial,
                    min,
                    max
                )
            )
        }

        // Return the initial guess if it is good enough.
        val yInitial: Double = func.invoke(initial)
        if (abs(yInitial) <= functionValueAccuracy) {
            return initial
        }

        // Return the first endpoint if it is good enough.
        val yMin: Double = func.invoke(min)
        if (abs(yMin) <= functionValueAccuracy) {
            return min
        }

        // Reduce interval if min and initial bracket the root.
        if (yInitial * yMin < 0) {
            return brent(func, min, initial, yMin, yInitial)
        }

        // Return the second endpoint if it is good enough.
        val yMax: Double = func.invoke(max)
        if (abs(yMax) <= functionValueAccuracy) {
            return max
        }

        // Reduce interval if initial and max bracket the root.
        if (yInitial * yMax < 0) {
            return brent(func, initial, max, yInitial, yMax)
        }
        throw SolverException(
            SolverException.BRACKETING(
                min,
                yMin,
                max,
                yMax
            )
        )
    }

    /**
     * Search for a zero inside the provided interval.
     * This implementation is based on the algorithm described at page 58 of
     * the book
     * <blockquote>
     * **Algorithms for Minimization Without Derivatives**,
     * *Richard P. Brent*,
     * Dover 0-486-41998-3
    </blockquote> *
     *
     * @param func Function to solve.
     * @param lo Lower bound of the search interval.
     * @param hi Higher bound of the search interval.
     * @param fLo Function value at the lower bound of the search interval.
     * @param fHi Function value at the higher bound of the search interval.
     * @return the value where the function is zero.
     */
    private fun brent(
        func: (Double)->Double,
        lo: Double, hi: Double,
        fLo: Double, fHi: Double
    ): Double {
        var a = lo
        var fa = fLo
        var b = hi
        var fb = fHi
        var c = a
        var fc = fa
        var d = b - a
        var e = d
        val t = absoluteAccuracy
        val eps = relativeAccuracy
        while (true) {
            if (abs(fc) < abs(fb)) {
                a = b
                b = c
                c = a
                fa = fb
                fb = fc
                fc = fa
            }
            val tol: Double = 2 * eps * abs(b) + t
            val m = 0.5 * (c - b)
            if (abs(m) <= tol ||
                Precision.equals(fb, 0.0)
            ) {
                return b
            }
            if (abs(e) < tol ||
                abs(fa) <= abs(fb)
            ) {
                // Force bisection.
                d = m
                e = d
            } else {
                var s = fb / fa
                var p: Double
                var q: Double
                // The equality test (a == c) is intentional,
                // it is part of the original Brent's method and
                // it should NOT be replaced by proximity test.
                if (a == c) {
                    // Linear interpolation.
                    p = 2 * m * s
                    q = 1 - s
                } else {
                    // Inverse quadratic interpolation.
                    q = fa / fc
                    val r = fb / fc
                    p = s * (2 * m * q * (q - r) - (b - a) * (r - 1))
                    q = (q - 1) * (r - 1) * (s - 1)
                }
                if (p > 0) {
                    q = -q
                } else {
                    p = -p
                }
                s = e
                e = d
                if (p >= 1.5 * m * q - abs(tol * q) ||
                    p >= abs(0.5 * s * q)
                ) {
                    // Inverse quadratic interpolation gives a value
                    // in the wrong direction, or progress is slow.
                    // Fall back to bisection.
                    d = m
                    e = d
                } else {
                    d = p / q
                }
            }
            a = b
            fa = fb
            if (abs(d) > tol) {
                b += d
            } else if (m > 0) {
                b += tol
            } else {
                b -= tol
            }
            fb = func.invoke(b)
            if (fb > 0 && fc > 0 ||
                fb <= 0 && fc <= 0
            ) {
                c = a
                fc = fa
                d = b - a
                e = d
            }
        }
    }

}