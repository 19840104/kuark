package org.kuark.data.jdbc.metadata

import java.sql.Connection
import java.sql.DatabaseMetaData


object RdbMetadataKit {

    fun getTablesByType(conn: Connection, vararg tableTypes: TableType?): List<Table> {
        val dbMetaData = conn.metaData
        val types = tableTypes?.mapTo(mutableListOf()) { it -> it!!.name }.toTypedArray()
        val talbes = mutableListOf<Table>()
        val tableRs = dbMetaData.getTables(conn.catalog, conn.schema, "%", types)
        tableRs.use {
            while (tableRs.next()) {
                talbes.add(Table().apply {
                    name = tableRs.getString("TABLE_NAME")
                    cat = tableRs.getString("TABLE_CAT")
                    schema = tableRs.getString("TABLE_SCHEM")
                    comment = tableRs.getString("REMARKS")
                    type = TableType.valueOf(tableRs.getString("TABLE_TYPE"))
                })
            }
        }
        return talbes
    }

    fun getTableByName(conn: Connection, tableName: String): Table {
        val dbMetaData = conn.metaData
        val rs = dbMetaData.getColumns(conn.catalog, conn.schema, tableName, null)
        rs.use {
            return Table().apply {
                name = tableName
                cat = rs.getString("TABLE_CAT")
                schema = rs.getString("TABLE_SCHEM")
                comment = rs.getString("REMARKS")
                type = TableType.valueOf(rs.getString("TABLE_TYPE"))
            }
        }
    }

    fun getColumnsByTableName(conn: Connection, tableName: String): Map<String, Column> {
        val dbMetaData = conn.metaData
        val rdbType = RdbType.byProductName(dbMetaData.databaseProductName)
        val linkedMap = linkedMapOf<String, Column>()

        // 获取所有列
        val columnRs = dbMetaData.getColumns(conn.catalog, conn.schema, tableName, null)
        columnRs.use {
            while (columnRs.next()) {
                val column = Column().apply {
                    name = columnRs.getString("COLUMN_NAME")
                    comment = columnRs.getString("REMARKS")
                    jdbcType = columnRs.getInt("DATA_TYPE")
                    jdbcTypeName = columnRs.getString("TYPE_NAME")
                    kotlinType = JdbcTypeToKotlinType.getKotlinType(rdbType, this)
                    length = columnRs.getInt("COLUMN_SIZE")
                    decimalDigits = columnRs.getInt("DECIMAL_DIGITS")
                    defaultValue = columnRs.getString("COLUMN_DEF")
                    isNullable = columnRs.getInt("NULLABLE") == DatabaseMetaData.columnNullable
                    isDictCode = name.toUpperCase().endsWith("__CODE")
                    autoIncrement = columnRs.getString("IS_AUTOINCREMENT")
                }
                linkedMap[column.name] = column
            }
        }

        // 主键
        val primaryKeyRs = dbMetaData.getPrimaryKeys(conn.catalog, conn.schema, tableName)
        primaryKeyRs.use {
            while (primaryKeyRs.next()) {
                val columnName = primaryKeyRs.getString("COLUMN_NAME")
                linkedMap[columnName]!!.isPrimaryKey = true
            }
        }

        // 外键
        val foreignKeyRs = dbMetaData.getImportedKeys(conn.catalog, conn.schema, tableName)
        foreignKeyRs.use {
            while (foreignKeyRs.next()) {
                val columnName = foreignKeyRs.getString("FKCOLUMN_NAME")
                linkedMap[columnName]!!.isForeignKey = true
            }
        }

        // 索引
        val indexInfoRs = dbMetaData.getIndexInfo(conn.catalog, conn.schema, tableName, false, false)
        indexInfoRs.use {
            while (indexInfoRs.next()) {
                val columnName = primaryKeyRs.getString("COLUMN_NAME")
                linkedMap[columnName]!!.isIndexed = true
            }
        }


        // 惟一约束
        val uniqueInfoRs = dbMetaData.getIndexInfo(conn.catalog, conn.schema, tableName, true, false)
        uniqueInfoRs.use {
            while (uniqueInfoRs.next()) {
                val columnName = uniqueInfoRs.getString("COLUMN_NAME")
                linkedMap[columnName]!!.isUnique = true
            }
        }

        return linkedMap
    }

}