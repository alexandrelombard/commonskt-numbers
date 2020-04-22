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
 * Converter between unidimensional storage structure and multidimensional
 * conceptual structure.
 * This utility will convert from indices in a multidimensional structure
 * to the corresponding index in a one-dimensional array. For example,
 * assuming that the ranges (in 3 dimensions) of indices are 2, 4 and 3,
 * the following correspondences, between 3-tuples indices and unidimensional
 * indices, will hold:
 *
 *  * (0, 0, 0) corresponds to 0
 *  * (0, 0, 1) corresponds to 1
 *  * (0, 0, 2) corresponds to 2
 *  * (0, 1, 0) corresponds to 3
 *  * ...
 *  * (1, 0, 0) corresponds to 12
 *  * ...
 *  * (1, 3, 2) corresponds to 23
 *
 */
class MultidimensionalCounter private constructor(vararg size: Int) {
    /**
     * Gets the number of dimensions of the multidimensional counter.
     *
     * @return the number of dimensions.
     */
    /**
     * Number of dimensions.
     */
    val dimension: Int

    /**
     * Offset for each dimension.
     */
    private val uniCounterOffset: IntArray

    /**
     * Counter sizes.
     */
    private val size: IntArray

    /**
     * Total number of (one-dimensional) slots.
     */
    val totalSize: Int

    /**
     * Index of last dimension.
     */
    private val last: Int

    /**
     * Converts to a multidimensional counter.
     *
     * @param index Index in unidimensional counter.
     * @return the multidimensional counts.
     * @throws IndexOutOfBoundsException if `index` is not between
     * `0` and the value returned by [.getSize] (excluded).
     */
    fun toMulti(index: Int): IntArray {
        var index = index
        if (index < 0 ||
            index >= this.totalSize
        ) {
            throw IndexOutOfBoundsException(
                createIndexOutOfBoundsMessage(
                    this.totalSize,
                    index
                )
            )
        }
        val indices = IntArray(dimension)
        for (i in 0 until last) {
            indices[i] = index / uniCounterOffset[i]
            // index = index % uniCounterOffset[i]
            index = index - indices[i] * uniCounterOffset[i]
        }
        indices[last] = index
        return indices
    }

    /**
     * Converts to a unidimensional counter.
     *
     * @param c Indices in multidimensional counter.
     * @return the index within the unidimensionl counter.
     * @throws IllegalArgumentException if the size of `c`
     * does not match the size of the array given in the constructor.
     * @throws IndexOutOfBoundsException if a value of `c` is not in
     * the range of the corresponding dimension, as defined in the
     * [constructor][MultidimensionalCounter.of].
     */
    fun toUni(vararg c: Int): Int {
        if (c.size != dimension) {
            throw IllegalArgumentException(
                "Wrong number of arguments: " + c.size +
                        "(expected: " + dimension + ")"
            )
        }
        var count = 0
        for (i in 0 until dimension) {
            val index = c[i]
            if (index < 0 ||
                index >= size[i]
            ) {
                throw IndexOutOfBoundsException(
                    createIndexOutOfBoundsMessage(
                        size[i], index
                    )
                )
            }
            count += uniCounterOffset[i] * index
        }
        return count
    }

    /**
     * Gets the number of multidimensional counter slots in each dimension.
     *
     * @return the number of slots in each dimension.
     */
    val sizes: IntArray
        get() = size.copyOf(size.size)

    /** {@inheritDoc}  */
    override fun toString(): String {
        return size.toString()
    }

    companion object {
        /**
         * Creates a counter.
         *
         * @param size Counter sizes (number of slots in each dimension).
         * @return a new instance.
         * @throws IllegalArgumentException if one of the sizes is negative
         * or zero.
         */
        fun of(vararg size: Int): MultidimensionalCounter {
            return MultidimensionalCounter(*size)
        }

        /**
         * Check the size is strictly positive: `size > 0`.
         *
         * @param name the name of the size
         * @param size the size
         */
        private fun checkStrictlyPositive(name: String, size: Int) {
            if (size <= 0) {
                throw IllegalArgumentException("Not positive $name: $size")
            }
        }

        /**
         * Creates the message for the index out of bounds exception.
         *
         * @param size the size
         * @param index the index
         * @return the message
         */
        private fun createIndexOutOfBoundsMessage(size: Int, index: Int): String {
            return "Index out of bounds [0, " + (size - 1) + "]: " + index
        }
    }

    /**
     * Creates a counter.
     *
     * @param size Counter sizes (number of slots in each dimension).
     * @throws IllegalArgumentException if one of the sizes is negative
     * or zero.
     */
    init {
        dimension = size.size
        this.size = size.copyOf(size.size)
        uniCounterOffset = IntArray(dimension)
        last = dimension - 1
        uniCounterOffset[last] = 1
        var tS = 1
        for (i in last - 1 downTo 0) {
            val index = i + 1
            checkStrictlyPositive("index size", size[index])
            tS *= size[index]
            checkStrictlyPositive("cumulative size", tS)
            uniCounterOffset[i] = tS
        }
        totalSize = tS * size[0]
        checkStrictlyPositive("total size", totalSize)
    }
}
