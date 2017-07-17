package com.heerkirov.qq.utils

import java.awt.*

class TableGrid(val rowDefine:Array<Weight> = arrayOf(wAuto()),
        val columnDefine:Array<Weight> = arrayOf(wAuto())) : Container() {
    private val container:Container = this
    private val layout:GridBagLayout by lazy {
        GridBagLayout().also {
            it.columnWeights=kotlin.DoubleArray(columnDefine.size){
                if(columnDefine[it].type==WeightType.Star)columnDefine[it].value else 0.0
            }
            it.columnWidths=kotlin.IntArray(columnDefine.size){
                if(columnDefine[it].type==WeightType.Pixel)columnDefine[it].value.toInt() else 0
            }
            it.rowWeights=kotlin.DoubleArray(rowDefine.size){
                if(rowDefine[it].type==WeightType.Star)rowDefine[it].value else 0.0
            }
            it.rowHeights=kotlin.IntArray(rowDefine.size){
                if(rowDefine[it].type==WeightType.Pixel)rowDefine[it].value.toInt() else 0
            }
            container.layout=it
        }
    }
    private val cs:GridBagConstraints = GridBagConstraints()
    fun add(component:Component,
            row:Int=0, column:Int=0, rowSpan:Int=1, columnSpan:Int=1,
            fill:Int = GridBagConstraints.BOTH){
        cs.gridx=column
        cs.gridy=row
        cs.gridwidth=columnSpan
        cs.gridheight=rowSpan
        cs.fill=fill
        container.add(component)
        layout.setConstraints(component, cs)
    }
}

class Weight(val type:WeightType = WeightType.Auto, val value:Double = 0.0)
fun wAuto():Weight = Weight()
fun wStar(value:Double):Weight = Weight(WeightType.Star, value)
fun wPixel(value:Int):Weight = Weight(WeightType.Pixel, value.toDouble())
enum class WeightType{
    Auto, Star, Pixel
}