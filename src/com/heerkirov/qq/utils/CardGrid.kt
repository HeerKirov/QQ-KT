package com.heerkirov.qq.utils

import java.awt.CardLayout
import java.awt.Component
import java.awt.Container

class CardGrid : Container(){
    private val container:Container = this
    private val layout:CardLayout by lazy { CardLayout().also { container.layout=it } }
    private var now:String? = null
    fun add(component:Component, flag:String) {
        this.add(component)
        layout.addLayoutComponent(component, flag)
    }
    fun getNow():String? = now
    fun goto(flag:String) {
        layout.show(container, flag)
        now = flag
    }
}