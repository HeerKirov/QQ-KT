/**
 * Created by heer on 17-7-18.
 */
import com.FileUtils
import java.lang.Runtime
import java.io.*
import com.alibaba.fastjson.*
import java.io.FileInputStream



object osUtils{

    object OS_NAME{
        const val LINUX = "Linux"
        const val WINDOWS = "Windows"
    }
    val osname:String by lazy {System.getProperties().getProperty("os.name")}

    fun osConfig(){
        val rt = Runtime.getRuntime()!!
        fun configWindows(){
            // todo windows版本的基础配置
        }
        fun configLinux(){
            rt.exec("sudo apt-get install libnotify-bin")
        }
        when(osname){
            OS_NAME.LINUX->configLinux()
            OS_NAME.WINDOWS->configWindows()
        }
    }
    fun SendNotice(title:String, content:String){
        val rt = Runtime.getRuntime()!!
        fun Windows(){
            // todo windows版本的推送通知
        }
        fun Linux(){
            try{
                rt.exec(arrayOf("notify-send",title, content))
            }catch (e:Exception){
                println("Error: ${e.message}")
            }
        }
        when(osname){
            OS_NAME.LINUX->Linux()
            OS_NAME.WINDOWS->Windows()
        }
    }
}

object osConfig{
    const val configpath = "config.json"
    private val data = JSON.parseObject(FileUtils.readFile(configpath, default_config))

    //app的配置的参数组
    val icon by lazy {data.getString("icon")}
    //用户的配置存储

}

const val default_config =
"""
{
    "icon":"icon.png"
}
"""