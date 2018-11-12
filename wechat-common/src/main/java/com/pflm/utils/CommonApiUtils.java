package com.pflm.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Properties;
/**
 * 通过文件流获取配置文件属性
 * @author qinxuewu
 * @version 1.00
 * @time 7/11/2018下午 2:07
 */
public class CommonApiUtils {
    private static final Logger logger = LoggerFactory.getLogger(CommonApiUtils.class);

    /**
     * mongodb 连接参数
     */
    public static String mongo_host_port;
    public static String mongo_user_pass_db;

    static{
        try{
            InputStream is = CommonApiUtils.class.getResourceAsStream("/application.properties");
            Properties properties = new Properties();
            properties.load(is);
            //是否存在 激活属性配置文件
            if(properties.containsKey("spring.profiles.active")){
                String type = properties.getProperty("spring.profiles.active");
                is = CommonApiUtils.class.getResourceAsStream("/application-"+type+".properties");
                properties = new Properties();
                properties.load(is);
            }
            mongo_host_port=properties.getProperty("mongo.host.port");
            mongo_user_pass_db=properties.getProperty("mongo.user.pass-db");
            is.close();
        }catch(Exception ex){
            logger.debug("加载配置文件出错：{}",ex);
        }
    }
}
