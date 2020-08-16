package org.kuark.resource.sys.service

import me.liuwj.ktorm.dsl.*
import org.kuark.resource.sys.dao.SysDictItems
import org.kuark.resource.sys.dao.SysDicts
import org.kuark.resource.sys.po.SysDictItem
import org.kuark.cache.context.CacheNames
import org.kuark.cache.core.BatchCacheable
import org.kuark.data.jdbc.support.RdbKit
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * 字典子表服务
 *
 * @author K
 * @since 1.0.0
 */
@Service
//region your codes 1
@CacheConfig(cacheNames = [CacheNames.SYS_DICT_ITEM])
open class SysDictItemService {
//endregion your codes 1

    //region yur codes 2

    /**
     * 根据模块和字典类型，取得对应字典项(仅包括处于启用状态的)，并将结果缓存，查不到不缓存
     *
     * @param module 如果没有请传入空串，此时请保证type的惟一性，否则结果将不确定是哪条记录
     * @param type 字典类型
     * @return 字典项列表。如果module为空串，且存在多个同名type，将任意返回一个type对应的字典项。查无结果返回空列表。
     */
    @Cacheable(key = "#module.concat(':').concat(#type)", unless = "#result.isEmpty()")
    open fun getItemsByModuleAndType(module: String, type: String): List<SysDictItem> {
        // 查出对应的dict id
        val ids: List<String> = RdbKit.getDatabase().from(SysDicts)
            .select(SysDicts.id)
            .whereWithConditions {
                it += (SysDicts.dictType eq type) and (SysDicts.isActive eq true)
                if (module.isNotEmpty()) {
                    it += SysDicts.module eq module
                }
            }
            .map { row -> row[SysDicts.id] }
            .toList() as List<String>

        return if (ids.isEmpty()) {
            listOf()
        } else {
            // 查出dict id的所有字典项
            RdbKit.getDatabase().from(SysDictItems)
                .select(SysDictItems.columns)
                .orderBy(SysDictItems.seqNo.asc())
                .where { SysDictItems.dictId eq ids.first() }
                .map { row -> SysDictItems.createEntity(row) }
                .toList()
        }
    }

    @BatchCacheable(valueClass = List::class)
    open fun testBatchCache(subSys: String, modules: List<String>, types: Array<String>): Map<String, List<SysDictItem>> {
        return mapOf("1" to listOf(), "2" to listOf())
    }

    //endregion your codes 2

}