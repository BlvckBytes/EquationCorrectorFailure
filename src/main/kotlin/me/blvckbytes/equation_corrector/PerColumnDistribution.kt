package me.blvckbytes.equation_corrector

import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class PerColumnDistribution private constructor(
  private val prependCount: Int,
  private val appendCount: Int,
) {
  companion object {
    private val permutationCache = mutableMapOf<Int, List<PerColumnDistribution>>()

    fun getPermutations(total: Int): List<PerColumnDistribution> {
      return permutationCache.computeIfAbsent(total, Companion::generatePermutations)
    }

    private fun generatePermutations(total: Int): List<PerColumnDistribution> {
      val result = mutableListOf<PerColumnDistribution>()

      for (i in 0 .. total) {
        result.add(PerColumnDistribution(i, total - i))
      }

      return result
    }

    private fun digitLength(number: Int): Int {
      return floor(log10(number.toDouble())).toInt() + 1
    }
  }

  fun apply(value: Int, digit: Int): Int {
    if (digit / 10 != 0)
      throw IllegalStateException("digit is not an element of [0;9]")

    val valueDigitLength = digitLength(value)
    var result = 0

    for (i in 0 until prependCount)
      result += (10.0.pow(i + appendCount + valueDigitLength) * digit).toInt()

    result += (10.0.pow(appendCount) * value).toInt()

    for (i in 0 until appendCount)
      result += (10.0.pow(i) * digit).toInt()

    return result
  }

  override fun toString(): String {
    return "($prependCount, $appendCount)"
  }

  fun visualize(digit: Int, letter: Char): String {
    val result = StringBuilder()
    val digitChar = digit.digitToChar().toString()

    result.append(digitChar.repeat(prependCount))
    result.append(letter)
    result.append(digitChar.repeat(appendCount))

    return result.toString()
  }
}