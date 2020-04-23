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

package com.github.alexandrelombard.commonskt.numbers.gamma

import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.tan

/**
 * [Digamma function](http://en.wikipedia.org/wiki/Digamma_function).
 *
 *
 * It is defined as the logarithmic derivative of the \( \Gamma \)
 * ([Gamma]) function:
 * \( \frac{d}{dx}(\ln \Gamma(x)) = \frac{\Gamma^\prime(x)}{\Gamma(x)} \).
 *
 *
 * @see Gamma
 */
object Digamma {
    /** [Euler-Mascheroni constant](http://en.wikipedia.org/wiki/Euler-Mascheroni_constant).  */
    private const val GAMMA = 0.577215664901532860606512090082

    /** C limit.  */
    private const val C_LIMIT = 49.0

    /** S limit.  */
    private const val S_LIMIT = 1e-5

    /** Fraction.  */
    private const val F_M1_12 = -1.0 / 12

    /** Fraction.  */
    private const val F_1_120 = 1.0 / 120

    /** Fraction.  */
    private const val F_M1_252 = -1.0 / 252

    /**
     * Computes the digamma function.
     *
     * This is an independently written implementation of the algorithm described in
     * [Jose Bernardo,
 * Algorithm AS 103: Psi (Digamma) Function, Applied Statistics, 1976](http://www.uv.es/~bernardo/1976AppStatist.pdf).
     * A [
 * reflection formula](https://en.wikipedia.org/wiki/Digamma_function#Reflection_formula) is incorporated to improve performance on negative values.
     *
     * Some of the constants have been changed to increase accuracy at the moderate
     * expense of run-time.  The result should be accurate to within `1e-8`.
     * relative tolerance for `0 < x < 1e-5`  and within `1e-8` absolute
     * tolerance otherwise.
     *
     * @param x Argument.
     * @return digamma(x) to within `1e-8` relative or absolute error whichever
     * is larger.
     */
    fun value(x: Double): Double {
        var x = x
        if (x.isNaN() || x.isInfinite()) {
            return x
        }
        var digamma = 0.0
        if (x < 0) {
            // Use reflection formula to fall back into positive values.
            digamma -= PI / tan(PI * x)
            x = 1 - x
        }
        if (x > 0 && x <= S_LIMIT) {
            // Use method 5 from Bernardo AS103, accurate to O(x).
            return digamma - GAMMA - 1 / x
        }
        while (x < C_LIMIT) {
            digamma -= 1 / x
            x += 1.0
        }

        // Use method 4, accurate to O(1/x^8)
        val inv = 1 / (x * x)
        //            1       1        1         1
        // log(x) -  --- - ------ + ------- - -------
        //           2 x   12 x^2   120 x^4   252 x^6
        digamma += ln(x) - 0.5 / x + inv * (F_M1_12 + inv * (F_1_120 + F_M1_252 * inv))
        return digamma
    }
}
