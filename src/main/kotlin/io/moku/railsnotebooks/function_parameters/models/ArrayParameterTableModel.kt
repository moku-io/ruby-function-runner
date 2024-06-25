package io.moku.railsnotebooks.function_parameters.models

class ArrayParameterTableModel: ParameterTableModel() {
    override fun getColumnCount(): Int = 1

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        values[rowIndex] = (aValue as? String) ?: ""
        if (rowIndex == rowCount - 1 && aValue != "") {
            values.add("")
            rowCount += 1
            fireTableRowsInserted(rowCount, rowCount)
        }
        if (aValue == "" && rowCount > 1) {
            values.removeAt(rowIndex)
            rowCount -= 1
            fireTableRowsDeleted(rowIndex, rowIndex)
        }
    }

    override fun toValue(): String = values.filter { it.isNotEmpty() }.joinToString(", ")
}
