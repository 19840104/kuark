package io.kuark.ability.data.rdb.support

import org.ktorm.schema.boolean
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar

/**
 * 可维护型的数据库记录的实体
 *
 * @param E 实体类型
 * @author K
 * @since 1.0.0
 */
open class MaintainableTable<E : IMaintainableDbEntity<String, E>>(tableName: String): StringIdTable<E>(tableName) {

    /** 记录创建时间 */
    val createTime = datetime("create_time").bindTo { it.createTime }

    /** 记录创建用户 */
    val createUser = varchar("create_user").bindTo { it.createUser }

    /** 记录更新时间 */
    val updateTime = datetime("update_time").bindTo { it.updateTime }

    /** 记录更新用户 */
    val updateUser = varchar("update_user").bindTo { it.updateUser }

    /** 是否启用 */
    val isActive = boolean("is_active").bindTo { it.isActive }

    /** 是否内置 */
    val isBuiltIn = boolean("is_built_in").bindTo { it.isBuiltIn }

    /** 备注 */
    val remark = varchar("remark").bindTo { it.remark }

}