package com.pflm.module.core.service;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 基础token获取
 * @author qinxuewu
 * @version 1.00
 * @time 7/11/2018下午 4:30
 */
@FeignClient(name="token", url = "${wx.api.url}",fallback = TokenHystric.class)
public interface TokenService {
    /**
     * 获取基础token
     * @param appid
     * @param secret
     * @param grant_type
     * @return
     */
    @RequestMapping(value = "/cgi-bin/token", method = RequestMethod.GET)
    public JSONObject token(@RequestParam("appid") String appid, @RequestParam("secret") String secret,@RequestParam("grant_type") String grant_type);
}
