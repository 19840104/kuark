package org.kuark.tools.codegen.dao

import me.liuwj.ktorm.schema.*
import org.kuark.tools.codegen.po.CodeGenFile
import org.kuark.data.jdbc.support.StringIdTable

/**
 * 代码生成-文件信息数据库实体DAO
 *
 * @author K
 * @since 1.0.0
 */
//region your codes 1
object CodeGenFiles : StringIdTable<CodeGenFile>("code_gen_file") {
//endregion your codes 1

    /** 文件名 */
    var filename = varchar("filename").bindTo { it.filename }

    /** 对象名 */
    var objectName = varchar("object_name").bindTo { it.objectName }


    //region your codes 2

    //endregion your codes 2

}