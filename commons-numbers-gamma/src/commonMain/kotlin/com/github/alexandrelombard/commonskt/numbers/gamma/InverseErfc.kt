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
 * Inverse of the [complementary error function](http://mathworld.wolfram.com/Erfc.html).
 *
 *
 * This implementation is described in the paper:
 * [Approximating
 * the erfinv function](http://people.maths.ox.ac.uk/gilesm/files/gems_erfinv.pdf) by Mike Giles, Oxford-Man Institute of Quantitative Finance,
 * which was published in GPU Computing Gems, volume 2, 2010.
 * The source code is available [here](http://gpucomputing.net/?q=node/1828).
 *
 */
object InverseErfc {
    /**
     * Returns the inverse complementary error function.
     *
     * @param x Value.
     * @return t such that `x =` [Erfc.value(t)][Erfc.value].
     */
    fun value(x: Double): Double {
        return InverseErf.value(1 - x)
    }
}