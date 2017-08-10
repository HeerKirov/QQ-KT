package com

import com.alibaba.fastjson.JSON
import java.io.*

object FileUtils{
    fun readFile(path:String, default_create_content:String=""):String{
        //读取配置json文件
        val file = File(path)
        if(!file.exists()){
            file.createNewFile()
            val writer = FileWriter(file)
            writer.write(default_create_content)
            writer.close()
            return default_create_content
        }else{
            val reader = FileReader(file)
            val text = reader.readText()
            reader.close()
            return text
        }

    }
}