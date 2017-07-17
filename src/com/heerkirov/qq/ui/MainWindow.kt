package com.heerkirov.qq.ui
import com.heerkirov.qq.*
import com.heerkirov.qq.utils.*
import com.scienjus.smartqq.callback.MessageCallback
import com.scienjus.smartqq.client.SmartQQClient
import com.scienjus.smartqq.model.*
import java.awt.*
import java.awt.event.ItemEvent
import java.awt.event.MouseAdapter
import java.awt.event.WindowAdapter
import javax.swing.*
import javax.swing.UIManager
import javax.swing.event.ListSelectionListener
import java.io.IOException
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.Timer
import kotlin.collections.HashMap


interface Queryable{
    fun insertElement(id:String, ele:Any):Any
    fun query(id:String):Any
}

object MainWindow : JFrame("QQ-KT"), Queryable{
    init {
        size = Dimension(640, 480)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ConstructUI()
        BuildEvent()

        isVisible = true
        BuildClient()
    }

    override fun insertElement(id: String, ele: Any):Any = querydict.put(id, ele)!!
    override fun query(id: String):Any  = QueryMemory.comments.dict[id]!!

    fun queryLabel(id:String):JLabel = query(id) as JLabel
    fun queryButton(id:String):JButton = query(id) as JButton
    fun queryTableGrid(id:String):TableGrid = query(id) as TableGrid
    fun queryCardGrid(id:String):CardGrid = query(id) as CardGrid
    fun queryText(id:String):JTextArea = query(id) as JTextArea
    fun queryList(id:String):JList<String> = query(id) as JList<String>
    fun queryCombo(id:String):JComboBox<String> = query(id) as JComboBox<String>
    fun queryPanel(id:String):DrawPanel = query(id) as DrawPanel
    private val querydict:HashMap<String,Any> by lazy { HashMap<String, Any>() }

    private fun ConstructUI() {
        cardgrid("frame"){
            tablegrid("loginpage",
                    arrayOf(wPixel(50), wStar(1.0), wPixel(165), wStar(1.0), wPixel(50)),
                    arrayOf(wStar(1.0), wPixel(165), wStar(1.0)), "loginpage"){
                label("login",JLabel.CENTER,0,0,1,3)
                panel("qrcode",2,1) {  }
                label("login-label","",JLabel.CENTER,4,0,1,3)
            }
            tablegrid("mainpage", arrayOf(wPixel(30), wStar(5.0)),
                    arrayOf(wPixel(50), wPixel(50), wStar(3.0)), "mainpage"){
                button("recent-btn", "Recent")
                button("friends-btn","Friends",0,1)
                cardgrid("top-select", 1,0,1,2){
                    tablegrid(arrayOf(wStar(1.0)),
                            arrayOf(wStar(1.0)), "recent"){
                        list("recent-list") {  }
                    }
                    tablegrid(arrayOf(wPixel(30), wStar(1.0)),
                            arrayOf(wStar(1.0), wStar(1.0), wStar(1.0)), "friends"){
                        button("sel-friends-btn","friends",0,0)
                        button("sel-discussions-btn","discussions",0,1)
                        button("sel-groups-btn","groups",0,2)
                        cardgrid("type-select", 1,0,1,3){
                            tablegrid(arrayOf(wPixel(30), wStar(1.0)), arrayOf(wStar(1.0)), "friends"){
                                combo("category-combo"){  }
                                list("friends-list",1,0){ item("friends") }
                            }
                            tablegrid(arrayOf(wStar(1.0)), arrayOf(wStar(1.0)),"discussions"){
                                list("discussions-list") { item("discussions") }
                            }
                            tablegrid(arrayOf(wStar(1.0)), arrayOf(wStar(1.0)),"groups"){
                                list("groups-list") { item("groups") }
                            }
                        }
                    }
                }
                tablegrid(arrayOf(wPixel(30), wStar(10.0), wStar(1.0)),
                        arrayOf(wStar(1.0), wPixel(50)),
                        0,2,2,1){
                    label("talk-title", "",JLabel.LEFT,0,0,1,2)
                    text("talk-text",1,0,1,2)
                    text("talk-input",2,0)
                    button("send-btn", "send",2,1)
                }
            }

        }
    }

    private fun BuildEvent(){
        queryButton("recent-btn").addMouseListener(MouseClick{
            queryCardGrid("top-select").goto("recent")
            select_top = RECENT
            selected_old = null
            updateFriendList(RECENT)
        })
        queryButton("friends-btn").addMouseListener(MouseClick{
            queryCardGrid("top-select").goto("friends")
            val now = queryCardGrid("type-select").getNow()
            selected_old = null
            if(now==null||now=="friends") {
                select_top = FRIENDS
                updateFriendList(FRIENDS)
            }else if(now=="discussions") {
                select_top = DISCUSSIONS
                updateFriendList(DISCUSSIONS)
            }
            else if(now=="groups") {
                select_top = GROUPS
                updateFriendList(GROUPS)
            }
        })

        queryButton("sel-friends-btn").addMouseListener(MouseClick{
            queryCardGrid("type-select").goto("friends")
            updateCategoryCombo()
            selected_old = null
            select_top = FRIENDS
            updateFriendList(FRIENDS)
        })
        queryButton("sel-discussions-btn").addMouseListener(MouseClick{
            queryCardGrid("type-select").goto("discussions")
            selected_old = null
            select_top = DISCUSSIONS
            updateFriendList(DISCUSSIONS)
        })
        queryButton("sel-groups-btn").addMouseListener(MouseClick{
            queryCardGrid("type-select").goto("groups")
            selected_old = null
            select_top = GROUPS
            updateFriendList(GROUPS)
        })

        queryCombo("category-combo").addItemListener {
            if(it.stateChange==ItemEvent.SELECTED) {
                val id = (it.source as JComboBox<String>).selectedIndex
                category = QQ.arr_categories[id]
            }
            else if(it.stateChange==ItemEvent.DESELECTED) category = null
            updateFriendList(FRIENDS)
        }

        queryList("recent-list").addListSelectionListener(ListSelector{
            val index = (it?.source as JList<String>).selectedIndex
            if(index>=0){
                val goal = QQ.recents[index]
                selected_old = goal
                turnTalker(goal)
                goal.turnAllMessageRead()
                updateFriendList(RECENT)
            }
        })
        queryList("friends-list").addListSelectionListener(ListSelector{
            if(category!=null){
                val index = (it?.source as JList<String>).selectedIndex
                if(index>=0){
                    val friend:Friend = category?.arr_friends?.get(index)!!
                    val goal = QQ.friends_all[friend.userId]
                    selected_old = goal
                    turnTalker(goal)
                    goal?.let {
                        it.turnAllMessageRead()
                        updateFriendList(FRIENDS)
                    }
                }
            }
        })
        queryList("groups-list").addListSelectionListener(ListSelector{
            val index = (it?.source as JList<String>).selectedIndex
            if(index>=0){
                val group:iGroup = QQ.arr_groups[index]
                selected_old = group
                turnTalker(group)
                group.turnAllMessageRead()
                updateFriendList(GROUPS)
            }
        })
        queryList("discussions-list").addListSelectionListener(ListSelector{
            val index = (it?.source as JList<String>).selectedIndex
            if(index>=0){
                val discuss:iDiscuss = QQ.arr_discussions[index]
                selected_old = discuss
                turnTalker(discuss)
                discuss.turnAllMessageRead()
                updateFriendList(DISCUSSIONS)
            }
        })
        queryText("talk-input").addKeyListener(KeySendClick{
            Send()
        })
        queryButton("send-btn").addMouseListener(MouseClick{
            Send()
        })

    }

    private fun BuildClient(){
        QQ.qrCodeDelegate = { filepath ->
            val panel = queryPanel("qrcode")
            try {
                val image = ImageIO.read(File(filepath))
                panel.action={ p, g -> g?.drawImage(image,0,0,p) }
                panel.repaint()
            } catch (ex: IOException) {
                ex.printStackTrace()
                println("A exception was threw while loading QR code:${ex.message}")
            }
        }
        QQ.verifyingDelegate = { success, userinfo ->
            println("QR code verifying result: $success")
            val login_label = queryLabel("login-label")
            if(success){
                login_label.text="Welcome, ${userinfo?.nick!!}"
            }else{
                login_label.text="QR code verifying failed. Try again."
            }
        }
        QQ.loginSuccessDelegate = { userinfo ->
            val timer=java.util.Timer()
            timer.schedule(object:TimerTask(){
                override fun run() {
                    queryCardGrid("frame").goto("mainpage")
                    updateCategoryCombo()
                    updateFriendList(RECENT)
                }
            },100)
            val login_label = queryLabel("login-label")
            login_label.text="Welcome, ${userinfo?.nick!!} Loading..."
        }
        QQ.messageCallBack = { msg ->
            msg.belong?.let {
                if(talker==msg.belong){  // 现在正在查看更新的消息的拥有者
                    val textfield = queryText("talk-text")
                    textfield.text += msg.toString()
                }else{
                    // todo 给出提醒信息
                }
                updateFriendList(RECENT)
            }
        }
        QQ.client.link()
    }
    const val RECENT = 0
    const val FRIENDS = 1
    const val DISCUSSIONS = 2
    const val GROUPS = 3

    // 用于记录现在正在显示的选项卡
    private var select_top:Int = 0
    // 用于记录本列表中上次选定的项，做刷新前后的传承使用
    private var selected_old:iAny? = null

    private fun updateFriendList(type:Int){
        /*
            type = 0 : Recent
                 = 1 : Friends
                 = 2 : Discussions
                 = 3 : Groups
         */
        fun updateRecent(){
            val ui = queryList("recent-list")
            var newindex = -1
            ui.model = DefaultListModel<String>().also { dlm ->
                QQ.recents.forEachIndexed{ i,iany ->
                    dlm.addElement(iany.getTypeTitle())
                    if(iany==selected_old)newindex=i
                }
            }
            if(newindex!=-1) {
                ui.selectedIndex = newindex
                println("Old extends select: $newindex")
            }
            else selected_old = null
        }
        fun updateFriends(){
            val ui = queryList("friends-list")
            if(category!=null){
                var newindex = -1
                println("Change category of friend: ${category?.category?.name}")
                ui.model = DefaultListModel<String>().also { dlm ->
                    category?.arr_friends?.forEachIndexed { i,iany ->
                        val friend = QQ.friends_all[iany.userId]
                        dlm.addElement(friend?.getTypeTitle())
                        if(friend==selected_old)newindex=i
                    }
                }
                if(newindex!=-1) ui.selectedIndex = newindex
                else selected_old = null
            }else{
                println("Change category of friend: null")
                ui.model = DefaultListModel<String>()
                ui.selectedIndex = 0
                selected_old = null
            }
        }
        fun updateDiscussions(){
            val ui = queryList("discussions-list")
            var newindex = -1
            ui.model = DefaultListModel<String>().also { dlm ->
                QQ.arr_discussions.forEachIndexed { i,iany ->
                    dlm.addElement(iany.discuss.name)
                    if(iany== selected_old)newindex=i
                }
            }
            if(newindex!=-1) ui.selectedIndex = newindex
            else selected_old = null
        }
        fun updateGroups(){
            val ui = queryList("groups-list")
            var newindex = -1
            ui.model = DefaultListModel<String>().also { dlm ->
                QQ.arr_groups.forEachIndexed { i,iany ->
                    dlm.addElement(iany.group.name )
                    if(iany== selected_old)newindex=i
                }
            }
            if(newindex!=-1) ui.selectedIndex = newindex
            else selected_old = null
        }
        //Thread({
            when(type){
                RECENT->updateRecent()
                FRIENDS->updateFriends()
                DISCUSSIONS->updateDiscussions()
                GROUPS->updateGroups()
            }
        //}).start()
    }

    private fun updateCategoryCombo(){
        val ui = queryCombo("category-combo")
        val vect = Vector(QQ.arr_categories.mapNotNull { "${it.category.name} (${it.arr_friends.size})" })
        ui.model = DefaultComboBoxModel<String>(vect)
        category = QQ.arr_categories.firstOrNull()
    }



    private var category:iCategory? = null

    private var talker:iAny? = null

    private fun turnTalker(t:iAny?){
        // 切换到目标聊天框
        val title = queryLabel("talk-title")
        val textfield = queryText("talk-text")
        val input = queryText("talk-input")
        talker = t

        if(talker!=null){
            title.text = talker?.getTitle() ?:""
            println("Turn to talker: ${title.text}")
            val sb = StringBuilder()
            talker?.getMessageList()?.forEach {
                sb.append(it.toString())
            }
            textfield.text = sb.toString()
        }else{
            title.text = ""
        }
    }

    private fun Send(){
        val textfield = queryText("talk-text")
        val input = queryText("talk-input")
        if(input.text.isNotBlank()){
            if(talker!=null){
                val nowtime = System.currentTimeMillis() / 1000
                if(talker is iFriend){
                    val id = (talker as iFriend).friend.userId
                    QQ.client.sendMessageToFriend(id, input.text)
                    val msg = iMessage(nowtime, QQ.me.getTitle(), input.text, QQ.me, read=true)
                    (talker as iFriend).getMessageList().add(msg)
                    textfield.text += msg.toString()
                }else if(talker is iGroup){
                    val id = (talker as iGroup).group.id
                    QQ.client.sendMessageToGroup(id, input.text)
                    val msg = iMessage(nowtime, QQ.me.getTitle(), input.text, QQ.me, read=true)
                    (talker as iGroup).getMessageList().add(msg)
                    textfield.text += msg.toString()
                }else if(talker is iDiscuss){
                    val id = (talker as iDiscuss).discuss.id
                    QQ.client.sendMessageToDiscuss(id, input.text)
                    val msg = iMessage(nowtime, QQ.me.getTitle(), input.text, QQ.me, read=true)
                    (talker as iDiscuss).getMessageList().add(msg)
                    textfield.text += msg.toString()
                }
                QQ.recents.addFirst(talker as iAny)
                updateFriendList(RECENT)
            }
            input.text = ""
        }
    }
}