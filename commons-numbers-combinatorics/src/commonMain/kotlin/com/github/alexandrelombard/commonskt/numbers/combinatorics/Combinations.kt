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
package com.github.alexandrelombard.commonskt.numbers.combinatorics


import com.github.alexandrelombard.commonskt.numbers.combinatorics.BinomialCoefficient.checkBinomial
import com.github.alexandrelombard.commonskt.numbers.core.ArithmeticUtils


/**
 * Utility to create [
 * combinations](http://en.wikipedia.org/wiki/Combination) `(n, k)` of `k` elements in a set of
 * `n` elements.
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Combinations private constructor(
    n: Int,
    k: Int
) : Iterable<IntArray> {
    /**
     * Gets the size of the set from which combinations are drawn.
     *
     * @return the size of the universe.
     */
    /** Size of the set from which combinations are drawn.  */
    val n: Int

    /**
     * Gets the number of elements in each combination.
     *
     * @return the size of the subsets to be enumerated.
     */
    /** Number of elements in each combination.  */
    val k: Int

    /**
     * Creates an iterator whose range is the k-element subsets of
     * {0, ..., n - 1} represented as `int[]` arrays.
     *
     *
     * The iteration order is lexicographic: the arrays returned by the
     * [iterator][.iterator] are sorted in descending order and
     * they are visited in lexicographic order with significance from
     * right to left.
     * For example, `new Combinations(4, 2).iterator()` returns
     * an iterator that will generate the following sequence of arrays
     * on successive calls to
     * `next()`:<br></br>
     * `[0, 1], [0, 2], [1, 2], [0, 3], [1, 3], [2, 3]`
     *
     * If `k == 0` an iterator containing an empty array is returned;
     * if `k == n` an iterator containing [0, ..., n - 1] is returned.
     */
    override fun iterator(): Iterator<IntArray> {
        return if (k == 0 || k == n) SingletonIterator(
            k
        ) else LexicographicIterator(
            n,
            k
        )
    }

    /**
     * Creates a comparator.
     * When performing a comparison, if an element of the array is not
     * within the interval [0, `n`), an `IllegalArgumentException`
     * will be thrown.
     *
     * @return a comparator.
     */
    fun comparator(): Comparator<IntArray> {
        return LexicographicComparator(
            n,
            k
        )
    }

    /**
     * Lexicographic combinations iterator.
     *
     *
     * Implementation follows Algorithm T in *The Art of Computer Programming*
     * Internet Draft (PRE-FASCICLE 3A), "A Draft of Section 7.2.1.3 Generating All
     * Combinations, D. Knuth, 2004.
     *
     *
     * The degenerate cases `k == 0` and `k == n` are NOT handled by this
     * implementation. It is assumed that `n > k > 0`.
     *
     */
    private class LexicographicIterator
        /**
         * Construct a CombinationIterator to enumerate `k`-sets from a set
         * of size `n`.
         *
         *
         * NOTE: It is assumed that `n > k > 0`.
         *
         *
         * @param n Size of the set from which subsets are enumerated.
         * @param k Size of the subsets to enumerate.
         */
        internal constructor(
            n: Int,
            /** Size of subsets returned by the iterator.  */
            private val k: Int
        ) : MutableIterator<IntArray> {

        /**
         * c[1], ..., c[k] stores the next combination; c[k + 1], c[k + 2] are
         * sentinels.
         *
         *
         * Note that c[0] is "wasted" but this makes it a little easier to
         * follow the code.
         *
         */
        private val c: IntArray = IntArray(k + 3)

        /** Return value for [.hasNext].  */
        private var more = true

        /** Marker: smallest index such that `c[j + 1] > j`.  */
        private var j: Int

        /**
         * {@inheritDoc}
         */
        override fun hasNext(): Boolean {
            return more
        }

        /**
         * {@inheritDoc}
         */
        override fun next(): IntArray {
            if (!more) {
                throw NoSuchElementException()
            }
            // Copy return value (prepared by last activation)
            val ret = IntArray(k)
            c.copyInto(ret, 0, 1, 1 + k)

            // Prepare next iteration
            // T2 and T6 loop
            var x = 0
            if (j > 0) {
                x = j
                c[j] = x
                j--
                return ret
            }
            // T3
            j = if (c[1] + 1 < c[2]) {
                c[1]++
                return ret
            } else {
                2
            }
            // T4
            var stepDone = false
            while (!stepDone) {
                c[j - 1] = j - 2
                x = c[j] + 1
                if (x == c[j + 1]) {
                    j++
                } else {
                    stepDone = true
                }
            }
            // T5
            if (j > k) {
                more = false
                return ret
            }
            // T6
            c[j] = x
            j--
            return ret
        }

        /**
         * Not supported.
         */
        override fun remove() {
            throw UnsupportedOperationException()
        }

        init {
            // Initialize c to start with lexicographically first k-set
            for (i in 1..k) {
                c[i] = i - 1
            }
            // Initialize sentinels
            c[k + 1] = n
            c[k + 2] = 0
            j = k // Set up invariant: j is smallest index such that c[j + 1] > j
        }
    }

    /**
     * Iterator with just one element to handle degenerate cases (full array,
     * empty array) for combination iterator.
     */
    private class SingletonIterator
    /**
     * Create a singleton iterator providing the given array.
     *
     * @param n Size of the singleton array returned by the iterator.
     */ internal constructor(
        /** Number of elements of the singleton array.  */
        private val n: Int
    ) : MutableIterator<IntArray> {

        /** True on initialization, false after first call to next.  */
        private var more = true

        /**
         * @return `true` until next is called the first time,
         * then `false`.
         */
        override fun hasNext(): Boolean {
            return more
        }

        /**
         * @return the singleton at the first activation.
         * @throws NoSuchElementException after the first activation.
         */
        override fun next(): IntArray {
            return if (more) {
                more = false
                // Create singleton.
                val s = IntArray(n)
                for (i in 0 until n) {
                    s[i] = i
                }
                s
            } else {
                throw NoSuchElementException()
            }
        }

        /**
         * Unsupported.
         *
         * @throws UnsupportedOperationException Remove is not supported.
         */
        override fun remove() {
            throw UnsupportedOperationException()
        }

    }

    /**
     * Defines a lexicographic ordering of the combinations.
     *
     * The comparison is based on the value (in base 10) represented
     * by the digit (interpreted in base `n`) in the input array,
     * in reverse order.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    private class LexicographicComparator
    /**
     * @param n Size of the set from which subsets are selected.
     * @param k Size of the subsets to be enumerated.
     */ internal constructor(
        /** Size of the set from which combinations are drawn.  */
        private val n: Int,
        /** Number of elements in each combination.  */
        private val k: Int
    ) : Comparator<IntArray> {

        /**
         * {@inheritDoc}
         *
         * @throws IllegalArgumentException if the array lengths are not
         * equal to `k`.
         * @throws IllegalArgumentException if an element of the array is not
         * within the interval [0, `n`).
         */
        override fun compare(
            a: IntArray,
            b: IntArray
        ): Int {
            if (a.size != k) {
                throw CombinatoricsException(
                    CombinatoricsException.MISMATCH(
                        a.size,
                        k
                    )
                )
            }
            if (b.size != k) {
                throw CombinatoricsException(
                    CombinatoricsException.MISMATCH(
                        b.size,
                        k
                    )
                )
            }

            // Method "lexNorm" works with ordered arrays.
            val c1s: IntArray = a.copyOf(k)
            val c2s: IntArray = b.copyOf(k)
            c1s.sort()
            c2s.sort()
            val v1 = lexNorm(c1s)
            val v2 = lexNorm(c2s)
            return v1.compareTo(v2)
        }

        /**
         * Computes the value (in base 10) represented by the digit
         * (interpreted in base `n`) in the input array in reverse
         * order.
         * For example if `c` is `{3, 2, 1}`, and `n`
         * is 3, the method will return 18.
         *
         * @param c Input array.
         * @return the lexicographic norm.
         * @throws IllegalArgumentException if an element of the array is not
         * within the interval [0, `n`).
         */
        private fun lexNorm(c: IntArray): Long {
            var ret: Long = 0
            for (i in c.indices) {
                val digit = c[i]
                if (digit < 0 ||
                    digit >= n
                ) {
                    throw CombinatoricsException(
                        CombinatoricsException.OUT_OF_RANGE(
                            digit,
                            0,
                            n - 1
                        )
                    )
                }
                ret += c[i] * ArithmeticUtils.pow(n.toLong(), i)
            }
            return ret
        }

    }

    companion object {
        /**
         * @param n Size of the set from which subsets are selected.
         * @param k Size of the subsets to be enumerated.
         * @throws IllegalArgumentException if `n < 0`.
         * @throws IllegalArgumentException if `k > n` or `k < 0`.
         * @return a new instance.
         */
        fun of(
            n: Int,
            k: Int
        ): Combinations {
            return Combinations(n, k)
        }
    }

    /**
     * @param n Size of the set from which subsets are selected.
     * @param k Size of the subsets to be enumerated.
     * @throws IllegalArgumentException if `n < 0`.
     * @throws IllegalArgumentException if `k > n` or `k < 0`.
     */
    init {
        checkBinomial(n, k)
        this.n = n
        this.k = k
    }
}
