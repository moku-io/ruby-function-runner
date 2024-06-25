package io.moku.railsnotebooks.function_parameters.models

import javax.swing.table.AbstractTableModel


sealed class ParameterTableModel: AbstractTableModel() {
    private var innerRowCount = 1
    protected val values = mutableListOf("")

    override fun getRowCount(): Int = innerRowCount
    protected fun setRowCount(value: Int) { innerRowCount = value }

    override fun getColumnName(column: Int) = "Value"

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean  = true

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = values[rowIndex]

    abstract fun toValue(): String


}
