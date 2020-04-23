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
package com.github.alexandrelombard.commonskt.numbers.gamma

/**
 * [Trigamma function](http://en.wikipedia.org/wiki/Trigamma_function).
 *
 * It is the derivative of the [digamma function][Digamma]:
 * \( \psi_1(x) = \frac{d^2}{dx^2} (\ln \Gamma(x)) \).
 */
object Trigamma {
    /** C limit.  */
    private const val C_LIMIT = 49.0

    /** S limit.  */
    private const val S_LIMIT = 1e-5

    /** Fraction.  */
    private const val F_1_6 = 1.0 / 6

    /** Fraction.  */
    private const val F_1_30 = 1.0 / 30

    /** Fraction.  */
    private const val F_1_42 = 1.0 / 42

    /**
     * Computes the trigamma function.
     *
     * @param x Argument.
     * @return trigamma(x) to within `1e-8` relative or absolute error whichever is larger.
     */
    fun value(x: Double): Double {
        if (x.isNaN() || x.isInfinite()) {
            return x
        }
        if (x > 0 && x <= S_LIMIT) {
            return 1 / (x * x)
        }
        if (x >= C_LIMIT) {
            val inv = 1 / (x * x)
            //  1    1      1       1       1
            //  - + ---- + ---- - ----- + -----
            //  x      2      3       5       7
            //      2 x    6 x    30 x    42 x
            return 1 / x + inv / 2 + inv / x * (F_1_6 - inv * (F_1_30 + F_1_42 * inv))
        }
        return value(x + 1) + 1 / (x * x)
    }
}