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
@file:Suppress("unused")

package com.github.alexandrelombard.commonskt.numbers.complex.streams

import com.github.alexandrelombard.commonskt.numbers.complex.Complex
import kotlin.math.cos
import kotlin.math.sin

/**
 * Static implementations of common [Complex] utilities functions.
 */
object ComplexUtils {
    /** Dimension X.  */
    private const val DIM_X = 0

    /** Dimension Y.  */
    private const val DIM_Y = 1

    /** Dimension Z.  */
    private const val DIM_Z = 2

    /**
     * Creates a complex number from the given polar representation.
     *
     *
     * If either `r` or `theta` is NaN, or `theta` is
     * infinite, `Complex(NaN, NaN)` is returned.
     *
     *
     * If `r` is infinite and `theta` is finite, infinite or NaN
     * values may be returned in parts of the result, following the rules for
     * double arithmetic.
     *
     * Examples:
     * <pre>
     * `polar2Complex(INFINITY, \(\pi\)) = INFINITY + INFINITY i
     * polar2Complex(INFINITY, 0) = INFINITY + NaN i
     * polar2Complex(INFINITY, \(-\frac{\pi}{4}\)) = INFINITY - INFINITY i
     * polar2Complex(INFINITY, \(5\frac{\pi}{4}\)) = -INFINITY - INFINITY i `
    </pre> *
     *
     * @param r the modulus of the complex number to create
     * @param theta the argument of the complex number to create
     * @return `Complex`
     * @throws IllegalArgumentException if `r` is negative
     */
    fun polar2Complex(r: Double, theta: Double): Complex {
        if (r < 0) {
            throw NegativeModulusException(
                r
            )
        }
        return Complex.ofCartesian(r * cos(theta), r * sin(theta))
    }

    /**
     * Creates `Complex[]` array given `double[]` arrays of r and
     * theta.
     *
     * @param r `double[]` of moduli
     * @param theta `double[]` of arguments
     * @return `Complex[]`
     * @throws IllegalArgumentException if any element in `r` is negative
     */
    fun polar2Complex(r: DoubleArray, theta: DoubleArray): Array<Complex> {
        val length = r.size
        val c: Array<Complex> = Array(length) { x ->
            if (r[x] < 0) {
                throw NegativeModulusException(
                    r[x]
                )
            }
            Complex.ofCartesian(
                r[x] * cos(theta[x]),
                r[x] * sin(theta[x]))
        }
        return c
    }

    /**
     * Creates `Complex[][]` array given `double[][]` arrays of r
     * and theta.
     *
     * @param r `double[]` of moduli
     * @param theta `double[]` of arguments
     * @return `Complex[][]`
     * @throws IllegalArgumentException if any element in `r` is negative
     */
    fun polar2Complex(
        r: Array<DoubleArray>,
        theta: Array<DoubleArray>
    ): Array<Array<Complex>> {
        val length = r.size
        val c: Array<Array<Complex>> = Array(length) {
            polar2Complex(
                r[it],
                theta[it]
            )
        }
        return c
    }

    /**
     * Creates `Complex[][][]` array given `double[][][]` arrays of
     * r and theta.
     *
     * @param r array of moduli
     * @param theta array of arguments
     * @return `Complex`
     * @throws IllegalArgumentException if any element in `r` is negative
     */
    fun polar2Complex(
        r: Array<Array<DoubleArray>>,
        theta: Array<Array<DoubleArray>>
    ): Array<Array<Array<Complex>>> {
        val length = r.size
        val c: Array<Array<Array<Complex>>> = Array(length) { x ->
            polar2Complex(
                r[x],
                theta[x]
            )
        }
        return c
    }

    /**
     * Returns double from array `real[]` at entry `index` as a
     * `Complex`.
     *
     * @param real array of real numbers
     * @param index location in the array
     * @return `Complex`.
     */
    fun extractComplexFromRealArray(real: DoubleArray, index: Int): Complex {
        return Complex.ofCartesian(real[index], 0.0)
    }

    /**
     * Returns float from array `real[]` at entry `index` as a
     * `Complex`.
     *
     * @param real array of real numbers
     * @param index location in the array
     * @return `Complex` array
     */
    fun extractComplexFromRealArray(real: FloatArray, index: Int): Complex {
        return Complex.ofCartesian(real[index].toDouble(), 0.0)
    }

    /**
     * Returns double from array `imaginary[]` at entry `index` as a
     * `Complex`.
     *
     * @param imaginary array of imaginary numbers
     * @param index location in the array
     * @return `Complex` array
     */
    fun extractComplexFromImaginaryArray(imaginary: DoubleArray, index: Int): Complex {
        return Complex.ofCartesian(0.0, imaginary[index])
    }

    /**
     * Returns float from array `imaginary[]` at entry `index` as a
     * `Complex`.
     *
     * @param imaginary array of imaginary numbers
     * @param index location in the array
     * @return `Complex` array
     */
    fun extractComplexFromImaginaryArray(imaginary: FloatArray, index: Int): Complex {
        return Complex.ofCartesian(0.0, imaginary[index].toDouble())
    }

    /**
     * Returns real component of Complex from array `Complex[]` at entry
     * `index` as a `double`.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return `double`.
     */
    fun extractRealFromComplexArray(complex: Array<Complex>, index: Int): Double {
        return complex[index].real
    }

    /**
     * Returns real component of array `Complex[]` at entry `index`
     * as a `float`.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return `float`.
     */
    fun extractRealFloatFromComplexArray(complex: Array<Complex>, index: Int): Float {
        return complex[index].real.toFloat()
    }

    /**
     * Returns imaginary component of Complex from array `Complex[]` at
     * entry `index` as a `double`.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return `double`.
     */
    fun extractImaginaryFromComplexArray(complex: Array<Complex>, index: Int): Double {
        return complex[index].imaginary
    }

    /**
     * Returns imaginary component of array `Complex[]` at entry
     * `index` as a `float`.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return `float`.
     */
    fun extractImaginaryFloatFromComplexArray(complex: Array<Complex>, index: Int): Float {
        return complex[index].imaginary.toFloat()
    }

    /**
     * Returns a Complex object from interleaved `double[]` array at entry
     * `index`.
     *
     * @param d array of interleaved complex numbers alternating real and imaginary values
     * @param index location in the array This is the location by complex number, e.g. index number 5 in the
     * array will return `Complex.ofCartesian(d[10], d[11])`
     * @return `Complex`.
     */
    fun extractComplexFromInterleavedArray(d: DoubleArray, index: Int): Complex {
        return Complex.ofCartesian(d[index * 2], d[index * 2 + 1])
    }

    /**
     * Returns a Complex object from interleaved `float[]` array at entry
     * `index`.
     *
     * @param f float array of interleaved complex numbers alternating real and imaginary values
     * @param index location in the array This is the location by complex number, e.g. index number 5
     * in the `float[]` array will return new `Complex(d[10], d[11])`
     * @return `Complex`.
     */
    fun extractComplexFromInterleavedArray(f: FloatArray, index: Int): Complex {
        return Complex.ofCartesian(f[index * 2].toDouble(), f[index * 2 + 1].toDouble())
    }

    /**
     * Returns values of Complex object from array `Complex[]` at entry
     * `index` as a size 2 `double` of the form {real, imag}.
     *
     * @param complex array of complex numbers
     * @param index location in the array
     * @return size 2 array.
     */
    fun extractInterleavedFromComplexArray(complex: Array<Complex>, index: Int): DoubleArray {
        return doubleArrayOf(complex[index].real, complex[index].imaginary)
    }

    /**
     * Returns Complex object from array `Complex[]` at entry
     * `index` as a size 2 `float` of the form {real, imag}.
     *
     * @param complex `Complex` array
     * @param index location in the array
     * @return size 2 `float[]`.
     */
    fun extractInterleavedFloatFromComplexArray(complex: Array<Complex>, index: Int): FloatArray {
        return floatArrayOf(complex[index].real.toFloat(), complex[index].imaginary.toFloat())
    }

    /**
     * Converts a `double[]` array to a `Complex[]` array.
     *
     * @param real array of numbers to be converted to their `Complex` equivalent
     * @return `Complex` array
     */
    fun real2Complex(real: DoubleArray): Array<Complex> {
        val c: Array<Complex> = Array(real.size) {
            val d = real[it]
            Complex.ofCartesian(d, 0.0)
        }
        return c
    }

    /**
     * Converts a `float[]` array to a `Complex[]` array.
     *
     * @param real array of numbers to be converted to their `Complex` equivalent
     * @return `Complex` array
     */
    fun real2Complex(real: FloatArray): Array<Complex> {
        val c: Array<Complex> = Array(real.size) {
            val d = real[it]
            Complex.ofCartesian(d.toDouble(), 0.0)
        }
        return c
    }

    /**
     * Converts a 2D real `double[][]` array to a 2D `Complex[][]`
     * array.
     *
     * @param d 2D array
     * @return 2D `Complex` array
     */
    fun real2Complex(d: Array<DoubleArray>): Array<Array<Complex>> {
        val w = d.size
        val c: Array<Array<Complex>> = Array(w) {
            real2Complex(d[it])
        }
        return c
    }

    /**
     * Converts a 2D real `float[][]` array to a 2D `Complex[][]`
     * array.
     *
     * @param d 2D array
     * @return 2D `Complex` array
     */
    fun real2Complex(d: Array<FloatArray>): Array<Array<Complex>> {
        val w = d.size
        val c: Array<Array<Complex>> = Array(w) {
            real2Complex(d[it])
        }
        return c
    }

    /**
     * Converts a 3D real `double[][][]` array to a `Complex [][][]`
     * array.
     *
     * @param d 3D complex interleaved array
     * @return 3D `Complex` array
     */
    fun real2Complex(d: Array<Array<DoubleArray>>): Array<Array<Array<Complex>>> {
        val w = d.size
        val c: Array<Array<Array<Complex>>> =
            Array(w) {
                real2Complex(
                    d[it]
                )
            }
        return c
    }

    /**
     * Converts a 3D real `float[][][]` array to a `Complex [][][]`
     * array.
     *
     * @param d 3D complex interleaved array
     * @return 3D `Complex` array
     */
    fun real2Complex(d: Array<Array<FloatArray>>): Array<Array<Array<Complex>>> {
        val w = d.size
        val c: Array<Array<Array<Complex>>> = Array(w) {
            real2Complex(d[it])
        }
        return c
    }

    /**
     * Converts a 4D real `double[][][][]` array to a `Complex [][][][]`
     * array.
     *
     * @param d 4D complex interleaved array
     * @return 4D `Complex` array
     */
    fun real2Complex(d: Array<Array<Array<DoubleArray>>>): Array<Array<Array<Array<Complex>>>> {
        val w = d.size
        val c: Array<Array<Array<Array<Complex>>>> = Array(w) {
            real2Complex(d[it])
        }
        return c
    }

    /**
     * Converts real component of `Complex[]` array to a `double[]`
     * array.
     *
     * @param c `Complex` array
     * @return array of the real component
     */
    fun complex2Real(c: Array<Complex>): DoubleArray {
        var index = 0
        val d = DoubleArray(c.size)
        for (cc in c) {
            d[index] = cc.real
            index++
        }
        return d
    }

    /**
     * Converts real component of `Complex[]` array to a `float[]`
     * array.
     *
     * @param c `Complex` array
     * @return `float[]` array of the real component
     */
    fun complex2RealFloat(c: Array<Complex>): FloatArray {
        var index = 0
        val f = FloatArray(c.size)
        for (cc in c) {
            f[index] = cc.real.toFloat()
            index++
        }
        return f
    }

    /**
     * Converts real component of a 2D `Complex[][]` array to a 2D
     * `double[][]` array.
     *
     * @param c 2D `Complex` array
     * @return `double[][]` of real component
     */
    fun complex2Real(c: Array<Array<Complex>>): Array<DoubleArray> {
        val length = c.size
        val d = Array<DoubleArray>(length) {
            complex2Real(c[it])
        }
        return d
    }

    /**
     * Converts real component of a 2D `Complex[][]` array to a 2D
     * `float[][]` array.
     *
     * @param c 2D `Complex` array
     * @return `float[][]` of real component
     */
    fun complex2RealFloat(c: Array<Array<Complex>>): Array<FloatArray> {
        val length = c.size
        val f = Array<FloatArray>(length) {
            complex2RealFloat(
                c[it]
            )
        }
        return f
    }

    /**
     * Converts real component of a 3D `Complex[][][]` array to a 3D
     * `double[][][]` array.
     *
     * @param c 3D complex interleaved array
     * @return array of real component
     */
    fun complex2Real(c: Array<Array<Array<Complex>>>): Array<Array<DoubleArray>> {
        val length = c.size
        val d: Array<Array<DoubleArray>> = Array(length) {
            complex2Real(c[it])
        }
        return d
    }

    /**
     * Converts real component of a 3D `Complex[][][]` array to a 3D
     * `float[][][]` array.
     *
     * @param c 3D `Complex` array
     * @return `float[][][]` of real component
     */
    fun complex2RealFloat(c: Array<Array<Array<Complex>>>): Array<Array<FloatArray>> {
        val length = c.size
        val f: Array<Array<FloatArray>> = Array(length) {
            complex2RealFloat(
                c[it]
            )
        }
        return f
    }

    /**
     * Converts real component of a 4D `Complex[][][][]` array to a 4D
     * `double[][][][]` array.
     *
     * @param c 4D complex interleaved array
     * @return array of real component
     */
    fun complex2Real(c: Array<Array<Array<Array<Complex>>>>): Array<Array<Array<DoubleArray>>> {
        val length = c.size
        val d: Array<Array<Array<DoubleArray>>> = Array(length) {
            complex2Real(c[it])
        }
        return d
    }

    /**
     * Converts real component of a 4D `Complex[][][][]` array to a 4D
     * `float[][][][]` array.
     *
     * @param c 4D `Complex` array
     * @return `float[][][][]` of real component
     */
    fun complex2RealFloat(c: Array<Array<Array<Array<Complex>>>>): Array<Array<Array<FloatArray>>> {
        val length = c.size
        val f: Array<Array<Array<FloatArray>>> = Array(length) {
            complex2RealFloat(
                c[it]
            )
        }
        return f
    }

    /**
     * Converts a `double[]` array to an imaginary `Complex[]`
     * array.
     *
     * @param imaginary array of numbers to be converted to their `Complex` equivalent
     * @return `Complex` array
     */
    fun imaginary2Complex(imaginary: DoubleArray): Array<Complex> {
        val c: Array<Complex> = Array(imaginary.size) {
            val d = imaginary[it]
            Complex.ofCartesian(0.0, d)
        }
        return c
    }

    /**
     * Converts a `float[]` array to an imaginary `Complex[]` array.
     *
     * @param imaginary array of numbers to be converted to their `Complex` equivalent
     * @return `Complex` array
     */
    fun imaginary2Complex(imaginary: FloatArray): Array<Complex> {
        val c: Array<Complex> = Array(imaginary.size) {
            val d = imaginary[it]
            Complex.ofCartesian(0.0, d.toDouble())
        }
        return c
    }

    /**
     * Converts a 2D imaginary array `double[][]` to a 2D
     * `Complex[][]` array.
     *
     * @param i 2D array
     * @return 2D `Complex` array
     */
    fun imaginary2Complex(i: Array<DoubleArray>): Array<Array<Complex>> {
        val w = i.size
        val c: Array<Array<Complex>> = Array(w) {
            imaginary2Complex(
                i[it]
            )
        }
        return c
    }

    /**
     * Converts a 3D imaginary array `double[][][]` to a `Complex[]`
     * array.
     *
     * @param i 3D complex imaginary array
     * @return 3D `Complex` array
     */
    fun imaginary2Complex(i: Array<Array<DoubleArray>>): Array<Array<Array<Complex>>> {
        val w = i.size
        val c: Array<Array<Array<Complex>>> = Array(w) {
            imaginary2Complex(
                i[it]
            )
        }
        return c
    }

    /**
     * Converts a 4D imaginary array `double[][][][]` to a 4D `Complex[][][][]`
     * array.
     *
     * @param i 4D complex imaginary array
     * @return 4D `Complex` array
     */
    fun imaginary2Complex(i: Array<Array<Array<DoubleArray>>>): Array<Array<Array<Array<Complex>>>> {
        val w = i.size
        val c: Array<Array<Array<Array<Complex>>>> = Array(w) {
            imaginary2Complex(
                i[it]
            )
        }
        return c
    }

    /**
     * Converts imaginary part of a `Complex[]` array to a
     * `double[]` array.
     *
     * @param c `Complex` array.
     * @return array of the imaginary component
     */
    fun complex2Imaginary(c: Array<Complex>): DoubleArray {
        var index = 0
        val i = DoubleArray(c.size)
        for (cc in c) {
            i[index] = cc.imaginary
            index++
        }
        return i
    }

    /**
     * Converts imaginary component of a `Complex[]` array to a
     * `float[]` array.
     *
     * @param c `Complex` array.
     * @return `float[]` array of the imaginary component
     */
    fun complex2ImaginaryFloat(c: Array<Complex>): FloatArray {
        var index = 0
        val f = FloatArray(c.size)
        for (cc in c) {
            f[index] = cc.imaginary.toFloat()
            index++
        }
        return f
    }

    /**
     * Converts imaginary component of a 2D `Complex[][]` array to a 2D
     * `double[][]` array.
     *
     * @param c 2D `Complex` array
     * @return `double[][]` of imaginary component
     */
    fun complex2Imaginary(c: Array<Array<Complex>>): Array<DoubleArray> {
        val length = c.size
        val i = Array(length) {
            complex2Imaginary(
                c[it]
            )
        }
        return i
    }

    /**
     * Converts imaginary component of a 2D `Complex[][]` array to a 2D
     * `float[][]` array.
     *
     * @param c 2D `Complex` array
     * @return `float[][]` of imaginary component
     */
    fun complex2ImaginaryFloat(c: Array<Array<Complex>>): Array<FloatArray> {
        val length = c.size
        val f = Array<FloatArray>(length) {
            complex2ImaginaryFloat(
                c[it]
            )
        }
        return f
    }

    /**
     * Converts imaginary component of a 3D `Complex[][][]` array to a 3D
     * `double[][][]` array.
     *
     * @param c 3D complex interleaved array
     * @return 3D `Complex` array
     */
    fun complex2Imaginary(c: Array<Array<Array<Complex>>>): Array<Array<DoubleArray>> {
        val length = c.size
        val i: Array<Array<DoubleArray>> = Array(length) {
            complex2Imaginary(
                c[it]
            )
        }
        return i
    }

    /**
     * Converts imaginary component of a 3D `Complex[][][]` array to a 3D
     * `float[][][]` array.
     *
     * @param c 3D `Complex` array
     * @return `float[][][]` of imaginary component
     */
    fun complex2ImaginaryFloat(c: Array<Array<Array<Complex>>>): Array<Array<FloatArray>> {
        val length = c.size
        val f: Array<Array<FloatArray>> = Array(length) {
            complex2ImaginaryFloat(
                c[it]
            )
        }
        return f
    }

    /**
     * Converts imaginary component of a 4D `Complex[][][][]` array to a 4D
     * `double[][][][]` array.
     *
     * @param c 4D complex interleaved array
     * @return 4D `Complex` array
     */
    fun complex2Imaginary(c: Array<Array<Array<Array<Complex>>>>): Array<Array<Array<DoubleArray>>> {
        val length = c.size
        val i: Array<Array<Array<DoubleArray>>> = Array(length) {
            complex2Imaginary(
                c[it]
            )
        }
        return i
    }

    /**
     * Converts imaginary component of a 4D `Complex[][][][]` array to a 4D
     * `float[][][][]` array.
     *
     * @param c 4D `Complex` array
     * @return `float[][][][]` of imaginary component
     */
    fun complex2ImaginaryFloat(c: Array<Array<Array<Array<Complex>>>>): Array<Array<Array<FloatArray>>> {
        val length = c.size
        val f: Array<Array<Array<FloatArray>>> = Array(length) {
            complex2ImaginaryFloat(
                c[it]
            )
        }
        return f
    }
    // INTERLEAVED METHODS
    /**
     * Converts a complex interleaved `double[]` array to a
     * `Complex[]` array.
     *
     * @param interleaved array of numbers to be converted to their `Complex` equivalent
     * @return `Complex` array
     */
    fun interleaved2Complex(interleaved: DoubleArray): Array<Complex> {
        val length = interleaved.size / 2
        val c: Array<Complex> = Array(length) {
            Complex.ofCartesian(interleaved[it * 2], interleaved[it * 2 + 1])
        }
        return c
    }

    /**
     * Converts a complex interleaved `float[]` array to a
     * `Complex[]` array.
     *
     * @param interleaved float[] array of numbers to be converted to their `Complex` equivalent
     * @return `Complex` array
     */
    fun interleaved2Complex(interleaved: FloatArray): Array<Complex> {
        val length = interleaved.size / 2
        val c: Array<Complex> = Array(length) {
            Complex.ofCartesian(interleaved[it * 2].toDouble(), interleaved[it * 2 + 1].toDouble())
        }
        return c
    }

    /**
     * Converts a `Complex[]` array to an interleaved complex
     * `double[]` array.
     *
     * @param c Complex array
     * @return complex interleaved array alternating real and
     * imaginary values
     */
    fun complex2Interleaved(c: Array<Complex>): DoubleArray {
        var index = 0
        val i = DoubleArray(c.size * 2)
        for (cc in c) {
            val real = index * 2
            val imag = index * 2 + 1
            i[real] = cc.real
            i[imag] = cc.imaginary
            index++
        }
        return i
    }

    /**
     * Converts a `Complex[]` array to an interleaved complex
     * `float[]` array.
     *
     * @param c Complex array
     * @return complex interleaved `float[]` alternating real and
     * imaginary values
     */
    fun complex2InterleavedFloat(c: Array<Complex>): FloatArray {
        var index = 0
        val f = FloatArray(c.size * 2)
        for (cc in c) {
            val real = index * 2
            val imag = index * 2 + 1
            f[real] = cc.real.toFloat()
            f[imag] = cc.imaginary.toFloat()
            index++
        }
        return f
    }
    /**
     * Converts a 2D `Complex[][]` array to an interleaved complex
     * `double[][]` array.
     *
     * @param c 2D Complex array
     * @param interleavedDim Depth level of the array to interleave
     * @return complex interleaved array alternating real and
     * imaginary values
     * @throws IllegalArgumentException if `interleavedDim` is not 0 or 1
     */
    fun complex2Interleaved(
        c: Array<Array<Complex>>,
        interleavedDim: Int = 1
    ): Array<DoubleArray> {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = c.size
        val h: Int = c[0].size
        val i: Array<DoubleArray>
        if (interleavedDim == 0) {
            i = Array(2 * w) { DoubleArray(h) }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    i[x * 2][y] = c[x][y].real
                    i[x * 2 + 1][y] = c[x][y].imaginary
                }
            }
        } else {
            i = Array(w) { DoubleArray(2 * h) }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    i[x][y * 2] = c[x][y].real
                    i[x][y * 2 + 1] = c[x][y].imaginary
                }
            }
        }
        return i
    }
    /**
     * Converts a 3D `Complex[][][]` array to an interleaved complex
     * `double[][][]` array.
     *
     * @param c 3D Complex array
     * @param interleavedDim Depth level of the array to interleave
     * @return complex interleaved array alternating real and
     * imaginary values
     * @throws IllegalArgumentException if `interleavedDim` is not 0, 1, or 2
     */
    fun complex2Interleaved(
        c: Array<Array<Array<Complex>>>,
        interleavedDim: Int = 2
    ): Array<Array<DoubleArray>> {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = c.size
        val h: Int = c[0].size
        val d: Int = c[0][0].size
        val i: Array<Array<DoubleArray>>
        if (interleavedDim == 0) {
            i = Array(
                2 * w
            ) { Array(h) { DoubleArray(d) } }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    for (z in 0 until d) {
                        i[x * 2][y][z] = c[x][y][z].real
                        i[x * 2 + 1][y][z] = c[x][y][z].imaginary
                    }
                }
            }
        } else if (interleavedDim == 1) {
            i = Array(
                w
            ) { Array(2 * h) { DoubleArray(d) } }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    for (z in 0 until d) {
                        i[x][y * 2][z] = c[x][y][z].real
                        i[x][y * 2 + 1][z] = c[x][y][z].imaginary
                    }
                }
            }
        } else {
            i = Array(
                w
            ) { Array(h) { DoubleArray(2 * d) } }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    for (z in 0 until d) {
                        i[x][y][z * 2] = c[x][y][z].real
                        i[x][y][z * 2 + 1] = c[x][y][z].imaginary
                    }
                }
            }
        }
        return i
    }
    /**
     * Converts a 4D `Complex[][][][]` array to an interleaved complex
     * `double[][][][]` array.
     *
     * @param c 4D Complex array
     * @param interleavedDim Depth level of the array to interleave
     * @return complex interleaved array alternating real and
     * imaginary values
     * @throws IllegalArgumentException if `interleavedDim` is not in the range `[0, 3]`
     */
    fun complex2Interleaved(
        c: Array<Array<Array<Array<Complex>>>>,
        interleavedDim: Int = 3
    ): Array<Array<Array<DoubleArray>>> {
        if (interleavedDim > 3 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = c.size
        val h: Int = c[0].size
        val d: Int = c[0][0].size
        val v: Int = c[0][0][0].size
        val i: Array<Array<Array<DoubleArray>>>
        if (interleavedDim == DIM_X) {
            i = Array(
                2 * w
            ) {
                Array(
                    h
                ) { Array(d) { DoubleArray(v) } }
            }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    for (z in 0 until d) {
                        for (t in 0 until v) {
                            i[x * 2][y][z][t] = c[x][y][z][t].real
                            i[x * 2 + 1][y][z][t] = c[x][y][z][t].imaginary
                        }
                    }
                }
            }
        } else if (interleavedDim == DIM_Y) {
            i = Array(
                w
            ) {
                Array(
                    2 * h
                ) { Array(d) { DoubleArray(v) } }
            }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    for (z in 0 until d) {
                        for (t in 0 until v) {
                            i[x][y * 2][z][t] = c[x][y][z][t].real
                            i[x][y * 2 + 1][z][t] = c[x][y][z][t].imaginary
                        }
                    }
                }
            }
        } else if (interleavedDim == DIM_Z) {
            i = Array(
                w
            ) {
                Array(
                    h
                ) { Array(2 * d) { DoubleArray(v) } }
            }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    for (z in 0 until d) {
                        for (t in 0 until v) {
                            i[x][y][z * 2][t] = c[x][y][z][t].real
                            i[x][y][z * 2 + 1][t] = c[x][y][z][t].imaginary
                        }
                    }
                }
            }
        } else {
            i = Array(
                w
            ) {
                Array(
                    h
                ) { Array(d) { DoubleArray(2 * v) } }
            }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    for (z in 0 until d) {
                        for (t in 0 until v) {
                            i[x][y][z][t * 2] = c[x][y][z][t].real
                            i[x][y][z][t * 2 + 1] = c[x][y][z][t].imaginary
                        }
                    }
                }
            }
        }
        return i
    }
    /**
     * Converts a 2D `Complex[][]` array to an interleaved complex
     * `float[][]` array.
     *
     * @param c 2D Complex array
     * @param interleavedDim Depth level of the array to interleave
     * @return complex interleaved `float[][]` alternating real and
     * imaginary values
     * @throws IllegalArgumentException if `interleavedDim` is not 0 or 1
     */
    fun complex2InterleavedFloat(
        c: Array<Array<Complex>>,
        interleavedDim: Int = 1
    ): Array<FloatArray> {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = c.size
        val h: Int = c[0].size
        val i: Array<FloatArray>
        if (interleavedDim == 0) {
            i = Array(2 * w) { FloatArray(h) }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    i[x * 2][y] = c[x][y].real.toFloat()
                    i[x * 2 + 1][y] = c[x][y].imaginary.toFloat()
                }
            }
        } else {
            i = Array(w) { FloatArray(2 * h) }
            for (x in 0 until w) {
                for (y in 0 until h) {
                    i[x][y * 2] = c[x][y].real.toFloat()
                    i[x][y * 2 + 1] = c[x][y].imaginary.toFloat()
                }
            }
        }
        return i
    }
    /**
     * Converts a 3D `Complex[][][]` array to an interleaved complex
     * `float[][][]` array.
     *
     * @param c 3D Complex array
     * @param interleavedDim Depth level of the array to interleave
     * @return complex interleaved `float[][][]` alternating real and
     * imaginary values
     * @throws IllegalArgumentException if `interleavedDim` is not 0, 1, or 2
     */
    fun complex2InterleavedFloat(
        c: Array<Array<Array<Complex>>>,
        interleavedDim: Int = 2
    ): Array<Array<FloatArray>> {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = c.size
        val h: Int = c[0].size
        val d: Int = c[0][0].size
        val i: Array<Array<FloatArray>>
        when (interleavedDim) {
            0 -> {
                i = Array(2 * w) { Array(h) { FloatArray(d) } }
                for (x in 0 until w) {
                    for (y in 0 until h) {
                        for (z in 0 until d) {
                            i[x * 2][y][z] = c[x][y][z].real.toFloat()
                            i[x * 2 + 1][y][z] = c[x][y][z].imaginary.toFloat()
                        }
                    }
                }
            }
            1 -> {
                i = Array(w) { Array(2 * h) { FloatArray(d) } }
                for (x in 0 until w) {
                    for (y in 0 until h) {
                        for (z in 0 until d) {
                            i[x][y * 2][z] = c[x][y][z].real.toFloat()
                            i[x][y * 2 + 1][z] = c[x][y][z].imaginary.toFloat()
                        }
                    }
                }
            }
            else -> {
                i = Array(
                    w
                ) { Array(h) { FloatArray(2 * d) } }
                for (x in 0 until w) {
                    for (y in 0 until h) {
                        for (z in 0 until d) {
                            i[x][y][z * 2] = c[x][y][z].real.toFloat()
                            i[x][y][z * 2 + 1] = c[x][y][z].imaginary.toFloat()
                        }
                    }
                }
            }
        }
        return i
    }

    /**
     * Converts a 2D interleaved complex `double[][]` array to a
     * `Complex[][]` array.
     *
     * @param i 2D complex interleaved array
     * @param interleavedDim Depth level of the array to interleave
     * @return 2D `Complex` array
     * @throws IllegalArgumentException if `interleavedDim` is not 0 or 1
     */
    fun interleaved2Complex(
        i: Array<DoubleArray>,
        interleavedDim: Int
    ): Array<Array<Complex>> {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = i.size
        val h: Int = i[0].size
        val c: Array<Array<Complex>>
        if (interleavedDim == 0) {
            c = Array(w / 2) { x ->
                Array(h) { y ->
                    Complex.ofCartesian(i[x * 2][y], i[x * 2 + 1][y])
                }
            }
        } else {
            c = Array(w) { x ->
                Array(h / 2) { y ->
                    Complex.ofCartesian(i[x][y * 2], i[x][y * 2 + 1])
                }
            }
        }
        return c
    }

    /**
     * Converts a 2D interleaved complex `double[][]` array to a
     * `Complex[][]` array. The second d level of the array is assumed
     * to be interleaved.
     *
     * @param d 2D complex interleaved array
     * @return 2D `Complex` array
     */
    fun interleaved2Complex(d: Array<DoubleArray>): Array<Array<Complex>> {
        return interleaved2Complex(
            d,
            1
        )
    }

    /**
     * Converts a 3D interleaved complex `double[][][]` array to a
     * `Complex[][][]` array.
     *
     * @param i 3D complex interleaved array
     * @param interleavedDim Depth level of the array to interleave
     * @return 3D `Complex` array
     * @throws IllegalArgumentException if `interleavedDim` is not 0, 1, or 2
     */
    fun interleaved2Complex(
        i: Array<Array<DoubleArray>>,
        interleavedDim: Int
    ): Array<Array<Array<Complex>>> {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = i.size
        val h: Int = i[0].size
        val d: Int = i[0][0].size
        val c: Array<Array<Array<Complex>>>
        when (interleavedDim) {
            DIM_X -> {
                c = Array(w / 2) { x ->
                    Array(h) { y ->
                        Array(d) { z ->
                            Complex.ofCartesian(i[x * 2][y][z], i[x * 2 + 1][y][z])
                        }
                    }
                }
            }
            DIM_Y -> {
                c = Array(w) { x ->
                    Array(h / 2) { y ->
                        Array(d) { z ->
                            Complex.ofCartesian(i[x][y * 2][z], i[x][y * 2 + 1][z])
                        }
                    }
                }
            }
            else -> {
                c = Array(w) { x ->
                    Array(h) { y ->
                        Array(d / 2) { z ->
                            Complex.ofCartesian(i[x][y][z * 2], i[x][y][z * 2 + 1])
                        }
                    }
                }
            }
        }
        return c
    }

    /**
     * Converts a 4D interleaved complex `double[][][][]` array to a
     * `Complex[][][][]` array.
     *
     * @param i 4D complex interleaved array
     * @param interleavedDim Depth level of the array to interleave
     * @return 4D `Complex` array
     * @throws IllegalArgumentException if `interleavedDim` is not in the range `[0, 3]`
     */
    fun interleaved2Complex(
        i: Array<Array<Array<DoubleArray>>>,
        interleavedDim: Int
    ): Array<Array<Array<Array<Complex>>>> {
        if (interleavedDim > 3 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = i.size
        val h: Int = i[0].size
        val d: Int = i[0][0].size
        val v: Int = i[0][0][0].size
        val c: Array<Array<Array<Array<Complex>>>>
        when (interleavedDim) {
            0 -> {
                c = Array(w / 2) { x ->
                    Array(h) { y ->
                        Array(d) { z ->
                            Array(v) { t ->
                                Complex.ofCartesian(
                                    i[x * 2][y][z][t], i[x * 2 + 1][y][z][t]
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                c = Array(w) { x ->
                    Array(h / 2) { y ->
                        Array(d) {  z ->
                            Array(v) { t ->
                                Complex.ofCartesian(
                                    i[x][y * 2][z][t], i[x][y * 2 + 1][z][t])
                            }
                        }
                    }
                }
            }
            2 -> {
                c = Array(w) { x ->
                    Array(h) { y ->
                        Array(d / 2) { z ->
                            Array(v) { t ->
                                Complex.ofCartesian(
                                    i[x][y][z * 2][t], i[x][y][z * 2 + 1][t])
                            }
                        }
                    }
                }
            }
            else -> {
                c = Array(w) { x ->
                    Array(h) { y ->
                        Array(d) { z ->
                            Array(v / 2) { t ->
                                Complex.ofCartesian(
                                    i[x][y][z][t * 2], i[x][y][z][t * 2 + 1])
                            }
                        }
                    }
                }
            }
        }
        return c
    }

    /**
     * Converts a 3D interleaved complex `double[][][]` array to a
     * `Complex[][][]` array. The third d level is assumed to be
     * interleaved.
     *
     * @param d 3D complex interleaved array
     * @return 3D `Complex` array
     */
    fun interleaved2Complex(d: Array<Array<DoubleArray>>): Array<Array<Array<Complex>>> {
        return interleaved2Complex(
            d,
            2
        )
    }

    /**
     * Converts a 2D interleaved complex `float[][]` array to a
     * `Complex[][]` array.
     *
     * @param i 2D complex interleaved float array
     * @param interleavedDim Depth level of the array to interleave
     * @return 2D `Complex` array
     * @throws IllegalArgumentException if `interleavedDim` is not 0 or 1
     */
    fun interleaved2Complex(
        i: Array<FloatArray>,
        interleavedDim: Int
    ): Array<Array<Complex>> {
        if (interleavedDim > 1 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = i.size
        val h: Int = i[0].size
        val c: Array<Array<Complex>>
        if (interleavedDim == 0) {
            c = Array(w / 2) { x ->
                Array(h) { y ->
                    Complex.ofCartesian(i[x * 2][y].toDouble(), i[x * 2 + 1][y].toDouble())
                }
            }
        } else {
            c = Array(w) { x ->
                Array(h / 2) { y ->
                    Complex.ofCartesian(i[x][y * 2].toDouble(), i[x][y * 2 + 1].toDouble())
                }
            }
        }
        return c
    }

    /**
     * Converts a 2D interleaved complex `float[][]` array to a
     * `Complex[][]` array. The second d level of the array is assumed
     * to be interleaved.
     *
     * @param d 2D complex interleaved float array
     * @return 2D `Complex` array
     */
    fun interleaved2Complex(d: Array<FloatArray>): Array<Array<Complex>> {
        return interleaved2Complex(
            d,
            1
        )
    }

    /**
     * Converts a 3D interleaved complex `float[][][]` array to a
     * `Complex[][][]` array.
     *
     * @param i 3D complex interleaved float array
     * @param interleavedDim Depth level of the array to interleave
     * @return 3D `Complex` array
     * @throws IllegalArgumentException if `interleavedDim` is not 0, 1, or 2
     */
    fun interleaved2Complex(
        i: Array<Array<FloatArray>>,
        interleavedDim: Int
    ): Array<Array<Array<Complex>>> {
        if (interleavedDim > 2 || interleavedDim < 0) {
            throw IndexOutOfRangeException(
                interleavedDim
            )
        }
        val w = i.size
        val h: Int = i[0].size
        val d: Int = i[0][0].size
        val c: Array<Array<Array<Complex>>>
        when (interleavedDim) {
            0 -> {
                c = Array(w / 2) { x ->
                    Array(h) { y ->
                        Array(d) { z ->
                            Complex.ofCartesian(i[x * 2][y][z].toDouble(), i[x * 2 + 1][y][z].toDouble())
                        }
                    }
                }
            }
            1 -> {
                c = Array(w) { x ->
                    Array(h / 2) { y ->
                        Array(d) { z ->
                            Complex.ofCartesian(i[x][y * 2][z].toDouble(), i[x][y * 2 + 1][z].toDouble())
                        }
                    }
                }
            }
            else -> {
                c = Array(w) { x ->
                    Array(h) { y ->
                        Array(d / 2) { z ->
                            Complex.ofCartesian(i[x][y][z * 2].toDouble(), i[x][y][z * 2 + 1].toDouble())
                        }
                    }
                }
            }
        }
        return c
    }

    /**
     * Converts a 3D interleaved complex `float[][][]` array to a
     * `Complex[]` array. The third level of the array is assumed to
     * be interleaved.
     *
     * @param d 3D complex interleaved float array
     * @return 3D `Complex` array
     */
    fun interleaved2Complex(d: Array<Array<FloatArray>>): Array<Array<Array<Complex>>> {
        return interleaved2Complex(
            d,
            2
        )
    }
    // SPLIT METHODS
    /**
     * Converts a split complex array `double[] r, double[] i` to a
     * `Complex[]` array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return `Complex` array
     */
    fun split2Complex(real: DoubleArray, imag: DoubleArray): Array<Complex> {
        val length = real.size
        val c: Array<Complex> = Array(length) {
            Complex.ofCartesian(real[it], imag[it])
        }
        return c
    }

    /**
     * Converts a 2D split complex array `double[][] r, double[][] i` to a
     * 2D `Complex[][]` array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return 2D `Complex` array
     */
    fun split2Complex(
        real: Array<DoubleArray>,
        imag: Array<DoubleArray>
    ): Array<Array<Complex>> {
        val length = real.size
        val c: Array<Array<Complex>> = Array<Array<Complex>>(length) {
            split2Complex(
                real[it],
                imag[it]
            )
        }
        return c
    }

    /**
     * Converts a 3D split complex array `double[][][] r, double[][][] i`
     * to a 3D `Complex[][][]` array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return 3D `Complex` array
     */
    fun split2Complex(
        real: Array<Array<DoubleArray>>,
        imag: Array<Array<DoubleArray>>
    ): Array<Array<Array<Complex>>> {
        val length = real.size
        val c: Array<Array<Array<Complex>>> = Array(length) {
            split2Complex(
                real[it],
                imag[it]
            )
        }
        return c
    }

    /**
     * Converts a 4D split complex array `double[][][][] r, double[][][][] i`
     * to a 4D `Complex[][][][]` array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return 4D `Complex` array
     */
    fun split2Complex(
        real: Array<Array<Array<DoubleArray>>>,
        imag: Array<Array<Array<DoubleArray>>>
    ): Array<Array<Array<Array<Complex>>>> {
        val length = real.size
        val c: Array<Array<Array<Array<Complex>>>> = Array(length) {
            split2Complex(
                real[it],
                imag[it]
            )
        }
        return c
    }

    /**
     * Converts a split complex array `float[] r, float[] i` to a
     * `Complex[]` array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return `Complex` array
     */
    fun split2Complex(real: FloatArray, imag: FloatArray): Array<Complex> {
        val length = real.size
        val c: Array<Complex> = Array(length) {
            Complex.ofCartesian(real[it].toDouble(), imag[it].toDouble())
        }
        return c
    }

    /**
     * Converts a 2D split complex array `float[][] r, float[][] i` to a
     * 2D `Complex[][]` array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return 2D `Complex` array
     */
    fun split2Complex(
        real: Array<FloatArray>,
        imag: Array<FloatArray>
    ): Array<Array<Complex>> {
        val length = real.size
        val c: Array<Array<Complex>> = Array(length) {
            split2Complex(
                real[it],
                imag[it]
            )
        }
        return c
    }

    /**
     * Converts a 3D split complex array `float[][][] r, float[][][] i` to
     * a 3D `Complex[][][]` array.
     *
     * @param real real component
     * @param imag imaginary component
     * @return 3D `Complex` array
     */
    fun split2Complex(
        real: Array<Array<FloatArray>>,
        imag: Array<Array<FloatArray>>
    ): Array<Array<Array<Complex>>> {
        val length = real.size
        val c: Array<Array<Array<Complex>>> = Array(length) {
            split2Complex(
                real[it],
                imag[it]
            )
        }
        return c
    }
    // MISC
    /**
     * Initializes a `Complex[]` array to zero, to avoid
     * NullPointerExceptions.
     *
     * @param c Complex array
     * @return c
     */
    fun initialize(c: Array<Complex>): Array<Complex> {
        val length = c.size
        for (x in 0 until length) {
            c[x] = Complex.ZERO
        }
        return c
    }

    /**
     * Initializes a `Complex[][]` array to zero, to avoid
     * NullPointerExceptions.
     *
     * @param c `Complex` array
     * @return c
     */
    fun initialize(c: Array<Array<Complex>>): Array<Array<Complex>> {
        val length = c.size
        for (x in 0 until length) {
            c[x] =
                initialize(
                    c[x]
                )
        }
        return c
    }

    /**
     * Initializes a `Complex[][][]` array to zero, to avoid
     * NullPointerExceptions.
     *
     * @param c `Complex` array
     * @return c
     */
    fun initialize(c: Array<Array<Array<Complex>>>): Array<Array<Array<Complex>>> {
        val length = c.size
        for (x in 0 until length) {
            c[x] =
                initialize(
                    c[x]
                )
        }
        return c
    }

    /**
     * Returns `double[]` containing absolute values (magnitudes) of a
     * `Complex[]` array.
     *
     * @param c `Complex` array
     * @return `double[]`
     */
    fun abs(c: Array<Complex>): DoubleArray {
        val length = c.size
        val i = DoubleArray(length)
        for (x in 0 until length) {
            i[x] = c[x].abs()
        }
        return i
    }

    /**
     * Returns `double[]` containing arguments (phase angles) of a
     * `Complex[]` array.
     *
     * @param c `Complex` array
     * @return `double[]` array
     */
    fun arg(c: Array<Complex>): DoubleArray {
        val length = c.size
        val i = DoubleArray(length)
        for (x in 0 until length) {
            i[x] = c[x].arg()
        }
        return i
    }

    /**
     * Exception to be throw when a negative value is passed as the modulus.
     */
    private class NegativeModulusException
    /**
     * @param r Wrong modulus.
     */
    internal constructor(r: Double) : IllegalArgumentException("Modulus is negative: $r") {
        companion object {
            /** Serializable version identifier.  */
            private const val serialVersionUID = 20181205L
        }
    }

    /**
     * Exception to be throw when an out-of-range index value is passed.
     */
    private class IndexOutOfRangeException
    /**
     * @param i Wrong index.
     */
    internal constructor(i: Int) : IllegalArgumentException("Out of range: $i") {
        companion object {
            /** Serializable version identifier.  */
            private const val serialVersionUID = 20181205L
        }
    }
}