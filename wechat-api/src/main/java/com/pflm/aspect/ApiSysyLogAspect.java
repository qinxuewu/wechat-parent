package com.pflm.aspect;
import com.pflm.annotation.ApiSysyLog;
import com.pflm.module.mongo.ApiSysLogMongo;
import com.pflm.utils.HttpContextUtils;
import com.pflm.utils.IPUtils;
import com.pflm.utils.TimeUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 日志，切面处理类
 * @author qinxuewu
 * @version 1.00
 * @time 7/11/2018下午 2:39
 */
@Aspect
@Component
public class ApiSysyLogAspect {
    public  final Logger log = LoggerFactory.getLogger(getClass());
    @Pointcut("@annotation(com.pflm.annotation.ApiSysyLog)")
    public void logPointCut() {

    }


    @Around("logPointCut()")
    public Object saveSysLog(ProceedingJoinPoint joinPoint)throws Throwable{
        Object result = null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Document doc=new Document();
        ApiSysyLog syslog = method.getAnnotation(ApiSysyLog.class);
        if (syslog != null) {
            //注解上的描述
            doc.put("operation", syslog.value());
        }
        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        doc.put("method", className + "." + methodName + "()");
        //请求的参数
        Object[] args = joinPoint.getArgs();

        doc.put("params", args);
        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        doc.put("ip", IPUtils.getIpAddr(request));
        doc.put("time",TimeUtil.getNowDayTimeFullStr());

        try {
            result=joinPoint.proceed();
        }catch (Exception e){
            log.error("api日志记录异常：{}",e);
            e.printStackTrace();
        }
        doc.put("result",result);
        ApiSysLogMongo.saveApiSysLog(doc);
        return result;
    }
}
