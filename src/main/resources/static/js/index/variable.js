let mainTab = new mdui.Tab("#main-tab", {});
let loginDialog = new mdui.Dialog("#dialog_login");
let memberDialog = new mdui.Dialog("#dialog_group_member")
let historyDialog = new mdui.Dialog("#dialog_history_member");

let historyBuff = [];
let nowPage = 1;
let nowHistoryId = "";
let startTime = 0;

let onAddFriend = false;
let onAddGroup = false;
let onDeleteFriend = false;

let username = "";
let token = "";


/**
 * Notification map
 * @type {{string: {msg: string, name: string, id: string, type: number}}}
 */
let notification = {};

/**
 * Friend list
 * @type {[{username: string, is_online: boolean}]}
 */
let friends = [];

/**
 * Group list
 * @type {[{id: string, name: string}]}
 */
let group = [];

/**
 * Message map
 * @type {{string: [{from_username: string, time: number, msg: string}]}}
 */
let message = {};


let chatBoxId = "";
// 0 -> None | 1 -> Friend | 2 -> Group
let chatBoxType = 0;
