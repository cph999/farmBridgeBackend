package com.cph.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cph.aspect.RecognizeAddress;
import com.cph.aspect.UserContext;
import com.cph.common.CommonResult;
import com.cph.entity.Message;
import com.cph.entity.User;
import com.cph.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    MessageMapper messagesMapper;

    /**
     * 查询当前用户的全部对话框
     * @return
     */
    @PostMapping("/messages")
    @RecognizeAddress
    public CommonResult getAllMessages() {
        User currentUser = UserContext.getCurrentUser();
        List<Message> messages = messagesMapper.selectList(
                new QueryWrapper<Message>()
                        .eq("from_id", currentUser.getId())
                        .or()
                        .eq("to_id", currentUser.getId())
        );

        // 将对话按照无序的 from_id 和 to_id 进行分组
        Map<String, List<Message>> collect = messages.stream()
                .collect(Collectors.groupingBy(m -> {
                    Long minId = (long) Math.min(m.getFromId(), m.getToId());
                    Long maxId = (long) Math.max(m.getFromId(), m.getToId());
                    return minId + " " + maxId;
                }));

        ArrayList<List<Message>> lists = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");

        collect.values().forEach(temp -> {
            if (!CollectionUtils.isEmpty(temp)) {
                lists.add(temp.stream()
                        .sorted(Comparator.comparing(Message::getCreatedTime))  // 按照创建时间排序
                        .map(m -> {
                            String formattedDate = sdf.format(m.getCreatedTime());
                            m.setShowTime(formattedDate);
                            return m;
                        })
                        .collect(Collectors.toList()));
            }
        });

        // 最终按照最新消息的创建时间对对话框进行排序
        List<List<Message>> messageList = lists.stream()
                .sorted((a, b) -> b.get(0).getCreatedTime().compareTo(a.get(0).getCreatedTime()))
                .collect(Collectors.toList());

        return new CommonResult(200, "success", messageList);
    }

    /**
     * 查询两个人的全部聊天信息
     * @param map
     * @return
     */
    @PostMapping("/messagesByDialog")
    @RecognizeAddress
    public CommonResult getAllMessagesByDialog(@RequestBody Map map) {
        User currentUser = UserContext.getCurrentUser();
        List<Message> messages = messagesMapper.selectList(
                new QueryWrapper<Message>()
                        .eq("from_id", currentUser.getId())
                        .or()
                        .eq("to_id", currentUser.getId())
        );
        Integer otherId = currentUser.getId();
        if(map.get("fromId") == otherId){
            otherId = (Integer) map.get("toId");
        }else{
            otherId = (Integer) map.get("fromId");
        }

        Integer finalOtherId = otherId;
        messages = messages.stream().filter(m -> Objects.equals(m.getFromId(), finalOtherId)
                || Objects.equals(m.getToId(), finalOtherId)).collect(Collectors.toList());
        messages.sort(Comparator.comparing(Message::getCreatedTime));

        ArrayList<List<Message>> lists = new ArrayList<>();
        lists.add(messages);
        return new CommonResult(200, "success", lists);
    }
}