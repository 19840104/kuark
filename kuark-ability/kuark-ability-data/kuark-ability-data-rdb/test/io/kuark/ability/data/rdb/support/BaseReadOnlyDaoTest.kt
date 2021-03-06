package io.kuark.ability.data.rdb.support

import io.kuark.ability.data.rdb.kit.RdbKit
import io.kuark.ability.data.rdb.table.TestTable
import io.kuark.ability.data.rdb.table.TestTableDao
import io.kuark.ability.data.rdb.table.TestTableKit
import io.kuark.base.query.Criteria
import io.kuark.base.query.Criterion
import io.kuark.base.query.enums.Operator
import io.kuark.base.query.sort.Order
import io.kuark.test.common.SpringTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired

/**
 * BaseReadOnlyDao测试用例
 *
 * @author K
 * @since 1.0.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BaseReadOnlyDaoTest : SpringTest() {

    @Autowired
    private lateinit var testTableDao: TestTableDao

    @BeforeAll
    fun setUp() {
        TestTableKit.create()
        TestTableKit.insert()
    }

    @AfterAll
    fun tearDown() {
        TestTableKit.drop()
    }


    //region Search
    @Test
    fun getById() {
        val entity = testTableDao.getById(-1)
        assertEquals("name1", entity!!.name)

        // 不存在指定主键对应的实体时
        assert(testTableDao.getById(1) == null)
    }

    @Test
    fun getByIds() {
        assert(testTableDao.getByIds().isEmpty())

        var entities = testTableDao.getByIds(-1, -2, -3)
        assertEquals(3, entities.size)

        entities = testTableDao.getByIds(-1, -2, -3, countOfEachBatch = 2)
        assertEquals(3, entities.size)
    }
    //endregion Search


    //region oneSearch
    @Test
    fun oneSearch() {
        var results = testTableDao.oneSearch(TestTable::name.name, "name1")
        assertEquals(1, results.size)
        assertEquals(-1, results.first().id)

        // value为null的情况
        results = testTableDao.oneSearch(TestTable::weight.name, null)
        assertEquals(2, results.size)

        // 单条件升序
        results = testTableDao.oneSearch(TestTable::isActive.name, true, Order.asc(TestTable::name.name))
        assertEquals(8, results.size)
        assertEquals("name1", results.first().name)
        assertEquals("name9", results.last().name)

        // 单条件降序
        results = testTableDao.oneSearch(TestTable::isActive.name, true, Order.desc(TestTable::name.name))
        assertEquals(8, results.size)
        assertEquals("name9", results.first().name)
        assertEquals("name1", results.last().name)

        // 多个排序条件
        val orders = arrayOf(Order.asc(TestTable::height.name), Order.desc(TestTable::name.name))
        results = testTableDao.oneSearch(TestTable::isActive.name, true, *orders)
        assertEquals(8, results.size)
        assertEquals("name8", results.first().name)
        assertEquals("name9", results.last().name)
    }

    @Test
    fun oneSearchProperty() {
        var results = testTableDao.oneSearchProperty(TestTable::name.name, "name1", TestTable::id.name)
        assertEquals(1, results.size)
        assertEquals(-1, results.first())
        results = testTableDao.oneSearchProperty(TestTable::weight.name, null, TestTable::id.name)
        assertEquals(2, results.size)
    }

    @Test
    fun oneSearchProperties() {
        val returnProperties = listOf(TestTable::name.name, TestTable::id.name)
        val results = testTableDao.oneSearchProperties(TestTable::name.name, "name1", returnProperties)
        assertEquals(1, results.size)
        assertEquals("name1", results.first()[TestTable::name.name])
    }
    //endregion oneSearch


    //region allSearch
    @Test
    fun allSearch() {
        val result = testTableDao.allSearch()
        assertEquals(11, result.size)
    }

    @Test
    fun allSearchProperty() {
        val results = testTableDao.allSearchProperty(TestTable::id.name, Order.desc(TestTable::id.name))
        assertEquals(11, results.size)
        assertEquals(Integer.valueOf(-1), results[0])
        assertEquals(Integer.valueOf(-11), results[10])
    }

    @Test
    fun allSearchProperties() {
        val returnProperties = listOf(TestTable::name.name, TestTable::id.name)
        val results = testTableDao.allSearchProperties(returnProperties)
        assertEquals(11, results.size)
    }
    //endregion allSearch


    //region andSearch
    @Test
    fun andSearch() {
        val propertyMap = mapOf(TestTable::name.name to "name5", TestTable::weight.name to null)
        val results = testTableDao.andSearch(propertyMap)
        assertEquals(1, results.size)
    }

    @Test
    fun andSearchProperty() {
        val propertyMap = mapOf(TestTable::name.name to "name5", TestTable::weight.name to null)
        val results = testTableDao.andSearchProperty(propertyMap, TestTable::name.name)
        assertEquals("name5", results.first())
    }

    @Test
    fun andSearchProperties() {
        val propertyMap = mapOf(TestTable::name.name to "name1", TestTable::isActive.name to true)
        val returnProperties = listOf(TestTable::name.name, TestTable::id.name)
        val results = testTableDao.andSearchProperties(propertyMap, returnProperties)
        assertEquals(1, results.size)
        assertEquals("name1", results.first()[TestTable::name.name])
    }
    //endregion andSearch


    //region orSearch
    @Test
    fun orSearch() {
        val propertyMap = mapOf(TestTable::name.name to "name5", TestTable::weight.name to null)
        val results = testTableDao.orSearch(propertyMap)
        assertEquals(2, results.size)
    }

    @Test
    fun orSearchProperty() {
        val propertyMap = mapOf(TestTable::name.name to "name5", TestTable::weight.name to null)
        val results = testTableDao.orSearchProperty(propertyMap, TestTable::name.name, Order.desc(TestTable::id.name))
        assertEquals(2, results.size)
        assertEquals("name5", results.first())
    }

    @Test
    fun orSearchProperties() {
        val propertyMap = mapOf(TestTable::name.name to "name5", TestTable::weight.name to null)
        val returnProperties = listOf(TestTable::name.name, TestTable::id.name)
        val results = testTableDao.orSearchProperties(propertyMap, returnProperties, Order.desc(TestTable::id.name))
        assertEquals(2, results.size)
        assertEquals("name5", results.first()[TestTable::name.name])
    }
    //endregion orSearch


    //region inSearch
    @Test
    fun inSearch() {
        val ids = listOf(-3, -2, -1, null)
        val results = testTableDao.inSearch(TestTable::id.name, ids, Order.desc(TestTable::id.name))
        assertEquals(3, results.size)
        assertEquals("name1", results.first().name)
    }

    @Test
    fun inSearchProperty() {
        val ids = listOf(-3, -2, -1)
        val results =
            testTableDao.inSearchProperty(TestTable::id.name, ids, TestTable::name.name, Order.desc(TestTable::id.name))
        assertEquals(3, results.size)
        assertEquals("name1", results.first())
    }

    @Test
    fun inSearchProperties() {
        val ids = listOf(-3, -2, -1)
        val returnProperties = listOf(TestTable::name.name, TestTable::id.name, TestTable::isActive.name)
        val results =
            testTableDao.inSearchProperties(TestTable::id.name, ids, returnProperties, Order.desc(TestTable::id.name))
        assertEquals(3, results.size)
        assertEquals("name1", results.first()[TestTable::name.name])
    }

    @Test
    fun inSearchById() {
        val ids = listOf(-3, -2, -1)
        val results = testTableDao.inSearchById(ids)
        assertEquals(3, results.size)
    }

    @Test
    fun inSearchPropertyById() {
        val ids = listOf(-3, -2, -1)
        val results =
            testTableDao.inSearchPropertyById(ids, TestTable::name.name, Order.desc(TestTable::id.name))
        assertEquals(3, results.size)
        assertEquals("name1", results.first())
    }

    @Test
    fun inSearchPropertiesById() {
        val ids = listOf(-3, -2, -1)
        val returnProperties = listOf(TestTable::name.name, TestTable::id.name)
        val results = testTableDao.inSearchPropertiesById(ids, returnProperties, Order.desc(TestTable::id.name))
        assertEquals(3, results.size)
        assertEquals("name1", results.first()[TestTable::name.name])
    }
    //endregion inSearch


    //region search Criteria
    @Test
    fun search() {
        val inIds = Criterion(TestTable::id.name, Operator.IN, listOf(-2, -4, -6, -7))
        val eqActive = Criterion(TestTable::isActive.name, Operator.EQ, true)
        val andCriteria = Criteria.and(inIds, eqActive)
        val likeName = Criterion(TestTable::name.name, Operator.LIKE_S, "name1")
        val orCriteria: Criteria = Criteria.or(likeName, andCriteria)
        val noNull = Criterion(TestTable::weight.name, Operator.IS_NOT_NULL, null)
        val criteria: Criteria = Criteria.and(orCriteria, noNull)
        val results = testTableDao.search(criteria, Order.desc(TestTable::weight.name))
        assertEquals(5, results.size)
        assertEquals(-10, results.first().id)
    }

    @Test
    fun searchProperty() {
        val criteria = Criteria().addAnd(
            Criterion(TestTable::name.name, Operator.LIKE_S, "name1"),
            Criterion(TestTable::weight.name, Operator.IS_NOT_NULL, null),
            Criterion(TestTable::height.name, Operator.GT, 160)
        )
        val results = testTableDao.searchProperty(criteria, TestTable::id.name, Order.desc(TestTable::weight.name))
        assertEquals(2, results.size)
        assertEquals(-10, results.first())
    }

    @Test
    fun searchProperties() {
        val inIds = Criterion(TestTable::id.name, Operator.IN, listOf(-2, -4, -6, -7))
        val eqActive = Criterion(TestTable::isActive.name, Operator.EQ, true)
        val andCriteria = Criteria.and(inIds, eqActive)
        val likeName = Criterion(TestTable::name.name, Operator.LIKE_S, "name1")
        val orCriteria: Criteria = Criteria.or(likeName, andCriteria)
        val noNull = Criterion(TestTable::weight.name, Operator.IS_NOT_NULL, null)
        val criteria: Criteria = Criteria.and(orCriteria, noNull)
        val returnProperties = listOf(TestTable::isActive.name, TestTable::name.name)
        val results = testTableDao.searchProperties(criteria, returnProperties, Order.desc(TestTable::weight.name))
        assertEquals(5, results.size)
        assertEquals("name10", results.first()[TestTable::name.name])
        assertEquals(false, results.first()[TestTable::isActive.name])
    }
    //endregion search Criteria


    //region pagingSearch
    @Test
    fun pagingSearch() {
        if (isSupportPaging()) {
            val criteria = Criteria.add(TestTable::isActive.name, Operator.EQ, true)
            val entities = testTableDao.pagingSearch(criteria, 1, 4, Order.asc(TestTable::id.name))
            assertEquals(4, entities.size)
            assertEquals(-11, entities.first().id)
        }
    }

    @Test
    fun pagingReturnProperty() {
        if (isSupportPaging()) {
            val criteria = Criteria.add(TestTable::isActive.name, Operator.EQ, true)
            val results =
                testTableDao.pagingReturnProperty(criteria, TestTable::id.name, 1, 4, Order.asc(TestTable::id.name))
            assertEquals(4, results.size)
            assertEquals(-11, results.first())
        }
    }

    @Test
    fun pagingReturnProperties() {
        if (isSupportPaging()) {
            val criteria = Criteria.add(TestTable::isActive.name, Operator.EQ, true)
            val returnProperties = listOf(TestTable::id.name, TestTable::name.name)
            val results =
                testTableDao.pagingReturnProperties(criteria, returnProperties, 1, 4, Order.asc(TestTable::id.name))
            assertEquals(4, results.size)
            assertEquals(-11, results.first()[TestTable::id.name])
        }
    }

    private fun isSupportPaging(): Boolean { // h2可以用PostgreSqlDialect来实现分页
        val dialect = RdbKit.getDatabase().dialect
        return !dialect::class.java.name.contains("SqlDialectKt\$detectDialectImplementation\$1")
    }
    //endregion pagingSearch


    //region aggregate
    @Test
    fun count() {
        assertEquals(11, testTableDao.count())
        val criteria = Criteria.add(TestTable::name.name, Operator.LIKE_S, "name1")
        assertEquals(3, testTableDao.count(criteria))
    }

    @Test
    fun sum() {
        assertEquals(1382, testTableDao.sum(TestTable::height.name))
        val criteria = Criteria.add(TestTable::name.name, Operator.LIKE_S, "name1")
        assertEquals(122.5, testTableDao.sum(TestTable::weight.name, criteria))
        assertEquals(445.74, testTableDao.sum(TestTable::weight.name))
    }

    @Test
    fun avg() {
        val criteria = Criteria.add(TestTable::name.name, Operator.LIKE_S, "name1")
        assertEquals(61.25, testTableDao.avg(TestTable::weight.name, criteria))
    }

    @Test
    fun max() {
        val criteria = Criteria.add(TestTable::name.name, Operator.LIKE_S, "name1")
        assertEquals(66.0, testTableDao.max(TestTable::weight.name, criteria))
    }

    @Test
    fun min() {
        val criteria = Criteria.add(TestTable::name.name, Operator.LIKE_S, "name1")
        assertEquals(56.5, testTableDao.min(TestTable::weight.name, criteria))
    }
    //endregion aggregate

}