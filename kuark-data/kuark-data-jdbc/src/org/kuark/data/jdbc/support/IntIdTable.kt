package org.kuark.data.jdbc.support

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int

/**
 * 整型主键的dao
 *
 * @author K
 * @since 1.0.0
 */
open class IntIdTable<E : IDbEntity<Int, E>>(tableName: String): Table<E>(tableName) {

    /** 主键 */
    val id = int("id").primaryKey().bindTo { it.id }

}