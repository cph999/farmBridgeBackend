package com.cph.handler;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cph.entity.Message;
import com.cph.entity.PostBid;
import com.cph.entity.User;
import com.cph.entity.vo.PostBidMessageVo;
import com.cph.mapper.MessageMapper;
import com.cph.mapper.PostBidMapper;
import com.cph.mapper.UserMapper;
import com.cph.utils.SpringContextUtil;
import com.google.gson.Gson;
import org.springframework.beans.BeanUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 存储所有活跃的WebSocket会话
    private static ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = getQueryParams(session);
        String userId = params.get("userId");
        if (userId != null) {
            // 在session的属性中存储userId
            session.getAttributes().put("userId", userId);
            sessions.put(userId, session); // 将新的会话添加到map中
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        MessageMapper messageMapper = SpringContextUtil.getBean(MessageMapper.class);
        String payload = message.getPayload();
        Gson gson = new Gson();
        Message m = gson.fromJson(payload, Message.class);
        if (StringUtils.isBlank(m.getToIcon()) || StringUtils.isBlank(m.getToNickname())) {
            UserMapper userMapper = SpringContextUtil.getBean(UserMapper.class);
            User user = userMapper.selectById(m.getToId());
            m.setToIcon(user.getCover()).setToNickname(user.getNickname());
        }

        // 处理竞价消息
        if ("bid".equals(m.getType()) || "bid-reply".equals(m.getType()) || "complete-bid".equals(m.getType())) {
            PostBidMapper postBidMapper = SpringContextUtil.getBean(PostBidMapper.class);
//            PostBidMessageVo postBidMessageVo = gson.fromJson(m.getMessage(), PostBidMessageVo.class);
            PostBid postBid = gson.fromJson(m.getMessage(), PostBid.class);
            if ("bid-reply".equals(m.getType()) || "complete-bid".equals(m.getType())) {
                Map<String, String> map = gson.fromJson(m.getMessage(), Map.class);
                postBid = gson.fromJson(map.get("message"), PostBid.class);
            }
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//            if(!StringUtils.isBlank(postBidMessageVo.getCreatedTime())){
//                postBid.setCreatedTime( dateFormat.parse(postBidMessageVo.getCreatedTime()));
//            }
//            if(!StringUtils.isBlank(postBidMessageVo.getUpdateTime())){
//                postBid.setUpdateTime( dateFormat.parse(postBidMessageVo.getUpdateTime()));
//            }
            postBid.setFromId(m.getFromId()).setToId(m.getToId());
            QueryWrapper<PostBid> wrapper = new QueryWrapper<>();
            wrapper.eq("from_id", postBid.getFromId()).eq("to_id", postBid.getToId()).eq("order_id", postBid.getOrderId());
            PostBid postBid1 = postBidMapper.selectOne(wrapper);
            if (postBid1 == null) {
                QueryWrapper<PostBid> wrapperA = new QueryWrapper<>();
                wrapperA.eq("from_id", postBid.getToId()).eq("to_id", postBid.getFromId()).eq("order_id", postBid.getOrderId());
                PostBid postBid2 = postBidMapper.selectOne(wrapperA);
                if (postBid2 != null) {
                    if (postBid2.getChatRestrictState() == 5) {
                        // 如果 chatRestrictState == 5，给发送者发送一条通知消息
                        Message notification = new Message();
                        notification.setFromId(m.getToId());
                        notification.setToId(m.getFromId());
                        notification.setType("str");
                        notification.setMessage("该交易已经处理，您的所有行为都将失效");
                        notification.setCreatedTime(new Date());
                        // 将通知消息发送给发送者
                        WebSocketSession senderSession = sessions.get(m.getFromId().toString());
                        if (senderSession != null && senderSession.isOpen()) {
                            senderSession.sendMessage(new TextMessage(gson.toJson(notification)));
                        }
                    }
                    postBid2.setChatRestrictState(postBid.getChatRestrictState());
                    postBid2.setBidPrice(postBid.getBidPrice());
                    postBid2.setUpdateTime(new Date());
                    postBidMapper.updateById(postBid2);
                    m.setMessage(gson.toJson(postBid2));
                } else {
                    postBidMapper.insert(postBid);
                    m.setMessage(gson.toJson(postBid));
                }
            } else {
                if (postBid1.getChatRestrictState() == 5) {
                    // 如果 chatRestrictState == 5，给发送者发送一条通知消息
                    Message notification = new Message();
                    notification.setFromId(m.getToId());
                    notification.setToId(m.getFromId());
                    notification.setType("str");
                    notification.setMessage("该交易已经处理,您的所有行为都将失效！");
                    notification.setCreatedTime(new Date());
                    // 将通知消息发送给发送者
                    WebSocketSession senderSession = sessions.get(m.getFromId().toString());
                    if (senderSession != null && senderSession.isOpen()) {
                        senderSession.sendMessage(new TextMessage(gson.toJson(notification)));
                    }
                }
                postBid1.setChatRestrictState(postBid.getChatRestrictState());
                postBid1.setBidPrice(postBid.getBidPrice());
                postBid1.setUpdateTime(new Date());
                m.setMessage(gson.toJson(postBid1));
                postBidMapper.updateById(postBid1);
            }
        }

        messageMapper.insert(m);

        // 将接收到的消息发送给指定用户
        WebSocketSession targetSession = sessions.get(m.getToId().toString());
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(new TextMessage(gson.toJson(m)));
        }

        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 获取存储在session中的userId
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.remove(userId); // 移除对应的会话
        }
    }

    private Map<String, String> getQueryParams(WebSocketSession session) {
        String query = session.getUri().getQuery();
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
    }
}
