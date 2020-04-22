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
package org.apache.commonskt.numbers.gamma

import org.apache.commonskt.numbers.gamma.Erf.value

/**
 * Computes the difference between [error function values][Erf].
 */
object ErfDifference {
    /**
     * This number solves `erf(x) = 0.5` within 1 ulp.
     * More precisely, the current implementations of
     * [Erf.value] and [Erfc.value] satisfy:
     *
     *  * `Erf.value(X_CRIT) < 0.5`,
     *  * `Erf.value(Math.nextUp(X_CRIT) > 0.5`,
     *  * `Erfc.value(X_CRIT) = 0.5`, and
     *  * `Erfc.value(Math.nextUp(X_CRIT) < 0.5`
     *
     */
    private const val X_CRIT = 0.4769362762044697

    /**
     * The implementation uses either [Erf] or [Erfc],
     * depending on which provides the most precise result.
     *
     * @param x1 First value.
     * @param x2 Second value.
     * @return [Erf.value(x2) - Erf.value(x1)][Erf.value].
     * @throws ArithmeticException if the algorithm fails to converge.
     */
    fun value(
        x1: Double,
        x2: Double
    ): Double {
        return if (x1 > x2) {
            -value(x2, x1)
        } else {
            if (x1 < -X_CRIT) {
                if (x2 < 0) {
                    Erfc.value(-x2) - Erfc.value(-x1)
                } else {
                    value(x2) - value(x1)
                }
            } else {
                if (x2 > X_CRIT &&
                    x1 > 0
                ) {
                    Erfc.value(x1) - Erfc.value(x2)
                } else {
                    value(x2) - value(x1)
                }
            }
        }
    }
}
