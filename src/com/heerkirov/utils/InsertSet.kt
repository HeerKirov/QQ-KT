package com.heerkirov.utils

import java.util.*

class InsertSet<T> : Iterable<T> {
    override fun iterator(): Iterator<T> = arr.iterator()

    fun insert(ele:T, index:Int){
        if(ele in arr)arr.remove(ele)
        arr.add(index, ele)
    }
    fun addFirst(ele:T){
        if(ele in arr)arr.remove(ele)
        arr.addFirst(ele)
    }
    fun addLast(ele:T){
        if(ele in arr)arr.remove(ele)
        arr.addLast(ele)
    }

    operator fun get(index:Int):T = arr[index]

    fun clear() = arr.clear()



    private val arr:LinkedList<T> = LinkedList()
}