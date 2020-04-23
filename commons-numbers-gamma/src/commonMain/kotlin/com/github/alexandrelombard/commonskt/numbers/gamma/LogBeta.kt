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

import kotlin.math.*

/**
 * Computes \( log_e \Beta(p, q) \).
 *
 *
 * This class is immutable.
 *
 */
object LogBeta {
    /** The threshold value of 10 where the series expansion of the Δ function applies.  */
    private const val TEN = 10.0

    /** The threshold value of 2 for algorithm switch.  */
    private const val TWO = 2.0

    /** The threshold value of 1000 for algorithm switch.  */
    private const val THOUSAND = 1000.0

    /** The constant value of ½log 2π.  */
    private const val HALF_LOG_TWO_PI = 0.9189385332046727

    /**
     * The coefficients of the series expansion of the Δ function. This function
     * is defined as follows:
     * <pre>
     * Δ(x) = log Γ(x) - (x - 0.5) log a + a - 0.5 log 2π,
    </pre> *
     *
     *
     * See equation (23) in Didonato and Morris (1992). The series expansion,
     * which applies for x ≥ 10, reads
     *
     * <pre>
     * 14
     * ====
     * 1  \                2 n
     * Δ(x) = ---  >    d  (10 / x)
     * x  /      n
     * ====
     * n = 0
    </pre> *
     */
    private val DELTA = doubleArrayOf(
        .833333333333333333333333333333E-01,
        -.277777777777777777777777752282E-04,
        .793650793650793650791732130419E-07,
        -.595238095238095232389839236182E-09,
        .841750841750832853294451671990E-11,
        -.191752691751854612334149171243E-12,
        .641025640510325475730918472625E-14,
        -.295506514125338232839867823991E-15,
        .179643716359402238723287696452E-16,
        -.139228964661627791231203060395E-17,
        .133802855014020915603275339093E-18,
        -.154246009867966094273710216533E-19,
        .197701992980957427278370133333E-20,
        -.234065664793997056856992426667E-21,
        .171348014966398575409015466667E-22
    )

    /**
     * Returns the value of Δ(b) - Δ(a + b), with 0 ≤ a ≤ b and b ≥ 10. Based
     * on equations (26), (27) and (28) in Didonato and Morris (1992).
     *
     * @param a First argument.
     * @param b Second argument.
     * @return the value of `Delta(b) - Delta(a + b)`
     * @throws IllegalArgumentException if `a < 0` or `a > b`
     * @throws IllegalArgumentException if `b < 10`
     */
    private fun deltaMinusDeltaSum(
        a: Double,
        b: Double
    ): Double {
        if (a < 0 ||
            a > b
        ) {
            throw GammaException(
                GammaException.OUT_OF_RANGE(
                    a,
                    0.0,
                    b
                )
            )
        }
        if (b < TEN) {
            throw GammaException(
                GammaException.OUT_OF_RANGE(
                    b,
                    TEN,
                    Double.POSITIVE_INFINITY
                )
            )
        }
        val h = a / b
        val p = h / (1 + h)
        val q = 1 / (1 + h)
        val q2 = q * q
        /*
         * s[i] = 1 + q + ... - q**(2 * i)
         */
        val s = DoubleArray(DELTA.size)
        s[0] = 1.0
        for (i in 1 until s.size) {
            s[i] = 1 + (q + q2 * s[i - 1])
        }
        /*
         * w = Delta(b) - Delta(a + b)
         */
        val sqrtT = 10 / b
        val t = sqrtT * sqrtT
        var w = DELTA[DELTA.size - 1] * s[s.size - 1]
        for (i in DELTA.size - 2 downTo 0) {
            w = t * w + DELTA[i] * s[i]
        }
        return w * p / b
    }

    /**
     * Returns the value of Δ(p) + Δ(q) - Δ(p + q), with p, q ≥ 10.
     * Based on the *NSWC Library of Mathematics Subroutines* implementation,
     * `DBCORR`.
     *
     * @param p First argument.
     * @param q Second argument.
     * @return the value of `Delta(p) + Delta(q) - Delta(p + q)`.
     * @throws IllegalArgumentException if `p < 10` or `q < 10`.
     */
    private fun sumDeltaMinusDeltaSum(
        p: Double,
        q: Double
    ): Double {
        if (p < TEN) {
            throw GammaException(
                GammaException.OUT_OF_RANGE(
                    p,
                    TEN,
                    Double.POSITIVE_INFINITY
                )
            )
        }
        if (q < TEN) {
            throw GammaException(
                GammaException.OUT_OF_RANGE(
                    q,
                    TEN,
                    Double.POSITIVE_INFINITY
                )
            )
        }
        val a: Double = min(p, q)
        val b: Double = max(p, q)
        val sqrtT = 10 / a
        val t = sqrtT * sqrtT
        var z = DELTA[DELTA.size - 1]
        for (i in DELTA.size - 2 downTo 0) {
            z = t * z + DELTA[i]
        }
        return z / a + deltaMinusDeltaSum(
            a,
            b
        )
    }

    /**
     * Returns the value of `log B(p, q)` for `0 ≤ x ≤ 1` and `p, q > 0`.
     * Based on the *NSWC Library of Mathematics Subroutines* implementation,
     * `DBETLN`.
     *
     * @param p First argument.
     * @param q Second argument.
     * @return the value of `log(Beta(p, q))`, `NaN` if
     * `p <= 0` or `q <= 0`.
     */
    fun value(
        p: Double,
        q: Double
    ): Double {
        if (p.isNaN() ||
            q.isNaN() || p <= 0 || q <= 0
        ) {
            return Double.NaN
        }
        val a: Double = min(p, q)
        val b: Double = max(p, q)
        return if (a >= TEN) {
            val w =
                sumDeltaMinusDeltaSum(
                    a,
                    b
                )
            val h = a / b
            val c = h / (1 + h)
            val u: Double = -(a - 0.5) * ln(c)
            val v: Double = b * ln1p(h)
            if (u <= v) {
                -0.5 * ln(b) + HALF_LOG_TWO_PI + w - u - v
            } else {
                -0.5 * ln(b) + HALF_LOG_TWO_PI + w - v - u
            }
        } else if (a > TWO) {
            if (b > THOUSAND) {
                val n = floor(a - 1).toInt()
                var prod = 1.0
                var ared = a
                for (i in 0 until n) {
                    ared -= 1.0
                    prod *= ared / (1 + ared / b)
                }
                ln(prod) - n * ln(b) +
                        (LogGamma.value(ared) +
                                logGammaMinusLogGammaSum(
                                    ared,
                                    b
                                ))
            } else {
                var prod1 = 1.0
                var ared = a
                while (ared > 2) {
                    ared -= 1.0
                    val h = ared / b
                    prod1 *= h / (1 + h)
                }
                if (b < TEN) {
                    var prod2 = 1.0
                    var bred = b
                    while (bred > 2) {
                        bred -= 1.0
                        prod2 *= bred / (ared + bred)
                    }
                    ln(prod1) +
                            ln(prod2) +
                            (LogGamma.value(ared) +
                                    (LogGamma.value(
                                        bred
                                    ) -
                                            LogGammaSum.value(
                                                ared,
                                                bred
                                            )))
                } else {
                    ln(prod1) +
                            LogGamma.value(ared) +
                            logGammaMinusLogGammaSum(
                                ared,
                                b
                            )
                }
            }
        } else if (a >= 1) {
            if (b > TWO) {
                if (b < TEN) {
                    var prod = 1.0
                    var bred = b
                    while (bred > 2) {
                        bred -= 1.0
                        prod *= bred / (a + bred)
                    }
                    ln(prod) +
                            (LogGamma.value(a) +
                                    (LogGamma.value(
                                        bred
                                    ) -
                                            LogGammaSum.value(
                                                a,
                                                bred
                                            )))
                } else {
                    LogGamma.value(a) +
                            logGammaMinusLogGammaSum(
                                a,
                                b
                            )
                }
            } else {
                LogGamma.value(a) +
                        LogGamma.value(b) -
                        LogGammaSum.value(a, b)
            }
        } else {
            if (b >= TEN) {
                LogGamma.value(a) +
                        logGammaMinusLogGammaSum(
                            a,
                            b
                        )
            } else {
                // The original NSWC implementation was
                //   LogGamma.value(a) + (LogGamma.value(b) - LogGamma.value(a + b));
                // but the following command turned out to be more accurate.
                ln(
                    Gamma.value(a) * Gamma.value(
                        b
                    ) /
                            Gamma.value(a + b)
                )
            }
        }
    }

    /**
     * Returns the value of log[Γ(b) / Γ(a + b)] for a ≥ 0 and b ≥ 10.
     * Based on the *NSWC Library of Mathematics Subroutines* implementation,
     * `DLGDIV`.
     *
     * @param a First argument.
     * @param b Second argument.
     * @return the value of `log(Gamma(b) / Gamma(a + b))`.
     * @throws IllegalArgumentException if `a < 0` or `b < 10`.
     */
    private fun logGammaMinusLogGammaSum(
        a: Double,
        b: Double
    ): Double {
        if (a < 0) {
            throw GammaException(
                GammaException.OUT_OF_RANGE(
                    a,
                    0.0,
                    Double.POSITIVE_INFINITY
                )
            )
        }
        if (b < TEN) {
            throw GammaException(
                GammaException.OUT_OF_RANGE(
                    b,
                    TEN,
                    Double.POSITIVE_INFINITY
                )
            )
        }

        /*
         * d = a + b - 0.5
         */
        val d: Double
        val w: Double
        if (a <= b) {
            d = b + (a - 0.5)
            w = deltaMinusDeltaSum(a, b)
        } else {
            d = a + (b - 0.5)
            w = deltaMinusDeltaSum(b, a)
        }
        val u: Double = d * ln1p(a / b)
        val v: Double = a * (ln(b) - 1)
        return if (u <= v) w - u - v else w - v - u
    }
}