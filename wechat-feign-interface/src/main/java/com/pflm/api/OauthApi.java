package com.pflm.api;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * 微信网页授权
 * @author qinxuewu
 * @create 18/11/4下午9:11
 * @since 1.0.0
 */

@FeignClient(name = "oauth",url = "${wx.url}",fallback=OauthApiHystric.class)
public interface OauthApi {
}
