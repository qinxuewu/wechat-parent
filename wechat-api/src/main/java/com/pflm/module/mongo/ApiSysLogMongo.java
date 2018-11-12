package com.pflm.module.mongo;
import com.pflm.mongodb.MongoSdkBase;
import org.aspectj.lang.annotation.Aspect;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * api工程接口请求日志
 * @author qinxuewu
 * @version 1.00
 * @time 7/11/2018下午 2:50
 */
@Aspect
@Component
public class ApiSysLogMongo{
    public static final Logger log = LoggerFactory.getLogger(ApiSysLogMongo.class);
    public static final String TABNAME="api_sys_log";

    /**
     * 添加日志
     * @param doc
     */
    public static void  saveApiSysLog(Document doc){
        MongoSdkBase.insertOne(MongoSdkBase.getColl(TABNAME),doc);
    }
}
