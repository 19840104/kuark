package io.kuark.ui.jfx.controls.table.cell.factory

import io.kuark.base.lang.string.isNumeric
import io.kuark.ui.jfx.controls.XTextFieldTableCell
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.util.Callback
import javafx.util.StringConverter

class NumberTextFieldTableCellFactory<S> : Callback<TableColumn<S, Int>?, TableCell<S, Int>?> {

    override fun call(param: TableColumn<S, Int>?): TableCell<S, Int> {
        return XTextFieldTableCell<S, Int>(object : StringConverter<Int?>() {

            override fun toString(int: Int?): String? {
                return int?.toString() ?: ""
            }

            override fun fromString(string: String): Int? {
                return return if (string.isBlank() || !string.isNumeric()) null else string.toInt()
            }
        }) as TableCell<S, Int>
    }

}