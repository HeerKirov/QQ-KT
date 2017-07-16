package com.heerkirov.qq.ui
import com.heerkirov.qq.utils.*
import java.awt.*
import java.awt.event.WindowAdapter
import javax.swing.*
import javax.swing.UIManager



class MainWindow : JFrame("QQ-KT"){
    init {
        size = Dimension(640, 480)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ConstructUI()

        isVisible = true
    }

    private fun ConstructUI() {
        tablelayout(
                arrayOf(wPixel(30), wStar(5.0)),
                arrayOf(wStar(1.0), wStar(1.0), wStar(3.0))
        ){
            button("btn1")
            button("btn2",0,1)
            button("btn3",1,0)
            button("btn4",0,2)
        }

    }
}

class MainWindowWindowListener : WindowAdapter()