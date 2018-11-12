package com.pflm.module.core.controller;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.FindIterable;
import com.pflm.annotation.ApiSysyLog;
import com.pflm.module.BaseController;
import com.pflm.module.core.service.TokenService;
import com.pflm.mongodb.MongoSdkBase;
import com.pflm.utils.DateUtils;
import com.pflm.utils.R;
import com.pflm.utils.TimeUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 基础token
 * @author qinxuewu
 * @version 1.00
 * @time 7/11/2018下午 6:00
 */
@RestController
@RequestMapping("/token")
public class TokenController extends BaseController {
    public  final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TokenService tokenService;

    /**
     * 获取基础token
     * @return
     */
    @ApiSysyLog("获取基础token")
    @RequestMapping("/get")
    public R getToken(){
        try {
            FindIterable<Document> result = MongoSdkBase.getColl("access_token").find();
            Document doc=result.first();
            if(doc==null){
                JSONObject info=tokenService.token(appid,appsecret,"client_credential");
                logger.debug("获取基础token接口返回结果：{}",info);
                if(info.containsKey("access_token")){
                    info.put("createTime",TimeUtil.getNowDayTimeFullStr());
                    info.put("expiry_date",TimeUtil.timet2FullString(Integer.parseInt(TimeUtil.getUnixDate()+"")+info.getIntValue("expires_in")));
                    MongoSdkBase.insertOne(MongoSdkBase.getColl("access_token"),info);
                }
                return R.ok().put("data",info);
            }else{
                Date expiryDate =DateUtils.stringToDate(doc.getString("expiry_date"),DateUtils.DATE_TIME_PATTERN);
                Date now = new Date();
                //续期token 离过期还要10分钟时
                if(DateUtils.getDateTimeMinutesBetween(now,expiryDate)<=10) {
                    JSONObject info=tokenService.token(appid,appsecret,"client_credential");
                    logger.debug("续期基础token接口返回结果：{}",info);
                    if(info.containsKey("access_token")){
                        doc.put("access_token",info.getString("access_token"));
                        doc.put("expires_in",info.getIntValue("expires_in"));
                        doc.put("updateTime",TimeUtil.getNowDayTimeFullStr());
                        doc.put("expiry_date",TimeUtil.timet2FullString(Integer.parseInt(TimeUtil.getUnixDate()+"")+info.getIntValue("expires_in")));
                        MongoSdkBase.updateOne(MongoSdkBase.getColl("access_token"),doc.getString("_id"),doc);
                        MongoSdkBase.insertOne(MongoSdkBase.getColl("access_token"),info);
                    }
                    return R.ok().put("data",info);
                }
            }
        }catch (Exception e){
            logger.error("获取基础token异常",e);
            return R.error("服务繁忙,请联系管理员");
        }
        return null;
    }


    @ApiSysyLog("测试获取基础token")
    @RequestMapping("/test/getToken")
    public R testGetTOken(){
        JSONObject info=tokenService.token(appid,appsecret,"client_credential");
        return  R.ok().put("data",info);
    }

}
