package org.kuark.base.tree

import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

/**
 * Create by (admin) on 6/11/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class TreeNodeState : Serializable {
    private var loaded: Boolean? = null
    private var opened: Boolean? = null
    private var selected: Boolean? = null
    private var disabled: Boolean? = null

    constructor() {}
    constructor(selected: Boolean?) {
        this.selected = selected
    }

    constructor(selected: Boolean?, opened: Boolean?) {
        this.selected = selected
        this.opened = opened
    }

    fun getLoaded(): Boolean? {
        return loaded
    }

    fun setLoaded(loaded: Boolean?) {
        this.loaded = loaded
    }

    fun getOpened(): Boolean? {
        return opened
    }

    fun setOpened(opened: Boolean?) {
        this.opened = opened
    }

    fun getSelected(): Boolean? {
        return selected
    }

    fun setSelected(selected: Boolean?) {
        this.selected = selected
    }

    fun getDisabled(): Boolean? {
        return disabled
    }

    fun setDisabled(disabled: Boolean?) {
        this.disabled = disabled
    }

    companion object {
        private const val serialVersionUID = 4133941367895174215L
    }
}