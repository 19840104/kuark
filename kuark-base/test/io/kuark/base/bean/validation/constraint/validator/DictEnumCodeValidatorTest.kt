package io.kuark.base.bean.validation.constraint.validator

import io.kuark.base.bean.validation.constraint.annotaions.DictEnumCode
import io.kuark.base.bean.validation.kit.ValidationKit
import io.kuark.base.support.enums.IDictEnum
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

/**
 * DictEnumCodeValidator测试用例
 *
 * @author K
 * @since 1.0.0
 */
internal class DictEnumCodeValidatorTest {

    @Test
    fun validate() {
        val bean1 = TestDictEnumCodeBean("1")
        assert(ValidationKit.validateBean(bean1).isEmpty())

        val bean2 = TestDictEnumCodeBean("4")
        assertFalse(ValidationKit.validateBean(bean2).isEmpty())
    }

    internal data class TestDictEnumCodeBean(

        @get:DictEnumCode(enumClass = TestEnum::class, message = "值必须在枚举TestEnum的代码中")
        val elemCode: String?

    )

    internal enum class TestEnum(override val code: String, override var trans: String): IDictEnum {

        ELEM1("1", "元素1"),
        ELEM2("2", "元素2"),
        ELEM3("3", "元素3")

    }

}