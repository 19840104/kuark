package io.kuark.service.user.provider.dao

import io.kuark.ability.data.rdb.support.MaintainableTable
import io.kuark.service.user.provider.po.UserAuthRole
import me.liuwj.ktorm.schema.varchar

/**
 * 角色数据库实体DAO
 *
 * @author K
 * @since 1.0.0
 */
//region your codes 1
object UserAuthRoles: MaintainableTable<UserAuthRole>("auth_role") {
//endregion your codes 1

    /** 角色名 */
    var roleName = varchar("role_name").bindTo { it.roleName }

    /** 子系统代码 */
    var subSysDictCode = varchar("sub_sys_dict_code").bindTo { it.subSysDictCode }

    /** 所有者id，依业务可以是店铺id、站点id、商户id等 */
    var ownerId = varchar("owner_id").bindTo { it.ownerId }


    //region your codes 2

	//endregion your codes 2

}