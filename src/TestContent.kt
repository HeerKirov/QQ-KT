/**
 * Created by heer on 17-7-18.
 */

fun main(args:Array<String>){
    val proc = System.getProperties()
    for((k,v) in proc){
        println("$k : $v")
    }
}