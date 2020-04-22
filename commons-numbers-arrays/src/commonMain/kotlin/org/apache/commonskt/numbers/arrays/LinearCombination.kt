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
package org.apache.commonskt.numbers.arrays

/**
 * Computes linear combinations accurately.
 * This method computes the sum of the products
 * `a<sub>i</sub> b<sub>i</sub>` to high accuracy.
 * It does so by using specific multiplication and addition algorithms to
 * preserve accuracy and reduce cancellation effects.
 *
 * It is based on the 2005 paper
 * [
 * Accurate Sum and Dot Product](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547) by Takeshi Ogita, Siegfried M. Rump,
 * and Shin'ichi Oishi published in *SIAM J. Sci. Comput*.
 */
object LinearCombination {
    /**
     * @param a Factors.
     * @param b Factors.
     * @return \( \sum_i a_i b_i \).
     * @throws IllegalArgumentException if the sizes of the arrays are different.
     */
    fun value(
        a: DoubleArray,
        b: DoubleArray
    ): Double {
        if (a.size != b.size) {
            throw IllegalArgumentException("Dimension mismatch: " + a.size + " != " + b.size)
        }
        val len = a.size
        if (len == 1) {
            // Revert to scalar multiplication.
            return a[0] * b[0]
        }
        val prodHigh = DoubleArray(len)
        var prodLowSum = 0.0
        for (i in 0 until len) {
            val ai = a[i]
            val aHigh = highPart(ai)
            val aLow = ai - aHigh
            val bi = b[i]
            val bHigh = highPart(bi)
            val bLow = bi - bHigh
            prodHigh[i] = ai * bi
            val prodLow = prodLow(aLow, bLow, prodHigh[i], aHigh, bHigh)
            prodLowSum += prodLow
        }
        val prodHighCur = prodHigh[0]
        var prodHighNext = prodHigh[1]
        var sHighPrev = prodHighCur + prodHighNext
        var sPrime = sHighPrev - prodHighNext
        var sLowSum = prodHighNext - (sHighPrev - sPrime) + (prodHighCur - sPrime)
        val lenMinusOne = len - 1
        for (i in 1 until lenMinusOne) {
            prodHighNext = prodHigh[i + 1]
            val sHighCur = sHighPrev + prodHighNext
            sPrime = sHighCur - prodHighNext
            sLowSum += prodHighNext - (sHighCur - sPrime) + (sHighPrev - sPrime)
            sHighPrev = sHighCur
        }
        var result = sHighPrev + (prodLowSum + sLowSum)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = 0.0
            for (i in 0 until len) {
                result += a[i] * b[i]
            }
        }
        return result
    }

    /**
     * @param a1 First factor of the first term.
     * @param b1 Second factor of the first term.
     * @param a2 First factor of the second term.
     * @param b2 Second factor of the second term.
     * @return \( a_1 b_1 + a_2 b_2 \)
     *
     * @see .value
     * @see .value
     * @see .value
     */
    fun value(
        a1: Double, b1: Double,
        a2: Double, b2: Double
    ): Double {
        // split a1 and b1 as one 26 bits number and one 27 bits number
        val a1High = highPart(a1)
        val a1Low = a1 - a1High
        val b1High = highPart(b1)
        val b1Low = b1 - b1High

        // accurate multiplication a1 * b1
        val prod1High = a1 * b1
        val prod1Low = prodLow(a1Low, b1Low, prod1High, a1High, b1High)

        // split a2 and b2 as one 26 bits number and one 27 bits number
        val a2High = highPart(a2)
        val a2Low = a2 - a2High
        val b2High = highPart(b2)
        val b2Low = b2 - b2High

        // accurate multiplication a2 * b2
        val prod2High = a2 * b2
        val prod2Low = prodLow(a2Low, b2Low, prod2High, a2High, b2High)

        // accurate addition a1 * b1 + a2 * b2
        val s12High = prod1High + prod2High
        val s12Prime = s12High - prod2High
        val s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime)

        // final rounding, s12 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        var result = s12High + (prod1Low + prod2Low + s12Low)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = a1 * b1 + a2 * b2
        }
        return result
    }

    /**
     * @param a1 First factor of the first term.
     * @param b1 Second factor of the first term.
     * @param a2 First factor of the second term.
     * @param b2 Second factor of the second term.
     * @param a3 First factor of the third term.
     * @param b3 Second factor of the third term.
     * @return \( a_1 b_1 + a_2 b_2 + a_3 b_3 \)
     *
     * @see .value
     * @see .value
     * @see .value
     */
    fun value(
        a1: Double, b1: Double,
        a2: Double, b2: Double,
        a3: Double, b3: Double
    ): Double {
        // split a1 and b1 as one 26 bits number and one 27 bits number
        val a1High = highPart(a1)
        val a1Low = a1 - a1High
        val b1High = highPart(b1)
        val b1Low = b1 - b1High

        // accurate multiplication a1 * b1
        val prod1High = a1 * b1
        val prod1Low = prodLow(a1Low, b1Low, prod1High, a1High, b1High)

        // split a2 and b2 as one 26 bits number and one 27 bits number
        val a2High = highPart(a2)
        val a2Low = a2 - a2High
        val b2High = highPart(b2)
        val b2Low = b2 - b2High

        // accurate multiplication a2 * b2
        val prod2High = a2 * b2
        val prod2Low = prodLow(a2Low, b2Low, prod2High, a2High, b2High)

        // split a3 and b3 as one 26 bits number and one 27 bits number
        val a3High = highPart(a3)
        val a3Low = a3 - a3High
        val b3High = highPart(b3)
        val b3Low = b3 - b3High

        // accurate multiplication a3 * b3
        val prod3High = a3 * b3
        val prod3Low = prodLow(a3Low, b3Low, prod3High, a3High, b3High)

        // accurate addition a1 * b1 + a2 * b2
        val s12High = prod1High + prod2High
        val s12Prime = s12High - prod2High
        val s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime)

        // accurate addition a1 * b1 + a2 * b2 + a3 * b3
        val s123High = s12High + prod3High
        val s123Prime = s123High - prod3High
        val s123Low = prod3High - (s123High - s123Prime) + (s12High - s123Prime)

        // final rounding, s123 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        var result = s123High + (prod1Low + prod2Low + prod3Low + s12Low + s123Low)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = a1 * b1 + a2 * b2 + a3 * b3
        }
        return result
    }

    /**
     * @param a1 First factor of the first term.
     * @param b1 Second factor of the first term.
     * @param a2 First factor of the second term.
     * @param b2 Second factor of the second term.
     * @param a3 First factor of the third term.
     * @param b3 Second factor of the third term.
     * @param a4 First factor of the fourth term.
     * @param b4 Second factor of the fourth term.
     * @return \( a_1 b_1 + a_2 b_2 + a_3 b_3 + a_4 b_4 \)
     *
     * @see .value
     * @see .value
     * @see .value
     */
    fun value(
        a1: Double, b1: Double,
        a2: Double, b2: Double,
        a3: Double, b3: Double,
        a4: Double, b4: Double
    ): Double {
        // split a1 and b1 as one 26 bits number and one 27 bits number
        val a1High = highPart(a1)
        val a1Low = a1 - a1High
        val b1High = highPart(b1)
        val b1Low = b1 - b1High

        // accurate multiplication a1 * b1
        val prod1High = a1 * b1
        val prod1Low = prodLow(a1Low, b1Low, prod1High, a1High, b1High)

        // split a2 and b2 as one 26 bits number and one 27 bits number
        val a2High = highPart(a2)
        val a2Low = a2 - a2High
        val b2High = highPart(b2)
        val b2Low = b2 - b2High

        // accurate multiplication a2 * b2
        val prod2High = a2 * b2
        val prod2Low = prodLow(a2Low, b2Low, prod2High, a2High, b2High)

        // split a3 and b3 as one 26 bits number and one 27 bits number
        val a3High = highPart(a3)
        val a3Low = a3 - a3High
        val b3High = highPart(b3)
        val b3Low = b3 - b3High

        // accurate multiplication a3 * b3
        val prod3High = a3 * b3
        val prod3Low = prodLow(a3Low, b3Low, prod3High, a3High, b3High)

        // split a4 and b4 as one 26 bits number and one 27 bits number
        val a4High = highPart(a4)
        val a4Low = a4 - a4High
        val b4High = highPart(b4)
        val b4Low = b4 - b4High

        // accurate multiplication a4 * b4
        val prod4High = a4 * b4
        val prod4Low = prodLow(a4Low, b4Low, prod4High, a4High, b4High)

        // accurate addition a1 * b1 + a2 * b2
        val s12High = prod1High + prod2High
        val s12Prime = s12High - prod2High
        val s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime)

        // accurate addition a1 * b1 + a2 * b2 + a3 * b3
        val s123High = s12High + prod3High
        val s123Prime = s123High - prod3High
        val s123Low = prod3High - (s123High - s123Prime) + (s12High - s123Prime)

        // accurate addition a1 * b1 + a2 * b2 + a3 * b3 + a4 * b4
        val s1234High = s123High + prod4High
        val s1234Prime = s1234High - prod4High
        val s1234Low = prod4High - (s1234High - s1234Prime) + (s123High - s1234Prime)

        // final rounding, s1234 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        var result =
            s1234High + (prod1Low + prod2Low + prod3Low + prod4Low + s12Low + s123Low + s1234Low)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = a1 * b1 + a2 * b2 + a3 * b3 + a4 * b4
        }
        return result
    }

    /**
     * @param value Value.
     * @return the high part of the value.
     */
    private fun highPart(value: Double): Double {
        return Double.fromBits(value.toRawBits() and (-1L shl 27))
    }

    /**
     * @param aLow Low part of first factor.
     * @param bLow Low part of second factor.
     * @param prodHigh Product of the factors.
     * @param aHigh High part of first factor.
     * @param bHigh High part of second factor.
     * @return `aLow * bLow - (((prodHigh - aHigh * bHigh) - aLow * bHigh) - aHigh * bLow)`
     */
    private fun prodLow(
        aLow: Double,
        bLow: Double,
        prodHigh: Double,
        aHigh: Double,
        bHigh: Double
    ): Double {
        return aLow * bLow - (prodHigh - aHigh * bHigh - aLow * bHigh - aHigh * bLow)
    }
}
