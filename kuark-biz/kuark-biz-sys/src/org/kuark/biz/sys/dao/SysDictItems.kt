package org.kuark.biz.sys.dao

import me.liuwj.ktorm.schema.*
import org.kuark.biz.sys.po.SysDictItem
import org.kuark.data.jdbc.support.MaintainableTable

/**
 * 字典子表数据库实体DAO
 *
 * @author K
 * @since 1.0.0
 */
//region your codes 1
object SysDictItems: MaintainableTable<SysDictItem>("sys_dict_item") {
//endregion your codes 1

    /** 外键，sys_dict表的主键 */
    var dictId = varchar("dict_id").bindTo { it.dictId }

    /** 字典项编号 */
    var itemCode = varchar("item_code").bindTo { it.itemCode }

    /** 父项编号 */
    var parentCode = varchar("parent_code").bindTo { it.parentCode }

    /** 字典项名称，或其国际化key */
    var itemName = varchar("item_name").bindTo { it.itemName }

    /** 该字典编号在同父节点下的排序号 */
    var seqNo = int("seq_no").bindTo { it.seqNo }


    //region your codes 2

	//endregion your codes 2

}