package me.blvckbytes.equation_corrector

import kotlin.math.pow

class AcrossColumnsDistribution {
  companion object {
    private val permutationCache = mutableMapOf<Int, List<IntArray>>()
    private const val halfIntSize = Int.SIZE_BYTES / 2
    private val halfIntMaxValue = (2.0.pow(halfIntSize * 8) - 1).toInt()

    fun getPermutations(total: Int, numberOfColumns: Int): List<IntArray> {
      return permutationCache.computeIfAbsent(makeHashKey(total, numberOfColumns)) {
        generatePermutations(total, numberOfColumns)
      }
    }

    fun generatePermutations(total: Int, numberOfColumns: Int): List<IntArray> {
      val result = mutableListOf<IntArray>()

      val rootTemplate = IntArray(numberOfColumns) { 0 }
      generatePermutationsRecursion(total, total, 0, numberOfColumns, rootTemplate, result)

      return result
    }

    private fun generatePermutationsRecursion(
      total: Int, remaining: Int,
      column: Int, numberOfColumns: Int,
      template: IntArray,
      result: MutableList<IntArray>
    ) {
      for (columnValue in 0..remaining) {
        template[column] = columnValue

        if (column == numberOfColumns - 1) {
          if (template.sum() == total)
            result.add(template.copyOf())
          continue
        }

        generatePermutationsRecursion(
          total,
          if (remaining == 0) 0 else remaining - columnValue,
          column + 1, numberOfColumns, template, result
        )
      }
    }

    private fun makeHashKey(total: Int, numberOfColumns: Int): Int {
      if (total < 0)
        throw IllegalStateException("total=$total cannot be less than zero")

      if (numberOfColumns < 0)
        throw IllegalStateException("numberOfColumns=$numberOfColumns cannot be less than zero")

      if (total > halfIntMaxValue)
        throw IllegalStateException("total=$total cannot be bigger than $halfIntMaxValue, due to packing")

      if (numberOfColumns > halfIntMaxValue)
        throw IllegalStateException("numberOfColumns=$numberOfColumns cannot be bigger than $halfIntMaxValue, due to packing")

      return (total and halfIntMaxValue) or (numberOfColumns and halfIntMaxValue)
    }
  }
}