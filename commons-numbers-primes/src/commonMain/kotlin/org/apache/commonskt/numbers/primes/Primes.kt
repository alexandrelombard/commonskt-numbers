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
package org.apache.commonskt.numbers.primes

/**
 * Methods related to prime numbers in the range of `int`.
 *
 *  * primality test
 *  * prime number generation
 *  * factorization
 *
 */
object Primes {
    /** Exception message format when an argument is too small.  */
    fun NUMBER_TOO_SMALL(a: Int, b: Int) = "$a is smaller than the minimum ($b)"

    /**
     * Primality test: tells if the argument is a (provable) prime or not.
     *
     *
     * It uses the Miller-Rabin probabilistic test in such a way that a result is guaranteed:
     * it uses the firsts prime numbers as successive base (see Handbook of applied cryptography
     * by Menezes, table 4.1).
     *
     * @param n Number to test.
     * @return true if `n` is prime. All numbers &lt; 2 return false.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    fun isPrime(n: Int): Boolean {
        if (n < 2) {
            return false
        }
        for (p in SmallPrimes.PRIMES) {
            if (0 == n % p) {
                return n == p
            }
        }
        return SmallPrimes.millerRabinPrimeTest(n)
    }

    /**
     * Return the smallest prime greater than or equal to n.
     *
     * @param n Positive number.
     * @return the smallest prime greater than or equal to `n`.
     * @throws IllegalArgumentException if n &lt; 0.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    fun nextPrime(n: Int): Int {
        var n = n
        if (n < 0) {
            throw IllegalArgumentException(NUMBER_TOO_SMALL(n, 0))
        }
        if (n == 2) {
            return 2
        }
        n = n or 1 // make sure n is odd
        if (n == 1) {
            return 2
        }
        if (isPrime(n)) {
            return n
        }

        // prepare entry in the +2, +4 loop:
        // n should not be a multiple of 3
        val rem = n % 3
        if (0 == rem) { // if n % 3 == 0
            n += 2 // n % 3 == 2
        } else if (1 == rem) { // if n % 3 == 1
            // if (isPrime(n)) return n;
            n += 4 // n % 3 == 2
        }
        while (true) { // this loop skips all multiple of 3
            if (isPrime(n)) {
                return n
            }
            n += 2 // n % 3 == 1
            if (isPrime(n)) {
                return n
            }
            n += 4 // n % 3 == 2
        }
    }

    /**
     * Prime factors decomposition.
     *
     * @param n Number to factorize: must be  2.
     * @return the list of prime factors of `n`.
     * @throws IllegalArgumentException if n &lt; 2.
     */
    fun primeFactors(n: Int): List<Int> {
        if (n < 2) {
            throw IllegalArgumentException(NUMBER_TOO_SMALL(n, 2))
        }
        return SmallPrimes.trialDivision(n)
    }
}