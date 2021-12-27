package com.heyanle.hehehe.controller;

import com.heyanle.hehehe.controller.info.GroupMessageInfo;
import com.heyanle.hehehe.controller.info.MessageInfo;
import com.heyanle.hehehe.entity.*;
import com.heyanle.hehehe.helper.JWTHelper;
import com.heyanle.hehehe.repository.AccountRepository;
import com.heyanle.hehehe.repository.GroupMemberRepository;
import com.heyanle.hehehe.repository.GroupMessageRepository;
import com.heyanle.hehehe.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by HeYanLe on 2021/5/31 21:27.
 * https://github.com/heyanLE
 */
@RestController
@RequestMapping(value = "/message", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    private final GroupMemberRepository groupMemberRepository;
    private final MessageRepository messageRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final JWTHelper jwtHelper;

    public MessageController(GroupMemberRepository groupMemberRepository, MessageRepository messageRepository, GroupMessageRepository groupMessageRepository, JWTHelper jwtHelper) {
        this.groupMemberRepository = groupMemberRepository;
        this.messageRepository = messageRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.jwtHelper = jwtHelper;
    }

    @RequestMapping(value = "friend", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse<List<Message>> getMessage(
            @RequestBody  MessageInfo info){
        String username;
        try{
            username = jwtHelper.getUsername(info.getToken());
        }catch (Exception e){
            return HttpResponse.withData(400, "Token Error", null);
        }

        String friend = info.getFriend();
        Pageable pageable = PageRequest.of(info.getPage(), info.getPageSize());
        Page<Message> page = messageRepository.findByUsername(username, friend, info.getTime(), pageable);
        List<Message> messages = page.stream().toList();

        return HttpResponse.withData(200, "Completely", messages);
    }
    @RequestMapping(value = "group", method = RequestMethod.POST)
    @ResponseBody
    public HttpResponse<List<GroupMessage>> getGroupMessage(
            @RequestBody GroupMessageInfo info){
        String username;
        try{
            username = jwtHelper.getUsername(info.getToken());
        }catch (Exception e){
            return HttpResponse.withData(400, "Token Error", null);
        }


        Long groupId = info.getGroupId();
        Optional<GroupMemberItem> o = groupMemberRepository.findFirstByGroupIdAndMemberUsername(groupId, username);
        if(o.isEmpty()){
            return HttpResponse.withData(401, "Not member", null);
        }
        Pageable pageable = PageRequest.of(info.getPage(), info.getPageSize());
        Page<GroupMessage> page = groupMessageRepository.findByGroupIdAndTimeLessThanOrderByTimeDesc(groupId, info.getTime(), pageable);
        List<GroupMessage> messages = page.stream().toList();
        return HttpResponse.withData(200, "Completely", messages);
    }


}
