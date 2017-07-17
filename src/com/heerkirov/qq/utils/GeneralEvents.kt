package com.heerkirov.qq.utils

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTextArea
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class MouseClick(private val e:(MouseEvent?)->Unit) : MouseAdapter() {
    override fun mouseClicked(p0: MouseEvent?) = e(p0)
}

class ListSelector(private val e:(ListSelectionEvent?)->Unit) : ListSelectionListener{
    override fun valueChanged(p0: ListSelectionEvent?){
        if(lock){
            lock=false
            e(p0)
            lock=true
        }
    }
    private var lock:Boolean = true
}

class KeySendClick(private val action:()->Unit) : KeyAdapter(){
    override fun keyPressed(p0: KeyEvent?) {
        if(p0!=null){
            if(p0.keyCode==KeyEvent.VK_ENTER){
                if(!p0.isControlDown){
                    p0.consume()
                    action()
                }else{
                    (p0.source as JTextArea).text += "\n"
                }
            }
        }
    }
}