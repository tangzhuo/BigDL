/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.analytics.sparkdl.tensor

import com.intel.analytics.sparkdl.utils.T
import org.scalatest.{FlatSpec, Matchers}

class DenseTensorMathSpec extends FlatSpec with Matchers {
  "vector + scalar" should "be correct" in {
    val s = 2.0
    val v: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val r = v + s
    r(Array(1)) should be(3.0)
    r(Array(2)) should be(4.0)
    r(Array(3)) should be(5.0)
  }

  "vector + vector" should "be correct" in {
    val v1: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val v2: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val r = v1 + v2
    r(Array(1)) should be(2.0)
    r(Array(2)) should be(4.0)
    r(Array(3)) should be(6.0)
  }

  "vector + vector which is not contiguous" should "be correct" in {
    val v1: Tensor[Double] = new DenseTensor[Double](2, 4).fill(1)
    v1.t()
    val v2: Tensor[Double] = new DenseTensor(torch.storage(
      Array(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)))
    val r = v1 + v2
    r(Array(1, 1)) should be(2.0)
    r(Array(1, 2)) should be(3.0)
    r(Array(1, 3)) should be(4.0)
    r(Array(1, 4)) should be(5.0)
    r(Array(2, 1)) should be(6.0)
    r(Array(2, 2)) should be(7.0)
    r(Array(2, 3)) should be(8.0)
    r(Array(2, 4)) should be(9.0)
  }

  "vector - scalar" should "be correct" in {
    val s = 2.0
    val v: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val r = v - s
    r(Array(1)) should be(-1.0)
    r(Array(2)) should be(0.0)
    r(Array(3)) should be(1.0)
  }

  "vector - vector" should "be correct" in {
    val v1: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val v2: Tensor[Double] = new DenseTensor(torch.storage(Array(2.0, 0.0, -1.0)))
    val r = v1 - v2
    r(Array(1)) should be(-1.0)
    r(Array(2)) should be(2.0)
    r(Array(3)) should be(4.0)
  }

  "vector * scalar" should "be correct" in {
    val s = 2.0
    val v: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val r = v * s
    r(Array(1)) should be(2.0)
    r(Array(2)) should be(4.0)
    r(Array(3)) should be(6.0)
  }

  "vector * vector" should "be correct" in {
    val v1: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val v2: Tensor[Double] = new DenseTensor(torch.storage(Array(2.0, 0.0, -1.0)))
    val r = v1 * v2
    r(Array(1)) should be(-1.0)
  }

  "matrix * vector" should "be correct" in {
    val mat: Tensor[Double] = new DenseTensor(2, 3)
    mat(Array(1, 1)) = 2
    mat(Array(1, 2)) = 4
    mat(Array(1, 3)) = 3
    mat(Array(2, 1)) = 5
    mat(Array(2, 2)) = 6
    mat(Array(2, 3)) = 1

    val vec: Tensor[Double] = new DenseTensor(torch.storage(Array(3.0, 1, 1)))
    val r = mat * vec
    r(Array(1)) should be(13.0)
    r(Array(2)) should be(22.0)
  }

  "transpose matrix * vector" should "be correct" in {
    val mat: Tensor[Double] = new DenseTensor(3, 2)
    mat(Array(1, 1)) = 2
    mat(Array(1, 2)) = 4
    mat(Array(2, 1)) = 3
    mat(Array(2, 2)) = 5
    mat(Array(3, 1)) = 6
    mat(Array(3, 2)) = 1

    val mat1 = mat.t

    val vec: Tensor[Double] = new DenseTensor(torch.storage(Array(3.0, 1, 1)))
    val r = mat1 * vec
    r(Array(1)) should be(15.0)
    r(Array(2)) should be(18.0)
  }

  "uncontiguous matrix * vector" should "be correct" in {
    val tensor: Tensor[Double] = new DenseTensor(3, 2, 2)
    tensor(Array(1, 1, 1)) = 2
    tensor(Array(1, 2, 1)) = 4
    tensor(Array(2, 1, 1)) = 3
    tensor(Array(2, 2, 1)) = 5
    tensor(Array(3, 1, 1)) = 6
    tensor(Array(3, 2, 1)) = 1

    val matrix = tensor(T(T(), T(), 1)).t()

    val vec: Tensor[Double] = new DenseTensor(torch.storage(Array(3.0, 1, 1)))
    val r = matrix * vec
    r(Array(1)) should be(15.0)
    r(Array(2)) should be(18.0)
  }

  "matrix * matrix" should "be correct" in {
    val mat1: Tensor[Double] = new DenseTensor(3, 2)
    var i = 0
    mat1.apply1(_ => {
      i = i + 1; i
    })
    val mat2: Tensor[Double] = new DenseTensor(2, 3)
    i = 0
    mat2.apply1(_ => {
      i = i + 1; i
    })
    val r = mat2 * mat1
    r(Array(1, 1)) should be(22)
    r(Array(1, 2)) should be(28)
    r(Array(2, 1)) should be(49)
    r(Array(2, 2)) should be(64)
  }

  "transpose matrix * matrix" should "be correct" in {
    val mat1: Tensor[Double] = new DenseTensor(3, 2)
    var i = 0
    mat1.apply1(_ => {
      i = i + 1; i
    })
    val mat2: Tensor[Double] = new DenseTensor(3, 2)
    i = 0
    mat2.apply1(_ => {
      i = i + 1; i
    })
    val r = mat2.t * mat1
    r(Array(1, 1)) should be(35)
    r(Array(1, 2)) should be(44)
    r(Array(2, 1)) should be(44)
    r(Array(2, 2)) should be(56)
  }

  "matrix * transpose matrix" should "be correct" in {
    val mat1: Tensor[Double] = new DenseTensor(2, 3)
    var i = 0
    mat1.apply1(_ => {
      i = i + 1; i
    })
    val mat2: Tensor[Double] = new DenseTensor(2, 3)
    i = 0
    mat2.apply1(_ => {
      i = i + 1; i
    })
    val r = mat2 * mat1.t
    r(Array(1, 1)) should be(14)
    r(Array(1, 2)) should be(32)
    r(Array(2, 1)) should be(32)
    r(Array(2, 2)) should be(77)
  }

  "transpose matrix * transpose matrix" should "be correct" in {
    val mat1: Tensor[Double] = new DenseTensor(3, 2)
    var i = 0
    mat1.apply1(_ => {
      i = i + 1; i
    })
    val mat2: Tensor[Double] = new DenseTensor(2, 3)
    i = 0
    mat2.apply1(_ => {
      i = i + 1; i
    })
    val r = mat1.t * mat2.t
    r(Array(1, 1)) should be(22)
    r(Array(1, 2)) should be(49)
    r(Array(2, 1)) should be(28)
    r(Array(2, 2)) should be(64)
  }

  "noncontiguous matrix * noncontiguous matrix" should "be correct" in {
    val tensor: Tensor[Double] = new DenseTensor(3, 2, 2)
    tensor(Array(1, 1, 1)) = 1
    tensor(Array(1, 2, 1)) = 2
    tensor(Array(2, 1, 1)) = 3
    tensor(Array(2, 2, 1)) = 4
    tensor(Array(3, 1, 1)) = 5
    tensor(Array(3, 2, 1)) = 6

    val mat1: Tensor[Double] = tensor(T(T(), T(), 1)).t
    val mat2: Tensor[Double] = tensor(T(T(), T(), 1))

    val r = mat1 * mat2
    r(Array(1, 1)) should be(35)
    r(Array(1, 2)) should be(44)
    r(Array(2, 1)) should be(44)
    r(Array(2, 2)) should be(56)
  }

  "vector / scalar" should "be correct" in {
    val s = 2.0
    val v: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val r = v / s
    r(Array(1)) should be(0.5)
    r(Array(2)) should be(1.0)
    r(Array(3)) should be(1.5)
  }

  "vector / vector" should "be correct" in {
    val v1: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val v2: Tensor[Double] = new DenseTensor(torch.storage(Array(2.0, 1.0, -1.0)))
    val r = v1 / v2
    r(Array(1)) should be(0.5)
    r(Array(2)) should be(2.0)
    r(Array(3)) should be(-3.0)
  }

  "-vector" should "be correct" in {
    val v: Tensor[Double] = new DenseTensor(torch.storage(Array(1.0, 2.0, 3.0)))
    val r = -v
    r(Array(1)) should be(-1.0)
    r(Array(2)) should be(-2.0)
    r(Array(3)) should be(-3.0)
  }

  "max operation" should "return correct value" in {
    val t: Tensor[Double] = new DenseTensor(3, 3)
    var i = 0
    t.apply1(v => {
      i = i + 1; i
    })

    t.max() should be(9)
  }

  "max with dim" should "return correct value" in {
    val test = torch.Tensor[Double](torch.storage(Array(1.0, 2, 3, 4, 5, 6, 7, 8)), 1, Array(2, 4))
    val (values1, indices1) = test.max(1)
    values1 should be(torch.Tensor[Double](torch.storage(Array(5.0, 6, 7, 8)), 1, Array(1, 4)))
    indices1 should be(torch.Tensor[Double](torch.storage(Array(2.0, 2, 2, 2)), 1, Array(1, 4)))

    val (values2, indices2) = test.max(2)
    values2 should be(torch.Tensor[Double](torch.storage(Array(4.0, 8.0)), 1, Array(2, 1)))
    indices2 should be(torch.Tensor[Double](torch.storage(Array(4.0, 4)), 1, Array(2, 1)))
  }

  "max with dim on 1d tensor" should "return correct value" in {
    val test = torch.Tensor[Double](torch.storage(Array(1.0, 2, 3, 4, 5, 6, 7, 8)))
    val (values, indices) = test.max(1)
    values should be(torch.Tensor[Double](torch.storage(Array(8.0))))
    indices should be(torch.Tensor[Double](torch.storage(Array(8.0))))
  }

  "sum operation" should "return correct value" in {
    val t: Tensor[Double] = new DenseTensor(2, 3)
    var i = 0
    t.apply1(e => {
      i = i + 1; i
    })
    t.sum() should be(21)

    val result1 = t.sum(1)
    result1.size(1) should be(1)
    result1.size(2) should be(3)

    result1(Array(1, 1)) should be(5)
    result1(Array(1, 2)) should be(7)
    result1(Array(1, 3)) should be(9)


    val result2 = t.sum(2)
    result2.size(1) should be(2)
    result2.size(2) should be(1)

    result2(Array(1, 1)) should be(6)
    result2(Array(2, 1)) should be(15)
  }

  "addmm" should "return correct value" in {
    val a_data = Array(
      1.0, 2, 3, 4,
      1, 2, 3, 4,
      1, 2, 3, 4
    )
    val a = new DenseTensor[Double](torch.storage(a_data), 1, Array(3, 4))


    val b_data = Array(
      1.0, 2,
      1, 2,
      1, 2,
      1, 2
    )
    val b = new DenseTensor[Double](torch.storage(b_data), 1, Array(4, 2))

    val c = torch.Tensor[Double]()
    c.resize(Array(3, 2))
    c.addmm(a, b)

    val expect_c_data = Array(
      10.0, 20.0,
      10, 20,
      10, 20
    )

    val expect_c = new DenseTensor[Double](torch.storage(expect_c_data), 1, Array(3, 2))
    c.map(expect_c, (a, b) => {
      a should be(b +- 1e-6)
      a
    })
  }

  "addmm plus another tensor" should "return correct value" in {
    val a_data = Array(
      1.0, 2, 3, 4,
      1, 2, 3, 4,
      1, 2, 3, 4
    )
    val a = new DenseTensor[Double](torch.storage(a_data), 1, Array(3, 4))


    val b_data = Array(
      1.0, 2,
      1, 2,
      1, 2,
      1, 2
    )
    val b = new DenseTensor[Double](torch.storage(b_data), 1, Array(4, 2))

    val m_data = Array(
      1.0, 2,
      1, 2,
      1, 2
    )
    val m = new DenseTensor[Double](torch.storage(m_data), 1, Array(3, 2))

    val c = torch.Tensor[Double]()
    c.addmm(m, a, b)

    val expect_c_data = Array(
      11.0, 22.0,
      11, 22,
      11, 22
    )

    val expect_c = new DenseTensor[Double](torch.storage(expect_c_data), 1, Array(3, 2))
    c.map(expect_c, (a, b) => {
      a should be(b +- 1e-6)
      a
    })
  }

  "uniform" should "return correct value" in {
    val t = torch.Tensor[Double]()
    for (i <- 0 to 1000) {
      val rand = t.uniform()
      rand should be(0.5 +- 0.5)
    }
  }

  "uniform(n)" should "return correct value" in {
    val t = torch.Tensor[Double]()
    t.uniform(1.0) should be(1.0)
    for (i <- 0 to 1000) {
      val rand = t.uniform(11.0)
      rand should be(6.0 +- 5.0)
    }
  }

  "uniform(l, n)" should "return correct value" in {
    val t = torch.Tensor[Double]()
    t.uniform(1.0, 1.0) should be(1.0)
    t.uniform(-2.0, -2.0) should be(-2.0)
    for (i <- 0 to 1000) {
      val rand = t.uniform(-11.0, 11.0)
      rand should be(0.0 +- 11.0)
    }
  }

  "mean operation" should "return correct value" in {
    val t: Tensor[Double] = new DenseTensor(2, 3)
    var i = 0
    t.apply1(e => {
      i = i + 1; i
    })
    t.mean() should be(3.5)

    val result1 = t.mean(1)
    result1.size(1) should be(1)
    result1.size(2) should be(3)

    result1(Array(1, 1)) should be(2.5)
    result1(Array(1, 2)) should be(3.5)
    result1(Array(1, 3)) should be(4.5)


    val result2 = t.mean(2)
    result2.size(1) should be(2)
    result2.size(2) should be(1)

    result2(Array(1, 1)) should be(2)
    result2(Array(2, 1)) should be(5)
  }

  "mean operation on 3D tensor" should "return correct value" in {
    val t: Tensor[Double] = new DenseTensor(2, 3, 4)
    var i = 0
    t.apply1(e => {
      i = i + 1; i
    })
    t.mean() should be(12.5)

    val result1 = t.mean(1)
    result1.size(1) should be(1)
    result1.size(2) should be(3)
    result1.size(3) should be(4)

    result1(Array(1, 1, 1)) should be(7)
    result1(Array(1, 1, 2)) should be(8)
    result1(Array(1, 1, 3)) should be(9)
    result1(Array(1, 1, 4)) should be(10)
    result1(Array(1, 2, 1)) should be(11)
    result1(Array(1, 2, 2)) should be(12)
    result1(Array(1, 2, 3)) should be(13)
    result1(Array(1, 2, 4)) should be(14)
    result1(Array(1, 3, 1)) should be(15)
    result1(Array(1, 3, 2)) should be(16)
    result1(Array(1, 3, 3)) should be(17)
    result1(Array(1, 3, 4)) should be(18)

    val result2 = t.mean(2)
    result2.size(1) should be(2)
    result2.size(2) should be(1)
    result2.size(3) should be(4)

    result2(Array(1, 1, 1)) should be(5)
    result2(Array(1, 1, 2)) should be(6)
    result2(Array(1, 1, 3)) should be(7)
    result2(Array(1, 1, 4)) should be(8)
    result2(Array(2, 1, 1)) should be(17)
    result2(Array(2, 1, 2)) should be(18)
    result2(Array(2, 1, 3)) should be(19)
    result2(Array(2, 1, 4)) should be(20)

    val result3 = t.mean(3)
    result3.size(1) should be(2)
    result3.size(2) should be(3)
    result3.size(3) should be(1)

    result3(Array(1, 1, 1)) should be(2.5)
    result3(Array(1, 2, 1)) should be(6.5)
    result3(Array(1, 3, 1)) should be(10.5)
    result3(Array(2, 1, 1)) should be(14.5)
    result3(Array(2, 2, 1)) should be(18.5)
    result3(Array(2, 3, 1)) should be(22.5)
  }

  "topk" should "be correct for 1d tensor" in {
    val t = torch.Tensor(torch.storage(Array(0.0, 1.0, 5.0, 3.0, 9.0, 0.8, 6.3)))
    val (v, i) = t.topk(5)
    v should be(torch.Tensor(torch.storage(Array(0.0, 0.8, 1.0, 3.0, 5.0))))
    i should be(torch.Tensor(torch.storage(Array(1.0, 6.0, 2.0, 4.0, 3.0))))
  }

  "topk" should "be correct for 2d tensor" in {
    val t = torch.Tensor(torch.storage(Array(
      0.0, 1.0, 5.0, 3.0, 9.0, 0.8, 6.3,
      0.0, 1.0, 5.0, 3.0, 9.0, 0.8, 6.3,
      0.0, 1.0, 5.0, 3.0, 9.0, 0.8, 6.3,
      0.0, 1.0, 5.0, 3.0, 9.0, 0.8, 6.3,
      0.0, 1.0, 5.0, 3.0, 9.0, 0.8, 6.3
    )), 1, Array(5, 7))
    val (v, i) = t.topk(5)
    v should be(torch.Tensor(torch.storage(Array(
      0.0, 0.8, 1.0, 3.0, 5.0,
      0.0, 0.8, 1.0, 3.0, 5.0,
      0.0, 0.8, 1.0, 3.0, 5.0,
      0.0, 0.8, 1.0, 3.0, 5.0,
      0.0, 0.8, 1.0, 3.0, 5.0
    )), 1, Array(5, 5)))
    i should be(torch.Tensor(torch.storage(Array(
      1.0, 6.0, 2.0, 4.0, 3.0,
      1.0, 6.0, 2.0, 4.0, 3.0,
      1.0, 6.0, 2.0, 4.0, 3.0,
      1.0, 6.0, 2.0, 4.0, 3.0,
      1.0, 6.0, 2.0, 4.0, 3.0
    )), 1, Array(5, 5)))
  }
}