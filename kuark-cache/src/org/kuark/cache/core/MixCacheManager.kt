package org.kuark.cache.core

import org.kuark.cache.context.CacheNameResolver
import org.kuark.cache.enums.CacheStrategy
import org.kuark.config.annotation.ConfigValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component("cacheManager")
class MixCacheManager : AbstractTransactionSupportingCacheManager() {

    @ConfigValue("\${cache.config.strategy}")
    private var strategyStr: String? = null

    @Autowired(required = false)
    @Qualifier("localCacheManager")
    private lateinit var localCacheManager: CacheManager
    @Autowired(required = false)
    @Qualifier("remoteCacheManager")
    private lateinit var remoteCacheManager: CacheManager
    @Autowired
    private lateinit var cacheNameResolver: CacheNameResolver

    override fun loadCaches(): MutableCollection<out Cache> {
        val caches = mutableListOf<Cache>()
        val strategy = try {
            CacheStrategy.valueOf(strategyStr!!)
        } catch (e: Exception) {
            return caches
        }
        val cacheNames = cacheNameResolver.resolve()
        when (strategy) {
            CacheStrategy.SINGLE_LOCAL -> {
                cacheNames.forEach { name ->
                    val localCache = localCacheManager.getCache(name)
                    caches.add(MixCache(strategy, localCache, null))
                }
            }
            CacheStrategy.REMOTE -> {
                cacheNames.forEach { name ->
                    val remoteCache = remoteCacheManager.getCache(name)
                    caches.add(MixCache(strategy, null, remoteCache))
                }
            }
            CacheStrategy.LOCAL_REMOTE -> {
                cacheNames.forEach { name ->
                    val localCache = localCacheManager.getCache(name)
                    val remoteCache = remoteCacheManager.getCache(name)
                    caches.add(MixCache(strategy, localCache, remoteCache))
                }
            }
        }
        return caches
    }

    fun clearLocal(cacheName: String, key: Any?) {
        val cache = getCache(cacheName) ?: return
        val mixCache = cache as MixCache
        mixCache.clearLocal(key)
    }

}