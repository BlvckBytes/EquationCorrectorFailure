package me.blvckbytes.equation_corrector

import java.math.BigInteger

class EquationCorrector {

  companion object {
    private const val withExponentiation = true

    @JvmStatic
    fun main(args: Array<String>) {
      val totalAmountOfAdditionalDigits = 4
      val numberOfColumns = 4

      val acrossColumnsPermutations = AcrossColumnsDistribution.getPermutations(totalAmountOfAdditionalDigits, numberOfColumns)

      var totalPermutations = 0

      var minDelta: BigInteger? = null
      val minDeltaPermutations = mutableListOf<List<PerColumnDistribution>>()

      // Distributions of additional digits across columns
      for (acrossColumnPermutation in acrossColumnsPermutations) {
        val columnPermutations = mutableListOf<List<PerColumnDistribution>>()

        // Number of additional digits per each column
        for (columnTotal in acrossColumnPermutation) {
          // Permutations of adding this number of additional digits to the column value
          val perColumnPermutations = PerColumnDistribution.getPermutations(columnTotal, withExponentiation)
          columnPermutations.add(perColumnPermutations)
        }

        val equationPermutations = cartesianProduct(columnPermutations)
        totalPermutations += equationPermutations.size

        for (equationPermutation in equationPermutations) {
          // Let's just hardcode it, for now at least

          // 2 + 7 = 1 * 3
          // a + b = c * d

          val a = equationPermutation[0].apply(2, 3)
          val b = equationPermutation[1].apply(7, 3)
          val c = equationPermutation[2].apply(1, 3)
          val d = equationPermutation[3].apply(3, 3)

          val delta = ((a + b) - (c * d)).abs()

          if (minDelta == null || minDelta > delta) {
            minDelta = delta
            minDeltaPermutations.clear()
          }

          if (delta == minDelta)
            minDeltaPermutations.add(equationPermutation)
        }
      }

      println("Minimum delta accomplished: $minDelta, with:\n")
      println(visualizePermutations(minDeltaPermutations))

      println("Tried $totalPermutations permutations in total")
    }

    private fun visualizePermutations(permutations: List<List<PerColumnDistribution>>): String {
      val result = StringBuilder()

      val maxLengthPerColumn = IntArray(4) { 0 }
      val rows = mutableListOf<Array<String>>()
      val infixSymbols = arrayOf('+', '=', '*')

      for (permutation in permutations) {
        val columns = arrayOf(
          permutation[0].visualize(3, '2'),
          permutation[1].visualize(3, '7'),
          permutation[2].visualize(3, '1'),
          permutation[3].visualize(3, '3'),
        )

        for (columnIndex in columns.indices) {
          val columnLength = columns[columnIndex].length

          if (maxLengthPerColumn[columnIndex] < columnLength)
            maxLengthPerColumn[columnIndex] = columnLength
        }

        rows.add(columns)
      }

      for (rowIndex in rows.indices) {
        val row = rows[rowIndex]

        for (columnIndex in row.indices) {
          val column = row[columnIndex]
          val maxColumnLength = maxLengthPerColumn[columnIndex]
          val paddedColumn = column.padEnd(maxColumnLength, ' ')

          result.append(paddedColumn)

          if (columnIndex != row.size - 1)
            result.append(' ').append(infixSymbols[columnIndex]).append(' ')
        }

        result.append(" [")
        result.append(permutations[rowIndex].joinToString())
        result.append("]\n")
      }

      return result.toString()
    }

    private fun <T> cartesianProduct(sets: List<List<T>>): List<List<T>> {
      val result = mutableListOf<List<T>>()
      cartesianProductHelper(sets, 0, mutableListOf(), result)
      return result
    }

    private fun <T> cartesianProductHelper(
      sets: List<List<T>>,
      index: Int,
      current: MutableList<T>,
      result: MutableList<List<T>>
    ) {
      if (index == sets.size) {
        result.add(current.toMutableList())
        return
      }

      for (element in sets[index]) {
        current.add(element)
        cartesianProductHelper(sets, index + 1, current, result)
        current.removeAt(current.size - 1)
      }
    }
  }
}