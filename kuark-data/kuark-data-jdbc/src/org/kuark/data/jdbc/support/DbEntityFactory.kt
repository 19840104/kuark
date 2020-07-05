package org.kuark.data.jdbc.support

import me.liuwj.ktorm.entity.Entity

/**
 * 数据库实体工厂
 * 可简化使用(companion object : Entity.Factory<E>() => companion object : DbEntityFactory<E>())
 * 并避免Ktorm相关代码侵入PO0
 *
 * @author K
 * @since 1.0.0
 */
abstract class  DbEntityFactory<E: Entity<E>>: Entity.Factory<E>()