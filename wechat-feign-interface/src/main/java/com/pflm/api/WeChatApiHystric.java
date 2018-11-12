package com.pflm.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

/**
 * 熔断处理
 * @author qinxuewu
 * @version 1.00
 * @time 25/10/2018下午 5:54
 */
@Component
public class WeChatApiHystric implements  WeChatApi {

    @Override
    public JSONObject getInfo() {
        JSONObject json=new JSONObject();
        json.put("msg","接口请求失败超时");
        json.put("code",502);
        return json;
    }
}
