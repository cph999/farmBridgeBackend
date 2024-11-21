package com.cph.handler;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cph.entity.Message;
import com.cph.entity.PostBid;
import com.cph.entity.User;
import com.cph.mapper.MessageMapper;
import com.cph.mapper.PostBidMapper;
import com.cph.mapper.UserMapper;
import com.cph.utils.SpringContextUtil;
import com.google.gson.Gson;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;
import java.util.Map;
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

        //竞价消息
        if ("bid".equals(m.getType()) || "bid-reply".equals(m.getType())) {
            PostBidMapper postBidMapper = SpringContextUtil.getBean(PostBidMapper.class);
            PostBid postBid = gson.fromJson(m.getMessage(), PostBid.class);
            if ("bid-reply".equals(m.getType())) {
                Map<String, String> map = gson.fromJson(m.getMessage(), Map.class);
                postBid = gson.fromJson(map.get("message"), PostBid.class);
            }
            postBid.setFromId(m.getFromId()).setToId(m.getToId());
            QueryWrapper<PostBid> wrapper = new QueryWrapper<>();
            wrapper.eq("from_id", postBid.getFromId()).eq("to_id", postBid.getToId()).eq("order_id", postBid.getOrderId());
            PostBid postBid1 = postBidMapper.selectOne(wrapper);
            if (postBid1 == null) {
                QueryWrapper<PostBid> wrapperA = new QueryWrapper<>();
                wrapperA.eq("from_id", postBid.getToId()).eq("to_id", postBid.getFromId()).eq("order_id", postBid.getOrderId());
                PostBid postBid2 = postBidMapper.selectOne(wrapperA);
                if (postBid2 != null) {
                    postBid2.setChatRestrictState(postBid.getChatRestrictState());
                    postBid2.setBidPrice(postBid.getBidPrice());
                    postBidMapper.updateById(postBid2);
                    m.setMessage(gson.toJson(postBid2));
                } else {
                    postBidMapper.insert(postBid);
                    m.setMessage(gson.toJson(postBid));
                }
            } else {
                postBid1.setChatRestrictState(postBid.getChatRestrictState());
                postBid1.setBidPrice(postBid.getBidPrice());
                m.setMessage(gson.toJson(postBid1));
                postBidMapper.updateById(postBid1);
            }
        }
        messageMapper.insert(m);

//         将接收到的消息发送给指定用户
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
