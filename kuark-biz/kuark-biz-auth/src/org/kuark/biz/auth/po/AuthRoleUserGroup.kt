package org.kuark.biz.auth.po

import org.kuark.data.jdbc.support.DbEntityFactory
import org.kuark.data.jdbc.support.IDbEntity

/**
 * 角色-用户组关系数据库实体
 *
 * @author K
 * @since 1.0.0
 */
//region your codes 1
interface AuthRoleUserGroup: IDbEntity<String, AuthRoleUserGroup> {
//endregion your codes 1

    companion object : DbEntityFactory<AuthRoleUserGroup>()

    /** 角色id */
    var roleId: String

    /** 用户组id */
    var groupId: String


    //region your codes 2

	//endregion your codes 2

}