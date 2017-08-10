package com.heerkirov.qq

import com.scienjus.smartqq.callback.MessageCallback
import com.scienjus.smartqq.client.SmartQQClient
import com.scienjus.smartqq.model.*
import sun.reflect.generics.tree.Tree
import com.heerkirov.utils.InsertSet

object QQ{
    // client
    val client:SmartQQClient = SmartQQClient(object : MessageCallback {
        override fun onMessage(message: Message) {
            println("Received message(${message.userId}):${message.content}")
            val person = friends_all[message.userId]
            if(person!=null){
                person.messages.add(message)
                val msg = iMessage(message.time, person.friend.nickname, message.content, person)
                person.getMessageList().add(msg)
                recents.addFirst(person)
                messageCallBack?.invoke(msg)
            }else{
                println("Receivcd a message which is not your friend.")
            }

        }

        override fun onGroupMessage(message: GroupMessage) {

            val group = groups[message.groupId]!!
            println("Received group message(${message.userId}) in (${group.group.name}):${message.content}")
            group.messages.add(message)
            val groupuser = group.users[message.userId]
            if(groupuser!=null){
                val person = friends_all[groupuser.uin]
                val msg = if(groupuser.card.isNullOrEmpty()&&person!=null)
                    iMessage(message.time, person.getTitle(), message.content, group, person)
                else if(groupuser.card.isNullOrEmpty())
                    iMessage(message.time, groupuser.card, message.content, group, null)
                else
                    iMessage(message.time, groupuser.nick, message.content, group, null)
                group.getMessageList().add(msg)
                recents.addFirst(group)
                messageCallBack?.invoke(msg)
            }else{
                println("Receivcd a message which is not belong to group.")
            }

        }

        override fun onDiscussMessage(message: DiscussMessage) {
            println("Received discuss message(${message.userId}):${message.content}")
            val discuss = discussions[message.discussId]!!
            discuss.messages.add(message)
            val discussuser = discuss.users[message.userId]
            if(discussuser!=null){
                val person = friends_all[discussuser.uin]
                val msg = if(person!=null)
                    iMessage(message.time, person.getTitle(), message.content, discuss, person)
                else
                    iMessage(message.time, discussuser.nick, message.content, discuss, null)
                discuss.getMessageList().add(msg)
                recents.addFirst(discuss)
                messageCallBack?.invoke(msg)
            }else{
                println("Receivcd a message which is not belong to group.")
            }

        }
    }).also { it.qrCodeDelegate = object : SmartQQClient.QRCodeDelegate{
        override fun run(filepath: String?) {
            // do anything
            qrCodeDelegate?.invoke(filepath)
        }
    } }.also { it.verifyDelegate = object : SmartQQClient.VerifyDelegate{
        override fun returns(success: Boolean, userinfo: UserInfo?) {
            // 在成功时，首先调用一次缓存刷新方法，将初始缓存置入。
            if(success){
                init_client(userinfo)
            }
            verifyingDelegate?.invoke(success, userinfo)
        }
    } }


    // 外部调用消息反馈时的回调
    var messageCallBack:((iMessage)->Unit)? = null
    // 外部调用获得QR码时的回调
    var qrCodeDelegate:((String?)->Unit)? = null
    // 外部调用QR码验证返回时的回调
    var verifyingDelegate:((Boolean, UserInfo?)->Unit)? = null
    // 发生登陆成功事件并且初始化完成之后执行的回调
    var loginSuccessDelegate:((UserInfo?)->Unit)? = null

    // 存储的所有联系人列表。给予uid形成字典
    val friends_all:HashMap<Long, iFriend> = HashMap()
    val friends_category:HashMap<Int, iCategory> = HashMap()
    val groups:HashMap<Long, iGroup> = HashMap()
    val discussions:HashMap<Long, iDiscuss> = HashMap()
    // 存储的有序列表，提供给UI的列表使用
    val arr_friends:ArrayList<iFriend> = ArrayList()
    val arr_categories:ArrayList<iCategory> = ArrayList()
    val arr_groups:ArrayList<iGroup> = ArrayList()
    val arr_discussions:ArrayList<iDiscuss> = ArrayList()
    //最近联系人列表
    val recents:InsertSet<iAny> = InsertSet()
    // 自己
    val me:iMe = iMe()

    fun init_client(userinfo: UserInfo?=null){
        //确认登陆成功之后执行的初始化操作.该操作会异步执行，并进行回调
        Thread({
            updateAllList()
            updateRecentFromServer()
            updateMeInfo()
            loginSuccessDelegate?.invoke(userinfo)

        }).start()
    }
    fun getGroupUser(uin:Long): GroupUser?{
        for((_,ig) in groups){
            if(ig.users.containsKey(uin))return ig.users[uin]
        }
        return null
    }
    fun getDiscusUser(uin:Long): DiscussUser? {
        for((_,ig) in discussions){
            if(ig.users.containsKey(uin))return ig.users[uin]
        }
        return null
    }
    fun updateAllList(delegate_func:(()->Unit)?=null){
        updateFriendList()
        updateDiscussList()
        updateGroupList()
        delegate_func?.invoke()
    }
    fun updateFriendList(delegate_func:(()->Unit)?=null){
        val datas = client.friendListWithCategory
        friends_all.clear()
        friends_category.clear()
        datas.forEach {
            iCategory(it).let { o ->
                friends_category.put(it.index, o)
                arr_categories.add(o)
            }
            it.friends.forEach {
                iFriend(it).let { o ->
                    friends_all.put(it.userId, o)
                    arr_friends.add(o)
                }
            }
        }
        delegate_func?.invoke()
    }
    fun updateGroupList(delegate_func:(()->Unit)?=null){
        val datas = client.groupList
        groups.clear()
        datas.forEach {
            val info = client.getGroupInfo(it.code)
            iGroup(it, info).let { o ->
                groups.put(it.id, o)
                arr_groups.add(o)
            }


        }
        delegate_func?.invoke()
    }
    fun updateDiscussList(delegate_func:(()->Unit)?=null){
        val datas = client.discussList
        discussions.clear()
        datas.forEach {
            val info = client.getDiscussInfo(it.id)
            iDiscuss(it, info).let { o ->
                discussions.put(it.id, o)
                arr_discussions.add(o)
            }

        }
        delegate_func?.invoke()
    }
    fun updateRecentFromServer(delegate_func:(()->Unit)?=null){
        // 从服务器更新最近联系人信息
        val datas = client.recentList
        recents.clear()
        datas.forEach {
            iAnyGet(it)?.let { recents.addLast(it) }
        }
        delegate_func?.invoke()
    }
    fun updateMeInfo(){
        me.userinfo = client.accountInfo
    }
}