package com.studio.socketIO;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.studio.common.constant.RequestConstant;
import com.studio.common.socketio.ResponseParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.*;


@Service
public class SocketIOServiceImpl implements SocketIOService {
    //存已连接的客户端
    private static Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();

    private static ExecutorService cachedThreadPool = new ThreadPoolExecutor(100, 200, 5000, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(1000), r -> {
        Thread t = new Thread(r);
        t.setName("Request--" + t.getId());
        return t;
    });

    @Autowired
    private SocketIOServer socketIOServer;


    @PostConstruct
    private void autoStartup(){
        start();
    }

    @PreDestroy
    private void autoStop(){
        stop();
    }

    @Override
    public void start() {
        //客户端连接
        socketIOServer.addConnectListener(client -> {
            String sessionId = client.getSessionId().toString();
            if (!StringUtils.isEmpty(sessionId)) {
                clientMap.put(sessionId, client);
            }
        });
        //客户端断开
        socketIOServer.addDisconnectListener(client -> {
            String sessionId = client.getSessionId().toString();
            if (!StringUtils.isEmpty(sessionId)) {
                clientMap.remove(sessionId);
                client.disconnect();
            }
        });
        //处理请求
        socketIOServer.addEventListener(RequestConstant.REQUEST, String.class, (client, data, ackSender) -> {

        });
        socketIOServer.start();
    }

    @Override
    public void stop() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            socketIOServer = null;
        }
    }

    @Override
    public void joinRoom(String room, String session) {
        if (!StringUtils.isEmpty(session)) {
            SocketIOClient client = SocketIOServiceImpl.clientMap.get(session);
            if (client != null) {
                client.joinRoom(room);
            }
        }
    }

    @Override
    public void leaveRoom(String room, String session) {
        if (!StringUtils.isEmpty(session)) {
            SocketIOClient client = SocketIOServiceImpl.clientMap.get(session);
            if (client != null) {
                client.leaveRoom(room);
            }
        }
    }

    @Override
    public Map<String, SocketIOClient> getClients() {
        return clientMap;
    }

    @Override
    public void pushResponse(ResponseParam response) {
        if (StringUtils.isEmpty(response.getSession())) {
            SocketIOClient client = clientMap.get(response.getSession());
            response.setSession(null);
            String value = JSON.toJSONString(response);
            if (client != null)
                client.sendEvent(RequestConstant.RESPONSE, value);
        }
    }
}
