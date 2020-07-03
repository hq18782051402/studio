package com.studio.socketIO;

import com.corundumstudio.socketio.SocketIOClient;
import com.studio.common.socketio.ResponseParam;

import java.util.Map;


public interface SocketIOService {
    void start();

    void stop();
    /**
     * 加入socket房间
     */
    void joinRoom(String room, String session);
    /**
     * 离开socket房间
     */
    void leaveRoom(String room, String session);
    /**
     *获取所有的在线客户端
     */
    Map<String, SocketIOClient> getClients();
    /**
     * 推送响应消息
     */
    void pushResponse(ResponseParam response);
}
