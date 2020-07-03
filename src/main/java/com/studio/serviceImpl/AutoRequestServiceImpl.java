package com.studio.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.studio.common.socketio.RequestParam;
import com.studio.common.socketio.ResponseParam;
import com.studio.service.AutoRequestService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AutoRequestServiceImpl implements AutoRequestService {
    @Override
    public void distribute(String data, String session) {
        ResponseParam response = new ResponseParam();
        if (StringUtils.isEmpty(data)){
            try {
                RequestParam request = JSON.parseObject(data,RequestParam.class);
            }catch (Exception e){

            }
        }else {
            response.setBody("无效请求");
        }
    }
}
