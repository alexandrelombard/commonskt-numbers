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
 * Function \( \frac{1}{\Gamma(1 + x)} - 1 \).
 *
 * Class is immutable.
 */
internal object InvGamma1pm1 {
    /*
     * Constants copied from DGAM1 in the NSWC library.
     */
    /** The constant `A0` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_A0 = .611609510448141581788E-08

    /** The constant `A1` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_A1 = .624730830116465516210E-08

    /** The constant `B1` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_B1 = .203610414066806987300E+00

    /** The constant `B2` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_B2 = .266205348428949217746E-01

    /** The constant `B3` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_B3 = .493944979382446875238E-03

    /** The constant `B4` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_B4 = -.851419432440314906588E-05

    /** The constant `B5` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_B5 = -.643045481779353022248E-05

    /** The constant `B6` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_B6 = .992641840672773722196E-06

    /** The constant `B7` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_B7 = -.607761895722825260739E-07

    /** The constant `B8` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_B8 = .195755836614639731882E-09

    /** The constant `P0` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_P0 = .6116095104481415817861E-08

    /** The constant `P1` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_P1 = .6871674113067198736152E-08

    /** The constant `P2` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_P2 = .6820161668496170657918E-09

    /** The constant `P3` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_P3 = .4686843322948848031080E-10

    /** The constant `P4` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_P4 = .1572833027710446286995E-11

    /** The constant `P5` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_P5 = -.1249441572276366213222E-12

    /** The constant `P6` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_P6 = .4343529937408594255178E-14

    /** The constant `Q1` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_Q1 = .3056961078365221025009E+00

    /** The constant `Q2` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_Q2 = .5464213086042296536016E-01

    /** The constant `Q3` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_Q3 = .4956830093825887312020E-02

    /** The constant `Q4` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_Q4 = .2692369466186361192876E-03

    /** The constant `C` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C = -.422784335098467139393487909917598E+00

    /** The constant `C0` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C0 = .577215664901532860606512090082402E+00

    /** The constant `C1` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C1 = -.655878071520253881077019515145390E+00

    /** The constant `C2` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C2 = -.420026350340952355290039348754298E-01

    /** The constant `C3` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C3 = .166538611382291489501700795102105E+00

    /** The constant `C4` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C4 = -.421977345555443367482083012891874E-01

    /** The constant `C5` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C5 = -.962197152787697356211492167234820E-02

    /** The constant `C6` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C6 = .721894324666309954239501034044657E-02

    /** The constant `C7` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C7 = -.116516759185906511211397108401839E-02

    /** The constant `C8` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C8 = -.215241674114950972815729963053648E-03

    /** The constant `C9` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C9 = .128050282388116186153198626328164E-03

    /** The constant `C10` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C10 = -.201348547807882386556893914210218E-04

    /** The constant `C11` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C11 = -.125049348214267065734535947383309E-05

    /** The constant `C12` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C12 = .113302723198169588237412962033074E-05

    /** The constant `C13` defined in `DGAM1`.  */
    private const val INV_GAMMA1P_M1_C13 = -.205633841697760710345015413002057E-06

    /**
     * Computes the function \( \frac{1}{\Gamma(1 + x)} - 1 \) for `-0.5 <= x <= 1.5`.
     *
     * This implementation is based on the double precision implementation in
     * the *NSWC Library of Mathematics Subroutines*, `DGAM1`.
     *
     * @param x Argument.
     * @return \( \frac{1}{\Gamma(1 + x)} - 1 \)
     * @throws IllegalArgumentException if `x < -0.5` or `x > 1.5`
     */
    fun value(x: Double): Double {
        if (x < -0.5 || x > 1.5) {
            throw GammaException(
                GammaException.OUT_OF_RANGE(
                    x,
                    -0.5,
                    1.5
                )
            )
        }
        val t = if (x <= 0.5) x else x - 0.5 - 0.5
        return if (t < 0) {
            val a = INV_GAMMA1P_M1_A0 + t * INV_GAMMA1P_M1_A1
            var b =
                INV_GAMMA1P_M1_B8
            b = INV_GAMMA1P_M1_B7 + t * b
            b = INV_GAMMA1P_M1_B6 + t * b
            b = INV_GAMMA1P_M1_B5 + t * b
            b = INV_GAMMA1P_M1_B4 + t * b
            b = INV_GAMMA1P_M1_B3 + t * b
            b = INV_GAMMA1P_M1_B2 + t * b
            b = INV_GAMMA1P_M1_B1 + t * b
            b = 1.0 + t * b
            var c = INV_GAMMA1P_M1_C13 + t * (a / b)
            c = INV_GAMMA1P_M1_C12 + t * c
            c = INV_GAMMA1P_M1_C11 + t * c
            c = INV_GAMMA1P_M1_C10 + t * c
            c = INV_GAMMA1P_M1_C9 + t * c
            c = INV_GAMMA1P_M1_C8 + t * c
            c = INV_GAMMA1P_M1_C7 + t * c
            c = INV_GAMMA1P_M1_C6 + t * c
            c = INV_GAMMA1P_M1_C5 + t * c
            c = INV_GAMMA1P_M1_C4 + t * c
            c = INV_GAMMA1P_M1_C3 + t * c
            c = INV_GAMMA1P_M1_C2 + t * c
            c = INV_GAMMA1P_M1_C1 + t * c
            c = INV_GAMMA1P_M1_C + t * c
            if (x > 0.5) {
                t * c / x
            } else {
                x * (c + 0.5 + 0.5)
            }
        } else {
            var p =
                INV_GAMMA1P_M1_P6
            p = INV_GAMMA1P_M1_P5 + t * p
            p = INV_GAMMA1P_M1_P4 + t * p
            p = INV_GAMMA1P_M1_P3 + t * p
            p = INV_GAMMA1P_M1_P2 + t * p
            p = INV_GAMMA1P_M1_P1 + t * p
            p = INV_GAMMA1P_M1_P0 + t * p
            var q =
                INV_GAMMA1P_M1_Q4
            q = INV_GAMMA1P_M1_Q3 + t * q
            q = INV_GAMMA1P_M1_Q2 + t * q
            q = INV_GAMMA1P_M1_Q1 + t * q
            q = 1.0 + t * q
            var c = INV_GAMMA1P_M1_C13 + p / q * t
            c = INV_GAMMA1P_M1_C12 + t * c
            c = INV_GAMMA1P_M1_C11 + t * c
            c = INV_GAMMA1P_M1_C10 + t * c
            c = INV_GAMMA1P_M1_C9 + t * c
            c = INV_GAMMA1P_M1_C8 + t * c
            c = INV_GAMMA1P_M1_C7 + t * c
            c = INV_GAMMA1P_M1_C6 + t * c
            c = INV_GAMMA1P_M1_C5 + t * c
            c = INV_GAMMA1P_M1_C4 + t * c
            c = INV_GAMMA1P_M1_C3 + t * c
            c = INV_GAMMA1P_M1_C2 + t * c
            c = INV_GAMMA1P_M1_C1 + t * c
            c = INV_GAMMA1P_M1_C0 + t * c
            if (x > 0.5) {
                t / x * (c - 0.5 - 0.5)
            } else {
                x * c
            }
        }
    }
}
