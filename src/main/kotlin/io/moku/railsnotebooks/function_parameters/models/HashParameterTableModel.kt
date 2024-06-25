package io.moku.railsnotebooks.function_parameters.models

class HashParameterTableModel : ParameterTableModel() {
    private val keys = mutableListOf("")

    override fun getColumnCount(): Int = 2

    override fun getColumnName(column: Int): String =
        if (column == 0) {
            "Key"
        } else {
            super.getColumnName(column)
        }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any =
        if (columnIndex == 0) {
            keys[rowIndex]
        } else {
            super.getValueAt(rowIndex, columnIndex)
        }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        val deleteRow = when (columnIndex) {
            0 -> {
                keys[rowIndex] = (aValue as? String) ?: ""
                aValue == "" && values[rowIndex] == ""
            }
            1 -> {
                values[rowIndex] = (aValue as? String) ?: ""
                aValue == "" && keys[rowIndex] == ""
            }
            else -> false
        }
        if (rowIndex == rowCount - 1 && aValue != "") {
            keys.add("")
            values.add("")
            rowCount += 1
            fireTableRowsInserted(rowCount, rowCount)
        }
        if (deleteRow && rowCount > 1) {
            keys.removeAt(rowIndex)
            values.removeAt(rowIndex)
            rowCount -= 1
            fireTableRowsDeleted(rowIndex, rowIndex)
        }
    }

    override fun toValue(): String = keys.mapIndexedNotNull { index, key ->
        if (key.isEmpty() || values[index].isEmpty()) {
            null
        } else {
            "$key: ${values[index]}"
        }
    }.joinToString(", ")
}
