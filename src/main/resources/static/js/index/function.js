
function init(){
    if(window.ws !== undefined){
        window.ws.close();
    }
    friends = [];
    group = [];
    notification = {};
    message = {};
    chatBoxType = 0;
    memberDialog.close();
    refreshChat();
    refreshGroup();
    refreshFriend();
    refreshNotification();
    if(token !== "" && username !== ""){
        let title = $("#appbar_title");
        title.empty();
        title.append(username);
        $("#appbar_btn_add").show();
        $("#appbar_btn_exit").show();
        $("#appbar_btn_login").hide();
        websocket();
    }else{
        username = "";
        let title = $("#appbar_title");
        title.empty();
        title.append("未登录");
        $("#appbar_btn_add").hide();
        $("#appbar_btn_exit").hide();
        $("#appbar_btn_login").show();
    }
}

function websocket(){
    let host = "ws://localhost:8080/ws"
    window.ws = new WebSocket(host + "/" + token);
    window.ws.onopen = function(){
        loadFriend();
        loadGroup();
    }
    window.ws.onmessage = function (me) {
        let m = JSON.parse(me.data);
        console.log(m);
        let msg = m.msg;
        let handler = {
            "get_group": function (msg){
                group = msg;
                refreshGroup();
            },
            "get_friends": function (msg){
                friends = msg;
                refreshFriend();
            },
            "group_message": function (msg){
                let name = msg["group_name"];
                notification[`group${msg["group_id"]}`] = {
                    id: msg["group_id"].toString(),
                    name: name+"#"+msg["group_id"].toString(),
                    type: 2,
                    time: msg["time"],
                    msg: `${msg["account_username"]}:${msg["msg"]}`
                };
                let index = `group${msg["group_id"]}`;
                if(!message.hasOwnProperty(index)){
                    message[index] = [];
                }
                message[`group${msg["group_id"]}`].push({
                    id: msg["group_id"].toString(),
                    type: 2,
                    time: msg["time"],
                    msg: msg["msg"],
                    from: msg["account_username"]
                });
                refreshNotification();
                refreshChatMessage(msg["group_id"].toString(), 2);

            },
            "friend_msg" : function (msg){
                let friend = msg["from_username"] === username ?  msg["to_username"] :  msg["from_username"];
                notification[`friend${friend}`] = {
                    name: friend.toString(),
                    id: friend.toString(),
                    type: 1,
                    time: msg["time"],
                    msg: `${msg["from_username"]}:${msg["msg"]}`
                };

                let index = `friend${friend}`;
                if(!message.hasOwnProperty(index)){
                    message[index] = [];
                }
                message[index].push({
                    id: friend.toString(),
                    type: 1,
                    time: msg["time"],
                    msg: `${msg["msg"]}`,
                    from: msg["from_username"]
                });

                refreshNotification();
                refreshChatMessage(friend.toString(), 1);

            },
            "friend_online": function (msg){
                for(let i in friends){
                    if(friends[i].username === msg){
                        friends[i].is_online = true;
                        break;
                    }
                }
                if(chatBoxId === msg){
                    refreshChat();
                }
                //friends[msg].is_online = true;
                refreshFriend();
            },
            "friend_offline": function (msg){
                for(let i in friends){
                    if(friends[i].username === msg){
                        friends[i].is_online = false;
                        break;
                    }
                }
                if(chatBoxId === msg){
                    refreshChat();
                }
                refreshFriend();
            },
            "delete_friend": function (msg){
                for(let i in friends){
                    if(friends[i].username === msg){
                        friends.splice(parseInt(i),1);
                        break;
                    }
                }

                refreshFriend();
                if (chatBoxType === 1 && chatBoxId === msg){
                    chatBoxType = 0;
                    refreshChat();
                }
                if(onDeleteFriend){
                    onDeleteFriend = false;
                    mdui.snackbar("删除好友成功！", {
                        position: "top"
                    });
                }else{
                    mdui.snackbar(msg + " 解除与你的好友关系！", {
                        position: "top"
                    });
                }
            },
            "add_group": function (msg){
                if(msg === "group not found"){
                    onAddGroup = false;
                    mdui.snackbar("群组不存在！", {
                        position: "top"
                    });
                    return;
                }
                if(onAddGroup){
                    onAddGroup = false;
                    mdui.snackbar("加入群组成功！", {
                        position: "top"
                    });
                }
                group.push(msg);
                refreshGroup();
            },
            "new_friend": function (msg){
                if(msg === "account not found"){
                    mdui.snackbar("用户不存在！", {
                        position: "top"
                    })
                    return;
                }
                if(onAddFriend){
                    onAddFriend = false;
                    mdui.snackbar("添加好友成功！", {
                        position: "top"
                    });
                }else{
                    mdui.snackbar(msg.username + " 添加你为好友！", {
                        position: "top"
                    });
                }
                friends.push(msg);
                refreshFriend();
            },
            "exit_group": function (msg){
                mdui.snackbar("退出群组成功！", {
                    position: "top"
                });
                //group.delete(msg);
                //delete group[msg];
                for(let i in group){
                    if(group[i].id.toString() === msg){
                        group.slice(parseInt(i),1);
                        break;
                    }
                }
                refreshGroup();
                if (chatBoxType === 2 && chatBoxId === msg){
                    chatBoxType = 0;
                    refreshChat();
                }
            },
            "new_group": function (msg){
                mdui.snackbar("创建群组成功！", {
                    position: "top"
                });
                group.push(msg);
                refreshGroup();
            },
            "get_member": function (msg){
                if(msg === "you are not member!"){
                    return;
                }
                showGroupMember(msg);
            }
        }

        if(handler.hasOwnProperty(m.key)){
            try{
                handler[m.key](msg);
            }catch (e) {
                console.log(e);
            }

        }
    }
    window.ws.onclose = function (ev){
        username = "";
        token = "";
        init();
        mdui.snackbar("连接断开，请重新登录！", {
            timeout: 0,
            position: "top"
        })
    }

}

function loadFriend(){
    window.ws.send("get_friends:{\"uuid\":\"FRIENDS"+username + "\"}");
}
function loadGroup(){
    window.ws.send("get_group:{\"uuid\":\"GROUP"+username +"\"}");
}

function refreshFriend(){
    let ul = $("#friends_list");
    ul.empty();
    for(let f of friends){
        let code = "<li class=\"mdui-list-item mdui-ripple\">\n" +
            "                    <div onclick='showChat(1,\""+f.username+"\")' class=\"mdui-list-item-avatar mdui-color-theme-accent \"><i class=\"mdui-icon material-icons\">person</i></div>\n" +
            "                    <div onclick='showChat(1,\""+f.username+"\")' class=\"mdui-list-item-content\">\n" +
            "                        <div class=\"mdui-list-item-title\">"+ f.username +"</div>\n" +
            "                        <div class=\"mdui-list-item-text mdui-list-item-one-line\">"+ (f.is_online ? "在线" : "离线") +"</div>\n" +
            "                    </div>\n" +
            "<button mdui-menu=\"{target: '#delete-friend-menu-"+f.username+"'}\" class=\"mdui-btn mdui-btn-icon\"><i class=\"mdui-icon material-icons\">more_horiz</i></button>"+
            "<ul id='delete-friend-menu-"+f.username+"' class=\"mdui-menu\">\n" +
            "<li class=\"mdui-menu-item\">" +
            "<a onclick='deleteFriendDialog(\""+f.username+"\")'  href=\"javascript:;\" class=\"mdui-ripple\">删除</a>" +
            "</li>" +
            "</ul>"+
            "                </li>\n" +
            "                <li class=\"mdui-divider-inset mdui-m-y-0\"></li>";
        ul.append($(code));
    }
    if(friends.length === 0){
        let code = "<li class=\"mdui-list-item mdui-ripple\">\n" +
            "                    <div class=\"mdui-list-item-avatar mdui-color-theme-accent \"><i class=\"mdui-icon material-icons\">hourglass_empty</i></div>\n" +
            "                    <div class=\"mdui-list-item-content\">\n" +
            "                        <div class=\"mdui-list-item-title\">无好友</div>\n" +
            "                    </div>\n" +
            "                </li>\n" +
            "                <li class=\"mdui-divider-inset mdui-m-y-0\"></li>";
        ul.append($(code));
    }
}
function refreshGroup(){
    let ul = $("#group_list");
    ul.empty();
    for(let g of group){
        let code = "<li  class=\"mdui-list-item mdui-ripple\">\n" +
            "                    <div onclick='showChat(2,\""+g.id+"\")' class=\"mdui-list-item-avatar mdui-color-theme-accent \"><i class=\"mdui-icon material-icons\">group</i></div>\n" +
            "                    <div onclick='showChat(2,\""+g.id+"\")' class=\"mdui-list-item-content\">\n" +
            "                        <div class=\"mdui-list-item-title\">"+ g.name + "#" + g.id +"</div>\n" +
            "                    </div>\n" +
            "<button mdui-menu=\"{target: '#delete-group-menu-"+g.id+"'}\" class=\"mdui-btn mdui-btn-icon\"><i class=\"mdui-icon material-icons\">more_horiz</i></button>"+
            "<ul id='delete-group-menu-"+g.id+"' class=\"mdui-menu\">\n" +
            "<li class=\"mdui-menu-item\">" +
            "<a onclick='deleteGroupDialog(\""+g.id+"\",\""+g.name+"\")'  href=\"javascript:;\" class=\"mdui-ripple\">删除</a>" +
            "</li>" +
            "</ul>"+
            "                </li>\n" +
            "                <li class=\"mdui-divider-inset mdui-m-y-0\"></li>";
        ul.append($(code));
    }
    if(group.length === 0){
        let code = "<li class=\"mdui-list-item mdui-ripple\">\n" +
            "                    <div class=\"mdui-list-item-avatar mdui-color-theme-accent \"><i class=\"mdui-icon material-icons\">hourglass_empty</i></div>\n" +
            "                    <div class=\"mdui-list-item-content\">\n" +
            "                        <div class=\"mdui-list-item-title\">无群组</div>\n" +
            "                    </div>\n" +
            "                </li>\n" +
            "                <li class=\"mdui-divider-inset mdui-m-y-0\"></li>";
        ul.append($(code));
    }
}

function refreshHistory(){
    if(historyDialog.getState() === "closed" || historyDialog.getState() === "closing"){
        return;
    }
    let pa = $("#span_now_page");
    pa.empty();
    pa.append(" "+nowPage+" ");

    $("#history_next").attr("disabled");
    $("#history_pre").attr("disabled");
    if(nowHistoryId !== chatBoxType+"|"+chatBoxId){
        nowHistoryId = chatBoxType+"|"+chatBoxId;
        historyBuff = [];
    }
    if(historyBuff.hasOwnProperty(nowPage)){
        showHistory();
    }else{
        if(chatBoxType === 1){
            let ul = $("#dialog_history_ul");
            ul.empty();
            $("#dialog_history_process").show();
            historyDialog.handleUpdate();
            fetchPost("/message/friend", {
                token: token,
                page: nowPage-1,
                page_size: 20,
                time: startTime,
                friend: chatBoxId
            }, (code, msg, data) => {
                if(code === 400){
                    mdui.snackbar("Token 过期，请重新登录！", {position:"top"});
                }
                if(code === 200){
                    historyBuff[nowPage-1] = [];
                    for(let msg of data){
                        historyBuff[nowPage-1].push({
                            from: msg["from_username"],
                            msg: msg["msg"],
                            time: msg["time"]
                        });
                    }
                    showHistory();
                }else{
                    mdui.snackbar("加载失败："+msg, {position:"top"});
                }
            });
        }else{
            let ul = $("#dialog_history_ul");
            ul.empty();
            $("#dialog_history_process").show();
            historyDialog.handleUpdate();
            fetchPost("/message/group", {
                group_id: chatBoxId,
                token: token,
                page: nowPage-1,
                page_size: 20,
                time: startTime
            }, (code, msg, data) => {
                if(code === 400){
                    mdui.snackbar("Token 过期，请重新登录！", {position:"top"});
                }
                if(code === 200){
                    historyBuff[nowPage-1] = [];
                    for(let msg of data){
                        historyBuff[nowPage-1].push({
                            from: msg["account_username"],
                            msg: msg["msg"],
                            time: msg["time"]
                        });
                    }
                    showHistory();
                }else{
                    mdui.snackbar("加载失败："+msg, {position:"top"});
                }
            });
        }
    }
}

function showHistory(){

    let ul = $("#dialog_history_ul");

    $("#dialog_history_process").hide();
    ul.empty();
    if(!historyBuff.hasOwnProperty(nowPage-1) || historyBuff[nowPage-1].length === 0){
        let li = "<li class=\"mdui-list-item mdui-ripple chat-bu-left\">\n" +
            "                        <div class=\"mdui-list-item-avatar mdui-color-theme-accent\"><i class=\"mdui-icon material-icons\">hourglass_empty</i></div>\n" +
            "                        <div class=\"mdui-list-item-content\">\n" +
            "                            <div class=\"mdui-list-item-title\">无消息</div>\n" +
            "                        </div>\n" +
            "                    </li>"
        ul.append(li);
    }
    for(let msg of historyBuff[nowPage-1]){
        if(msg.from === username){
            let li = $("<li class=\"mdui-list-item mdui-ripple chat-bu-right\">\n" +
                "                        <div class=\"mdui-list-item-content\">\n" +
                "                            <div class=\"mdui-list-item-text mdui-list-item-one-line\">"+formatDate(msg.time)+" " + msg.from+"</div>\n" +
                "                            <div class=\"mdui-list-item-title\">"+msg.msg+"</div>\n" +
                "                        </div>\n" +
                "                        <div class=\"mdui-list-item-avatar mdui-color-theme\"><i class=\"mdui-icon material-icons\">person</i></div>\n" +
                "                    </li>");
            ul.append(li);
        }else{
            let li = "<li class=\"mdui-list-item mdui-ripple chat-bu-left\">\n" +
                "                        <div class=\"mdui-list-item-avatar mdui-color-theme-accent\"><i class=\"mdui-icon material-icons\">person</i></div>\n" +
                "                        <div class=\"mdui-list-item-content\">\n" +
                "                            <div class=\"mdui-list-item-text mdui-list-item-one-line\">"+msg.from+" " +formatDate(msg.time) +"</div>\n" +
                "                            <div class=\"mdui-list-item-title\">"+msg.msg+"</div>\n" +
                "                        </div>\n" +
                "                    </li>";
            ul.append(li);
        }
    }


    historyDialog.handleUpdate();
    $("#history_next").removeAttr("disabled");
    $("#history_pre").removeAttr("disabled");
}

function onPreHistoryButtonClick(){
    if(nowPage <= 1){
        return;
    }
    $("#history_next").attr("disabled");
    $("#history_pre").attr("disabled");
    nowPage --;
    refreshHistory();
}
function onNextHistoryButtonClick(){
    if(!historyBuff.hasOwnProperty(nowPage-1) || historyBuff[nowPage-1].length === 0){
        return;
    }
    $("#history_next").attr("disabled");
    $("#history_pre").attr("disabled");
    nowPage ++;
    refreshHistory();
}

function showGroupMemberDialog(){
    let title = $("#dialog_group_title");
    title.empty();
    for(let g of group){
        if(g.id.toString() === chatBoxId){
            title.append(g.name+"#"+g.id + " 群成员");
        }
    }
    $("#dialog_group_member_process").show();
    $("#dialog_group_ul").empty();
    memberDialog.open();
    //mdui.updateSpinners("#dialog_group_member_process");

}
function showGroupMember(msg){
    if(memberDialog.getState() === "closed" || memberDialog.getState() === "closing"){
        return;
    }
    let ul = $("#dialog_group_ul");
    ul.empty();
    for(let f of msg){
        let code = "<li class=\"mdui-list-item mdui-ripple\">\n" +
            "                    <div class=\"mdui-list-item-avatar mdui-color-theme-accent \"><i class=\"mdui-icon material-icons\">person</i></div>\n" +
            "                    <div class=\"mdui-list-item-content\">\n" +
            "                        <div class=\"mdui-list-item-title\">"+ f.username +"</div>\n" +
            "                        <div class=\"mdui-list-item-text mdui-list-item-one-line\">"+ (f.is_online ? "在线" : "离线") +"</div>\n" +
            "                    </div>\n" +
            "                </li>\n" ;
        ul.append($(code));
    }
    $("#dialog_group_member_process").hide();
    memberDialog.handleUpdate()
    //mdui.updateSpinners("#dialog_group_member_process");
}

function refreshNotification(){
    let list = [];
    for(let key in notification){
        if(notification.hasOwnProperty(key))
            list.push(notification[key]);
    }
    list.sort(
        function(a, b){
            return  b.time - a.time;
        }
    );
    let ul = $("#notification_list");
    ul.empty();
    for(let n of list){
        let code = "<li onclick='showChat("+n.type+",\""+n.id+"\")' class=\"mdui-list-item mdui-ripple\">\n" +
            "                    <div class=\"mdui-list-item-avatar mdui-color-theme-accent \"><i class=\"mdui-icon material-icons\">chat</i></div>\n" +
            "                    <div class=\"mdui-list-item-content\">\n" +
            "                        <div class=\"mdui-list-item-title\">"+ n.name +"</div>\n" +
            "                        <div class=\"mdui-list-item-text mdui-list-item-one-line\">"+ n.msg +"</div>\n" +
            "                    </div>\n" +
            "                </li>\n" +
            "                <li class=\"mdui-divider-inset mdui-m-y-0\"></li>";
        ul.append($(code));
    }
    if(list.length === 0){
        let code = "<li class=\"mdui-list-item mdui-ripple\">\n" +
            "                    <div class=\"mdui-list-item-avatar mdui-color-theme-accent \"><i class=\"mdui-icon material-icons\">hourglass_empty</i></div>\n" +
            "                    <div class=\"mdui-list-item-content\">\n" +
            "                        <div class=\"mdui-list-item-title\">无通知</div>\n" +
            "                    </div>\n" +
            "                </li>\n" +
            "                <li class=\"mdui-divider-inset mdui-m-y-0\"></li>";
        ul.append($(code));
    }
}
function refreshChatMessage(id, type){
    if(id === chatBoxId && chatBoxType === type){
        refreshChat();
    }
}

function onChatTargetClick(){
    if(chatBoxType === 2){
        window.ws.send("get_member:{\"uuid\":\""+Date.now()+"\",\"group_id\":\""+chatBoxId+"\"}");
        showGroupMemberDialog();
    }
}

function refreshChat(){
    let ul = $("#chat_msg");
    let targetM = $('#chat-target-msg');
    let targetD = $("#chat-target-div");
    let avFriend = $('#chat-avatar-friend');
    let avGroup = $('#chat-avatar-group');
    let targetG = $('#chat-target-group-msg');
    ul.empty();
    if(chatBoxType === 0){
        let code = "<li class=\"mdui-list-item mdui-ripple\">\n" +
            "                    <div class=\"mdui-list-item-avatar mdui-color-theme-accent \"><i class=\"mdui-icon material-icons\">hourglass_empty</i></div>\n" +
            "                    <div class=\"mdui-list-item-content\">\n" +
            "                        <div class=\"mdui-list-item-title\">无聊天</div>\n" +
            "                    </div>\n" +
            "                </li>\n" ;
        ul.append($(code));
        avFriend.hide();
        avGroup.hide();
        targetD.hide();
        targetG.hide();
        $("#chat-input-div").hide();
        return;
    }
    $('#chat-input-div').show();
    let index ;
    if(chatBoxType === 1){
        index = "friend"+chatBoxId;
        avFriend.show();
        avGroup.hide();
        targetD.show();
        targetM.empty();
        targetG.hide();
        for(let f of friends){
            if(f.username === chatBoxId){
                targetM.append(chatBoxId+(f.is_online? "- 在线":"- 离线"));
            }
        }
        console.log(chatBoxId);
    }else{
        avFriend.hide();
        avGroup.show();
        index = "group"+chatBoxId;
        targetD.show();
        targetM.empty();
        targetG.show();
        for(let g of group){
            if(g.id.toString() === chatBoxId){
                targetM.append(g.name + "#" +chatBoxId);
                break;
            }
        }

    }
    if(message.hasOwnProperty(index)){
        let mList = message[index];
        for(let msg of mList){
            if(msg.from === username){
                let li = $("<li class=\"mdui-list-item mdui-ripple chat-bu-right\">\n" +
                    "                        <div class=\"mdui-list-item-content\">\n" +
                    "                            <div class=\"mdui-list-item-text mdui-list-item-one-line\">"+msg.from+"</div>\n" +
                    "                            <div class=\"mdui-list-item-title\">"+msg.msg+"</div>\n" +
                    "                        </div>\n" +
                    "                        <div class=\"mdui-list-item-avatar mdui-color-theme\"><i class=\"mdui-icon material-icons\">person</i></div>\n" +
                    "                    </li>");
                ul.append(li);
            }else{
                let li = "<li class=\"mdui-list-item mdui-ripple chat-bu-left\">\n" +
                    "                        <div class=\"mdui-list-item-avatar mdui-color-theme-accent\"><i class=\"mdui-icon material-icons\">person</i></div>\n" +
                    "                        <div class=\"mdui-list-item-content\">\n" +
                    "                            <div class=\"mdui-list-item-text mdui-list-item-one-line\">"+msg.from+"</div>\n" +
                    "                            <div class=\"mdui-list-item-title\">"+msg.msg+"</div>\n" +
                    "                        </div>\n" +
                    "                    </li>"
                ul.append(li);
            }
        }
    }
    //ul.scrollIntoView();
    ul.scrollTop(ul[0].scrollHeight);
}
function showChat(type, id){
    chatBoxType = type;
    chatBoxId = id;
    refreshChat();
    mainTab.show(3);
}

function onSendBtnClick(){
    let sendBox = $("#chat-input-textfield");
    let text = sendBox.val();
    if(text === ""){
        mdui.snackbar("请输入内容", {position:"top"});
        return;
    }
    let time = Date.now();
    if(chatBoxType === 1){
        window.ws.send("friend_msg:{\"uuid\":\""+time+"\",\"to_username\":\""+chatBoxId+"\",\"msg\":\""+text+"\",\"time\":"+time+"}");
    }else{
        window.ws.send("group_message:{\"uuid\":\""+time+"\",\"group_id\":\""+chatBoxId+"\",\"msg\":\""+text+"\",\"time\":"+time+"}");

    }
}
function onHistBtnClick(){
    historyBuff = [];
    nowPage = 1;
    nowHistoryId = chatBoxType+"|"+chatBoxId;
    startTime = Date.now();
    historyDialog.open();
    let title = $("#dialog_history_title");
    title.empty();
    if(chatBoxType === 1){
        title.append("与 " + chatBoxId + " 的聊天记录");
    }else{
        title.append("群组 " + chatBoxId + " 的聊天记录");
    }
    refreshHistory();
}

function register(username, password){
    fetchPost("/account/register", {
        username: username,
        password: password
    }, (code, msg, data) => {
        if(code === 200){
            login(username, password);
        }else{
            mdui.snackbar({
                message: '注册失败',
                position: 'top'
            });
        }
    })
}
function login(name, password){
    if(loginDialog != null){
        loginDialog.close();
    }
    fetchPost("/account/login", {
        username: name,
        password: password
    }, (code, msg, data)=>{
        if(code === 401){
            mdui.dialog({
                title: '用户名不存在',
                content: '需要直接注册吗？',
                buttons: [
                    {
                        text: '取消'
                    },
                    {
                        text: '确认',
                        onClick: function(){
                            register(name, password);
                        }
                    }
                ]
            });
        }else if(code === 400){
            mdui.snackbar({
                message: '用户名或密码错误',
                position: 'top'
            });
        }else if(code === 200){
            mdui.snackbar({
                message: '登录成功',
                position: 'top'
            });
            console.log(data);
            username = data.username;
            token = data["token"];
            init();
        }
    })
}

function onAppbarLoginButtonClick(){
    loginDialog.open();
}
function onAppbarExitButtonClick(){
    token = "";
    init();
    mdui.snackbar("退出登录成功 ！", {
        timeout: 0,
        position: "top"
    })
}



function onAddFriendButtonClick(){
    mdui.prompt('请输入要添加的好友用户名：',
        function (value) {
            // 添加好友
            addFriend(value);
        },
        function (value) {
            //mdui.alert('你输入了：' + value + '，点击了取消按钮');
        },
    {
        confirmText:'确认',
        cancelText:'取消'
    });
}
function onAddGroupButtonClick(){
    mdui.prompt('请输入要创建的群组名：',
        function (value) {
            // 创建群组
            createGroup(value);
        },
        function (value) {
            //mdui.alert('你输入了：' + value + '，点击了取消按钮');
        }
        ,
        {
            confirmText:'确认',
            cancelText:'取消'
        });
}
function onJoinGroupButtonClick(){
    mdui.prompt('请输入要加入的群 ID：',
        function (value) {
            // 加入群组
            joinGroup(value);
        },
        function (value) {
            //mdui.alert('你输入了：' + value + '，点击了取消按钮');
        }
        ,
        {
            confirmText:'确认',
            cancelText:'取消'
        });
}

function deleteGroupDialog(groupId, groupName){
    mdui.confirm('确认要删除群组 '+groupName + '#' +groupId+' 吗？',
        function(){
            exitGroup(groupId);
        },
        function(){
            //mdui.alert('点击了取消按钮');
        }
        ,
        {
            confirmText:'确认',
            cancelText:'取消'
        });
}
function deleteFriendDialog(username){
    mdui.confirm('确认要删除好友 '+username+' 吗？',
        function(){
            deleteFriend(username);
        },
        function(){
            //mdui.alert('点击了取消按钮');
        }
        ,
        {
            confirmText:'确认',
            cancelText:'取消'
        });
}
function deleteFriend(username){
    if(username === ""){
        return;
    }
    onDeleteFriend = true;
    let msg = 'delete_friend:{"uuid":"'+Date.now()+'","friend_username":"'+username+'"}';
    window.ws.send(msg);
}
function exitGroup(groupId){
    let n = parseInt(groupId);
    if(isNaN(n)){
        return;
    }
    let msg = 'exit_group:{"uuid":"'+Date.now()+'","group_id":"'+n+'"}';
    window.ws.send(msg);
}
function addFriend(username){
    if(username === ""){
        mdui.snackbar({
            message: '用户名不能为空！',
            position: 'top'
        });
        return;
    }
    onAddFriend = true;
    let msg = 'new_friend:{"uuid":"'+Date.now()+'","friend_username":"'+username+'"}';
    window.ws.send(msg);
}
function joinGroup(groupId){
    let n = parseInt(groupId);
    if(isNaN(n)){
        mdui.snackbar({
            message: '请输入正确的群组ID！',
            position: 'top'
        });
        return;
    }
    onAddGroup = true;
    let msg = 'add_group:{"uuid":"'+Date.now()+'","group_id":"'+groupId+'"}';
    window.ws.send(msg);
}
function createGroup(groupName){
    if(groupName === ""){
        mdui.snackbar({
            message: '群组名不能为空！',
            position: 'top'
        });
        return;
    }
    let msg = 'new_group:{"uuid":"'+Date.now()+'","group_name":"'+groupName+'"}';
    window.ws.send(msg);
}

function onLoginButtonClick(){
    const loginEmailDiv = $("#dialog_login_username_input_div");
    if (loginEmailDiv.hasClass("mdui-textfield-invalid")
        || loginEmailDiv.hasClass("mdui-textfield-invalid-html5")){
        return;
    }
    const username = $("#login_username_input").val();
    const password = $("#login_password_input").val();
    if (username === ""){
        loginEmailDiv.addClass("mdui-textfield-invalid");
        return;
    }
    if (password === ""){
        $("#dialog_login_password_div").addClass("mdui-textfield-invalid");
        return;
    }
    login(username, password);
}
function onLoginPasswordInputClick() {
    $("#dialog_login_password_error").text("密码不能为空");
    $("#dialog_login_password_div").removeClass("mdui-textfield-invalid");
}

function formatDate(time) {
    let date = new Date(time);
    let YY = date.getFullYear() + '/';
    let MM = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '/';
    let DD = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate());
    let hh = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    let mm = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    return YY + MM + DD +" "+hh + mm;
}

