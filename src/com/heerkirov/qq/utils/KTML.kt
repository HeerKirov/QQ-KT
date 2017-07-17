package com.heerkirov.qq.utils
import com.heerkirov.qq.ui.MainWindow
import com.heerkirov.qq.ui.Queryable
import javax.swing.*
import java.awt.*
import java.util.*



fun Container.cardgrid(func:CardGrid.()->Unit)
        = this.cardgrid(null, func)
fun Container.cardgrid(id:String? = null, func:CardGrid.()->Unit)
        = this.add(CardGrid().also(func).also{this.insertId(id, it)})

fun Container.tablegrid(rowDefine:Array<Weight> = arrayOf(wAuto()), columnDefine:Array<Weight> = arrayOf(wAuto()),
                        func:TableGrid.()->Unit)
        = this.tablegrid(null, rowDefine, columnDefine, func)
fun Container.tablegrid(id:String? = null, rowDefine:Array<Weight> = arrayOf(wAuto()), columnDefine:Array<Weight> = arrayOf(wAuto()),
                          func:TableGrid.()->Unit)
        = this.add(TableGrid(rowDefine, columnDefine).also(func).also{this.insertId(id, it)})

fun Container.panel(func: DrawPanel.() -> Unit)
        = this.panel(null,func)
fun Container.panel(id:String? = null, func:DrawPanel.()->Unit)
        = this.add(DrawPanel().also(func).also { this.insertId(id, it) })


// TableLayout的html构造函数
fun TableGrid.label(text:String="", horizontalAlignment:Int=JLabel.LEFT, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1)
        = this.label(null, text, horizontalAlignment, row, column, rowSpan, columnSpan)
fun TableGrid.label(id:String? = null, text:String="", horizontalAlignment:Int=JLabel.LEFT, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1)
        = this.add(JLabel(text).also { it.horizontalAlignment=horizontalAlignment }.also { this.insertId(id, it) }, row, column, rowSpan, columnSpan)

fun TableGrid.panel(row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func: DrawPanel.() -> Unit)
        = this.panel(null, row, column, rowSpan, columnSpan, func)
fun TableGrid.panel(id:String? = null, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:DrawPanel.()->Unit)
        = this.add(DrawPanel().also(func).also { this.insertId(id, it) }, row, column, rowSpan, columnSpan)

fun TableGrid.button(title:String, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1)
        = this.button(null, title, row, column, rowSpan, columnSpan)
fun TableGrid.button(id:String? = null, title:String, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1)
        = this.add(JButton(title).also{this.insertId(id, it)}, row, column, rowSpan, columnSpan)

fun TableGrid.tablegrid(rowDefine:Array<Weight> = arrayOf(wAuto()), columnDefine:Array<Weight> = arrayOf(wAuto()),
                        row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:TableGrid.()->Unit)
        = this.tablegrid(null, rowDefine, columnDefine, row, column, rowSpan, columnSpan, func)
fun TableGrid.tablegrid(id:String? = null, rowDefine:Array<Weight> = arrayOf(wAuto()), columnDefine:Array<Weight> = arrayOf(wAuto()),
                            row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:TableGrid.()->Unit)
        = this.add(TableGrid(rowDefine, columnDefine).also(func).also{this.insertId(id, it)}, row, column, rowSpan, columnSpan)

fun TableGrid.cardgrid(row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:CardGrid.()->Unit)
        = this.cardgrid(null, row, column, rowSpan, columnSpan, func)
fun TableGrid.cardgrid(id:String? = null, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:CardGrid.()->Unit)
        = this.add(CardGrid().also(func).also{this.insertId(id, it)}, row, column, rowSpan, columnSpan)

fun TableGrid.list(row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:DefaultListModel<String>.()->Unit)
        = this.list(null, row, column, rowSpan, columnSpan, func)
fun TableGrid.list(id:String? = null, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:DefaultListModel<String>.()->Unit)
        = this.add(JScrollPane(JList<String>().also { it.model=DefaultListModel<String>().also(func) }.also{this.insertId(id, it)}), row, column, rowSpan, columnSpan)

fun TableGrid.combo(row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:Vector<String>.()->Unit)
        = this.combo(null, row, column, rowSpan, columnSpan, func)
fun TableGrid.combo(id:String? = null, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1, func:Vector<String>.()->Unit)
        = this.add(JComboBox<String>().also { it.model = DefaultComboBoxModel(Vector<String>().also(func)) }.also { this.insertId(id, it) }, row, column, rowSpan, columnSpan)

fun TableGrid.text(row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1)
        = this.text(null, row, column, rowSpan, columnSpan)
fun TableGrid.text(id:String? = null, row:Int=0, column:Int=0,rowSpan:Int=1,columnSpan:Int=1)
        = this.add(JScrollPane(JTextArea().also{this.insertId(id, it)}), row, column, rowSpan, columnSpan)

// CardLayout的html构造函数
fun CardGrid.tablegrid(rowDefine:Array<Weight> = arrayOf(wAuto()), columnDefine:Array<Weight> = arrayOf(wAuto()),
                       flag:String, func:TableGrid.()->Unit):Unit
        = this.tablegrid(null, rowDefine, columnDefine, flag, func)
fun CardGrid.tablegrid(id:String? = null, rowDefine:Array<Weight> = arrayOf(wAuto()), columnDefine:Array<Weight> = arrayOf(wAuto()),
                           flag:String, func:TableGrid.()->Unit):Unit
        = this.add(TableGrid(rowDefine, columnDefine).also(func).also{this.insertId(id, it)}, flag)

fun CardGrid.button(title:String, flag:String)
        = this.button(null, title, flag)
fun CardGrid.button(id:String? = null, title:String, flag:String)
        = this.add(JButton(title).also{this.insertId(id, it)}, flag)


// 辅助函数
fun Vector<String>.item(i:String) = this.add(i)
fun DefaultListModel<String>.item(i:String) = this.addElement(i)

fun Container.insertId(id:String?, ele:Any?):Any? {
    if(id != null && ele != null){
//        var now:Container? = this
//        while(now != null){
//            if(now is Queryable){
//                now.insertElement(id, ele)
//            }
//            now = now.parent
//        }
        QueryMemory.comments.dict.put(id, ele)
    }
    return ele
}

class QueryMemory{
    object comments{
        val dict:HashMap<String, Any> = HashMap()
    }
}