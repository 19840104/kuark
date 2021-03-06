package io.kuark.ability.auth.login.third.core

/**
 *
 *
 * @author K
 * @since 1.0.0
 */
interface IAuthUser {

    fun loginAccount(): String

    fun getOpenId(): String

    fun getAvatarUrl(): String?

}