package ${packagePrefix}.${moduleName}.dao

import me.liuwj.ktorm.schema.*
import ${packagePrefix}.${moduleName}.po.${className}
<#if daoSuperClass == "MaintainableTable">
import org.kuark.data.jdbc.support.MaintainableTable
<#elseif daoSuperClass == "StringIdTable">
import org.kuark.data.jdbc.support.StringIdTable
<#elseif daoSuperClass == "IntIdTable">
import org.kuark.data.jdbc.support.IntIdTable
<#elseif daoSuperClass == "LongIdTable">
import org.kuark.data.jdbc.support.LongIdTable
</#if>

<@generateClassComment table.comment+"数据库实体DAO"/>
//region your codes 1
object ${className}s: ${daoSuperClass}<${className}>("${table.name}") {
//endregion your codes 1

	<#list columns as column>
	<#if daoSuperClass == "Table" || (daoSuperClass == "StringIdTable" || daoSuperClass == "IntIdTable" || daoSuperClass == "LongIdTable") && column.columnHumpName != "id" || daoSuperClass == "MaintainableTable" && !["id", "create_time", "create_user", "update_time", "update_user", "is_active", "is_built_in", "remark"]?seq_contains(column.name)>
	/** ${column.comment!""} */
	var ${column.columnHumpName} = ${column.ktormSqlTypeFunName}("${column.name}").bindTo { it.${column.columnHumpName} }
	</#if>
	</#list>

	//region your codes 2

	//endregion your codes 2

}