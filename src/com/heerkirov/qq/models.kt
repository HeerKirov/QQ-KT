package com.heerkirov.qq

import com.scienjus.smartqq.model.*
import java.text.SimpleDateFormat
import java.util.Date



class iCategory(val category:Category) : iAny(){
    override fun getTypeTitle():String = "${category.name} (${arr_friends.size})"
    override fun getTitle():String = category.name
    override fun getMessageList(): ArrayList<iMessage> = throw UnsupportedOperationException()
    val arr_friends:ArrayList<Friend> by lazy {
        ArrayList<Friend>().also { arr -> category.friends.forEach { arr.add(it) } }
    }
    val friends:HashMap<Long, Friend> by lazy {
        HashMap<Long, Friend>().also { hash -> category.friends.forEach { hash.put(it.userId, it) } }
    }
}
class iFriend(val friend:Friend) : iAny(){
    override fun getTypeTitle(): String = "[friend]${getTitle()} ${getUnreadMessageFlag()}"
    override fun getTitle(): String {
        if(friend.markname.isNullOrEmpty()) {
            if(friend.nickname.isNullOrEmpty())return "???"
            else return friend.nickname
        }
        else return friend.markname
    }

    val messages:ArrayList<Message> = ArrayList()
    override fun getMessageList(): ArrayList<iMessage> = imessages
    private val imessages:ArrayList<iMessage> = ArrayList()
}
class iGroup(val group:Group, val info:GroupInfo) : iAny(){
    override fun getTypeTitle(): String = "[group]${group.name} ${getUnreadMessageFlag()}"
    override fun getTitle(): String = group.name
    val arr_users:ArrayList<GroupUser> by lazy {
        ArrayList<GroupUser>().also { arr -> info.users.forEach { arr.add(it) } }
    }
    val users:HashMap<Long, GroupUser> by lazy {
        HashMap<Long, GroupUser>().also { hash -> info.users.forEach { hash.put(it.uin, it) } }
    }
    val messages:ArrayList<GroupMessage> = ArrayList()

    override fun getMessageList(): ArrayList<iMessage> = imessages
    private val imessages:ArrayList<iMessage> = ArrayList()
}
class iDiscuss(val discuss:Discuss, val info:DiscussInfo) : iAny(){
    override fun getTypeTitle(): String = "[discuss]${discuss.name} ${getUnreadMessageFlag()}"
    override fun getTitle(): String = discuss.name?:"???"
    val arr_users:ArrayList<DiscussUser> by lazy {
        ArrayList<DiscussUser>().also { arr -> info.users.forEach { arr.add(it) } }
    }
    val users:HashMap<Long, DiscussUser> by lazy {
        HashMap<Long, DiscussUser>().also { hash -> info.users.forEach { hash.put(it.uin, it) } }
    }
    val messages:ArrayList<DiscussMessage> = ArrayList()

    override fun getMessageList(): ArrayList<iMessage> = imessages
    private val imessages:ArrayList<iMessage> = ArrayList()
}
class iMe(var userinfo:UserInfo?=null) : iAny(){
    override fun getMessageList(): ArrayList<iMessage> = throw UnsupportedOperationException()
    override fun getTypeTitle(): String = userinfo?.nick?:""
    override fun getTitle(): String = userinfo?.nick?:""
}

abstract class iAny{
    abstract fun getTypeTitle():String
    abstract fun getTitle():String
    abstract fun getMessageList():ArrayList<iMessage>
    fun getUnreadMessage():Iterator<iMessage> = getMessageList().filter { !it.read }.iterator()
    fun getUnreadMessageCount():Int = getMessageList().count { !it.read }
    fun getUnreadMessageFlag():String {
        val count = getUnreadMessageCount()
        if(count>0)return "($count)" else return ""
    }
    fun turnAllMessageRead() = getUnreadMessage().forEach { it.read=true }
}

class iMessage(val time:Long,
               val username:String,
               val content:String,
               val belong:iAny?=null,
               val user:iAny?=null,
               var read:Boolean=false){
    constructor(time:Long, username:String, content:String, belong:iAny?):
            this(time, username, content, belong, belong)

    val format_time:String
        get() {
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
            val dt = Date(time * 1000)
            return sdf.format(dt)
        }
    override fun toString(): String = "[$format_time]$username : $content\n"
}

fun iAnyGet(type:ObjType, id:Long):iAny?{
    return when(type){
        ObjType.Friend->QQ.friends_all[id]
        ObjType.Group->QQ.groups[id]
        ObjType.Discuss->QQ.discussions[id]
    }
}
fun iAnyGet(recent:Recent):iAny?{
    return iAnyGet(ObjType.comments.toEnum(recent.type), recent.uin)
}


enum class ObjType{
    Friend, Group, Discuss;
    object comments {
        fun toInt(type:ObjType):Int = when(type){
            Friend->0
            Group->1
            Discuss->2
        }
        fun toEnum(type:Int):ObjType = when(type){
            0->Friend
            1->Group
            2->Discuss
            else->throw Exception("Out of Range.")
        }
    }
}
