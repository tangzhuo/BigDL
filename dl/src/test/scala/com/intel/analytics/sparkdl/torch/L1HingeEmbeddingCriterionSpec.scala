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
package com.intel.analytics.sparkdl.torch

import com.intel.analytics.sparkdl.nn.L1HingeEmbeddingCriterion
import com.intel.analytics.sparkdl.tensor.Tensor
import com.intel.analytics.sparkdl.utils.RandomGenerator._
import com.intel.analytics.sparkdl.utils.Table
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.collection.mutable.HashMap
import scala.util.Random


class L1HingeEmbeddingCriterionSpec extends FlatSpec with BeforeAndAfter with Matchers{
  before {
    if (!TH.hasTorch()) {
      cancel("Torch is not installed")
    }
  }

  "A L1HingeEmbeddingCriterion" should "generate correct output and grad with y == 1 " in {
    val seed = 2
    RNG.setSeed(seed)
    val module = new L1HingeEmbeddingCriterion[Double](0.6)

    val input1 = Tensor[Double](2).apply1(e => Random.nextDouble())
    val input2 = Tensor[Double](2).apply1(e => Random.nextDouble())
    val input = new Table()
    input(1.0) = input1
    input(2.0) = input2

    val target = new Table()
    target(1.0) = 1.0

    val start = System.nanoTime()
    val output = module.forward(input, target)
    val gradInput = module.backward(input, target)
    val end = System.nanoTime()
    val scalaTime = end - start

    val code = "torch.manualSeed(" + seed + ")\n" +
      "module = nn.L1HingeEmbeddingCriterion(0.6)\n" +
      "output = module:forward(input, 1)\n" +
      "gradInput = module:backward(input, 1)\n"

    val (luaTime, torchResult) = TH.run(code, Map("input" -> input), Array("output", "gradInput"))
    val luaOutput1 = torchResult("output").asInstanceOf[Double]
    val luaOutput2 = torchResult("gradInput").asInstanceOf[HashMap[Double, Tensor[Double]]]

    luaOutput1 should be(output)
    val luagradInput1 = luaOutput2.get(1.0).getOrElse(null)
    val luagradInput2 = luaOutput2.get(2.0).getOrElse(null)

    val gradInput1 = gradInput.apply(1.toDouble).asInstanceOf[Tensor[Double]]
    val gradInput2 = gradInput.apply(2.toDouble).asInstanceOf[Tensor[Double]]
    gradInput1 should be(luagradInput1)
    gradInput2 should be(luagradInput2)

    println("Test case : L1HingeEmbeddingCriterion, Torch : " + luaTime +
      " s, Scala : " + scalaTime / 1e9 + " s")
  }

  "A L1HingeEmbeddingCriterion" should "generate correct output and grad with y == -1 " in {
    val seed = 2
    RNG.setSeed(seed)
    val module = new L1HingeEmbeddingCriterion[Double](0.6)

    val input1 = Tensor[Double](2).apply1(e => Random.nextDouble())
    val input2 = Tensor[Double](2).apply1(e => Random.nextDouble())
    val input = new Table()
    input(1.0) = input1
    input(2.0) = input2

    val target = new Table()
    target(1.0) = -1.0

    val start = System.nanoTime()
    val output = module.forward(input, target)
    val gradInput = module.backward(input, target)
    val end = System.nanoTime()
    val scalaTime = end - start

    val code = "torch.manualSeed(" + seed + ")\n" +
      "module = nn.L1HingeEmbeddingCriterion(0.6)\n" +
      "output = module:forward(input, -1.0)\n" +
      "gradInput = module:backward(input, -1.0)\n"

    val (luaTime, torchResult) = TH.run(code, Map("input" -> input), Array("output", "gradInput"))
    val luaOutput1 = torchResult("output").asInstanceOf[Double]
    val luaOutput2 = torchResult("gradInput").asInstanceOf[HashMap[Double, Tensor[Double]]]

    luaOutput1 should be(output)
    val luagradInput1 = luaOutput2.get(1.0).getOrElse(null)
    val luagradInput2 = luaOutput2.get(2.0).getOrElse(null)

    val gradInput1 = gradInput.apply(1.toDouble).asInstanceOf[Tensor[Double]]
    val gradInput2 = gradInput.apply(2.toDouble).asInstanceOf[Tensor[Double]]
    gradInput1 should be(luagradInput1)
    gradInput2 should be(luagradInput2)

    println("Test case : L1HingeEmbeddingCriterion, Torch : " + luaTime +
      " s, Scala : " + scalaTime / 1e9 + " s")
  }
}