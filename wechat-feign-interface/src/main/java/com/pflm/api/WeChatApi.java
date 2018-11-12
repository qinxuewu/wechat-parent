package com.pflm.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 接口调用
 */
@FeignClient(value ="${api.server.name}",fallback=WeChatApiHystric.class)
public interface WeChatApi {


    @RequestMapping(value = "/api/base/getInfo", method = RequestMethod.POST)
    public JSONObject getInfo();
}
