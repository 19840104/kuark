package org.kuark.base.bean.validation.constraint.validator

import org.kuark.base.bean.validation.constraint.annotaions.CnIdCardNo
import org.kuark.base.cn.IdCardNoKit
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/**
 * CnIdCardNo约束验证器
 *
 * @author K
 * @since 1.0.0
 */
class CnIdCardNoValidator: ConstraintValidator<CnIdCardNo, String?> {

    private lateinit var cnIdCardNo: CnIdCardNo

    override fun initialize(cnIdCardNo: CnIdCardNo) {
        this.cnIdCardNo = cnIdCardNo
    }

    override fun isValid(value: String?, p1: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        return if (cnIdCardNo.support15) {
            IdCardNoKit.isIdCardNo(value)
        } else {
            IdCardNoKit.isIdCardNo18(value)
        }
    }

}