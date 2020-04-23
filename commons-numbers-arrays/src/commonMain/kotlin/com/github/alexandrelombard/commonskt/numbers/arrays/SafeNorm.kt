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
package com.github.alexandrelombard.commonskt.numbers.arrays

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Computes the Cartesian norm (2-norm), handling both overflow and underflow.
 * Translation of the [minpack](http://www.netlib.org/minpack)
 * "enorm" subroutine.
 */
object SafeNorm {
    /** Constant.  */
    private const val R_DWARF = 3.834e-20

    /** Constant.  */
    private const val R_GIANT = 1.304e+19

    /**
     * @param v Cartesian coordinates.
     * @return the 2-norm of the vector.
     */
    fun value(v: DoubleArray): Double {
        var s1 = 0.0
        var s2 = 0.0
        var s3 = 0.0
        var x1max = 0.0
        var x3max = 0.0
        val floatn = v.size.toDouble()
        val agiant = R_GIANT / floatn
        for (i in v.indices) {
            val xabs: Double = abs(v[i])
            if (xabs < R_DWARF || xabs > agiant) {
                if (xabs > R_DWARF) {
                    if (xabs > x1max) {
                        val r = x1max / xabs
                        s1 = 1 + s1 * r * r
                        x1max = xabs
                    } else {
                        val r = xabs / x1max
                        s1 += r * r
                    }
                } else {
                    if (xabs > x3max) {
                        val r = x3max / xabs
                        s3 = 1 + s3 * r * r
                        x3max = xabs
                    } else {
                        if (xabs != 0.0) {
                            val r = xabs / x3max
                            s3 += r * r
                        }
                    }
                }
            } else {
                s2 += xabs * xabs
            }
        }
        val norm: Double
        if (s1 != 0.0) {
            norm = x1max * sqrt(s1 + s2 / x1max / x1max)
        } else {
            if (s2 == 0.0) {
                norm = x3max * sqrt(s3)
            } else {
                norm = if (s2 >= x3max) {
                    sqrt(s2 * (1 + x3max / s2 * (x3max * s3)))
                } else {
                    sqrt(x3max * (s2 / x3max + x3max * s3))
                }
            }
        }
        return norm
    }
}
