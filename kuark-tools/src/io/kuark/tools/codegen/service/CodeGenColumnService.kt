package io.kuark.tools.codegen.service


import io.kuark.base.bean.BeanKit
import io.kuark.data.jdbc.metadata.Column
import io.kuark.data.jdbc.metadata.RdbMetadataKit
import io.kuark.data.jdbc.support.RdbKit
import io.kuark.tools.codegen.core.CodeGeneratorContext
import io.kuark.tools.codegen.dao.CodeGenColumns
import io.kuark.tools.codegen.po.CodeGenColumn
import io.kuark.tools.codegen.vo.ColumnInfo
import me.liuwj.ktorm.dsl.batchInsert
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.filter
import me.liuwj.ktorm.entity.removeIf
import me.liuwj.ktorm.entity.sequenceOf
import java.util.*

/**
 * 生成的文件历史信息服务
 *
 * @author K
 * @since 1.0.0
 */
object CodeGenColumnService {

    fun readColumns(): List<ColumnInfo> {
        val table = CodeGeneratorContext.tableName
        // from meta data
        val columns: Map<String, Column> = RdbMetadataKit.getColumnsByTableName(table)

        // from code_gen_column table
        val codeGenColumns = RdbKit.getDatabase().sequenceOf(CodeGenColumns).filter { it.objectName eq table }
        val columnMap = HashMap<String, CodeGenColumn>(columns.size, 1f)
        for (column in codeGenColumns) {
            columnMap[column.name] = column
        }

        // merge
        val results = mutableListOf<ColumnInfo>()
        for (column in columns.values) {
            val codeGenColumn = columnMap[column.name]
            val columnInfo = ColumnInfo()
            results.add(columnInfo)
            if (codeGenColumn != null) { // old column
                BeanKit.copyProperties(codeGenColumn, columnInfo)
                columnInfo.setCustomComment(codeGenColumn.comment)
                columnInfo.setOrigComment(column.comment)
            } else {
                with(columnInfo) {
                    setName(column.name)
                    setOrigComment(column.comment)
                }
            }
        }
        return results
    }

    fun saveColumns(): Boolean {
        val table = CodeGeneratorContext.tableName
        val columnInfos = CodeGeneratorContext.columns

        // delete old columns first
        RdbKit.getDatabase().sequenceOf(CodeGenColumns).removeIf { it.objectName eq table }

        // insert new columns
        return RdbKit.getDatabase().batchInsert(CodeGenColumns) {
            for (column in columnInfos) {
                item {
                    it.name to column.getName()
                    it.objectName to table
                    it.comment to column.getCustomComment()
                    it.isSearchable to column.getSearchable()
                    it.isSortable to column.getSortable()
                    it.orderInEdit to column.getOrderInEdit()
                    it.orderInList to column.getOrderInList()
                    it.orderInView to column.getOrderInView()
                    it.defaultOrder to column.getDefaultOrder()
                }
            }
        }.isNotEmpty()
    }



}