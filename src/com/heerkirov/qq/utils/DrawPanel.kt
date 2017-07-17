package com.heerkirov.qq.utils

import java.awt.Graphics
import javax.swing.JPanel


class DrawPanel(var action:((DrawPanel, Graphics?)->Unit)?=null) : JPanel() {
    override fun paintComponent(p0: Graphics?) {
        super.paintComponent(p0)
        action?.invoke(this,p0)
    }
}