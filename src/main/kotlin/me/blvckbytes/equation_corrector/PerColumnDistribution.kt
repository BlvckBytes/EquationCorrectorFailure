package me.blvckbytes.equation_corrector

import java.math.BigInteger
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class PerColumnDistribution private constructor(
  private val prependCount: Int,
  private val exponentCount: Int,
  private val appendCount: Int,
) {
  companion object {
    private val permutationCache = mutableMapOf<Int, List<PerColumnDistribution>>()
    private val totalMaxValue = (2.0.pow(Int.SIZE_BITS - 1) - 1).toInt()
    private const val withExponentiationFlagValue = 1 shl Int.SIZE_BITS - 1

    fun getPermutations(total: Int, withExponentiation: Boolean): List<PerColumnDistribution> {
      return permutationCache.computeIfAbsent(makeHashKey(total, withExponentiation)) {
        generatePermutations(total, withExponentiation)
      }
    }

    private fun makeHashKey(total: Int, withExponentiation: Boolean): Int {
      if (total < 0)
        throw IllegalStateException("total=$total cannot be less than zero")

      if (total > totalMaxValue)
        throw IllegalStateException("total=$total cannot be bigger than $totalMaxValue, due to packing")

      return total and totalMaxValue or (if (withExponentiation) withExponentiationFlagValue else 0)
    }

    private fun generatePermutations(total: Int, withExponentiation: Boolean): List<PerColumnDistribution> {
      if (withExponentiation) {
        return AcrossColumnsDistribution.generatePermutations(total, 3).map {
          PerColumnDistribution(it[0], it[1], it[2])
        }
      }

      return AcrossColumnsDistribution.generatePermutations(total, 2).map {
        PerColumnDistribution(it[0], 0, it[1])
      }
    }

    private fun digitLength(number: Int): Int {
      return floor(log10(number.toDouble())).toInt() + 1
    }
  }

  fun apply(value: Int, digit: Int): BigInteger {
    if (digit / 10 != 0)
      throw IllegalStateException("digit is not an element of [0;9]")

    val valueDigitLength = digitLength(value)
    var result = 0

    for (i in 0 until prependCount)
      result += (10.0.pow(i + appendCount + valueDigitLength) * digit).toInt()

    result += (10.0.pow(appendCount) * value).toInt()

    result += repeatDigits(digit, appendCount)

    val exponentValue = repeatDigits(digit, exponentCount)

    return BigInteger.valueOf(result.toLong()).pow(if (exponentValue == 0) 1 else exponentValue)
  }

  private fun repeatDigits(digit: Int, count: Int): Int {
    var result = 0

    for (i in 0 until count)
      result += (10.0.pow(i) * digit).toInt()

    return result
  }

  override fun toString(): String {
    return "($prependCount, $exponentCount, $appendCount)"
  }

  fun visualize(digit: Int, letter: Char): String {
    val result = StringBuilder()
    val digitChar = digit.digitToChar().toString()

    result.append(digitChar.repeat(prependCount))
    result.append(letter)
    result.append(digitChar.repeat(appendCount))

    if (exponentCount != 0)
      result.append('^').append(digitChar.repeat(exponentCount))

    return result.toString()
  }
}