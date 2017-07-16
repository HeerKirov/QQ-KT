package com.heerkirov.qq.utils
import javax.swing.*
import java.awt.*
import java.util.*

fun Container.tablelayout(rowDefine:Array<Weight> = arrayOf(wAuto()), columnDefine:Array<Weight> = arrayOf(wAuto()),
                          func:TableLayout.()->Unit)
        = this.add(TableLayout(rowDefine, columnDefine).also(func))

fun TableLayout.button(title:String, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1)
        = this.add(JButton(title), row, column, rowSpan, columnSpan)

fun TableLayout.tablelayout(rowDefine:Array<Weight> = arrayOf(wAuto()), columnDefine:Array<Weight> = arrayOf(wAuto()),
                            row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:TableLayout.()->Unit)
        = this.add(TableLayout(rowDefine, columnDefine).also(func), row, column, rowSpan, columnSpan)

fun TableLayout.list(row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:Vector<String>.()->Unit){
    this.add(JList<String>().also { it.model=DefaultComboBoxModel(Vector<String>().also(func)) })
}

fun Vector<String>.item(i:String) = this.add(i)

